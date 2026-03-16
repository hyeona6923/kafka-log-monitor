package io.github.hyuna.logmonitor.persistence.logging.repository;

import io.github.hyuna.logmonitor.logging.entity.LogEvent;
import io.github.hyuna.logmonitor.logging.entity.LogEventDlq;
import jakarta.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * LogEventRepository
 *
 * 역할:
 * - 로그 엔티티 DB 접근
 * - 기본 조회 / 조건 조회 / 페이징 조회 / 통계 조회 담당
 */
public interface LogEventRepository extends JpaRepository<LogEvent, Long> {

    /**
     * 전체 로그 조회
     * createdAt 기준 내림차순 정렬
     */
    List<LogEvent> findAllByOrderByCreatedAtDesc();

    /**
     * level 기준 조회
     */
    List<LogEvent> findByLevelOrderByCreatedAtDesc(String level);

    /**
     * serviceName 기준 조회
     */
    List<LogEvent> findByServiceNameOrderByCreatedAtDesc(String serviceName);

    /**
     * level + serviceName 기준 조회
     */
    List<LogEvent> findByLevelAndServiceNameOrderByCreatedAtDesc(String level, String serviceName);

    /**
     * 전체 로그 페이징 조회
     */
    Page<LogEvent> findAllByOrderByCreatedAtDesc(Pageable pageable);

    /**
     * level 기준 페이징 조회
     */
    Page<LogEvent> findByLevelOrderByCreatedAtDesc(String level, Pageable pageable);

    /**
     * serviceName 기준 페이징 조회
     */
    Page<LogEvent> findByServiceNameOrderByCreatedAtDesc(String serviceName, Pageable pageable);

    /**
     * level + serviceName 기준 페이징 조회
     */
    Page<LogEvent> findByLevelAndServiceNameOrderByCreatedAtDesc(
            String level,
            String serviceName,
            Pageable pageable
    );

    /**
     * 로그 레벨별 개수 집계
     *
     * 결과 예:
     * [
     *   ["INFO", 12],
     *   ["WARN", 3],
     *   ["ERROR", 5]
     * ]
     */
    @Query("""
           SELECT e.level, COUNT(e)
           FROM LogEvent e
           GROUP BY e.level
           """)
    List<Object[]> countLogsByLevel();

    /**
     * 서비스명별 로그 개수 집계
     *
     * 결과 예:
     * [
     *   ["payment", 10],
     *   ["auth", 7]
     * ]
     */
    @Query("""
           SELECT e.serviceName, COUNT(e)
           FROM LogEvent e
           GROUP BY e.serviceName
           """)
    List<Object[]> countLogsByServiceName();

        /**
     * 검색용 전체 조회
     *
     * 조건:
     * - level
     * - serviceName
     * - keyword
     * - startDateTime 이상
     * - endDateTime 이하
     *
     * 주의:
     * - keyword는 null 대신 "" 로 넘긴다.
     * - startDateTime / endDateTime도 null 대신 실제 기본값을 넘긴다.
     *
     * 그래서 쿼리 안에서 날짜 null 체크를 하지 않아도 된다.
     */
    @Query("""
           SELECT e
           FROM LogEvent e
           WHERE (:level IS NULL OR e.level = :level)
             AND (:serviceName IS NULL OR e.serviceName = :serviceName)
             AND e.message LIKE CONCAT('%', :keyword, '%')
             AND e.createdAt >= :startDateTime
             AND e.createdAt <= :endDateTime
           ORDER BY e.createdAt DESC
           """)
    List<LogEvent> searchLogs(
            @Param("level") String level,
            @Param("serviceName") String serviceName,
            @Param("keyword") String keyword,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime
    );

    /**
     * 검색용 페이징 조회
     */
    @Query("""
           SELECT e
           FROM LogEvent e
           WHERE (:level IS NULL OR e.level = :level)
             AND (:serviceName IS NULL OR e.serviceName = :serviceName)
             AND e.message LIKE CONCAT('%', :keyword, '%')
             AND e.createdAt >= :startDateTime
             AND e.createdAt <= :endDateTime
           ORDER BY e.createdAt DESC
           """)
    Page<LogEvent> searchLogs(
            @Param("level") String level,
            @Param("serviceName") String serviceName,
            @Param("keyword") String keyword,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime,
            Pageable pageable
    );

    /**
     * 기준 시각보다 오래된 로그 삭제
     *
     * @param threshold 삭제 기준 시각
     * @return 삭제된 row 수
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM LogEvent e WHERE e.createdAt < :threshold")
    int deleteOldLogs(LocalDateTime threshold);

    @Query("""
       SELECT e.serviceName, e.errorType, COUNT(e)
       FROM LogEvent e
       WHERE e.level = 'ERROR'
       GROUP BY e.serviceName, e.errorType
       ORDER BY COUNT(e) DESC
       """)
    List<Object[]> countErrorsByServiceAndType();
}