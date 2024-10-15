# Event Management App

## Project Overview

This is an Event Management Application built with **Spring Boot** that allows users to manage events both **offline** and **online**. It includes features to add, edit, delete, and synchronize events with **Google Calendar**. The application is designed to work seamlessly in environments with intermittent connectivity and offers efficient event management with caching support.

## Features

- **Offline Event Management**: Users can add, edit, and delete events even when offline.
- **Online Synchronization**: Once online, events are automatically synced with the server.
- **Google Calendar Integration**: Events are synchronized with Google Calendar, allowing users to manage their schedules efficiently.
- **Caching**: Implemented caching to optimize performance and reduce unnecessary API calls.
- **SQL Database**: The app uses a SQL database to store events and event data from the Google Calendar API.

## Technologies Used

- **Spring Boot**: The backbone of the application.
- **Google Calendar API**: For integrating event management with Google Calendar.
- **Spring Cache**: Used to cache event data for performance optimization.
- **SQL Database**: Event data is stored in a relational database.
- **Thymeleaf**: Used for front-end rendering.
- **JPA (Java Persistence API)**: To manage database operations.


