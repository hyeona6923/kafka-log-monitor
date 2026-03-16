package io.github.hyuna.logmonitor.logging.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.hyuna.logmonitor.logging.entity.LogEventDlq;
import io.github.hyuna.logmonitor.persistence.logging.repository.LogEventDlqRepository;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/dlq")
@RequiredArgsConstructor
public class DlqController {

    private final LogEventDlqRepository repository;

    @GetMapping
    public Page<LogEventDlq> getDlqLogs(Pageable pageable) {

        return repository.findAllByOrderByCreatedAtDesc(pageable);
    }

}