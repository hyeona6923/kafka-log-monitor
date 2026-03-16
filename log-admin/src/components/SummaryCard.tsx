interface SummaryCardProps {
  title: string;
  value: number;
  type?: "default" | "error" | "warn" | "info";
}

function SummaryCard({
  title,
  value,
  type = "default",
}: SummaryCardProps) {
  return (
    <div className={`summary-card ${type}`}>
      <div className="summary-title">{title}</div>
      <div className="summary-value">{value}</div>
    </div>
  );
}

export default SummaryCard;