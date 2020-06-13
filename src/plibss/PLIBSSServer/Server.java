package plibss.PLIBSSServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sun.xml.internal.ws.policy.EffectiveAlternativeSelector;
import plibss.PLIBSSServer.DAO.*;
import plibss.core.Protocol;
import plibss.PLIBSSServer.Mysql;
import plibss.core.model.*;

public class Server {
	private final static int PORT = 9999;
	private final static int MAX_USER = 50;
	public static boolean isAdminLogin = false;
	public static void start() {
		try {
			ExecutorService pool = Executors.newFixedThreadPool(MAX_USER);
			ServerSocket theServer = new ServerSocket(PORT);
			System.out.println("Client Wait...");

			while (true) {
				Socket connection = theServer.accept();
				Callable<Void> task = new Task(connection); // 클라이언트마다 쓰레드 하나와 연결한다
				pool.submit(task);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	private static class Task implements Callable<Void> {
		private Socket socket;
		private OutputStream os;
		private InputStream is;
		private String userID = "";

		Task(Socket connection) throws IOException {
			this.socket = connection;
			os = socket.getOutputStream();
			is = socket.getInputStream();
		}

		private void ping() throws IOException, SQLException, Exception {
			System.out.println(userID + " 클라이언트 : PING 메시지");

			Protocol sndData = new Protocol();
			sndData.setType(Protocol.TYPE_UNDEFINED);
			os.write(sndData.getPacket());
		}

		private void exit() throws IOException, SQLException, Exception {
			System.out.println(userID + " 클라이언트 : 종료 메시지");

			is.close();
			os.close();
			socket.close();
			System.out.println(userID + " Client : Closed");
		}

		public Void call() {
			byte[] header = new byte[Protocol.LEN_HEADER];
			byte[] buf = new byte[Protocol.LEN_MAX];
			Protocol protocol = new Protocol();
			System.out.println("Client Connected");

			try {
				int totalReceived, readSize;
				while (true) {
					totalReceived = 0;
					readSize = 0;
					is.read(header, 0, Protocol.LEN_HEADER);
					protocol.setPacketHeader(header);
					while (totalReceived < protocol.getBodyLength()) {
						readSize = is.read(buf, totalReceived, protocol.getBodyLength() - totalReceived);
						totalReceived += readSize;
						System.out.println(userID + " : Data Received (" + totalReceived + "/" + protocol.getBodyLength() + ")");
						if (readSize == -1) {
							System.out.println(userID + " 클라이언트가 종료됨");
							return null;
						}
					}
					protocol.setPacketBody(buf);

					switch (protocol.getType()) {
						case Protocol.TYPE_UNDEFINED:
							ping();
							break;
						case Protocol.TYPE_EXIT: //종료
							exit();
							return null;
						case Protocol.TYPE_LOGIN_REQ:
							login(protocol);
							break;
						case Protocol.TYPE_LOGOUT_REQ:
							logout(protocol);
							break;
						case Protocol.TYPE_LABRARY_LIST_INFO_REQ: //검색조건으로 조회하는 경우
							getLibraryInfo(protocol);
							break;
						case Protocol.TYPE_LABRARY_DETAILS_INFO_REQ: //특정 도서관만 조회하는 경우
							getLibraryDetailsInfo(protocol);
							break;
						case Protocol.TYPE_BOOK_LIST_INFO_REQ: //검색조건으로 조회하는 경우
							getBookInfo(protocol);
							break;
						case Protocol.TYPE_BOOK_DETAILS_INFO_REQ: //특정 도서만 조회하는 경우
							getBookDetailsInfo(protocol);
							break;
						case Protocol.TYPE_LABRARY_FAVORITEINFO_ENROLL_REQ:
							createLibFavorite(protocol);
							break;
						case Protocol.TYPE_LABRARY_FAVORITEINFO_DELETE_REQ:
							deleteLibFavorite(protocol);
							break;
						case Protocol.TYPE_LABRARY_FAVORITEINFO_INFO_REQ:
							getLibFavorites(protocol);
							break;
						case Protocol.TYPE_BOOK_FAVORITEINFO_ENROLL_REQ:
							createBookFavorite(protocol);
							break;
						case Protocol.TYPE_BOOK_FAVORITEINFO_DELETE_REQ:
							deleteBookFavorite(protocol);
							break;
						case Protocol.TYPE_BOOK_FAVORITEINFO_INFO_REQ:
							getBookFavorite(protocol);
							break;
						case Protocol.TYPE_BOOK_BORROW_POSSIBILITY_REQ:
							//getBookPossibility(protocol);
							break;
					}
				}
			} catch (IOException e) { // 연결 오류 발생시
				try {
					System.out.println(userID + " Client : Connection Error Occured");
					Mysql mysql = Mysql.getConnection();
					is.close();
					os.close();
					socket.close();
				} catch (Exception ex) {
					System.out.println(userID + " Client : Connection Error Process Failed");
					e.printStackTrace();
				}
				return null;
			} catch (SQLException e) { // DB 접속 오류 발생시
				try {
					System.out.println(userID + " Client : DB Error Occured");
					Mysql mysql = Mysql.getConnection();
					Protocol sndData = new Protocol();
					sndData.setType(Protocol.TYPE_ERROR);
					os.write(sndData.getPacket());
					is.close();
					os.close();
					socket.close();
				} catch (Exception ex) {
					System.out.println(userID + " Client : DB Error Process Failed");
					e.printStackTrace();
				}
				return null;
			} catch (Exception e) { // 일반 오류 발생시
				try {
					System.out.println(userID + " Client : General Error Occured");
					Mysql mysql = Mysql.getConnection();
					Protocol sndData = new Protocol();
					sndData.setType(Protocol.TYPE_ERROR);
					os.write(sndData.getPacket());
					is.close();
					os.close();
					socket.close();
				} catch (Exception ex) {
					System.out.println(userID + " Client : General Error Process Failed");
					e.printStackTrace();
				}
			}


			return null;
		}

		private void login(Protocol rcvData) throws IOException, SQLException, Exception {
			System.out.println(userID + " 클라이언트 : 로그인 메시지");
			String[] str = (String[]) rcvData.getBody();
			Mysql mysql = Mysql.getConnection();
			mysql.sql("SELECT `아이디`, `패스워드`, `이름` FROM `유저` WHERE `아이디` = ?");
			mysql.set(1, str[0]);
			ResultSet rs = mysql.select();
			Protocol sndData = new Protocol(Protocol.TYPE_LOGIN_RES);

			if (rs.next() && rs.getString("패스워드").equals(str[0])) //아이디가 존재하고 패스워드도 일치할 경우
			{
				this.userID = rs.getString("아이디");
				sndData.setCode(1);
				sndData.setBody(null);
			}
			else
			{
				sndData.setCode(0);
				sndData.setBody(null);
			}

			os.write(sndData.getPacket());
		}

		private void logout(Protocol rcvData) throws IOException, SQLException, Exception {
			System.out.println(userID + " 클라이언트 : 로그아웃 메시지");
			this.userID = "";
			Protocol sndData = new Protocol(Protocol.TYPE_LOGOUT_RES, 1);
			os.write(sndData.getPacket());
		}


		//도서관 검색 정보 조회
		private void getLibraryInfo(Protocol rcvData) throws IOException, SQLException, Exception{
			LibraryDAO libraryDAO = LibraryDAO.getInstance();
			StringTokenizer token1 = new StringTokenizer((String) rcvData.getBody(),"/");
			Library[] libraries1 = libraryDAO.getLibraries();
			Vector<Library> arr = new Vector<Library>();
			String[] scon= new String[4]; // /로 구분되는 정보
			String[] key = new String[4];
			String[] value = new String[4];
			String[] context = new String[4];
			int idx = 0;
			while (token1.hasMoreTokens()) {
				scon[idx] = token1.nextToken();
				idx++;
			}

			for (int i =0; i < 4; i++)
			{
				StringTokenizer token2 = new StringTokenizer(scon[i],":");
				key[i] = token2.nextToken();
				value[i] = token2.nextToken();
			}
			for(Library lib : libraries1) //전체 도서관에서 한 도서관씩 정보를 찾는다.
			{
				context[0] = lib.getLname();
				context[1] = lib.getCname();
				context[2] = lib.getDistrictname();
				context[3] = lib.getLtype();

				boolean isExist =true;
				for (int i=0; i<4; i++) {
					if (value != null) //상세조건을 기술한 조건에 대해 검색
					{
						Pattern pt = Pattern.compile(value[i],Pattern.CASE_INSENSITIVE ); //일부라도 일치하는 조건을 찾기 위함
						Matcher ptmatcher = pt.matcher(context[i]);
						if(!ptmatcher.find()) //만약 적은 검색조건에서 하나라도 맞는 칼럼이 존재하지 않는다면
						{
							isExist = false;
							break; //조건문 종료
						}
					}
				}

				if (isExist) //방금 조건문에서 모두 통과하여 칼럼이 존재 할때.
				{
					arr.add(lib); //벡터 리스트에 추가.
				}
			}

			Protocol sndData = new Protocol(Protocol.TYPE_LABRARY_LIST_INFO_RES,1);
			sndData.setBody(arr.toArray(new Library[0]));
			os.write(sndData.getPacket());
		}

		//도서관 세부정보 조회
		private void getLibraryDetailsInfo(Protocol rcvData) throws IOException, SQLException, Exception{
			LibraryDAO libraryDAO = LibraryDAO.getInstance();
			String lid = (String) rcvData.getBody();
			Library library = libraryDAO.getLibrary(lid);

			if(library == null) //도서관 정보가 존재하지 않을 때
			{
				Protocol sndData = new Protocol(Protocol.TYPE_LABRARY_DETAILS_INFO_RES, 0);
				os.write(sndData.getPacket());
				return;
			}

			Protocol sndData = new Protocol(Protocol.TYPE_LABRARY_DETAILS_INFO_RES);
			sndData.setBody(library);
			os.write(sndData.getPacket());
		}

		// 도서 검색정보 조회
		private void getBookInfo(Protocol rcvData) throws IOException, SQLException, Exception{
			BookDAO bookDAO = BookDAO.getInstance();
			StringTokenizer token1 = new StringTokenizer((String) rcvData.getBody(),"/");

			Vector<Book> arr = new Vector<Book>();
			String[] scon= new String[4]; // /로 구분되는 정보
			String[] key = new String[4];
			String[] value = new String[4];
			String[] context = new String[3]; //도서관 id는 제외함. 필요하지않음
			int idx = 0;
			while (token1.hasMoreTokens()) {
				scon[idx] = token1.nextToken();
				idx++;
			}

			for (int i =0; i < 4; i++)
			{
				StringTokenizer token2 = new StringTokenizer(scon[i],":");
				key[i] = token2.nextToken();
				value[i] = token2.nextToken();
			}

			Book[] books = bookDAO.getBooks(value[0]); // 첫번째 조건은 도서관id. 무조건 있어야 함. 그래서 나중 검색에도 제외됨

			for (Book bk : books)
			{
				context[0] = bk.getBname();
				context[1] = bk.getAuthorn();
				context[2] = bk.getKdc();
				boolean isExist =true;

				for (int i=0; i<3; i++) {
					if (value != null) //상세조건을 기술한 조건에 대해 검색
					{
						Pattern pt = Pattern.compile(value[i+1],Pattern.CASE_INSENSITIVE); //일부라도 일치하는 조건을 찾기 위함
						Matcher ptmatcher = pt.matcher(context[i]);
						if(!ptmatcher.find()) //만약 적은 검색조건에서 하나라도 맞는 칼럼이 존재하지 않는다면
						{
							isExist = false;
							break; //조건문 종료
						}
					}
				}
				if (isExist) //방금 조건문에서 모두 통과하여 칼럼이 존재 할때.
				{
					arr.add(bk); //벡터 리스트에 추가.
				}
			}

			Protocol sndData = new Protocol(Protocol.TYPE_BOOK_LIST_INFO_RES,1);
			sndData.setBody(arr.toArray(new Book[0]));
			os.write(sndData.getPacket());
		}

		// 도서 세부정보 조회
		private void getBookDetailsInfo(Protocol rcvData) throws IOException, SQLException, Exception{
			BookDAO bookDAO = BookDAO.getInstance();
			Book bk1 = (Book) rcvData.getBody();

			Book bk2 = bookDAO.getBook(bk1.getLid(),bk1.getBname());

			if(bk2 == null) // 도서 정보가 존재하지 않을 때
			{
				Protocol sndData = new Protocol(Protocol.TYPE_BOOK_DETALES_INFO_RES, 0);
				os.write(sndData.getPacket());
				return;
			}
			Protocol sndData = new Protocol(Protocol.TYPE_BOOK_DETALES_INFO_RES,1);
			sndData.setBody(bk2);
			os.write(sndData.getPacket());
		}



		// 도서관 즐겨찾기 추가
		private void createLibFavorite(Protocol rcvData) throws IOException, SQLException, Exception {
			Libraryf lif = new Libraryf();
			lif= (Libraryf) rcvData.getBody();

			LibraryfDAO DAO = LibraryfDAO.getInstance();
			Libraryf libraryf = DAO.getFavorite(userID,lif.getLid());
			if (libraryf != null) // 이미 중복된 도서관이 존재할경우 실패.
			{
				Protocol sndData = new Protocol(Protocol.TYPE_LABRARY_FAVORITEINFO_ENROLL_RES, 0);
				os.write(sndData.getPacket());
				return;
			}

			DAO.insertFavorite(lif);

			Protocol sndData = new Protocol(Protocol.TYPE_LABRARY_FAVORITEINFO_ENROLL_RES, 1);
			os.write(sndData.getPacket());
		}

		// 도서관 즐겨찾기 제거
		private void deleteLibFavorite(Protocol rcvData) throws IOException, SQLException, Exception {
			LibraryfDAO DAO = LibraryfDAO.getInstance();
			Libraryf lif = (Libraryf) rcvData.getBody();

			Libraryf libraryf = DAO.getFavorite(userID,lif.getLid());
			if (libraryf == null) // 삭제하고자 하는 도서관 즐겨찾기 정보가 없다면 실패.
			{
				Protocol sndData = new Protocol(Protocol.TYPE_LABRARY_FAVORITEINFO_DELETE_RES, 0);
				os.write(sndData.getPacket());
				return;
			}
			DAO.deleteFavorite(lif);

			Protocol sndData = new Protocol(Protocol.TYPE_LABRARY_FAVORITEINFO_DELETE_RES, 1);
			os.write(sndData.getPacket());
		}

		// 도서관 즐겨찾기 정보 조회
		private void getLibFavorites(Protocol rcvData) throws IOException, SQLException, Exception {
			LibraryfDAO DAO = LibraryfDAO.getInstance();
			Libraryf[] libraryfs = DAO.getFavorites(userID);

			if(libraryfs.length == 0) //도서관 즐겨찾기 정보가 없을경우
			{
				Protocol sndData = new Protocol(Protocol.TYPE_LABRARY_FAVORITEINFO_INFO_RES,0);
				os.write(sndData.getPacket());
				return;
			}
			Protocol sndData = new Protocol(Protocol.TYPE_LABRARY_FAVORITEINFO_INFO_RES,1);
			sndData.setBody(libraryfs);
			os.write(sndData.getPacket());

		}

		// 도서 즐겨찾기 정보 등록
		private void createBookFavorite(Protocol rcvData)throws IOException, SQLException, Exception {
			Bookf bookf = new Bookf();
			bookf = (Bookf) rcvData.getBody();

			BookfDAO dao = BookfDAO.getInstance();
			Bookf bf = dao.getFavorite(userID,bookf.getLid(),bookf.getBname());
			if(bf != null) //도서 즐겨찾기 정보가 이미 존재하는 경우
			{
				Protocol sndData = new Protocol(Protocol.TYPE_BOOK_FAVORITEINFO_ENROLL_RES,0);
				os.write(sndData.getPacket());
				return;
			}
			dao.insertFavorite(bookf);
			Protocol sndData = new Protocol(Protocol.TYPE_BOOK_FAVORITEINFO_ENROLL_RES,1);
			os.write(sndData.getPacket());
		}

		// 도서 즐겨찾기 정보 삭제
		private void deleteBookFavorite(Protocol rcvData) throws IOException, SQLException, Exception{
			Bookf bookf = new Bookf();
			bookf = (Bookf) rcvData.getBody();

			BookfDAO DAO = BookfDAO.getInstance();
			Bookf bf = DAO.getFavorite(userID,bookf.getLid(),bookf.getBname());

			if(bf == null) //삭제하고자 하는 정보가 없다면 실패.
			{
				Protocol sndData = new Protocol(Protocol.TYPE_BOOK_FAVORITEINFO_DELETE_RES,0);
				os.write(sndData.getPacket());
				return;
			}
			DAO.deleteFavorite(bookf);
			Protocol sndData = new Protocol(Protocol.TYPE_BOOK_FAVORITEINFO_DELETE_RES,1);
			os.write(sndData.getPacket());
		}

		private void getBookFavorite(Protocol rcvData)  throws IOException, SQLException, Exception {
			BookfDAO DAO = BookfDAO.getInstance();
			Bookf[] bookfs = DAO.getFavorites(userID);

			if(bookfs.length == 0)
			{
				Protocol sndData = new Protocol(Protocol.TYPE_BOOK_FAVORITEINFO_INFO_RES,0);
				os.write(sndData.getPacket());
				return;
			}
			Protocol sndData = new Protocol(Protocol.TYPE_BOOK_FAVORITEINFO_INFO_RES,1);
			sndData.setBody(bookfs);
			os.write(sndData.getPacket());
		}

		private void getBookPossibility(Protocol rcvData) throws  IOException, SQLException, Exception {
			BorrowDAO borrowDAO = BorrowDAO.getInstance();
			BookfDAO bookfDAO = BookfDAO.getInstance();

			Bookf[] bookfs = bookfDAO.getFavorites(userID);

			int idx = 0;
			for(Bookf bf : bookfs)
			{
				if (borrowDAO.getBorrowState(bf.getLid(),bf.getBname())) //대출 가능하다면
				{
					bookfs[idx].setPossibility(true);
				}
				else
				{
					bookfs[idx].setPossibility(false);
				}
				idx++;
			}
			Protocol sndData = new Protocol(Protocol.TYPE_BOOK_BORROW_POSSIBILITY_RES,1);

		}
	}
}

