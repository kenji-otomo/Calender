package com.example.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.domain.Todo;
import com.example.repository.TodoMapper;

@Service
public class CalenderService {
	
	@Autowired
	private TodoMapper mapper;

	public Todo searchById(Integer id) {
		return mapper.searchById(id);
	}
	
	public List<Todo> searchListByDate(String date) {
		return mapper.searchListByDate(date);
	}
	
	public List<Todo>  searchAll() {
		return mapper.searchAll();
	}
	
	public void addTodo(Todo todo) {
		mapper.addTodo(todo);
	}
}
