package com.example.service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;

@Service
@Transactional
public class GoogleCalendarService {

	  private static final String APPLICATION_NAME = "CalendarQuickstart";
	  private static final JsonFactory JSON_FACTORY = GsonFactory
	      .getDefaultInstance();
	  private static final String CREDENTIALS_FOLDER = "credentials";

	  /**
	   * Global instance of the scopes required by this quickstart. If modifying
	   * these scopes, delete your previously saved credentials/ folder.
	   */
	  private static final List<String> SCOPES = Collections
	      .singletonList(CalendarScopes.CALENDAR_READONLY);
	  private static final String CLIENT_SECRET_DIR = "client_secret.json";

	  /**
	   * Creates an authorized Credential object.
	   * 
	   * @param HTTP_TRANSPORT
	   *          The network HTTP Transport.
	   * @return An authorized Credential object.
	   * @throws IOException
	   *           If there is no client_secret.
	   */
	  private Credential getCredentials(
	      final NetHttpTransport HTTP_TRANSPORT) throws IOException {
	    // Load client secrets.
	    URL url = ClassLoader.getSystemResource(CLIENT_SECRET_DIR);
	    URI uri = URI.create(url.toString());
	    Path path = Paths.get(uri);
	    GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
	        new InputStreamReader(new FileInputStream(path.toString())));

	    // Build flow and trigger user authorization request.
	    GoogleAuthorizationCodeFlow flow =
	    		new GoogleAuthorizationCodeFlow
	    		.Builder(
	        HTTP_TRANSPORT,
	        JSON_FACTORY, 
	        clientSecrets,
	        SCOPES)
	            .setDataStoreFactory(
	                new FileDataStoreFactory(new java.io.File(CREDENTIALS_FOLDER)))
	            .setAccessType("offline").build();
	    return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver())
	        .authorize("user");
	  }

	  
	  
	  public List<Event> holiday(String firstDay,String lastDay)
	      throws IOException, GeneralSecurityException {
	    // Build a new authorized API client service.
	    final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport
	        .newTrustedTransport();
	    Calendar service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY,
	        getCredentials(HTTP_TRANSPORT)).setApplicationName(APPLICATION_NAME)
	            .build();

	    // List the next 10 events from the primary calendar.
	    DateTime firstday = new DateTime(firstDay);
	    DateTime lastday = new DateTime(lastDay);
	    
	    Events events = service.events().list("japanese__ja@holiday.calendar.google.com")
	        .setTimeMin(firstday).setTimeMax(lastday)
	        .setOrderBy("startTime").setSingleEvents(true)
	        .execute();
	    List<Event> items = events.getItems();
	    return items;
	  }
	  
	  public List<Event> holidayUpdate(String firstDay,String lastDay,String updateTime)
			  throws IOException, GeneralSecurityException {
		  // Build a new authorized API client service.
		  final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport
				  .newTrustedTransport();
		  Calendar service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY,
				  getCredentials(HTTP_TRANSPORT)).setApplicationName(APPLICATION_NAME)
				  .build();
		  System.out.println(service.getBaseUrl());
		  // List the next 10 events from the primary calendar.
		  DateTime firstday = new DateTime(firstDay);
		  DateTime lastday = new DateTime(lastDay);
		  DateTime updatetime = new DateTime(updateTime);
		  
		  //?fields=updated,summary,description,creator(displayName),start(date,dateTime)
		  
		  Events events = service.events().list("japanese__ja@holiday.calendar.google.com")
				
				  .setTimeMin(firstday).setTimeMax(lastday).setUpdatedMin(updatetime)
				  .setOrderBy("startTime").setSingleEvents(true)
				  .execute();
		  List<Event> items = events.getItems();
		  return items;
	  }
	  
	  public List<Event> myEvent(String firstDay,String lastDay)
			  throws IOException, GeneralSecurityException {
		  // Build a new authorized API client service.
		  final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport
				  .newTrustedTransport();
		  Calendar service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY,
				  getCredentials(HTTP_TRANSPORT)).setApplicationName(APPLICATION_NAME)
				  .build();
		  
		  // List the next 10 events from the primary calendar.
		  DateTime firstday = new DateTime(firstDay);
		  DateTime lastday = new DateTime(lastDay);
		    
		  Events myEvents = service.events().list("primary")
				  .setTimeMin(firstday).setTimeMax(lastday)
				  .setOrderBy("startTime").setSingleEvents(true)
				  .execute();
		  List<Event> myItems = myEvents.getItems();
		  
		  return myItems;
	  }

	  public List<Event> myEventUpdate(String firstDay,String lastDay,String updateTime)
			  throws IOException, GeneralSecurityException {
		  // Build a new authorized API client service.
		  final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport
				  .newTrustedTransport();
		  Calendar service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY,
				  getCredentials(HTTP_TRANSPORT)).setApplicationName(APPLICATION_NAME)
				  .build();
		  
		  // List the next 10 events from the primary calendar.
		  DateTime firstday = new DateTime(firstDay);
		  DateTime lastday = new DateTime(lastDay);
		  DateTime updatetime = new DateTime(updateTime);
		  
		  Events myEvents = service.events().list("primary")
				  .setTimeMin(firstday).setTimeMax(lastday).setUpdatedMin(updatetime)
				  .setOrderBy("startTime").setSingleEvents(true)
			//	  .setFields("summary,description,updated,start")
				  .execute();
		  List<Event> myItems = myEvents.getItems();
		  return myItems;
	  }
	  
}
