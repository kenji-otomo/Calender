package com.example.form;

public class OtherMonthForm {

	private String year;
	private String month;
	private String add;
	
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	public String getAdd() {
		return add;
	}
	public void setAdd(String add) {
		this.add = add;
	}
	@Override
	public String toString() {
		return "OtherMonthForm [year=" + year + ", month=" + month + ", add=" + add + "]";
	}
}
