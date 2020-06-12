package plibss.PLIBSSServer.DAO;

public class BorrowDAO {
    private static final String SQL_SELECT = "SELECT 사용자id, 도서명, 도서관id, CAST(대출일시 AS CHAR) AS 대출일시, CAST(반납일시 AS CHAR) AS 반납일시, 현재상태 FROM `대출정보`";
    private BorrowDAO() {}
    private static class LazyHolder {
        public static final BorrowDAO INSTANCE = new BorrowDAO();
    }
    public static BorrowDAO getInstance() {
        return LazyHolder.INSTANCE;
    }
}
