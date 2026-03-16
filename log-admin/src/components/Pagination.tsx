interface PaginationProps {
  page: number;
  totalPages: number;
  onPageChange: (page: number) => void;
}

function Pagination({ page, totalPages, onPageChange }: PaginationProps) {
  return (
    <div className="pagination-container">

      <button
        className="page-button"
        onClick={() => onPageChange(page - 1)}
        disabled={page === 0}
      >
        이전
      </button>

      <span className="page-info">
        페이지 {page + 1} / {totalPages || 1}
      </span>

      <button
        className="page-button"
        onClick={() => onPageChange(page + 1)}
        disabled={page + 1 >= totalPages}
      >
        다음
      </button>

    </div>
  );
}

export default Pagination;