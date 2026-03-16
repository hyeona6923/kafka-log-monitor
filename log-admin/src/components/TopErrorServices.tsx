import type { ErrorStat } from "../types/log"

interface Props {
  data: ErrorStat[]
}

function TopErrorServices({ data }: Props) {

  const serviceMap: Record<string, number> = {}

  data.forEach((item) => {
    serviceMap[item.serviceName] =
      (serviceMap[item.serviceName] || 0) + item.count
  })

  const sorted = Object.entries(serviceMap)
    .map(([serviceName, count]) => ({ serviceName, count }))
    .sort((a, b) => b.count - a.count)
    .slice(0, 5)

  return (
    <div className="stats-card">
      <h3 className="stats-title">🔥 오류 TOP 서비스</h3>

      <ul className="top-error-list">
        {sorted.map((item, idx) => (
          <li key={item.serviceName}>
            <span className="rank">{idx + 1}</span>
            <span className="service">{item.serviceName}</span>
            <span className="count">{item.count}</span>
          </li>
        ))}
      </ul>
    </div>
  )
}

export default TopErrorServices