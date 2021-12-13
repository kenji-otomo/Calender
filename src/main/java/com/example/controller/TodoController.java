package com.example.controller;

import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.domain.Todo;
import com.example.form.AddTodoForm;
import com.example.form.TodoForm;
import com.example.repository.TodoMapper;

@Controller
@RequestMapping("/todo")
public class TodoController {
	
	@Autowired
	private TodoMapper todoMapper;
	
	@ModelAttribute
	public TodoForm setUpTodoForm() {
		return new TodoForm();
	}
	
	@ModelAttribute
	public AddTodoForm setupAddTodoForm() {
		return new AddTodoForm();
	}
	

	@RequestMapping("")
	public String todo(TodoForm form,Model model) {

		String day = form.getDay();
		
		if (Integer.parseInt(day) < 10) {
			day = "0"+day;
		}
		
		String datetime = form.getYear()+"-"+form.getMonth()+"-"+day;
		
		List<Todo>todoList = todoMapper.searchListByDate(datetime);
		
		model.addAttribute("year", form.getYear());
		model.addAttribute("month", form.getMonth());
		model.addAttribute("day", form.getDay());
		model.addAttribute("todoList", todoList);
		return "todo";
	}
	
	@RequestMapping("/otherDay")
	public String otherDay(TodoForm form,Model model) {
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(Integer.parseInt(form.getYear()), Integer.parseInt(form.getMonth())-1, Integer.parseInt(form.getDay()));
		
		calendar.add(Calendar.DAY_OF_MONTH, Integer.parseInt(form.getAdd()));
		
		String year = String.valueOf(calendar.get(Calendar.YEAR));
		String month = String.valueOf(calendar.get(Calendar.MONTH) + 1 );
		String day = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
		
		if (calendar.get(Calendar.DAY_OF_MONTH) < 10) {
			day = "0"+day;
		}
		
		String datetime = year+"-"+month+"-"+day;
		
		List<Todo>todoList = todoMapper.searchListByDate(datetime);
		
		model.addAttribute("year", year);
		model.addAttribute("month", month);
		model.addAttribute("day", String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
		model.addAttribute("todoList", todoList);
		return "todo";
	}
	
	@RequestMapping("/addTodo")
	public String addTodo(@Validated AddTodoForm form,BindingResult result,Model model) {
		
		if (result.hasErrors()) {
			
			String day = form.getDay();
			
			if (Integer.parseInt(day) < 10) {
				day = "0"+day;
			}
			String datetime = form.getYear()+"-"+form.getMonth()+"-"+day;
			List<Todo>todoList = todoMapper.searchListByDate(datetime);

			model.addAttribute("year", form.getYear());
			model.addAttribute("month", form.getMonth());
			model.addAttribute("day", form.getDay());
			model.addAttribute("todoList", todoList);
			return "todo";
		}
		
		String day = form.getDay();
		
		if (Integer.parseInt(day) < 10) {
			day = "0"+day;
		}
		
		if (form.getTime() == null) {
			form.setTime("00:00");
		}
		
		String datetime = form.getYear()+"-"+form.getMonth()+"-"+day+" "+form.getTime()+":00"; 
		
		Todo todo = new Todo();
		
		todo.setTitle(form.getTitle());
		todo.setContents(form.getContents());
		todo.setDate(datetime);
		
		todoMapper.addTodo(todo);
		
		return "redirect:/todo?year=" + form.getYear() + "&month=" + form.getMonth() + "&day=" + form.getDay();
	}
}
