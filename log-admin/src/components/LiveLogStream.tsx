import { useLogStream } from "../hooks/useLogStream"

function LiveLogStream() {

  const logs = useLogStream()

  return (
    <div className="stats-card">

      <h3 className="stats-title">실시간 로그 스트림</h3>

      <div className="live-log">

        {logs.length === 0 && (
          <div className="live-log-empty">
            실시간 로그 대기 중...
          </div>
        )}

        {logs.map((log, idx) => (

          <div key={idx} className={`live-log-row ${log.level}`}>

            <span className="live-log-time">{log.time}</span>

            <span className="live-log-service">
              {log.service}
            </span>

            <span className={`live-log-level ${log.level}`}>
              {log.level}
            </span>

            <span className="live-log-message">
              {log.message}
            </span>

          </div>

        ))}

      </div>

    </div>
  )
}

export default LiveLogStream