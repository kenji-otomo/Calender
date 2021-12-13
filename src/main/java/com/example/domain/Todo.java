package com.example.domain;

public class Todo {

	private Integer id;
	
	private String title;

	private String contents;
	
	private String date;

	
	
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

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

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	@Override
	public String toString() {
		return "Todo [id=" + id + ", title=" + title + ", contents=" + contents + ", date=" + date + "]";
	}
	
	
}
