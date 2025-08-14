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
 * 현재 시간에 분을 추가한 Date 객체 반환
 * @param minutes - 추가할 분
 * @returns 새로운 Date 객체
 */
export const addMinutes = (minutes: number): Date => {
  const date = new Date();
  date.setMinutes(date.getMinutes() + minutes);
  return date;
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
      
      const date = new Date();
      date.setHours(hour);
      date.setMinutes(minute);
      date.setSeconds(0);
      date.setMilliseconds(0);
      
      const value = date.toISOString();
      const label = date.toLocaleTimeString('ko-KR', {
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