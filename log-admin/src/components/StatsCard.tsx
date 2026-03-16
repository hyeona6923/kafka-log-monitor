import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  Tooltip,
  ResponsiveContainer,
  CartesianGrid,
} from "recharts";

interface StatsCardProps {
  title: string;
  data: Record<string, number>;
}

function StatsCard({ title, data }: StatsCardProps) {

  const chartData = Object.entries(data).map(([key, value]) => ({
    name: key,
    value: value,
  }));

  return (
    <div className="stats-card">
      <h3 className="stats-title">{title}</h3>

      <ResponsiveContainer width="100%" height={250}>
        <BarChart data={chartData}>

          <CartesianGrid strokeDasharray="3 3" />

          <XAxis dataKey="name" />
          <YAxis allowDecimals={false} />

          <Tooltip />

          <Bar
            dataKey="value"
            fill="#3b82f6"
            radius={[6, 6, 0, 0]}
          />

        </BarChart>
      </ResponsiveContainer>

    </div>
  );
}

export default StatsCard;