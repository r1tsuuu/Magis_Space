# Magis Space

A REST API for finding vacant study rooms on the Ateneo campus. Students report whether a room is available or occupied, and other students can filter rooms by building, capacity, and amenities. Each report is timestamped and gets marked unverified after an hour. If you subscribe to a room, you get a notification when it goes from occupied to available.

This is the finals project for CSCI 181.05.

## Stack

- Java 8
- Spring Boot 2 (dependency injection, scheduling)
- Jersey (REST endpoints)
- Spring Data JPA with Hibernate
- MySQL (XAMPP / phpMyAdmin)

## Prerequisites

- Eclipse with the class `SpringBootDependencies` project in the same workspace. `.classpath` references it for all the Spring, Jersey, Hibernate, and MySQL jars.
- XAMPP with MySQL running on port 3306, user `root`, no password.

## Setup

1. Clone the repo and import it into Eclipse (File > Import > Existing Projects into Workspace).
2. Start MySQL from the XAMPP control panel.
3. Run `src/app/SpringBootApp.java` as a Java application.

The app creates the `magis_space_db` database and all tables on first run. It also seeds 5 amenities, 3 students, 6 rooms, and one subscription (student 2 watches room 1). You can check the tables in phpMyAdmin.

## Configuration

Settings are in `config/application.properties`.

| Property | Default | Notes |
|---|---|---|
| `server.port` | `9999` | |
| `spring.jersey.application-path` | `/api` | Prefix for every endpoint |
| `spring.datasource.url` | `jdbc:mysql://localhost:3306/magis_space_db?...` | Change if your MySQL runs elsewhere |
| `spring.datasource.username` / `password` | `root` / empty | XAMPP defaults |
| `notification.api.url` | `http://localhost:8080/` | Mock SMS service, not wired up yet |

## Endpoints

Base URL: `http://localhost:9999/api`

| Method | Path | Description |
|---|---|---|
| GET | `/rooms?building=&minCapacity=&amenity=` | List rooms. All filters are optional. |
| GET | `/rooms/{id}` | One room with its amenities |
| POST | `/rooms` | Create a room |
| GET | `/availability?roomId=&status=` | Reports, newest first |
| POST | `/availability` | Report a room as `AVAILABLE` or `OCCUPIED` |
| GET | `/students` | List students |
| GET | `/students/{id}` | One student |
| POST | `/students` | Create a student |
| GET | `/notifications/{studentId}?unread=true` | A student's notifications |
| GET | `/subscriptions/{studentId}` | Rooms a student is subscribed to |
| POST | `/subscriptions` | Subscribe a student to a room |
| GET | `/amenities` | List amenities |
| GET | `/amenities/{id}` | One amenity |
| POST | `/amenities` | Create an amenity |

## Usage

Filter rooms:

```
GET http://localhost:9999/api/rooms?building=SEC-A&minCapacity=30&amenity=Projector
```

Create a room. Amenities are matched by name against the existing amenity list:

```
POST http://localhost:9999/api/rooms
Content-Type: application/json

{
  "name": "SEC-C 101",
  "building": "SEC-C",
  "floor": 1,
  "capacity": 30,
  "amenities": [{ "name": "Projector" }, { "name": "Outlets" }]
}
```

Submit a report:

```
POST http://localhost:9999/api/availability
Content-Type: application/json

{ "studentId": 1, "roomId": 1, "status": "OCCUPIED" }
```

Response (`201 Created`):

```json
{
  "id": 1,
  "studentId": 1,
  "roomId": 1,
  "status": "OCCUPIED",
  "reportedAt": "2026-11-14T18:30:00",
  "verified": true
}
```

To see a notification, report room 1 as `OCCUPIED`, then report it as `AVAILABLE`. Student 2 is subscribed to room 1 in the seed data, so `GET /api/notifications/2` will show the new notification.

## Errors

Errors come back as JSON:

```json
{ "status": 404, "error": "Not Found", "message": "Room with ID 99 does not exist." }
```

| Status | When |
|---|---|
| 400 | Invalid `status` value, like `"status": "FREE"` |
| 404 | Student, room, or amenity not found, or an unknown URL |
| 409 | Duplicate room name, student number, email, or subscription |
| 422 | A field fails validation, like a missing name or capacity below 1 |
| 500 | Anything unexpected. The response body is the stack trace. |

## Project layout

```
config/application.properties   database and server settings
src/app/SpringBootApp.java       entry point
src/app/entity/                  JPA entities
src/app/repository/              Spring Data repositories
src/app/service/                 business logic
src/app/rest/controllers/        Jersey resources
src/app/rest/errorhandlers/      exception to JSON mapping
src/app/components/              seed data and the report expiry scheduler
```

## Not done yet

- Notifications are saved and printed to the console. Sending them through the mock SMS API with Retrofit is still a TODO in `NotificationService`.
- `ClassSchedule` has a table but no endpoints.
- Room map and lat/long coordinates are empty in the seed data.
