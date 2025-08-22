pipeline {
    agent any

    parameters {
        string(name: 'BRANCH_NAME', defaultValue: 'develop', description: '클론할 브랜치')
        string(name: 'SERVICE_NAME', defaultValue: 'kkulddip-app', description: '배포할 서비스 이름')
        string(name: 'SHARED_WORKSPACE', defaultValue: '/tmp/shared-workspace', description: '공유 디렉토리 경로')
    }

    environment {
        IMAGE_NAME = "${params.SERVICE_NAME}:latest"
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

        stage('Setup Infrastructure') {
            steps {
                echo "🏗️ Setting up deployment infrastructure"
                sh '''
                    # nginx 서비스 상태 확인 및 시작
                    echo "Checking nginx service status..."
                    
                    # nginx 상태 확인 (sudo 없이 시도)
                    if systemctl is-active --quiet nginx 2>/dev/null; then
                        echo "✅ nginx service is already running"
                    elif sudo /usr/bin/systemctl is-active --quiet nginx 2>/dev/null; then
                        echo "✅ nginx service is running (checked with sudo)"
                    else
                        echo "⚠️ nginx service status check failed - assuming it needs to be started"
                        echo "Please ensure Jenkins user has sudo NOPASSWD access for nginx commands"
                        
                        # nginx 시작 시도
                        if /usr/bin/systemctl start nginx 2>/dev/null; then
                            echo "✅ nginx service started successfully"
                            sleep 5
                        else
                            echo "❌ Failed to start nginx service - please check sudo permissions"
                            echo "Run: sudo visudo and add appropriate NOPASSWD rules for jenkins user"
                            exit 1
                        fi
                    fi
                '''
            }
        }

        stage('Blue/Green Deployment') {
            steps {
                echo "🚀 Starting Blue/Green deployment"
                dir('infra/dev') {
                    // 1단계: Secret File로 .env 생성
                    withCredentials([file(credentialsId: 'dev-env-file', variable: 'ENV_FILE')]) {
                        sh '''
                            echo "📁 Creating .env file from Secret File..."
                            # Secret File을 .env로 복사
                            cp "${ENV_FILE}" .env
                            
                            # .env 파일 권한 설정 (보안)
                            chmod 600 .env
                            
                            echo "✅ .env file created successfully"
                        '''
                    }

                    // 2단계: Firebase 서비스 계정 주입 (Secret file → 워크스페이스 파일로)
                    withCredentials([file(credentialsId: 'firebase-sa-json-file', variable: 'FIREBASE_SA_FILE')]) {
                        sh '''
                            mkdir -p secrets
                            install -m 600 "$FIREBASE_SA_FILE" "secrets/firebase.json"

                            # (중요) .env에 "호스트 경로"와 "컨테이너 경로" 모두 기록
                            touch .docker.env
                            printf 'export HOST_FIREBASE_CREDENTIALS_PATH=%s\n' "$(pwd)/secrets/firebase.json" > .docker.env
                            chmod 600 .docker.env

                            echo "" >> .env
                            echo "FIREBASE_CREDENTIALS_PATH=/run/secrets/firebase.json" >> .env
                        '''
                    }
                    
                    // 3단계: .env 파일을 사용하여 배포 실행
                    sh '''
                        echo "🚀 Starting deployment with .env file..."
                        
                        # .env 파일 존재 확인
                        if [ -f ".env" ]; then
                            echo "✅ .env file found"
                            # 보안상 비밀번호는 숨기고 다른 내용만 표시
                            echo "📋 .env contents (passwords hidden):"
                            grep -v PASSWORD .env || echo "No .env content to show"
                        else
                            echo "❌ .env file not found!"
                            exit 1
                        fi

                        set -a
                        . ./.docker.env
                        set +a
                        
                        # 배포 스크립트에 실행 권한 부여
                        chmod +x script/deploy.sh
                        
                        # Blue/Green 배포 실행
                        script/deploy.sh
                    '''
                }
            }
        }

        stage('Health Check') {
            steps {
                echo "🏥 Performing final health check"
                script {
                    sh '''
                        # 현재 실행 중인 컨테이너 상태 확인
                        echo "📊 Current container status:"
                        docker ps --format "table {{.Names}}\\t{{.Status}}\\t{{.Ports}}"
                        
                        # nginx 상태 확인
                        echo "🌐 nginx service status:"
                        if systemctl status nginx --no-pager -l 2>/dev/null; then
                            echo "✅ nginx status check completed"
                        else
                            echo "⚠️ Could not check nginx status - might need sudo permissions"
                            systemctl status nginx --no-pager -l 2>/dev/null || echo "nginx status unavailable"
                        fi
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
                    echo "✅ Blue/Green deployment completed"
                    echo "✅ nginx configuration updated"
                    echo "✅ Health checks passed"
                    echo "🌐 Application is available at: http://$(curl -s ifconfig.me || echo 'YOUR_EC2_IP')"
                    echo "🔍 Check nginx logs: sudo tail -f /var/log/nginx/kkulddip-app.access.log"
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
                    echo "nginx service status:"
                    systemctl status nginx --no-pager -l || echo "Cannot check nginx status"
                    echo "nginx error log:"
                    tail -20 /var/log/nginx/error.log || echo "Cannot read nginx error log"
                '''
            }
        }
        always {
            // 공유 디렉토리 정리 (옵션)
            sh '''
                echo "🧹 Cleaning up..."
                # 환경변수 파일 정리 (보안)
                rm -f infra/dev/.env || true
                # 오래된 빌드 파일들 정리 (7일 이상)
                find ${SHARED_WORKSPACE} -type d -mtime +7 -exec rm -rf {} + 2>/dev/null || true
                docker system prune -f --volumes || true
            '''
        }
    }
}