package plibss.PLIBSSServer.DAO;

import plibss.PLIBSSServer.Mysql;
import plibss.core.model.Book;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

public class BookDAO {
    private static final String SQL_SELECT = "SELECT * FROM `도서 정보`";
    private BookDAO() {}
    private static class LazyHolder {
        public static final BookDAO INSTANCE = new BookDAO();
    }
    public static BookDAO getInstance() {
        return LazyHolder.INSTANCE;
    }

    public Book match(ResultSet rs) throws IOException, SQLException, Exception {
        Book bk = new Book();
        bk.setBname(rs.getString("도서명"));
        bk.setLid(rs.getString("도서관id"));
        bk.setAuthorn(rs.getString("저자"));
        bk.setKeyword(rs.getString("키워드"));
        bk.setYear(rs.getInt("발행년도"));
        bk.setKdc(rs.getString("KDC분류기호"));
        bk.setIsbn(rs.getString("ISBN"));
        return bk;
    }

    // 도서관id로 보유장서정보 찾기
    public Book[] getBooks(String lid) throws IOException, SQLException, Exception {
        Mysql mysql = Mysql.getConnection();
        mysql.sql(SQL_SELECT + "WHERE `도서관id` = ?");
        mysql.set(1,lid);
        ResultSet rs = mysql.select();
        Vector<Book> vector = new Vector<Book>();
        while (rs.next()) {
            vector.add(match(rs));
        }
        return vector.toArray(new Book[0]);
    }

    //도서관id, 도서이름으로 도서 상세정보 찾기
    public Book getBook(String lid, String bn) throws IOException, SQLException, Exception {
        Mysql mysql = Mysql.getConnection();
        mysql.sql(SQL_SELECT + "WHERE `도서관id` = ? AND `도서명` = ? LIMIT 1");
        mysql.set(1,lid);
        mysql.set(2,bn);
        ResultSet rs = mysql.select();
        while (rs.next()) {
            return match(rs);
        }
        return null;
    }


}
