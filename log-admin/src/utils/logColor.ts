export const getLevelColor = (level: string) => {
  switch (level) {
    case "INFO":
      return "#3498db";
    case "WARN":
      return "#f39c12";
    case "ERROR":
      return "#e74c3c";
    default:
      return "#7f8c8d";
  }
};