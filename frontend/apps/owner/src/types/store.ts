// TODO: 실제 api 타입으로 변경
export interface Store {
  id: string;
  name: string;
  description: string;
  address: string;
  isActive: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface StoreListResponse {
  stores: Store[];
  totalCount: number;
}
