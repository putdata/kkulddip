/**
 * 날짜와 시간을 한국어 형식으로 포맷팅
 * @param dateString - ISO 8601 형식의 날짜 문자열
 * @returns 포맷된 날짜와 시간 문자열 (예: "12월 25일 오후 3:30")
 */
export const formatDateTime = (dateString: string): string => {
  const date = new Date(dateString);
  return date.toLocaleString('ko-KR', {
    month: 'numeric',
    day: 'numeric',
    hour: 'numeric',
    minute: 'numeric',
    hour12: true,
  });
};

/**
 * 시간만 HH:mm 형식으로 포맷팅
 * @param dateString - ISO 8601 형식의 날짜 문자열
 * @returns 포맷된 시간 문자열 (예: "15:30")
 */
export const formatTime = (dateString: string): string => {
  const date = new Date(dateString);
  return date.toLocaleTimeString('ko-KR', {
    hour: '2-digit',
    minute: '2-digit',
    hour12: false,
  });
};

/**
 * 날짜만 MM/DD 형식으로 포맷팅
 * @param dateString - ISO 8601 형식의 날짜 문자열
 * @returns 포맷된 날짜 문자열 (예: "12/25")
 */
export const formatDate = (dateString: string): string => {
  const date = new Date(dateString);
  return `${date.getMonth() + 1}/${date.getDate()}`;
};

/**
 * 한국시간 기준으로 현재 시간에 분을 추가한 Date 객체 반환
 * @param minutes - 추가할 분
 * @returns 한국시간 기준 Date 객체
 */
export const addMinutes = (minutes: number): Date => {
  const now = new Date();
  now.setMinutes(now.getMinutes() + minutes);
  return now;
};

/**
 * 픽업 시간 옵션 생성 (현재 시간 이후 + 오후 8,9,10시)
 * @returns 시간 옵션 배열
 */
export const getTimeOptions = (): Array<{ value: string; label: string }> => {
  const now = new Date();
  const options: Array<{ value: string; label: string }> = [];

  // 현재 시간 이후 30분 단위로 추가 (최대 3개까지)
  let currentTime = addMinutes(30);
  let count = 0;

  while (count < 3) {
    // 30분 단위로 정렬
    const minutes = currentTime.getMinutes() < 30 ? 30 : 0;
    let hour =
      minutes === 0 ? currentTime.getHours() + 1 : currentTime.getHours();
    let targetDate = new Date(currentTime);

    // 시간이 24시를 넘으면 다음날로 설정
    if (hour >= 24) {
      hour = hour - 24;
      targetDate.setDate(targetDate.getDate() + 1);
    }

    const value = createKoreanTimeWithDate(targetDate, hour, minutes);
    const labelDate = new Date(value);
    const label = labelDate.toLocaleTimeString('ko-KR', {
      hour: 'numeric',
      minute: '2-digit',
      hour12: true,
    });

    options.push({ value, label });
    count++;

    // 30분 후로 이동
    currentTime = new Date(currentTime.getTime() + 30 * 60 * 1000);
  }

  // 기본 옵션: 오후 8시, 9시, 10시 (현재 시간과 중복되지 않는 경우만)
  const defaultHours = [20, 21, 22]; // 8PM, 9PM, 10PM

  defaultHours.forEach(hour => {
    // 이미 추가된 시간인지 확인
    const existingOption = options.find(option => {
      const date = new Date(option.value);
      return date.getHours() === hour && date.getMinutes() === 0;
    });

    if (!existingOption && hour > now.getHours()) {
      const value = createKoreanTime(hour, 0);
      const labelDate = new Date(value);
      const label = labelDate.toLocaleTimeString('ko-KR', {
        hour: 'numeric',
        minute: '2-digit',
        hour12: true,
      });

      options.push({ value, label });
    }
  });

  return options;
};

/**
 * 시간을 "오후 3시 30분" 형식으로 포맷팅
 * @param dateString - ISO 8601 형식의 날짜 문자열
 * @returns 포맷된 시간 문자열
 */
export const formatTimeKorean = (dateString: string): string => {
  const date = new Date(dateString);
  return date.toLocaleTimeString('ko-KR', {
    hour: 'numeric',
    minute: 'numeric',
    hour12: true,
  });
};

/**
 * 한국시간 기준으로 현재 시간 생성
 * @returns 한국시간 기준 Date 객체
 */
export const getKoreanTime = (): Date => {
  // 한국시간대로 설정된 Date 객체 생성
  const now = new Date();
  const koreaTime = new Date(
    now.toLocaleString('en-US', { timeZone: 'Asia/Seoul' }),
  );
  return koreaTime;
};

/**
 * 한국시간 기준으로 시간 생성
 * @param hour - 시간 (0-23)
 * @param minute - 분 (0-59)
 * @returns 한국시간 기준 ISO 문자열
 */
export const createKoreanTime = (hour: number, minute: number): string => {
  const today = new Date();
  const year = today.getFullYear();
  const month = today.getMonth();
  const date = today.getDate();

  // 한국시간 그대로 ISO 형식 문자열 생성 (UTC 변환 없이)
  const isoString =
    year +
    '-' +
    String(month + 1).padStart(2, '0') +
    '-' +
    String(date).padStart(2, '0') +
    'T' +
    String(hour).padStart(2, '0') +
    ':' +
    String(minute).padStart(2, '0') +
    ':00.000';

  return isoString;
};

/**
 * 특정 날짜와 시간으로 한국시간 기준 시간 생성
 * @param targetDate - 대상 날짜
 * @param hour - 시간 (0-23)
 * @param minute - 분 (0-59)
 * @returns 한국시간 기준 ISO 문자열
 */
export const createKoreanTimeWithDate = (
  targetDate: Date,
  hour: number,
  minute: number,
): string => {
  const year = targetDate.getFullYear();
  const month = targetDate.getMonth();
  const date = targetDate.getDate();

  // 한국시간 그대로 ISO 형식 문자열 생성 (UTC 변환 없이)
  const isoString =
    year +
    '-' +
    String(month + 1).padStart(2, '0') +
    '-' +
    String(date).padStart(2, '0') +
    'T' +
    String(hour).padStart(2, '0') +
    ':' +
    String(minute).padStart(2, '0') +
    ':00.000';

  return isoString;
};
