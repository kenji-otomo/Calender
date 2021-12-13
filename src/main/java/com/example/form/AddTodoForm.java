package com.example.form;

import javax.validation.constraints.NotBlank;

public class AddTodoForm {

	@NotBlank(message = "入力必須項目です")
	private String title;
	@NotBlank(message = "入力必須項目です")
	private String contents;
	private String time;
	private String year;
	private String month;
	private String day;
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContents() {
		return contents;
	}
	public void setContents(String contents) {
		this.contents = contents;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String date) {
		this.time = date;
	}
	
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
	public String getDay() {
		return day;
	}
	public void setDay(String day) {
		this.day = day;
	}
	@Override
	public String toString() {
		return "AddTodoForm [title=" + title + ", contents=" + contents + ", date=" + time + ", year=" + year
				+ ", month=" + month + ", day=" + day + "]";
	}
}
