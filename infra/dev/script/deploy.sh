#!/bin/bash

set -e

echo "🚀 BLUE/GREEN 배포 시작"
echo "⏰ 시작 시간: $(date '+%Y-%m-%d %H:%M:%S')"

# nginx 설정 파일 경로
NGINX_SITES_AVAILABLE="/etc/nginx/sites-available"
NGINX_SITES_ENABLED="/etc/nginx/sites-enabled"
NGINX_SITE_NAME="kkulddip-app"

# 현재 활성 환경 확인
CURRENT_ENV=""
BLUE_RUNNING=false
GREEN_RUNNING=false

if docker ps --format "table {{.Names}}" | grep -q "kkulddip-app-blue"; then
    if [ "$(docker inspect --format='{{.State.Running}}' kkulddip-app-blue 2>/dev/null)" = "true" ]; then
        BLUE_RUNNING=true
    fi
fi

if docker ps --format "table {{.Names}}" | grep -q "kkulddip-app-green"; then
    if [ "$(docker inspect --format='{{.State.Running}}' kkulddip-app-green 2>/dev/null)" = "true" ]; then
        GREEN_RUNNING=true
    fi
fi

# 환경 상태에 따른 처리
if [ "$BLUE_RUNNING" = true ] && [ "$GREEN_RUNNING" = true ]; then
    echo "⚠️ 두 환경 모두 실행 중입니다. green 환경을 중지하고 blue를 유지합니다..."
    docker-compose -f docker/docker-compose-green.yaml down
    CURRENT_ENV="blue"
elif [ "$BLUE_RUNNING" = true ]; then
    CURRENT_ENV="blue"
elif [ "$GREEN_RUNNING" = true ]; then
    CURRENT_ENV="green"
else
    echo "📋 실행 중인 환경이 없습니다. blue로 시작합니다..."
    CURRENT_ENV=""
fi

# 새로운 환경 결정
if [ "$CURRENT_ENV" = "blue" ]; then
    NEW_ENV="green"
    NEW_PORT="8082"
    OLD_ENV="blue"
    OLD_PORT="8081"
else
    NEW_ENV="blue"
    NEW_PORT="8081"
    OLD_ENV="green"
    OLD_PORT="8082"
fi

echo "📋 현재: ${CURRENT_ENV:-없음} → 배포 대상: $NEW_ENV (포트 $NEW_PORT)"

# 새로운 환경 시작
echo "🎯 $NEW_ENV 환경 시작 중..."
docker-compose -f docker/docker-compose-${NEW_ENV}.yaml up -d

# Health check 대기
echo "🏥 헬스 체크 시작..."
echo "🔍 URL: http://localhost:${NEW_PORT}/actuator/health"
echo "⏳ 컨테이너 초기화를 위해 5초 대기..."

sleep 5

MAX_ATTEMPTS=30
ATTEMPT=0

