package io.github.hyuna.logmonitor.persistence.logging.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import io.github.hyuna.logmonitor.logging.entity.LogEventDlq;

public interface LogEventDlqRepository extends JpaRepository<LogEventDlq, Long> {

        Page<LogEventDlq> findAllByOrderByCreatedAtDesc(Pageable pageable);

}

