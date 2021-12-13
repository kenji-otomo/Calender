package com.example.domain;

import java.util.List;

public class Day {

	private String day;
	private List<Todo>todoList;
	
	public String getDay() {
		return day;
	}
	public void setDay(String day) {
		this.day = day;
	}
	public List<Todo> getTodoList() {
		return todoList;
	}
	public void setTodoList(List<Todo> todoList) {
		this.todoList = todoList;
	}
	
	@Override
	public String toString() {
		return "Day [day=" + day + ", todoList=" + todoList + "]";
	}
	
}
