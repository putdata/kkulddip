FROM python:3.11-slim

# 시스템 패키지 업데이트 및 필요한 의존성 설치
RUN apt-get update && apt-get install -y \
    build-essential \
    gcc \
    g++ \
    pkg-config \
    libssl-dev \
    libffi-dev \
    python3-dev \
    libpq-dev \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# 의존성 설치 (단계별로)
COPY requirements.txt .

# 기본 패키지 먼저 설치
RUN pip install --upgrade pip setuptools wheel

# Prophet 관련 의존성 먼저 설치
RUN pip install --no-cache-dir \
    numpy==1.24.3 \
    pandas==2.1.3 \
    pystan==3.7.0 \
    cmdstanpy==1.2.0

# 나머지 패키지 설치
RUN pip install --no-cache-dir -r requirements.txt

# 애플리케이션 코드 복사
COPY . .

# 포트 노출
EXPOSE 8001

# 애플리케이션 실행
CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "8001"]