import axios from "axios";
import type { LogResponse, LogSearchParams, PageResponse, ErrorStat } from "../types/log";
/**
 * log-monitor 백엔드 API 호출용 axios 인스턴스
 *
 * - baseURL: Spring Boot 서버 주소
 * - timeout: 너무 오래 걸리는 요청 방지
 */
const api = axios.create({
  baseURL: "http://localhost:8081",
  timeout: 10000,
});

/**
 * 요청 파라미터에서 빈 문자열, null, undefined 제거
 *
 * 이유:
 * - level="", keyword="" 같은 값이 그대로 들어가면
 *   백엔드에서 불필요한 파라미터로 처리될 수 있음
 * - 실제 값이 있는 것만 query string으로 보낸다.
 */
const cleanParams = (params: LogSearchParams) => {
  return Object.fromEntries(
    Object.entries(params).filter(
      ([, value]) => value !== "" && value !== undefined && value !== null
    )
  );
};

/**
 * 로그 페이징 조회 API
 *
 * 예:
 * GET /api/logs/page?page=0&size=10
 * GET /api/logs/page?page=0&size=10&level=ERROR
 * GET /api/logs/page?page=0&size=10&serviceName=payment
 */
export const fetchLogs = async (
  params: LogSearchParams
): Promise<PageResponse<LogResponse>> => {
  const cleanedParams = cleanParams(params);

  const response = await api.get<PageResponse<LogResponse>>("/api/logs/page", {
    params: cleanedParams,
  });

  return response.data;
};

/**
 * 로그 레벨별 통계 조회 API
 *
 * 예:
 * GET /api/logs/stats/level
 *
 * 응답 예:
 * {
 *   "INFO": 120,
 *   "WARN": 25,
 *   "ERROR": 8
 * }
 */
export const fetchLevelStats = async (): Promise<Record<string, number>> => {
  const response = await api.get<Record<string, number>>("/api/logs/stats/level");
  return response.data;
};

/**
 * 서비스별 로그 통계 조회 API
 *
 * 예:
 * GET /api/logs/stats/service
 *
 * 응답 예:
 * {
 *   "payment": 40,
 *   "auth": 20,
 *   "order": 15
 * }
 */
export const fetchServiceStats = async (): Promise<Record<string, number>> => {
  const response = await api.get<Record<string, number>>("/api/logs/stats/service");
  return response.data;
};

export default api;

/**
 * 서비스별 오류 통계 조회 API
 */
export const fetchErrorStats = async (): Promise<ErrorStat[]> => {
  const response = await api.get<ErrorStat[]>("/api/logs/stats/errors");
  return response.data;
};