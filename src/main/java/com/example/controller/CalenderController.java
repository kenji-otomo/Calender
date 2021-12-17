package com.example.controller;

import java.io.IOException;
import java.security.GeneralSecurityException;
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
import com.example.service.CalenderService;
import com.example.service.GoogleCalendarService;
import com.google.api.services.calendar.model.Event;

@Controller
@RequestMapping("/calender")
public class CalenderController {
	
	@Autowired
	private TodoMapper todoMapper;
	
	@Autowired
	private GoogleCalendarService googleCalendarService;
	
	@Autowired
	private CalenderService calenderService;
	
	@ModelAttribute
	public TodoForm setUpTodoForm() {
		return new TodoForm();
	}
	
	@ModelAttribute
	public OtherMonthForm setUpOtherMonthForm() {
		return new OtherMonthForm();
	}

	@RequestMapping("")
	public String home(Model model) throws IOException, GeneralSecurityException {
		
		Calendar calendar = Calendar.getInstance();
		
		calendar.set(Calendar.DATE, 1);
		
		Integer year = calendar.get(Calendar.YEAR);
		
		Integer month = calendar.get(Calendar.MONTH) + 1;
		
		Integer beforeBlank = calendar.get(Calendar.DAY_OF_WEEK)-1;
		Integer lastDayInteger = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		
		

		String searchMonth = String.valueOf(year)+"-"+String.valueOf(month);
		
		List<Todo> searchList = calenderService.searchListByMonth(searchMonth);
		
		if (searchList.isEmpty()) {

			String setCalMonth = String.valueOf(month);
			if (month < 10) {
				setCalMonth = "0" + setCalMonth;
			}
			
			String firstDay = String.valueOf(year)+"-"+setCalMonth+"-01T00:00:00+09:00";
			String lastDay = String.valueOf(year)+"-"+setCalMonth+"-"+String.valueOf(lastDayInteger)+"T23:59:59+09:00";
			
			List<Event> holidayList = googleCalendarService.holiday(firstDay,lastDay);
			List<Event> eventList = googleCalendarService.myEvent(firstDay,lastDay);
			
			if (holidayList.isEmpty()) {
				System.out.println("祝日ないです");
			}else {
				for (Event event : holidayList) {
					Todo todo = new Todo();
					todo.setTitle(event.getSummary());
					todo.setContents(event.getCreator().getDisplayName());
					todo.setDate(event.getStart().getDate().toString()+" 00:00:00");
					calenderService.addTodo(todo);
				}
			}
			
			if (eventList.isEmpty()) {
				System.out.println("予定ないです");
			}else {
				for (Event event : eventList) {
					Todo todo = new Todo();
					todo.setTitle(event.getSummary());
					todo.setContents(event.getDescription());
					todo.setDate(event.getStart().getDateTime().toString());
					calenderService.addTodo(todo);
				}
			}

		}else {

			String setCalMonth = String.valueOf(month);
			if (month < 10) {
				setCalMonth = "0" + setCalMonth;
			}
			
			String firstDay = String.valueOf(year)+"-"+setCalMonth+"-01T00:00:00+09:00";
			String lastDay = String.valueOf(year)+"-"+setCalMonth+"-"+String.valueOf(lastDayInteger)+"T23:59:59+09:00";
			String updateTime = searchList.get(0).getUpdateTime();
			updateTime = updateTime.replace(" ", "T")+"+09:00";
			List<Event> holidayList = googleCalendarService.holidayUpdate(firstDay,lastDay,updateTime);
			List<Event> eventList = googleCalendarService.myEventUpdate(firstDay,lastDay,updateTime);
			
			if (holidayList.isEmpty()) {
				System.out.println("祝日追加ないです");
			}else {
				for (Event event : holidayList) {
					Todo todo = new Todo();
					todo.setTitle(event.getSummary());
					todo.setContents(event.getCreator().getDisplayName());
					todo.setDate(event.getStart().getDate().toString()+" 00:00:00");
					calenderService.addTodo(todo);
				}
			}
			
			if (eventList.isEmpty()) {
				System.out.println("予定追加ないです");
			}else {
				for (Event event : eventList) {
					Todo todo = new Todo();
					todo.setTitle(event.getSummary());
					todo.setContents(event.getDescription());
					todo.setDate(event.getStart().getDateTime().toString());
					calenderService.addTodo(todo);
				}
			}
		}
		
		List<Day>dayList1 = new ArrayList<>();
		List<Day>dayList2 = new ArrayList<>();
		List<Day>dayList3 = new ArrayList<>();
		List<Day>dayList4 = new ArrayList<>();
		List<Day>dayList5 = new ArrayList<>();
		List<Day>dayList6 = new ArrayList<>();
		
		for (int i = 0; i < 42; i++) {
			String str = "0";
			String dayString = "0";
			
			if (i >= beforeBlank && i + 1 - beforeBlank <= lastDayInteger) {
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
		
		if ((beforeBlank == 5 && lastDayInteger >= 31)||(beforeBlank == 6 && lastDayInteger >= 30)) {
			model.addAttribute("dayList6", dayList6);
		}
				return "home";
	}
	
	@RequestMapping("/otherMonth")
	public String otherMonth(OtherMonthForm form, Model model) throws IOException, GeneralSecurityException {
		
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
		Integer lastDayInteger = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		
		

		String setCalMonth = String.valueOf(month);
		if (month < 10) {
			setCalMonth = "0" + setCalMonth;
		}
		String searchMonth = String.valueOf(year)+"-"+setCalMonth;
		List<Todo> searchList = calenderService.searchListByMonth(searchMonth);
		
		if (searchList.isEmpty()) {

			String firstDay = String.valueOf(year)+"-"+setCalMonth+"-01T00:00:00+09:00";
			String lastDay = String.valueOf(year)+"-"+setCalMonth+"-"+String.valueOf(lastDayInteger)+"T23:59:59+09:00";
			
			List<Event> holidayList = googleCalendarService.holiday(firstDay,lastDay);
			List<Event> eventList = googleCalendarService.myEvent(firstDay,lastDay);
			
			if (holidayList.isEmpty()) {
				System.out.println("祝日ないです");
			}else {
				for (Event event : holidayList) {
					Todo todo = new Todo();
					todo.setTitle(event.getSummary());
					todo.setContents(event.getCreator().getDisplayName());
					todo.setDate(event.getStart().getDate().toString()+" 00:00:00");
					calenderService.addTodo(todo);
				}
			}
			
			if (eventList.isEmpty()) {
				System.out.println("予定ないです");
			}else {
				for (Event event : eventList) {
					Todo todo = new Todo();
					todo.setTitle(event.getSummary());
					todo.setContents(event.getDescription());
					todo.setDate(event.getStart().getDateTime().toString());
					calenderService.addTodo(todo);
				}
			}
					
			}else {

			String firstDay = String.valueOf(year)+"-"+setCalMonth+"-01T00:00:00+09:00";
			String lastDay = String.valueOf(year)+"-"+setCalMonth+"-"+String.valueOf(lastDayInteger)+"T23:59:59+09:00";
			String updateTime = searchList.get(0).getUpdateTime();
			updateTime = updateTime.replace(" ", "T")+"+09:00";
			List<Event> holidayList = googleCalendarService.holidayUpdate(firstDay,lastDay,updateTime);
			List<Event> eventList = googleCalendarService.myEventUpdate(firstDay,lastDay,updateTime);
			
			if (holidayList.isEmpty()) {
				System.out.println("祝日追加ないです");
			}else {
				for (Event event : holidayList) {
					Todo todo = new Todo();
					todo.setTitle(event.getSummary());
					todo.setContents(event.getCreator().getDisplayName());
					todo.setDate(event.getStart().getDate().toString()+" 00:00:00");
					calenderService.addTodo(todo);
				}
			}
			
			if (eventList.isEmpty()) {
				System.out.println("予定追加ないです");
			}else {
				for (Event event : eventList) {
					Todo todo = new Todo();
					todo.setTitle(event.getSummary());
					todo.setContents(event.getDescription());
					todo.setDate(event.getStart().getDateTime().toString());
					calenderService.addTodo(todo);
				}
			}
		}
		
		List<Day>dayList1 = new ArrayList<>();
		List<Day>dayList2 = new ArrayList<>();
		List<Day>dayList3 = new ArrayList<>();
		List<Day>dayList4 = new ArrayList<>();
		List<Day>dayList5 = new ArrayList<>();
		List<Day>dayList6 = new ArrayList<>();
		
		for (int i = 0; i < 42; i++) {
			String str = "0";
			String dayString = "0";
			
			if (i >= beforeBlank && i + 1 - beforeBlank <= lastDayInteger) {
				str = String.valueOf( i + 1 - beforeBlank);
				dayString = String.valueOf( i + 1 - beforeBlank);
			}
			
			if (Integer.parseInt(str) < 10) {
				dayString = "0"+str;
			}
			String datetime = String.valueOf(year)+"-"+setCalMonth+"-"+dayString;
			
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
		
		if ((beforeBlank == 5 && lastDayInteger >= 31)||(beforeBlank == 6 && lastDayInteger >= 30)) {
			model.addAttribute("dayList6", dayList6);
		}
		return "home";
		
		}
		
}
