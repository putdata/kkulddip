pipeline {
    agent any

    parameters {
        string(name: 'SERVICE_NAME', defaultValue: 'kkulddip-app', description: '빌드할 서비스 이름')
        string(name: 'SERVICE_PORT', defaultValue: '8080', description: '빌드할 서비스의 포트 번호')
        string(name: 'SHARED_WORKSPACE', defaultValue: '/tmp/shared-workspace', description: '공유 작업 공간 디렉토리')
    }

    environment {
        IMAGE_NAME = "${params.SERVICE_NAME}:latest"
        SHARED_DIR = "${params.SHARED_WORKSPACE}/${params.SERVICE_NAME}"
    }

    stages {
        stage('Copy Artifacts from Shared Directory') {
            steps {
                echo "📦 Copying artifacts from shared directory"
                sh '''
                    echo "Checking shared directory contents:"
                    ls -la ${SHARED_DIR}/ || echo "Shared directory is empty"
                    
                    # 공유 디렉토리에서 JAR 파일 확인
                    if [ ! -d "${SHARED_DIR}/libs" ]; then
                        echo "❌ No libs directory found in shared workspace"
                        echo "Available directories in ${SHARED_DIR}:"
                        ls -la ${SHARED_DIR}/ || echo "Shared directory not accessible"
                        exit 1
                    fi
                    
                    # JAR 파일 존재 확인
                    JAR_COUNT=$(find ${SHARED_DIR}/libs -name "*.jar" 2>/dev/null | wc -l)
                    if [ "$JAR_COUNT" -eq 0 ]; then
                        echo "❌ No JAR files found in shared directory"
                        ls -la ${SHARED_DIR}/libs/
                        exit 1
                    fi
                    
                    echo "✅ Found $JAR_COUNT JAR file(s) in shared directory"
                    
                    # 현재 workspace에 build/libs 디렉토리 생성
                    mkdir -p build/libs
                    
                    # JAR 파일들을 현재 workspace로 복사
                    cp ${SHARED_DIR}/libs/*.jar build/libs/
                    echo "✅ JAR files copied to workspace"
                    
                    # 복사된 파일 확인
                    echo "📂 Copied JAR files:"
                    ls -la build/libs/
                '''
            }
        }

        stage('Make Dockerfile') {
            steps {
                echo "📄 Creating Dockerfile"
                sh '''
                    # Dockerfile 생성
                    cat > Dockerfile << 'EOF'
FROM openjdk:21-jdk-slim

# 타임존 환경변수 설정
ENV TZ=Asia/Seoul

# 타임존 데이터 설치 및 적용
RUN apt-get update && \
    apt-get install -y tzdata && \
    ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && \
    echo $TZ > /etc/timezone

# 작업 디렉토리 설정
WORKDIR /app

# jar 파일을 컨테이너로 복사
COPY build/libs/*.jar app.jar

# 애플리케이션 포트 설정
EXPOSE 8080

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=", "app.jar"]
EOF
                    echo "✅ Dockerfile created successfully"
                '''
            }
        }

        stage('Verify Artifacts') {
            steps {
                echo "🔍 Verifying copied artifacts"
                sh '''
                    echo "Current workspace contents:"
                    ls -la
                    
                    echo "Build directory contents:"
                    ls -la build/libs/ || echo "No build/libs directory found"
                    
                    echo "Dockerfile check:"
                    if [ -f "Dockerfile" ]; then
                        echo "✅ Dockerfile exists"
                        head -5 Dockerfile
                    else
                        echo "❌ Dockerfile missing"
                        exit 1
                    fi
                    
                    # jar 파일 존재 확인
                    JAR_COUNT=$(find build/libs -name "*.jar" 2>/dev/null | wc -l)
                    if [ "$JAR_COUNT" -eq 0 ]; then
                        echo "❌ No jar files found"
                        exit 1
                    else
                        echo "✅ Found $JAR_COUNT jar file(s)"
                    fi
                '''
            }
        }

        stage('Build Docker Image') {
            steps {
                echo "🔨 Building Docker image: ${IMAGE_NAME}"
                script {
                    sh '''
                        # Docker 이미지 빌드
                        docker build -t ${IMAGE_NAME} .
                        
                        # 이미지가 성공적으로 빌드되었는지 확인
                        docker images | grep ${SERVICE_NAME}
                        
                        echo "✅ Docker image built successfully: ${IMAGE_NAME}"
                    '''
                }
            }
        }
    }

    post {
        success {
            echo "🎉 Deployment completed successfully!"
            script {
                sh '''
                    echo "=== Deployment Summary ==="
                    echo "✅ CI pipeline triggered and build completed"
                    echo "✅ Artifacts copied from shared directory: ${SHARED_DIR}"
                    echo "✅ Docker image built: ${IMAGE_NAME}"
                '''
            }
        }
        failure {
            echo "❌ Deployment failed!"
            script {
                sh '''
                    echo "=== Failure Debugging Info ==="
                    echo "Shared directory contents:"
                    ls -la ${SHARED_DIR}/ || echo "Shared directory not accessible"
                    echo "Current workspace:"
                    ls -la
                    echo "Docker containers:"
                    docker ps -a
                    echo "Docker images:"
                    docker images | head -10
                '''
            }
        }
        always {
            // 공유 디렉토리 정리 (옵션)
            sh '''
                echo "🧹 Cleaning up..."
                # 오래된 빌드 파일들 정리 (7일 이상)
                find ${SHARED_WORKSPACE} -type d -mtime +7 -exec rm -rf {} + 2>/dev/null || true
                docker system prune -f --volumes || true
            '''
        }
    }
}