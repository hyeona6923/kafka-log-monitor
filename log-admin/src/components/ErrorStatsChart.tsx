import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  Tooltip,
  ResponsiveContainer,
  CartesianGrid,
} from "recharts"
import type { ErrorStat } from "../types/log"

interface ErrorStatsChartProps {
  data: ErrorStat[]
}

function ErrorStatsChart({ data }: ErrorStatsChartProps) {
  const chartData = data.map((item) => ({
    name: `${item.serviceName}-${item.errorType}`,
    count: item.count,
  }))

  return (
    <div className="stats-card">
      <h3 className="stats-title">서비스별 오류 분석</h3>

      <ResponsiveContainer width="100%" height={320}>
        <BarChart data={chartData}>
          <CartesianGrid strokeDasharray="3 3" />
          <XAxis dataKey="name" angle={-20} textAnchor="end" height={80} />
          <YAxis allowDecimals={false} />
          <Tooltip />
          <Bar dataKey="count" fill="#ef4444" radius={[6, 6, 0, 0]} />
        </BarChart>
      </ResponsiveContainer>
    </div>
  )
}

export default ErrorStatsChart