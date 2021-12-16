package com.example.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.domain.Todo;

@Mapper
public interface TodoMapper {
	
	public Todo searchById(Integer id);
		
	public List<Todo> searchListByDate(String date);
	
	public List<Todo> searchListByMonth(String month);

	public List<Todo> searchAll();
	
	public void addTodo(Todo todo);

}
