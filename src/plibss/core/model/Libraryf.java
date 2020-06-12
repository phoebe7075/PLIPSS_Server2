package plibss.core.model;

import java.io.Serializable;

public class Libraryf implements Serializable{
	private String userid;
	private String Lid;

	public Libraryf() {
	}

	public Libraryf(String userid, String lid) {
		this.userid = userid;
		Lid = lid;
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

}
