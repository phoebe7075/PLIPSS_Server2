package plibss.PLIBSSServer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;



public class Mysql {
	private static Mysql obj = null;
	private Connection conn = null;
    private PreparedStatement stmt = null;
    private ResultSet rs = null;
    private ResultSetMetaData rsmd = null;
	
	private Mysql()	throws SQLException {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost", "root", "apdlvmf1");
		}
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public static Mysql getConnection() throws SQLException {
		if (Mysql.obj == null)
			Mysql.obj = new Mysql();

		return Mysql.obj;
	}
	
	public void sql(String sql) throws SQLException {
		stmt = conn.prepareStatement(sql);
	}
	
	public void set(int idx, String str) throws SQLException{
		stmt.setString(idx, str);
	}
	
	public ResultSet select() throws SQLException {
		rs = stmt.executeQuery();
		rsmd = rs.getMetaData();

		return rs;
	}	
	
	public ResultSetMetaData getMetaData() throws SQLException {
		return rsmd;
	}
	
	public int insert() throws SQLException {
		return stmt.executeUpdate();
	}
	
	public int update() throws SQLException {
		return stmt.executeUpdate();
	}
	
	public int delete() throws SQLException {
		return stmt.executeUpdate();
	}
	
	
	public void close() throws SQLException {
		if (rs != null)
			rs.close();
		if (stmt != null)
			stmt.close();
		if (conn != null)
			conn.close();
	}


	
}
