# Kafka Log Monitoring System

Kafka 기반 **대용량 로그 수집 및 실시간 모니터링 시스템**입니다.

서비스에서 발생하는 로그를 Kafka로 수집하고\
Consumer에서 처리하여 DB에 저장하고\
관리자 대시보드에서 **실시간 로그 모니터링 및 오류 분석**을 제공합니다.

------------------------------------------------------------------------

# Architecture

Service\
↓\
Kafka Producer\
↓\
Kafka Topic\
↓\
Kafka Consumer\
↓\
PostgreSQL\
↓\
Admin Dashboard (React)

------------------------------------------------------------------------

# Tech Stack

## Backend

-   Java 17
-   Spring Boot
-   Spring Kafka
-   Spring Data JPA
-   PostgreSQL

## Frontend

-   React
-   TypeScript
-   Vite
-   Recharts

## Infra

-   Apache Kafka
-   Docker

------------------------------------------------------------------------

# 주요 기능

## 로그 수집 API

서비스 로그를 Kafka Topic으로 전송합니다.

POST /api/logs

------------------------------------------------------------------------

## Kafka 기반 로그 처리

Kafka Consumer가 로그 메시지를 수신하여 DB에 저장합니다.

특징

-   Batch Insert 처리
-   Consumer Group 기반 병렬 처리

------------------------------------------------------------------------

## 로그 조회

로그 데이터를 검색 및 필터링할 수 있습니다.

GET /api/logs/page

지원 기능

-   로그 레벨 필터
-   서비스 필터
-   키워드 검색
-   페이징 조회

------------------------------------------------------------------------

## 오류 분석

서비스별 오류 발생 통계를 제공합니다.

GET /api/logs/stats/errors

------------------------------------------------------------------------

## 실시간 로그 스트림

SSE(Server Sent Events)를 활용하여 실시간 로그 모니터링 기능을
제공합니다.

GET /api/logs/stream

------------------------------------------------------------------------

# 대용량 처리 설계

## Kafka Partition 분산 전략

Kafka 메시지 key를

serviceName + bucket

전략으로 설계했습니다.

예시

payment-0\
payment-1\
payment-2

특정 서비스 TPS 집중 시 Partition Hot Spot을 완화하도록 설계했습니다.

------------------------------------------------------------------------

## Batch Insert

Kafka Consumer에서 로그를 Batch 단위로 DB에 저장하여 DB I/O를 줄이고
성능을 개선했습니다.

------------------------------------------------------------------------

## DLQ (Dead Letter Queue)

로그 처리 실패 시 DLQ Topic으로 메시지를 전송합니다.

log-events-dlq

데이터 손실 없이 장애 로그를 별도로 관리할 수 있습니다.

------------------------------------------------------------------------

# 프로젝트 구조

kafka-log-monitor\
├ log-monitor (Spring Boot Backend)\
└ log-admin (React Admin Dashboard)

------------------------------------------------------------------------

# 실행 방법

## Kafka 실행

docker compose up -d

## Backend 실행

cd log-monitor\
./gradlew bootRun

## Frontend 실행

cd log-admin\
npm install\
npm run dev

------------------------------------------------------------------------

# 향후 확장

-   Kafka Consumer Lag 모니터링
-   AI 기반 로그 분석
-   Slack / Email 알림 시스템
-   로그 패턴 기반 장애 탐지
