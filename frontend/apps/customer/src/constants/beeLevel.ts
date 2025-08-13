/**
 * 벌 레벨 타입 정의
 */
export type BeeLevelType =
  | 'SPROUT_BEE'
  | 'WORKER_BEE'
  | 'HONEY_BEE'
  | 'QUEEN_BEE';

/**
 * 벌 레벨 상수
 */
export const BEE_LEVEL = {
  SPROUT_BEE: 'SPROUT_BEE',
  WORKER_BEE: 'WORKER_BEE',
  HONEY_BEE: 'HONEY_BEE',
  QUEEN_BEE: 'QUEEN_BEE',
} as const satisfies Record<string, BeeLevelType>;

/**
 * 벌 레벨별 한국어 이름
 */
export const BEE_LEVEL_NAMES: Record<BeeLevelType, string> = {
  SPROUT_BEE: '새싹벌',
  WORKER_BEE: '일벌',
  HONEY_BEE: '꿀벌',
  QUEEN_BEE: '여왕벌',
};

/**
 * 레벨 코드를 한국어 이름으로 변환하는 함수
 */
export const getBeeLevel = (levelCode: string): string => {
  return BEE_LEVEL_NAMES[levelCode as BeeLevelType] || levelCode;
};
