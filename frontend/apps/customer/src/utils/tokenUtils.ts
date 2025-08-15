/**
 * OpenVidu 토큰 유틸리티 함수들
 */

/**
 * 백엔드에서 받은 토큰이 URL 형식인지 확인
 */
export const isUrlFormatToken = (token: string): boolean => {
  return token.startsWith('wss://') || token.startsWith('ws://');
};

/**
 * URL 형식 토큰에서 실제 JWT 토큰 추출
 */
export const extractTokenFromUrl = (urlToken: string): string => {
  try {
    const url = new URL(urlToken);

    // OpenVidu 토큰 ID 추출 (token 파라미터에서)
    const tokenParam = url.searchParams.get('token');
    if (!tokenParam) {
      throw new Error('URL에서 OpenVidu 토큰을 찾을 수 없습니다');
    }

    return tokenParam;
  } catch (error) {
    console.error('토큰 URL 파싱 실패:', error);
    throw new Error('유효하지 않은 토큰 URL 형식입니다');
  }
};

/**
 * OpenVidu 토큰 정규화 (WebSocket URL 전체를 그대로 사용)
 */
export const normalizeOpenViduToken = (token: string): string => {
  if (isUrlFormatToken(token)) {
    // OpenVidu는 전체 WebSocket URL을 토큰으로 사용
    console.log('[tokenUtils] OpenVidu WebSocket URL 토큰 사용:', {
      tokenUrl: token,
      tokenType: 'OpenVidu WebSocket URL Token',
    });
    return token; // 전체 URL 그대로 반환
  }

  return token;
};

/**
 * 토큰에서 세션 ID 추출 (URL 형식인 경우)
 */
export const extractSessionIdFromToken = (token: string): string | null => {
  if (!isUrlFormatToken(token)) {
    return null;
  }

  try {
    const url = new URL(token);
    return url.searchParams.get('sessionId');
  } catch (error) {
    console.error('세션 ID 추출 실패:', error);
    return null;
  }
};
