import { useEffect, useState } from "react";
import { fetchLevelStats, fetchLogs, fetchServiceStats, fetchErrorStats } from "../api/logApi";
import type { ErrorStat, LogResponse, PageInfo } from "../types/log";

/**
 * 로그 페이지 전용 커스텀 훅
 *
 * 역할:
 * - 로그 목록 조회
 * - 레벨/서비스 통계 조회
 * - 필터 상태 관리
 * - 페이지 이동 처리
 * - 수동 새로고침 처리
 */
export const useLogPage = () => {
    /**
     * 로그 목록 상태
     */
    const [logs, setLogs] = useState<LogResponse[]>([]);

    /**
     * 페이지 정보 상태
     */
    const [pageInfo, setPageInfo] = useState<PageInfo>({
    number: 0,
    size: 10,
    totalPages: 0,
    totalElements: 0,
    });

    /**
     * 검색 조건 상태
     *
     * - page, size 는 페이징
     * - level, serviceName, keyword 는 필터
     * - startDate, endDate 는 날짜 검색
     */
    const [filters, setFilters] = useState({
    page: 0,
    size: 10,
    level: "",
    serviceName: "",
    keyword: "",
    startDate: "",
    endDate: "",
    });

    /**
     * 레벨별 통계 상태
     */
    const [levelStats, setLevelStats] = useState<Record<string, number>>({});

    /**
     * 서비스별 통계 상태
     */
    const [serviceStats, setServiceStats] = useState<Record<string, number>>({});

    /**
     * 로그 목록 로딩 상태
     */
    const [loading, setLoading] = useState(false);

    /**
     * 에러 메시지 상태
     */
    const [errorMessage, setErrorMessage] = useState("");


    /**
     * 에러 유형별 통계 상태
     */
    const [errorStats, setErrorStats] = useState<ErrorStat[]>([]);

    /**
     * 로그 목록 조회
     */
    const loadLogs = async () => {
    try {
        setLoading(true);
        setErrorMessage("");

        const data = await fetchLogs(filters);

        setLogs(data.content || []);
        setPageInfo({
        number: data.number ?? 0,
        size: data.size ?? 10,
        totalPages: data.totalPages ?? 0,
        totalElements: data.totalElements ?? 0,
        });
    } catch (error: any) {
        console.error("로그 조회 실패", error);

        if (error.response) {
        console.error("응답 데이터:", error.response.data);
        console.error("응답 상태:", error.response.status);
        setErrorMessage(`로그 조회 실패 (${error.response.status})`);
        } else if (error.request) {
        console.error("요청은 갔지만 응답이 없음:", error.request);
        setErrorMessage("백엔드 응답이 없습니다.");
        } else {
        console.error("요청 생성 중 오류:", error.message);
        setErrorMessage(`오류: ${error.message}`);
        }
    } finally {
        setLoading(false);
    }
    };

    /**
     * 통계 조회
     *
     * 레벨별 통계 / 서비스별 통계를 동시에 조회한다.
     */
    const loadStats = async () => {
    try {
        const [levelData, serviceData, errorData] = await Promise.all([
            fetchLevelStats(),
            fetchServiceStats(),
            fetchErrorStats(),
            ])

            setLevelStats(levelData || {})
            setServiceStats(serviceData || {})
            setErrorStats(errorData || [])
    } catch (error) {
        console.error("통계 조회 실패", error);
    }
    };

    /**
     * 필터 변경 처리
     *
     * 검색 조건이 바뀌면 첫 페이지부터 다시 조회한다.
     */
    const handleFilterChange = (name: string, value: string) => {
    setFilters((prev) => ({
        ...prev,
        [name]: value,
        page: 0,
    }));
    };

    /**
     * 페이지 이동 처리
     */
    const movePage = (page: number) => {
    setFilters((prev) => ({
        ...prev,
        page,
    }));
    };

    /**
     * 수동 새로고침 처리
     *
     * 현재 필터 조건 그대로 다시 조회
     */
    const reloadLogs = async () => {
    await loadLogs();
    await loadStats();
    };

    /**
     * 최초 진입 시 통계 조회
     */
    useEffect(() => {
    loadStats();
    }, []);

    /**
     * 필터/페이지 변경 시 로그 목록 조회
     */
    useEffect(() => {
    loadLogs();
    }, [filters]);

    return {
    logs,
    pageInfo,
    filters,
    levelStats,
    serviceStats,
    loading,
    errorMessage,
    handleFilterChange,
    movePage,
    reloadLogs,
    errorStats
    };

    
};