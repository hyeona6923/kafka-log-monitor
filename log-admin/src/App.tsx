import "./App.css";
import DashboardHeader from "./components/DashboardHeader";
import SummaryCard from "./components/SummaryCard";
import StatsCard from "./components/StatsCard";
import LogFilter from "./components/LogFilter";
import LogTable from "./components/LogTable";
import Pagination from "./components/Pagination";
import { useLogPage } from "./hooks/useLogPage";
import ErrorStatsChart from "./components/ErrorStatsChart";
import TopErrorServices from "./components/TopErrorServices";
import LiveLogStream from "./components/LiveLogStream";

function App() {
  const {
    logs,
    pageInfo,
    filters,
    levelStats,
    serviceStats,
    errorStats,
    loading,
    errorMessage,
    handleFilterChange,
    movePage,
    reloadLogs,
  } = useLogPage();

  const totalCount = pageInfo.totalElements;
  const errorCount = levelStats.ERROR || 0;
  const warnCount = levelStats.WARN || 0;
  const serviceCount = Object.keys(serviceStats).length;

  return (
    <div className="dashboard-container">
      <DashboardHeader onRefresh={reloadLogs} />

      <div className="summary-grid">
        <SummaryCard title="전체 로그" value={totalCount} type="default" />
        <SummaryCard title="ERROR" value={errorCount} type="error" />
        <SummaryCard title="WARN" value={warnCount} type="warn" />
        <SummaryCard title="서비스 수" value={serviceCount} type="info" />
      </div>

      <div className="chart-grid">
        <StatsCard title="레벨별 통계" data={levelStats} />
        <StatsCard title="서비스별 통계" data={serviceStats} />
        <TopErrorServices data={errorStats} />
      </div>
      <div className="chart-grid single-chart">
        <ErrorStatsChart data={errorStats} />
      </div>

      <div className="chart-grid single-chart">
        <LiveLogStream />
      </div>

      <div className="panel">
        <h3 className="panel-title">검색 조건</h3>
        <LogFilter
          level={filters.level}
          serviceName={filters.serviceName}
          keyword={filters.keyword}
          startDate={filters.startDate}
          endDate={filters.endDate}
          onChange={handleFilterChange}
        />
      </div>

      <div className="panel">
        <div className="panel-header">
          <h3 className="panel-title">최근 로그</h3>
          <span className="panel-subtitle">총 {pageInfo.totalElements}건</span>
        </div>

        {errorMessage && <div className="error-message">{errorMessage}</div>}

        <LogTable logs={logs} loading={loading} />

        <Pagination
          page={pageInfo.number}
          totalPages={pageInfo.totalPages}
          onPageChange={movePage}
        />
      </div>
    </div>
  );
}

export default App;