while [ $ATTEMPT -lt $MAX_ATTEMPTS ]; do
    ATTEMPT=$((ATTEMPT + 1))
    
    # Container status check
    CONTAINER_STATUS=$(docker inspect --format='{{.State.Status}}' kkulddip-app-${NEW_ENV} 2>/dev/null || echo "unknown")
    
    if [ "$CONTAINER_STATUS" != "running" ]; then
        echo "❌ 컨테이너가 실행되지 않음 (시도 $ATTEMPT/$MAX_ATTEMPTS) - 상태: $CONTAINER_STATUS"
        sleep 3
        continue
    fi
    
    # Health check - curl 명령을 더 안전하게 처리
    echo "🔍 헬스 체크 요청 중... (시도 $ATTEMPT/$MAX_ATTEMPTS)"
    
    # curl이 실패할 경우를 대비해 기본값 설정
    HEALTH_RESPONSE=""
    HTTP_CODE=""
    
    # curl 명령 실행 (타임아웃과 재시도 옵션 추가)
    if HEALTH_RESPONSE=$(curl -s -w "%{http_code}" --connect-timeout 5 --max-time 10 http://localhost:${NEW_PORT}/actuator/health 2>/dev/null); then
        HTTP_CODE=$(echo "$HEALTH_RESPONSE" | tail -c 4 | tr -d '\n')
        RESPONSE_BODY=$(echo "$HEALTH_RESPONSE" | sed 's/...$//')
        echo "📡 응답 코드: ${HTTP_CODE}, 응답 길이: ${#RESPONSE_BODY}자"
    else
        echo "⚠️ curl 요청 실패 - 서비스가 아직 시작되지 않았을 수 있습니다"
        HTTP_CODE=""
        RESPONSE_BODY=""
    fi
    # 헬스 체크 결과 검증
    if [ "$HTTP_CODE" = "200" ] && [ ! -z "$RESPONSE_BODY" ] && echo "$RESPONSE_BODY" | grep -q '"status":"UP"'; then
        echo "✅ $NEW_ENV 환경이 정상 상태입니다! (시도 $ATTEMPT)"
        break
    elif [ ! -z "$HTTP_CODE" ] && [ "$HTTP_CODE" != "200" ]; then
        echo "⚠️ HTTP 오류: $HTTP_CODE (시도 $ATTEMPT/$MAX_ATTEMPTS)"
    elif [ ! -z "$RESPONSE_BODY" ] && echo "$RESPONSE_BODY" | grep -q '"status":"DOWN"'; then
        echo "⚠️ 애플리케이션 상태: DOWN (시도 $ATTEMPT/$MAX_ATTEMPTS)"
    else
        echo "⚠️ 헬스 체크 대기 중... (시도 $ATTEMPT/$MAX_ATTEMPTS)"
    fi
    
    if [ $((ATTEMPT % 5)) -eq 0 ]; then
        echo "📋 진행 상황: $ATTEMPT/$MAX_ATTEMPTS 시도 완료"
        # 5번째마다 컨테이너 로그 확인
        echo "📄 최근 컨테이너 로그:"
        docker logs kkulddip-app-${NEW_ENV} --tail 5 --since 30s 2>/dev/null || echo "   로그를 가져올 수 없습니다"
    fi
    
    sleep 3
done

if [ $ATTEMPT -eq $MAX_ATTEMPTS ]; then
    echo "❌ 헬스 체크 실패! $MAX_ATTEMPTS번 시도 후 실패"
    echo "📋 컨테이너 로그:"
    docker logs kkulddip-app-${NEW_ENV} --tail 20 2>&1 || echo "로그를 가져올 수 없습니다"
    echo "🛑 실패한 환경을 중지합니다..."
    docker-compose -f docker/docker-compose-${NEW_ENV}.yaml down
    exit 1
fi

# nginx 설정 업데이트
echo "🔧 $NEW_ENV 환경을 위한 nginx 설정 업데이트 중..."

if sudo cp $(pwd)/nginx/nginx-${NEW_ENV}.conf ${NGINX_SITES_AVAILABLE}/${NGINX_SITE_NAME} 2>/dev/null; then
    echo "✅ nginx 설정 파일 복사 완료"
else
    echo "❌ nginx 설정 파일 복사 실패 - sudo 권한을 확인하세요"
    exit 1
fi

sudo rm -f ${NGINX_SITES_ENABLED}/${NGINX_SITE_NAME} 2>/dev/null || true
if sudo ln -s ${NGINX_SITES_AVAILABLE}/${NGINX_SITE_NAME} ${NGINX_SITES_ENABLED}/${NGINX_SITE_NAME} 2>/dev/null; then
    echo "✅ nginx 심볼릭 링크 생성 완료"
else
    echo "❌ nginx 심볼릭 링크 생성 실패"
    exit 1
fi

# nginx 설정 테스트
if nginx -t 2>/dev/null || sudo nginx -t 2>/dev/null; then
    echo "✅ nginx 설정 테스트 통과"
else
    echo "❌ nginx 설정 테스트 실패"
    if [ "$CURRENT_ENV" != "" ]; then
        echo "🔄 이전 설정으로 롤백 중..."
        sudo cp $(pwd)/nginx/nginx-${OLD_ENV}.conf ${NGINX_SITES_AVAILABLE}/${NGINX_SITE_NAME} 2>/dev/null || true
        sudo ln -sf ${NGINX_SITES_AVAILABLE}/${NGINX_SITE_NAME} ${NGINX_SITES_ENABLED}/${NGINX_SITE_NAME} 2>/dev/null || true
    fi
    exit 1
fi

# nginx 설정 리로드
if sudo systemctl reload nginx 2>/dev/null; then
    echo "✅ nginx 재로드 완료"
else
    echo "❌ nginx 재로드 실패"
    exit 1
fi

# 기존 환경 종료
if [ "$CURRENT_ENV" != "" ]; then
    echo "🛑 기존 $OLD_ENV 환경 중지 중..."
    sleep 5
    docker-compose -f docker/docker-compose-${OLD_ENV}.yaml down
fi

echo ""
echo "🎉 배포가 성공적으로 완료되었습니다!"
echo "   이전: ${CURRENT_ENV:-없음} → 신규: $NEW_ENV (포트 $NEW_PORT)"
echo "   완료 시간: $(date '+%Y-%m-%d %H:%M:%S')"

# Final verification
echo ""
echo "📊 최종 상태:"
docker ps --filter "name=kkulddip-app" --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"

FINAL_HEALTH=$(curl -s http://localhost:${NEW_PORT}/actuator/health 2>/dev/null || echo '{"status":"ERROR"}')
if echo "$FINAL_HEALTH" | grep -q '"status":"UP"'; then
    echo "✅ 애플리케이션 상태: 정상"
else
    echo "⚠️ 애플리케이션 상태: 알 수 없음"
fi