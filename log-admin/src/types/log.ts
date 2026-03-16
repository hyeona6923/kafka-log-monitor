/**
 * 로그 1건 응답 타입
 */
export interface LogResponse {
  id: number;
  level: string;
  serviceName: string;
  message: string;
  createdAt: string;
}

/**
 * Spring Page 응답 타입
 */
export interface PageResponse<T> {
  content: T[];
  number: number;
  size: number;
  totalPages: number;
  totalElements: number;
}

/**
 * 로그 조회 요청 파라미터 타입
 */
export interface LogSearchParams {
  page: number;
  size: number;
  level?: string;
  serviceName?: string;
  keyword?: string;
  startDate?: string;
  endDate?: string;
}

/**
 * 페이지 정보 타입
 */
export interface PageInfo {
  number: number;
  size: number;
  totalPages: number;
  totalElements: number;
}

export interface ErrorStat {
  serviceName: string;
  errorType: string;
  count: number;
}