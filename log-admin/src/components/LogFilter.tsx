interface LogFilterProps {
  level: string;
  serviceName: string;
  keyword: string;
  startDate?: string;
  endDate?: string;
  onChange: (name: string, value: string) => void;
}

function LogFilter({
  level,
  serviceName,
  keyword,
  startDate,
  endDate,
  onChange,
}: LogFilterProps) {
  return (
    <div className="filter-container">

      {/* level select */}
      <select
        className="filter-input"
        value={level}
        onChange={(e) => onChange("level", e.target.value)}
      >
        <option value="">ALL</option>
        <option value="INFO">INFO</option>
        <option value="WARN">WARN</option>
        <option value="ERROR">ERROR</option>
      </select>

      {/* service name */}
      <input
        className="filter-input"
        placeholder="serviceName"
        value={serviceName}
        onChange={(e) => onChange("serviceName", e.target.value)}
      />

      {/* keyword */}
      <input
        className="filter-input"
        placeholder="keyword"
        value={keyword}
        onChange={(e) => onChange("keyword", e.target.value)}
      />

      {/* start date */}
      <input
        type="date"
        className="filter-input"
        value={startDate}
        onChange={(e) => onChange("startDate", e.target.value)}
      />

      {/* end date */}
      <input
        type="date"
        className="filter-input"
        value={endDate}
        onChange={(e) => onChange("endDate", e.target.value)}
      />

    </div>
  );
}

export default LogFilter;