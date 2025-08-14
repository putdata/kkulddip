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
 * 30분 단위 시간 옵션 생성
 * @param startHour - 시작 시간 (기본값: 현재 시간)
 * @param endHour - 종료 시간 (기본값: 현재 시간 + 3시간)
 * @returns 시간 옵션 배열
 */
export const getTimeOptions = (
  startHour?: number,
  endHour?: number,
): Array<{ value: string; label: string }> => {
  const now = new Date();
  const start = startHour ?? now.getHours();
  const end = endHour ?? now.getHours() + 3;
  const options: Array<{ value: string; label: string }> = [];

  // 현재 시간부터 30분 단위로 옵션 생성
  const currentMinutes = now.getMinutes();
  const startMinutes = currentMinutes < 30 ? 30 : 0;

  for (let hour = start; hour <= Math.min(end, 23); hour++) {
    const minuteStart = hour === start ? startMinutes : 0;
    for (let minute = minuteStart; minute < 60; minute += 30) {
      // 현재 시간보다 이전 시간은 제외
      if (hour === start && minute < currentMinutes) {
        continue;
      }

      const value = createKoreanTime(hour, minute);
      // value와 동일한 시간으로 label 생성
      const labelDate = new Date(value);
      const label = labelDate.toLocaleTimeString('ko-KR', {
        hour: 'numeric',
        minute: '2-digit',
        hour12: true,
      });

      options.push({ value, label });
    }
  }

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
