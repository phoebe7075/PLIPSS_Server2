package plibss.PLIBSSServer.DAO;

import jdk.nashorn.internal.parser.Token;
import plibss.PLIBSSServer.Mysql;
import plibss.core.model.Library;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.StringTokenizer;
import java.util.Vector;

public class LibraryDAO {
    private static final String SQL_SELECT = "SELECT * FROM `도서관 정보`";

    private LibraryDAO() {
    }

    private static class LazyHolder {
        public static final LibraryDAO INSTANCE = new LibraryDAO();
    }

    public static LibraryDAO getInstance() {
        return LazyHolder.INSTANCE;
    }

    public Library match(ResultSet rs) throws IOException, SQLException, Exception {
        Library library = new Library();
        library.setLid(rs.getString("도서관id"));
        library.setLname(rs.getString("도서관명"));
        library.setCname(rs.getString("시도명"));
        library.setDistrictname(rs.getString("시군구명"));
        library.setLtype(rs.getString("도서관유형"));
        library.setHomepage(rs.getString("홈페이지주소"));
        library.setLphnum(rs.getString("도서관전화번호"));
        library.setHolidayEndTime(rs.getString("공휴일운영종료시각"));
        library.setHolidayStartTime(rs.getString("공휴일운영시작시각"));
        library.setSaturdayEndTime(rs.getString("토요일운영종료시각"));
        library.setSaturdayStartTime(rs.getString("토요일운영시작시각"));
        library.setWeekdayEndTime(rs.getString("평일운영종료시각"));
        library.setWeekdayStartTime(rs.getString("평일운영시작시각"));
        library.setClosedDay(rs.getString("휴관일"));
        library.setBorrowday(rs.getInt("대출가능일수"));
        library.setBorrownum(rs.getInt("대출가능권수"));
        library.setReadingseat(rs.getInt("열람좌석수"));

        return library;
    }

    //전체 도서관 목록 조회
    public Library[] getLibraries() throws IOException, SQLException, Exception {
        Mysql mysql = Mysql.getConnection();
        mysql.sql(SQL_SELECT);

        ResultSet resultSet = mysql.select();
        Vector<Library> vector = new Vector<Library>();
        while (resultSet.next()) {
            vector.add(match(resultSet));
        }
        return vector.toArray(new Library[0]);
    }


    // 도서관 세부정보 조회
    public Library getLibrary(String lid) throws IOException, SQLException, Exception {
        Mysql mysql = Mysql.getConnection();
        mysql.sql(SQL_SELECT + "WHERE `도서관id` = ? LIMIT 1");
        mysql.set(1, lid);
        ResultSet resultSet = mysql.select();
        while (resultSet.next()) {
            return match(resultSet);
        }
        return null;
    }


}
