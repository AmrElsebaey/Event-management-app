package com.spring_boot.task_management.services;

import com.spring_boot.task_management.entity.Event;
import com.spring_boot.task_management.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final GoogleCalendarService googleCalendarService;

    @Autowired
    public EventService(EventRepository eventRepository, GoogleCalendarService googleCalendarService) {
        this.eventRepository = eventRepository;
        this.googleCalendarService = googleCalendarService;
    }

    @Cacheable("events")
    public List<Event> getAllEvents() throws IOException {
        return eventRepository.findAll();
    }

    @CacheEvict(value = "events", allEntries = true)
    public Event createEvent(Event event) throws IOException {
        String googleEventId = googleCalendarService.createGoogleCalendarEvent(event);
        event.setGoogleId(googleEventId);
        return eventRepository.save(event);
    }

    @CacheEvict(value = "events", allEntries = true)
    public void deleteEvent(Long id) {
        Event event = eventRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("Event not found with id: " + id));

        if (event.getGoogleId() != null) {
            try {
                googleCalendarService.deleteGoogleCalendarEvent(event.getGoogleId());
            } catch (IOException e) {
                throw new RuntimeException("Failed to delete event from Google Calendar: " + e.getMessage());
            }
        }
        eventRepository.deleteById(id);
    }

    @CacheEvict(value = "events", allEntries = true)
    public Event updateEvent(Long id, Event updatedEventDetails) throws IOException {
        Event existingEvent = eventRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("Event not found with id: " + id));

        existingEvent.setSummary(updatedEventDetails.getSummary());
        existingEvent.setLocation(updatedEventDetails.getLocation());
        existingEvent.setDescription(updatedEventDetails.getDescription());
        existingEvent.setStartTime(updatedEventDetails.getStartTime());
        existingEvent.setEndTime(updatedEventDetails.getEndTime());

        Event savedEvent = eventRepository.save(existingEvent);

        if (savedEvent.getGoogleId() != null && !savedEvent.getGoogleId().isEmpty()) {
            googleCalendarService.updateGoogleCalendarEvent(savedEvent);
        }

        return savedEvent;
    }

    public Event getEventById(Long id) {
        return eventRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("Event not found with id: " + id));
    }
}
