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

/**
 * @author ootomokenji
 *
 */
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

	/**
	 * home画面を表示する
	 * 
	 * @param model
	 * @return
	 * @throws IOException
	 * @throws GeneralSecurityException
	 */
	@RequestMapping("")
	public String home(Model model) throws IOException, GeneralSecurityException {
		
		//カレンダーに現在の月の1日を設定する
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DATE, 1);
		
//		年、月、前空白の数、最後の日をそれぞれ取得
		Integer year = calendar.get(Calendar.YEAR);
		Integer month = calendar.get(Calendar.MONTH) + 1;
		Integer beforeBlank = calendar.get(Calendar.DAY_OF_WEEK)-1;
		Integer lastDayInteger = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		
//		（2021-12）のような今の年月の文字列を定義し、予定リストが存在するか判定
		String searchMonth = String.valueOf(year)+"-"+String.valueOf(month);
		List<Todo> searchList = calenderService.searchListByMonth(searchMonth);
		
//		月の文字列を取得
		String setCalMonth = String.valueOf(month);
//		一桁の場合、"09"のようにする
		if (month < 10) {
			setCalMonth = "0" + setCalMonth;
		}
//		予定を取得する範囲をRFC 3339の形式で定義
		String firstDay = String.valueOf(year)+"-"+setCalMonth+"-01T00:00:00+09:00";
		String lastDay = String.valueOf(year)+"-"+setCalMonth+"-"+String.valueOf(lastDayInteger)+"T23:59:59+09:00";
//		予定リストが空だった場合
		if (searchList.isEmpty()) {

//			GoogleCarendarから祝日、自分の予定、を取得
			List<List<Event>> items = googleCalendarService.getEvent(firstDay,lastDay);
			List<Event> holidayList = items.get(0);
			List<Event> eventList = items.get(1);
			
//			祝日リストが空の場合
			if (holidayList.isEmpty()) {
				System.out.println("祝日ないです");
//			祝日が存在した場合
			}else {
//				祝日をDBに追加
				for (Event event : holidayList) {
					Todo todo = new Todo();
					todo.setTitle(event.getSummary());
					todo.setContents(event.getCreator().getDisplayName());
					todo.setDate(event.getStart().getDate().toString()+" 00:00:00");
					calenderService.addTodo(todo);
				}
			}
//			自分の予定が空の場合
			if (eventList.isEmpty()) {
				System.out.println("予定ないです");
//			自分の予定が存在した場合
			}else {
//				自分の予定をDBに追加
				for (Event event : eventList) {
					Todo todo = new Todo();
					todo.setTitle(event.getSummary());
					todo.setContents(event.getDescription());
					todo.setDate(event.getStart().getDateTime().toString());
					calenderService.addTodo(todo);
				}
			}
//		予定リストに予定が存在した場合
		}else {
//			最終更新時刻を取得
			String updateTime = searchList.get(0).getUpdateTime();
//			RFC 3339の形式に変換
			updateTime = updateTime.replace(" ", "T")+"+09:00";
//			最終更新時刻以降に追加・更新された予定を取得
			List<List<Event>> items = googleCalendarService.eventUpdate(firstDay,lastDay,updateTime);
			List<Event> holidayList = items.get(0);
			List<Event> eventList = items.get(1);
//			祝日リストが空だったら
			if (holidayList.isEmpty()) {
				System.out.println("祝日追加ないです");
//			祝日リストの中身が存在したら
			}else {
//				祝日をDBに追加
				for (Event event : holidayList) {
					Todo todo = new Todo();
					todo.setTitle(event.getSummary());
					todo.setContents(event.getCreator().getDisplayName());
					todo.setDate(event.getStart().getDate().toString()+" 00:00:00");
					calenderService.addTodo(todo);
				}
			}
//			予定リストが空だったら
			if (eventList.isEmpty()) {
				System.out.println("予定追加ないです");
//			予定リストの中身が存在したら
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
//		6週間分の日付リスト
		List<Day>dayList1 = new ArrayList<>();
		List<Day>dayList2 = new ArrayList<>();
		List<Day>dayList3 = new ArrayList<>();
		List<Day>dayList4 = new ArrayList<>();
		List<Day>dayList5 = new ArrayList<>();
		List<Day>dayList6 = new ArrayList<>();
		
//		全日分回す
		for (int i = 0; i < 42; i++) {
//			日付の初期値を"0"に定義
			String str = "0";
			String dayString = "0";
//			存在する日であれば、その日を設定
			if (i >= beforeBlank && i + 1 - beforeBlank <= lastDayInteger) {
				str = String.valueOf( i + 1 - beforeBlank);
				dayString = String.valueOf( i + 1 - beforeBlank);
			}
//			検索用の日付が１桁だった場合、"09"のように修正
			if (Integer.parseInt(str) < 10) {
				dayString = "0"+str;
			}
//			日付の文字列（ex.2021-02-09）を定義し、予定を検索
			String datetime = String.valueOf(year)+"-"+String.valueOf(month)+"-"+dayString;
			List<Todo>todoList = todoMapper.searchListByDate(datetime);
//			日付ドメインをインスタンス化し、日付と予定をセット
			Day day = new Day();
			day.setDay(str);
			day.setTodoList(todoList);
//			その週ごとに入れる
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
//		情報を格納
		model.addAttribute("year", year);
		model.addAttribute("month", month);
		model.addAttribute("dayList1", dayList1);
		model.addAttribute("dayList2", dayList2);
		model.addAttribute("dayList3", dayList3);
		model.addAttribute("dayList4", dayList4);
		model.addAttribute("dayList5", dayList5);
//		６週目が存在する場合、格納
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

		String firstDay = String.valueOf(year)+"-"+setCalMonth+"-01T00:00:00+09:00";
		String lastDay = String.valueOf(year)+"-"+setCalMonth+"-"+String.valueOf(lastDayInteger)+"T23:59:59+09:00";
		
		if (searchList.isEmpty()) {

			List<List<Event>> items = googleCalendarService.getEvent(firstDay,lastDay);
			List<Event> holidayList = items.get(0);
			List<Event> eventList = items.get(1);
			
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

			String updateTime = searchList.get(0).getUpdateTime();
			updateTime = updateTime.replace(" ", "T")+"+09:00";
			List<List<Event>> items = googleCalendarService.eventUpdate(firstDay,lastDay,updateTime);
			List<Event> holidayList = items.get(0);
			List<Event> eventList = items.get(1);
			
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
