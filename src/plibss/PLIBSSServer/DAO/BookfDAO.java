package plibss.PLIBSSServer.DAO;

import plibss.PLIBSSServer.Mysql;
import plibss.core.model.Book;
import plibss.core.model.Bookf;
import plibss.core.model.Libraryf;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

public class BookfDAO {
    private static final String SQL_SELECT = "SELECT * FROM `도서 즐겨찾기` ";
    private BookfDAO() {}
    private static class LazyHolder {
        public static final BookfDAO INSTANCE = new BookfDAO();
    }
    public static BookfDAO getInstance() {
        return LazyHolder.INSTANCE;
    }

    public Bookf match(ResultSet rs) throws IOException, SQLException, Exception {
        Bookf bookf = new Bookf();
        bookf.setUserid(rs.getString("사용자id"));
        bookf.setLid(rs.getString("도서관id"));
        bookf.setBname(rs.getString("도서명"));
        return bookf;
    }

    //사용자가 도서 즐겨찾기 정보를 추가하는 매서드
    public void insertFavorite(Bookf bookf) throws IOException, SQLException, Exception {
        Mysql mysql = Mysql.getConnection();
        mysql.sql("INSERT INTO `도서 즐겨찾기` (`사용자id`, `도서관ID`, `도서명`) VALUES (?,?,?)");
        mysql.set(1,bookf.getUserid());
        mysql.set(2,bookf.getLid());
        mysql.set(3,bookf.getBname());
        mysql.insert();
    }

    // 사용자가 도서 즐겨찾기 정보를 조회함.
    public Bookf[] getFavorites(String uid) throws IOException, SQLException, Exception {
        Mysql mysql = Mysql.getConnection();
        mysql.sql(SQL_SELECT + "WHERE `사용자id` = ?");
        mysql.set(1,uid);
        ResultSet resultSet = mysql.select();
        Vector<Bookf> vector = new Vector<Bookf>();
        while (resultSet.next()) {
            vector.add(match(resultSet));
        }

        return vector.toArray(new Bookf[0]);
    }

    public void deleteFavorite(Bookf bookf) throws IOException, SQLException, Exception {
        Mysql mysql = Mysql.getConnection();
        mysql.sql("DELETE FROM `도서 즐겨찾기` WHERE `사용자id` = ? AND `도서관id` = ? AND `도서명` = ?");
        mysql.set(1,bookf.getUserid());
        mysql.set(2,bookf.getLid());
        mysql.set(3, bookf.getBname());
        mysql.delete();
    }

    public Bookf getFavorite(String uid, String lid, String bname)throws IOException, SQLException, Exception {
        Mysql mysql = Mysql.getConnection();
        mysql.sql(SQL_SELECT + "WHERE `사용자id` = ? AND `도서관id` = ? AND `도서명` = ?");
        mysql.set(1,uid);
        mysql.set(2,lid);
        mysql.set(3,bname);
        ResultSet rs = mysql.select();
        while (rs.next()){
            return match(rs);
        }
        return null;
    }
}
