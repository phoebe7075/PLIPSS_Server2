package plibss.core.model;
import java.io.Serializable;
public class Bookf implements Serializable{
	private String userid;
	private String Lid;
	private String Bname;

	public Bookf() {
	}

	public Bookf(String userid, String lid, String bname) {
		this.userid = userid;
		Lid = lid;
		Bname = bname;
	}

	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getLid() {
		return Lid;
	}
	public void setLid(String lid) {
		Lid = lid;
	}
	public String getBname() {
		return Bname;
	}
	public void setBname(String bname) {
		Bname = bname;
	}
}
