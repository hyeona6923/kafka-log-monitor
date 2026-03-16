import { useEffect, useState } from "react";

export interface StreamLog {
  time: string
  service: string
  level: string
  message: string
}

export function useLogStream() {
  const [logs, setLogs] = useState<StreamLog[]>([]);

  useEffect(() => {
    const eventSource = new EventSource("http://localhost:8081/api/logs/stream");

    eventSource.addEventListener("log", (event) => {
      const data = JSON.parse(event.data) as StreamLog;

      setLogs((prev) => [data, ...prev].slice(0, 20));
    });

    eventSource.onerror = () => {
      console.error("SSE 연결 오류");
    };

    return () => {
      eventSource.close();
    };
  }, []);

  return logs;
}