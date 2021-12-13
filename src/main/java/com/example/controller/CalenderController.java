package com.example.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.domain.Day;
import com.example.domain.Todo;
import com.example.form.OtherMonthForm;
import com.example.form.TodoForm;
import com.example.repository.TodoMapper;

@Controller
@RequestMapping("/calender")
public class CalenderController {
	
	@Autowired
	private TodoMapper todoMapper;
	
	
	@ModelAttribute
	public TodoForm setUpTodoForm() {
		return new TodoForm();
	}
	
	@ModelAttribute
	public OtherMonthForm setUpOtherMonthForm() {
		return new OtherMonthForm();
	}

	@RequestMapping("")
	public String home(Model model) {
		
		Calendar calendar = Calendar.getInstance();
		
		calendar.set(Calendar.DATE, 1);
		
		Integer year = calendar.get(Calendar.YEAR);
		Integer month = calendar.get(Calendar.MONTH) + 1;
		
		Integer beforeBlank = calendar.get(Calendar.DAY_OF_WEEK)-1;
		Integer lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		
		List<Day>dayList1 = new ArrayList<>();
		List<Day>dayList2 = new ArrayList<>();
		List<Day>dayList3 = new ArrayList<>();
		List<Day>dayList4 = new ArrayList<>();
		List<Day>dayList5 = new ArrayList<>();
		List<Day>dayList6 = new ArrayList<>();
		
		for (int i = 0; i < 42; i++) {
			String str = "0";
			String dayString = "0";
			
			if (i >= beforeBlank && i + 1 - beforeBlank <= lastDay) {
				str = String.valueOf( i + 1 - beforeBlank);
				dayString = String.valueOf( i + 1 - beforeBlank);
			}
			
			if (Integer.parseInt(str) < 10) {
				dayString = "0"+str;
			}
			String datetime = String.valueOf(year)+"-"+String.valueOf(month)+"-"+dayString;
			
			List<Todo>todoList = todoMapper.searchListByDate(datetime);
			
			Day day = new Day();
			day.setDay(str);
			day.setTodoList(todoList);
			
			if (i < 7) {
				dayList1.add(day);
			}else if (i < 14) {
				dayList2.add(day);
			}else if (i < 21) {
				dayList3.add(day);
			}else if (i < 28) {
				dayList4.add(day);
			}else if(i < 35){
				dayList5.add(day);
			}else {
				dayList6.add(day);
			}
		}
		
		model.addAttribute("year", year);
		model.addAttribute("month", month);
		model.addAttribute("dayList1", dayList1);
		model.addAttribute("dayList2", dayList2);
		model.addAttribute("dayList3", dayList3);
		model.addAttribute("dayList4", dayList4);
		model.addAttribute("dayList5", dayList5);
		
		if ((beforeBlank == 5 && lastDay >= 31)||(beforeBlank == 6 && lastDay >= 30)) {
			model.addAttribute("dayList6", dayList6);
		}
		
		return "home";
	}
	
	@RequestMapping("/otherMonth")
	public String otherMonth(OtherMonthForm form, Model model) {
		
		Integer setYear = Integer.parseInt(form.getYear());
		Integer setMonth =Integer.parseInt(form.getMonth());
		
		Calendar calendar = Calendar.getInstance();
		
		calendar.set(Calendar.YEAR, setYear);
		calendar.set(Calendar.MONTH, setMonth - 1);
		calendar.set(Calendar.DATE, 1);
		
		calendar.add(Calendar.MONTH, Integer.parseInt(form.getAdd()));
		
		Integer year = calendar.get(Calendar.YEAR);
		Integer month = calendar.get(Calendar.MONTH) + 1;
		
		Integer beforeBlank = calendar.get(Calendar.DAY_OF_WEEK) - 1;
		Integer lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		
		List<Day>dayList1 = new ArrayList<>();
		List<Day>dayList2 = new ArrayList<>();
		List<Day>dayList3 = new ArrayList<>();
		List<Day>dayList4 = new ArrayList<>();
		List<Day>dayList5 = new ArrayList<>();
		List<Day>dayList6 = new ArrayList<>();
		
		for (int i = 0; i < 42; i++) {
			String str = "0";
			String dayString = "0";
			
			if (i >= beforeBlank && i + 1 - beforeBlank <= lastDay) {
				str = String.valueOf( i + 1 - beforeBlank);
				dayString = String.valueOf( i + 1 - beforeBlank);
			}
			
			if (Integer.parseInt(str) < 10) {
				dayString = "0"+str;
			}
			String datetime = String.valueOf(year)+"-"+String.valueOf(month)+"-"+dayString;
			
			List<Todo>todoList = todoMapper.searchListByDate(datetime);
			
			Day day = new Day();
			day.setDay(str);
			day.setTodoList(todoList);
			
			if (i < 7) {
				dayList1.add(day);
			}else if (i < 14) {
				dayList2.add(day);
			}else if (i < 21) {
				dayList3.add(day);
			}else if (i < 28) {
				dayList4.add(day);
			}else if(i < 35){
				dayList5.add(day);
			}else {
				dayList6.add(day);
			}
		}
		
		model.addAttribute("year", year);
		model.addAttribute("month", month);
		model.addAttribute("dayList1", dayList1);
		model.addAttribute("dayList2", dayList2);
		model.addAttribute("dayList3", dayList3);
		model.addAttribute("dayList4", dayList4);
		model.addAttribute("dayList5", dayList5);
		
		if ((beforeBlank == 5 && lastDay >= 31)||(beforeBlank == 6 && lastDay >= 30)) {
			model.addAttribute("dayList6", dayList6);
		}
		
		return "home";
	}
}
