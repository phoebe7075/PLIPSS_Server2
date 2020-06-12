package plibss.PLIBSSServer.DAO;

import plibss.PLIBSSServer.Mysql;
import plibss.core.model.Bookf;
import plibss.core.model.Libraryf;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

public class LibraryfDAO {
    private static final String SQL_SELECT = "SELECT 사용자id, 도서관id FROM `도서관 즐겨찾기`";
    private LibraryfDAO() {}
    private static class LazyHolder {
        public static final LibraryfDAO INSTANCE = new LibraryfDAO();
    }
    public static LibraryfDAO getInstance() {
        return LazyHolder.INSTANCE;
    }

    public Libraryf match(ResultSet rs) throws IOException, SQLException, Exception {
        Libraryf libraryf = new Libraryf();
        libraryf.setUserid(rs.getString("사용자id"));
        libraryf.setLid(rs.getString("도서관id"));
        return libraryf;
    }


    // 사용자가 도서관 즐겨찾기 정보를 추가함
    public void insertFavorite(Libraryf libraryf) throws IOException, SQLException, Exception {
        Mysql mysql = Mysql.getConnection();
        mysql.sql("INSERT INTO `도서관 즐겨찾기` (`사용자id`, `도서관ID`) VALUES (?,?)");
        mysql.set(1,libraryf.getUserid());
        mysql.set(2,libraryf.getLid());
        mysql.insert();
    }


    // 사용자가 도서관 즐겨찾기 정보를 조회함
    public Libraryf[] getFavorites(String uid) throws IOException, SQLException, Exception {
        Mysql mysql = Mysql.getConnection();
        mysql.sql(SQL_SELECT + "WHERE `사용자id` = ?");
        mysql.set(1,uid);
        ResultSet rs = mysql.select();
        Vector<Libraryf> vector = new Vector<Libraryf>();
        while (rs.next()){
            vector.add(match(rs));
        }

        return vector.toArray(new Libraryf[0]);
    }

    // 사용자가 도서관 즐겨찾기 정보를 삭제함
    public void deleteFavorite(Libraryf libraryf) throws IOException, SQLException, Exception {
        Mysql mysql = Mysql.getConnection();
        mysql.sql("DELETE FROM `도서관 즐겨찾기` WHERE `사용자id`= ? AND `도서관id` = ?");
        mysql.set(1,libraryf.getUserid());
        mysql.set(2,libraryf.getLid());
        mysql.delete();
    }

    // 사용자가 특정 즐겨찾기를 찾음
    public Libraryf getFavorite(String uid, String lid)throws IOException, SQLException, Exception {
        Mysql mysql = Mysql.getConnection();
        mysql.sql(SQL_SELECT + "WHERE `사용자id` = ? AND `도서관id` = ?");
        mysql.set(1,uid);
        mysql.set(2,lid);
        ResultSet rs = mysql.select();
        while (rs.next()){
            return match(rs);
        }
        return null;
    }
}
