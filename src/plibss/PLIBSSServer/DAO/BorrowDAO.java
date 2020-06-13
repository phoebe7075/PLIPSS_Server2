package plibss.PLIBSSServer.DAO;

import plibss.PLIBSSServer.Mysql;
import plibss.core.model.Borrow;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Vector;

public class BorrowDAO {
    private static final String SQL_SELECT = "SELECT 사용자id, 도서관id, 도서명, CAST(대출일시 AS CHAR) AS 대출일시, CAST(반납일시 AS CHAR) AS 반납일시, 현재상태 FROM `대출정보`";
    private BorrowDAO() {}
    private static class LazyHolder {
        public static final BorrowDAO INSTANCE = new BorrowDAO();
    }
    public static BorrowDAO getInstance() {
        return LazyHolder.INSTANCE;
    }

    public Borrow match(ResultSet rs)  throws IOException, SQLException, Exception
    {
        Borrow borrow = new Borrow();
        borrow.setUserid(rs.getString("사용자id"));
        borrow.setLid(rs.getString("도서관id"));
        borrow.setBname(rs.getString("도서명"));
        if (rs.getString("대출일시") != null && !rs.getString("대출일시").equals("0000-00-00 00:00:00"))
            borrow.setStartTime(LocalDateTime.parse(rs.getString("대출일시"), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        if (rs.getString("반납일시") != null && !rs.getString("반납일시").equals("0000-00-00 00:00:00"))
            borrow.setStartTime(LocalDateTime.parse(rs.getString("대출일시"), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        return borrow;
    }

    //도서관 별 대출 현황 가져오기
    public Borrow[] getBorrowList(String lid) throws IOException, SQLException, Exception
    {
        Mysql mysql = Mysql.getConnection();
        mysql.sql(SQL_SELECT + "WHERE `도서관id` = ? AND `현재상태` = ?");
        mysql.set(1,lid);
        mysql.set(2,"대출중");
        ResultSet rs = mysql.select();
        Vector<Borrow> arr = new Vector<Borrow>();

        while (rs.next()){
            arr.add(match(rs));
        }

        return arr.toArray(new Borrow[0]);
    }

    public boolean getBorrowState(String lid,String bname) throws IOException, SQLException, Exception
    {
        Mysql mysql = Mysql.getConnection();
        mysql.sql(SQL_SELECT + "WHERE `도서관id` = ? AND `도서명` = ? AND `현재상태` =?");
        mysql.set(1,lid);
        mysql.set(2,bname);
        mysql.set(3,"대출중");
        ResultSet rs = mysql.select();

        if (rs.getString("현재상태") == "대출중")
        {
            return false;
        }

        return true;



    }
}
