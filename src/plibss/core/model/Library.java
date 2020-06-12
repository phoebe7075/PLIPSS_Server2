package plibss.core.model;


import java.io.Serializable;
public class Library implements Serializable {
	private String Lid;
	private String Lname;
	private String Cname;
	private String Districtname;
	private String Ltype;
	private String Homepage;
	private String Lphnum;
	private String HolidayEndTime;
	private String HolidayStartTime;
	private String SaturdayEndTime;
	private String SaturdayStartTime;
	private String WeekdayEndTime;
	private String WeekdayStartTime;
	private String ClosedDay;
	private int borrowday;
	private int borrownum;
	private int readingseat;

	public Library() {
	}

	public Library(String lid, String lname, String cname, String districtname, String ltype, String homepage, String lphnum, String holidayEndTime, String holidayStartTime, String saturdayEndTime, String saturdayStartTime, String weekdayEndTime, String weekdayStartTime, String closedDay, int borrowday, int borrownum, int readingseat) {
		Lid = lid;
		Lname = lname;
		Cname = cname;
		Districtname = districtname;
		Ltype = ltype;
		Homepage = homepage;
		Lphnum = lphnum;
		HolidayEndTime = holidayEndTime;
		HolidayStartTime = holidayStartTime;
		SaturdayEndTime = saturdayEndTime;
		SaturdayStartTime = saturdayStartTime;
		WeekdayEndTime = weekdayEndTime;
		WeekdayStartTime = weekdayStartTime;
		ClosedDay = closedDay;
		this.borrowday = borrowday;
		this.borrownum = borrownum;
		this.readingseat = readingseat;
	}

	public String getLid() {
		return Lid;
	}
	public void setLid(String lid) {
		Lid = lid;
	}
	public String getLname() {
		return Lname;
	}
	public void setLname(String lname) {
		Lname = lname;
	}
	public String getCname() {
		return Cname;
	}
	public void setCname(String cname) {
		Cname = cname;
	}
	public String getDistrictname() {
		return Districtname;
	}
	public void setDistrictname(String districtname) {
		Districtname = districtname;
	}
	public String getLtype() {
		return Ltype;
	}
	public void setLtype(String ltype) {
		Ltype = ltype;
	}
	public String getHomepage() {
		return Homepage;
	}
	public void setHomepage(String homepage) {
		Homepage = homepage;
	}
	public String getLphnum() {
		return Lphnum;
	}
	public void setLphnum(String lphnum) {
		Lphnum = lphnum;
	}
	public String getHolidayEndTime() {
		return HolidayEndTime;
	}
	public void setHolidayEndTime(String holidayEndTime) {
		HolidayEndTime = holidayEndTime;
	}
	public String getHolidayStartTime() {
		return HolidayStartTime;
	}
	public void setHolidayStartTime(String holidayStartTime) {
		HolidayStartTime = holidayStartTime;
	}
	public String getSaturdayEndTime() {
		return SaturdayEndTime;
	}
	public void setSaturdayEndTime(String saturdayEndTime) {
		SaturdayEndTime = saturdayEndTime;
	}
	public String getSaturdayStartTime() {
		return SaturdayStartTime;
	}
	public void setSaturdayStartTime(String saturdayStartTime) {
		SaturdayStartTime = saturdayStartTime;
	}
	public String getWeekdayEndTime() {
		return WeekdayEndTime;
	}
	public void setWeekdayEndTime(String weekdayEndTime) {
		WeekdayEndTime = weekdayEndTime;
	}
	public String getWeekdayStartTime() {
		return WeekdayStartTime;
	}
	public void setWeekdayStartTime(String weekdayStartTime) {
		WeekdayStartTime = weekdayStartTime;
	}
	public String getClosedDay() {
		return ClosedDay;
	}
	public void setClosedDay(String closedDay) {
		ClosedDay = closedDay;
	}
	public int getBorrowday() {
		return borrowday;
	}
	public void setBorrowday(int borrowday) {
		this.borrowday = borrowday;
	}
	public int getBorrownum() {
		return borrownum;
	}
	public void setBorrownum(int borrownum) {
		this.borrownum = borrownum;
	}
	public int getReadingseat() {
		return readingseat;
	}
	public void setReadingseat(int readingseat) {
		this.readingseat = readingseat;
	}
	
}
