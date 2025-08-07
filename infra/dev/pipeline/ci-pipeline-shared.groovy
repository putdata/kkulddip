pipeline {
    agent any
    
    parameters {
        string(name: 'BRANCH_NAME', defaultValue: 'develop', description: '빌드할 브랜치')
        string(name: 'SHARED_WORKSPACE', defaultValue: '/tmp/shared-workspace', description: '공유 작업 공간 디렉토리')
        string(name: 'SERVICE_NAME', defaultValue: 'kkulddip-app', description: '서비스 이름')
    }
    
    environment {
        SHARED_DIR = "${params.SHARED_WORKSPACE}/${params.SERVICE_NAME}"
    }
    
    stages {
        stage('Clone') {
            steps {
                echo "📥 Cloning repository from branch: ${params.BRANCH_NAME}"
                git branch: "${params.BRANCH_NAME}",
                    credentialsId: 'gitlab_username_with_pw',
                    url: 'https://lab.ssafy.com/a701-be/a701-be.git'
            }
        }
        
        stage('Build') {
            steps {
                echo "🔨 Building Spring Boot application"
                sh '''
                    # 실행 권한 부여
                    chmod +x ./gradlew

                    # Gradle build 실행
                    ./gradlew clean build --refresh-dependencies --no-build-cache --rerun-tasks
                    
                    echo "✅ Build completed successfully"
                '''
            }
        }
        
        stage('Copy to Shared Directory') {
            steps {
                echo "📤 Copying build artifacts to shared directory"
                sh '''
                    # 공유 디렉토리 생성
                    mkdir -p ${SHARED_DIR}
                    
                    # 기존 파일들 정리
                    rm -rf ${SHARED_DIR}/*
                    
                    # jar 파일들 복사
                    if [ -d "build/libs" ]; then
                        echo "Copying build/libs directory..."
                        cp -r build/libs ${SHARED_DIR}/
                        echo "✅ Build artifacts copied to ${SHARED_DIR}/libs"
                    else
                        echo "❌ No build/libs directory found"
                        exit 1
                    fi
                    
                    # 빌드 정보 파일 생성
                    cat > ${SHARED_DIR}/build-info.txt << EOF
BUILD_DATE=$(date)
BRANCH_NAME=${BRANCH_NAME}
BUILD_NUMBER=${BUILD_NUMBER}
JOB_NAME=${JOB_NAME}
WORKSPACE=${WORKSPACE}
EOF
                    
                    echo "📋 Build information saved to ${SHARED_DIR}/build-info.txt"
                    
                    # 공유 디렉토리 내용 확인
                    echo "📂 Shared directory contents:"
                    ls -la ${SHARED_DIR}/
                    
                    if [ -d "${SHARED_DIR}/libs" ]; then
                        echo "📦 Jar files in shared directory:"
                        ls -la ${SHARED_DIR}/libs/
                    fi
                '''
            }
        }
        
        stage('Verify Shared Files') {
            steps {
                echo "🔍 Verifying files in shared directory"
                sh '''
                    # jar 파일 존재 확인
                    JAR_COUNT=$(find ${SHARED_DIR}/libs -name "*.jar" 2>/dev/null | wc -l)
                    if [ "$JAR_COUNT" -eq 0 ]; then
                        echo "❌ No jar files found in shared directory"
                        exit 1
                    else
                        echo "✅ Found $JAR_COUNT jar file(s) in shared directory"
                    fi
                    
                    # 파일 권한 설정
                    chmod -R 755 ${SHARED_DIR}
                    
                    echo "✅ Shared directory verification completed"
                '''
            }
        }
    }
    
    post {
        success {
            echo "🎉 CI Build completed successfully!"
            echo "📦 Artifacts are ready in shared directory: ${SHARED_DIR}"
            
            // 성공 시 알림이나 추가 작업 (옵션)
            script {
                sh '''
                    echo "=== CI Build Summary ==="
                    echo "✅ Source code built successfully"
                    echo "✅ Artifacts copied to: ${SHARED_DIR}"
                    echo "📂 Shared directory size: $(du -sh ${SHARED_DIR} | cut -f1)"
                    echo "🕐 Build completed at: $(date)"
                '''
            }
        }
        failure {
            echo "❌ CI Build failed!"
            script {
                sh '''
                    echo "=== CI Build Failure Info ==="
                    echo "Branch: ${BRANCH_NAME}"
                    echo "Workspace: ${WORKSPACE}"
                    echo "Shared Directory: ${SHARED_DIR}"
                    
                    # 실패한 경우 공유 디렉토리 정리
                    # rm -rf ${SHARED_DIR}/* 2>/dev/null || true
                '''
            }
        }
        always {
            // 워크스페이스 정리
            cleanWs()
        }
    }
}