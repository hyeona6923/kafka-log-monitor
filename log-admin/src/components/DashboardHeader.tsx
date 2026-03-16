interface DashboardHeaderProps {
  onRefresh: () => void;
}

function DashboardHeader({ onRefresh }: DashboardHeaderProps) {
  const now = new Date().toLocaleString();

  return (
    <div className="dashboard-header">
      <div>
        <h1 className="dashboard-title">Log Monitoring Dashboard</h1>
        <p className="dashboard-desc">Kafka 기반 로그 모니터링 관리자 화면</p>
      </div>

      <div className="dashboard-actions">
        <span className="last-updated">마지막 확인: {now}</span>
        <button className="refresh-button" onClick={onRefresh}>
          Refresh
        </button>
      </div>
    </div>
  );
}

export default DashboardHeader;