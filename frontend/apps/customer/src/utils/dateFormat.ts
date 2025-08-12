/**
 * 날짜 포맷팅 유틸리티
 */

/**
 * ISO 날짜 문자열을 한국 형식으로 변환
 * @param dateString - ISO 형식의 날짜 문자열
 * @returns 'YYYY.MM.DD' 형식의 문자열
 */
export const formatDate = (dateString: string): string => {
  const date = new Date(dateString);
  return date.toLocaleDateString('ko-KR', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
  });
};

/**
 * ISO 날짜 문자열을 날짜+시간 형식으로 변환
 * @param dateString - ISO 형식의 날짜 문자열
 * @returns 'YYYY.MM.DD HH:MM' 형식의 문자열
 */
export const formatDateTime = (dateString: string): string => {
  const date = new Date(dateString);
  const dateStr = date.toLocaleDateString('ko-KR', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
  });
  const timeStr = date.toLocaleTimeString('ko-KR', {
    hour: '2-digit',
    minute: '2-digit',
    hour12: false,
  });
  return `${dateStr} ${timeStr}`;
};
