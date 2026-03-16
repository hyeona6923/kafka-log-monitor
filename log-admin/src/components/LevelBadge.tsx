import { getLevelColor } from "../utils/logColor";

interface Props {
  level: string;
}

function LevelBadge({ level }: Props) {
  return (
    <span
      style={{
        padding: "4px 10px",
        borderRadius: "6px",
        color: "white",
        backgroundColor: getLevelColor(level),
        fontWeight: "bold",
        fontSize: "12px"
      }}
    >
      {level}
    </span>
  );
}

export default LevelBadge;