package com.spring_boot.task_management.rest;

import com.spring_boot.task_management.entity.Event;
import com.spring_boot.task_management.services.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.IOException;

@Controller
public class EventViewController {


    private EventService eventService;

    @Autowired
    public EventViewController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/events")
    public String getAllEvents(Model model) throws IOException {
        model.addAttribute("events", eventService.getAllEvents());
        return "events";
    }

    @GetMapping("/events/create")
    public String createEventForm(Model model) {
        model.addAttribute("event", new Event());
        return "create_event";
    }

    @PostMapping("/events")
    public String createEvent(@ModelAttribute Event event) throws IOException {
        eventService.createEvent(event);
        return "redirect:/events";
    }

    @GetMapping("/events/update/{id}")
    public String updateEventForm(@PathVariable Long id, Model model) throws IOException {
        Event event = eventService.getEventById(id);
        model.addAttribute("event", event);
        return "update_event";
    }

    @PostMapping("/events/{id}")
    public String updateEvent(@PathVariable Long id, @ModelAttribute Event updatedEvent) throws IOException {
        eventService.updateEvent(id, updatedEvent);
        return "redirect:/events";
    }

    @PostMapping("/events/delete/{id}")
    public String deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return "redirect:/events";
    }


}
