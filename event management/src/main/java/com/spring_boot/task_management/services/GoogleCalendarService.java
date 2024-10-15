package com.spring_boot.task_management.services;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.time.ZoneId;
import java.util.Collections;

@Service
public class GoogleCalendarService {
    private static final String APPLICATION_NAME = "Task Management App";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final HttpTransport HTTP_TRANSPORT;

    private static final String TOKENS_DIRECTORY_PATH = System.getProperty("user.home") + "/tokens";
    private static final Logger logger = LoggerFactory.getLogger(GoogleCalendarService.class);

    static {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        } catch (GeneralSecurityException | IOException e) {
            logger.error("Failed to create HTTP transport", e);
            throw new RuntimeException(e);
        }
    }

    private final Calendar calendarService;

    public GoogleCalendarService() throws GeneralSecurityException, IOException {
        this.calendarService = getCalendarService();
    }

    private Calendar getCalendarService() throws IOException {
        try (InputStream in = new ClassPathResource("client_secret.json").getInputStream()) {
            GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
            GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                    HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, Collections.singleton(CalendarScopes.CALENDAR))
                    .setDataStoreFactory(new FileDataStoreFactory(new File(TOKENS_DIRECTORY_PATH)))
                    .setAccessType("offline")
                    .build();

            LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8080).build();
            Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");

            return new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                    .setApplicationName(APPLICATION_NAME)
                    .build();
        } catch (IOException e) {
            logger.error("Error loading secrets file: {}", e.getMessage());
            throw e;
        }
    }

    public String createGoogleCalendarEvent(com.spring_boot.task_management.entity.Event event) throws IOException {
        Event googleEvent = new Event()
                .setSummary(event.getSummary())
                .setLocation(event.getLocation())
                .setDescription(event.getDescription());

        DateTime startDateTime = new DateTime(event.getStartTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        googleEvent.setStart(new EventDateTime().setDateTime(startDateTime).setTimeZone("Africa/Cairo"));

        DateTime endDateTime = new DateTime(event.getEndTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        googleEvent.setEnd(new EventDateTime().setDateTime(endDateTime).setTimeZone("Africa/Cairo"));

        Event createdEvent = calendarService.events().insert("primary", googleEvent).execute();
        logger.info("Event created: {}", createdEvent.getHtmlLink());

        return createdEvent.getId();
    }

    public void deleteGoogleCalendarEvent(String googleId) throws IOException {
        String calendarId = "primary";
        calendarService.events().delete(calendarId, googleId).execute();
        logger.info("Event deleted: {}", googleId);
    }

    public com.spring_boot.task_management.entity.Event updateGoogleCalendarEvent(com.spring_boot.task_management.entity.Event event) throws IOException {
        if (event.getGoogleId() == null) {
            throw new IllegalArgumentException("Google Event ID is missing for this event.");
        }

        Event googleEvent = calendarService.events().get("primary", event.getGoogleId()).execute();

        googleEvent.setSummary(event.getSummary())
                .setLocation(event.getLocation())
                .setDescription(event.getDescription());

        DateTime startDateTime = new DateTime(event.getStartTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        googleEvent.setStart(new EventDateTime().setDateTime(startDateTime).setTimeZone("Africa/Cairo"));

        DateTime endDateTime = new DateTime(event.getEndTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        googleEvent.setEnd(new EventDateTime().setDateTime(endDateTime).setTimeZone("Africa/Cairo"));

        calendarService.events().update("primary", googleEvent.getId(), googleEvent).execute();
        logger.info("Event updated: {}", googleEvent.getHtmlLink());

        return event;
    }
}
