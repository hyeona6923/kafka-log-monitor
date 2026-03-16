import type { LogResponse } from "../types/log";
import LevelBadge from "./LevelBadge";

interface LogTableProps {
  logs: LogResponse[];
  loading: boolean;
}

function LogTable({ logs, loading }: LogTableProps) {
  return (
    <table className="log-table">
      <thead>
        <tr>
          <th>ID</th>
          <th>Level</th>
          <th>Service</th>
          <th>Message</th>
          <th>Created At</th>
        </tr>
      </thead>

      <tbody>
        {loading ? (
          <tr>
            <td colSpan={5} className="table-message">
              로딩 중...
            </td>
          </tr>
        ) : logs.length === 0 ? (
          <tr>
            <td colSpan={5} className="table-message">
              조회 결과가 없습니다.
            </td>
          </tr>
        ) : (
          logs.map((log) => (
            <tr
              key={log.id}
              style={{
                backgroundColor: log.level === "ERROR" ? "#fdecea" : "transparent"
              }}
            >
              <td>{log.id}</td>

              <td>
                <LevelBadge level={log.level} />
              </td>

              <td>{log.serviceName}</td>
              <td>{log.message}</td>
              <td>{log.createdAt}</td>
            </tr>
          ))
        )}
      </tbody>
    </table>
  );
}

export default LogTable;