package plibss.core.model;

import java.io.Serializable;


public class Book implements Serializable{
	private String Bname;
	private String Lid;
	private String authorn;
	private String keyword;
	private int year;
	private String kdc;
	private String isbn;

	public Book() {
	}

	public Book(String bname, String lid, String authorn, String keyword, int year, String kdc, String isbn) {
		Bname = bname;
		Lid = lid;
		this.authorn = authorn;
		this.keyword = keyword;
		this.year = year;
		this.kdc = kdc;
		this.isbn = isbn;
	}

	public String getBname() {
		return Bname;
	}
	public void setBname(String bname) {
		Bname = bname;
	}
	public String getLid() {
		return Lid;
	}
	public void setLid(String lid) {
		Lid = lid;
	}
	public String getAuthorn() {
		return authorn;
	}
	public void setAuthorn(String authorn) {
		this.authorn = authorn;
	}
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public String getKdc() {
		return kdc;
	}
	public void setKdc(String kdc) {
		this.kdc = kdc;
	}
	public String getIsbn() {
		return isbn;
	}
	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}
	
	
}
