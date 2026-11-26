# Magis Space

A REST API for finding vacant study rooms on the Ateneo campus. Students report whether a room is vacant or occupied. A student can subscribe to a room, and when someone reports that room vacant, the subscriber gets a notification: it is saved in the database and sent as an SMS to a mock Twilio service through Retrofit.

This is the finals project for CSCI 181.05. It covers the assignment requirements and nothing more.

## Requirements and where they are in the code

| Requirement | Where |
|---|---|
| Jersey REST, JSON | `src/app/rest/controllers/` (JAX-RS `@Path`, `@GET`, `@POST`, `@DELETE`), registered in `src/app/config/JerseyConfig.java` |
| Spring-based annotations | `@Component` controllers, `@Service` services, `@Autowired`, `@Transactional`, `@Configuration`, `@Bean` |
| JPA / Spring Data repositories | `src/app/entity/`, `src/app/repository/` |
| Javax validation | Constraints on `src/app/rest/dto/*Request.java` and on the entities (`@NotNull`, `@NotBlank`, `@Size`, `@Min`, `@Email`, `@Pattern`); `@Valid` on every POST body |
| Retrofit (fake Twilio) | `src/app/notification/SmsClient.java`, `SmsClientConfig.java`; used by `NotificationService`; mock server in `src/mock/MockSmsServer.java` |
| Scheduling | `src/app/components/ReportExpiryScheduler.java` (`@Scheduled`), enabled by `@EnableScheduling` in `SpringBootApp` |
| At least 5 entity types, justified | Six entities, see below |
| `@PostConstruct` seed | `src/app/components/DataLoader.java` |
| GET and POST, create and read for all entities | Every controller has GET list, GET by id and POST |
| Create and delete rows at runtime | `POST` and `DELETE` on every entity; nothing needs the seed data |
| Controllers forward to a component with `@Autowired` | Each controller has one `@Autowired` service and only passes data in and out |
| Postman definition | `postman/MagisSpace.postman_collection.json` |

### Entities

| Entity | Fields | Why it exists |
|---|---|---|
| Student | id, name, email (unique), phone | The user who reports rooms and receives SMS |
| Room | id, name, building, floor, capacity, amenities | The thing being reported. Name is unique within a building |
| Amenity | id, name (unique) | A feature a room can have (Outlets, Aircon, Whiteboard). Many-to-many with Room |
| Availability | id, studentId, roomId, occupied, reportedAt, verified | One report that a room is vacant or occupied |
| Subscription | id, studentId, roomId | A student who wants to hear when a room is reported vacant. One per pair |
| Notification | id, studentId, message, sentAt | A message sent to a student |

## Stack

- Java 8
- Spring Boot 2.1 (dependency injection, scheduling)
- Jersey (REST endpoints, JSON)
- Spring Data JPA with Hibernate, javax validation
- Retrofit 2.4.0 with OkHttp 3.10.0 (client for the mock SMS service)
- MySQL (XAMPP / phpMyAdmin)

## Prerequisites

- Eclipse with the class `SpringBootDependencies` project in the same workspace. `.classpath` references it for the Spring, Jersey, Jackson, Hibernate, and MySQL jars.
- No extra jars beyond `SpringBootDependencies`. The SMS client uses OkHttp request and response bodies and Jackson, so no Retrofit converter jar is needed.
- XAMPP with MySQL running on port 3306, user `root`, no password.
- Optional: Node.js, to run the Postman collection from the command line with newman.

## Setup in Eclipse

1. Clone the repo and import it into Eclipse (File > Import > Existing Projects into Workspace).
2. Start MySQL from the XAMPP control panel.
3. Run `src/app/SpringBootApp.java` as a Java application.

The app creates the `magis_space_db` database and its tables on first run. `DataLoader` then fills each table that is empty:

- 3 amenities: Outlets, Aircon, Whiteboard.
- 5 rooms: F-204 and F-227 (Faura), SEC-A 210, CTC 102, K-301, each with amenities.
- 3 students: Juan Dela Cruz, Maria Santos, Paolo Reyes.
- 1 subscription: Juan to F-204.

Restarting does not duplicate rows. The app does not need the seed data: everything can also be created through the API.

If you ran an older version of this project against the same `magis_space_db`, drop that database in phpMyAdmin first. The `subscription_table` columns changed and `ddl-auto=update` does not remove the old ones.

To present the API, follow [DEMO.md](DEMO.md).

## Running the mock SMS server

Notifications are sent as SMS through a small mock of a Twilio-style API in `src/mock/MockSmsServer.java`. It needs only the JDK. It is in package `mock`, outside `app`, so Spring does not scan it.

In Eclipse: right-click `src/mock/MockSmsServer.java`, then Run As > Java Application. Leave its Console tab running; every SMS is printed there. The Spring Boot app has its own Console (use the "Display Selected Console" button to switch).

From a terminal (Java 8), in the project folder:

```
javac -d out src/mock/MockSmsServer.java
java -cp out mock.MockSmsServer
```

It listens on port 8080 unless you pass another port as the first argument. It accepts `POST /sms/send` with `{"to": "...", "body": "..."}`, answers `{"sid": "SM...", "status": "queued"}`, and prints each message:

```
==================== SMS #1 received 22:27:10 ====================
  To:   09171112222
  Text: F-301 (Faura) is now vacant as of 10:27 PM.
  Sid:  SMfd814ff2ae292bb2e6e53cd363517ac2
====================================================================
```

If port 8080 is already in use, it prints an error and stops. Give it another port (Run > Run Configurations > Arguments > Program arguments: `8081`) and set `notification.api.url=http://localhost:8081/` in `config/application.properties`.

The API works without the mock. If the SMS server is down, the notification is still saved and the SMS failure is only logged.

## How the vacant-report flow works

1. `POST /api/availability` with `{studentId, roomId, occupied}`. `ReportController` passes the body to `ReportService`.
2. `ReportService` saves an `Availability` row with `reportedAt` set to now and `verified` true.
3. If `occupied` is false, `ReportService` calls `NotificationService.notifySubscribers`. For every subscription to that room it saves a `Notification` ("F-204 (Faura) is now vacant as of 2:26 PM.") and sends the same text to the student's phone through `SmsClient` (Retrofit) to the mock server.
4. An SMS failure is logged and does not change the response.

Every 5 minutes `ReportExpiryScheduler` sets `verified` to false on reports older than 1 hour.

## Configuration

Settings are in `config/application.properties`.

| Property | Default | Notes |
|---|---|---|
| `server.port` | `9999` | |
| `spring.jersey.application-path` | `/api` | Prefix for every endpoint |
| `spring.datasource.url` | `jdbc:mysql://localhost:3306/magis_space_db?createDatabaseIfNotExist=true&serverTimezone=Asia/Manila` | Change if your MySQL runs elsewhere |
| `spring.datasource.username` / `password` | `root` / empty | XAMPP defaults |
| `spring.jpa.hibernate.ddl-auto` | `update` | Creates and updates tables |
| `spring.jpa.show-sql` | `false` | Set to `true` to print every SQL statement |
| `spring.jpa.open-in-view` | `false` | |
| `spring.main.allow-bean-definition-overriding` | `true` | |
| `notification.api.url` | `http://localhost:8080/` | Base URL of the mock SMS service. If empty or invalid, `http://localhost:8080/` is used. |

## Endpoints

Base URL: `http://localhost:9999/api`. Requests and responses are JSON. Every POST returns 201 with the created row. Every DELETE returns 204 with no body.

| Method | Path | Forwards to | Description |
|---|---|---|---|
| GET | `/students` | `StudentService.findAll` | List students |
| GET | `/students/{id}` | `StudentService.get` | One student |
| POST | `/students` | `StudentService.create` | Create a student |
| DELETE | `/students/{id}` | `StudentService.delete` | Delete a student with their reports, subscriptions and notifications |
| GET | `/rooms?building=` | `RoomService.findRooms` | List rooms; `building` is optional and not case sensitive |
| GET | `/rooms/{id}` | `RoomService.getRoom` | One room |
| POST | `/rooms` | `RoomService.createRoom` | Create a room |
| DELETE | `/rooms/{id}` | `RoomService.deleteRoom` | Delete a room with its reports and subscriptions |
| GET | `/amenities` | `AmenityService.findAll` | List amenities |
| GET | `/amenities/{id}` | `AmenityService.get` | One amenity |
| POST | `/amenities` | `AmenityService.create` | Create an amenity |
| DELETE | `/amenities/{id}` | `AmenityService.delete` | Delete an amenity and remove it from rooms |
| GET | `/availability?roomId=` | `ReportService.findAll` | List reports, newest first; `roomId` is optional |
| GET | `/availability/{id}` | `ReportService.get` | One report |
| POST | `/availability` | `ReportService.submitReport` | Save a report; a vacant report notifies subscribers |
| DELETE | `/availability/{id}` | `ReportService.deleteReport` | Delete a report |
| GET | `/subscriptions?studentId=` | `SubscriptionService.findAll` | List subscriptions; `studentId` is optional |
| GET | `/subscriptions/{id}` | `SubscriptionService.get` | One subscription |
| POST | `/subscriptions` | `SubscriptionService.create` | Subscribe a student to a room |
| DELETE | `/subscriptions/{id}` | `SubscriptionService.delete` | Delete a subscription |
| GET | `/notifications?studentId=` | `NotificationService.findAll` | List notifications, newest first; `studentId` is optional |
| GET | `/notifications/{id}` | `NotificationService.get` | One notification |
| POST | `/notifications` | `NotificationService.sendAndSave` | Send one SMS and save the notification |
| DELETE | `/notifications/{id}` | `NotificationService.delete` | Delete a notification |

### Request bodies

| Endpoint | Body | Rules |
|---|---|---|
| `POST /students` | `{"name": "Ana Lim", "email": "ana.lim@example.com", "phone": "09171112222"}` | name required, up to 100 characters; email required, valid, unique; phone required, `09xxxxxxxxx` or `+639xxxxxxxxx` |
| `POST /rooms` | `{"name": "F-301", "building": "Faura", "floor": 3, "capacity": 20, "amenityIds": [1, 4]}` | name up to 50 characters; building required; floor 0 or more; capacity 1 or more; `amenityIds` optional; name unique within a building |
| `POST /amenities` | `{"name": "Projector"}` | name required, up to 50 characters, unique |
| `POST /availability` | `{"studentId": 2, "roomId": 6, "occupied": false}` | all three required |
| `POST /subscriptions` | `{"studentId": 4, "roomId": 6}` | both required; one subscription per pair |
| `POST /notifications` | `{"studentId": 4, "message": "Study group at 3 PM"}` | studentId required; message required, up to 300 characters |

### Sample responses

These were recorded from a run on a fresh database.

`GET /rooms/1`

```json
{"id":1,"name":"F-204","building":"Faura","floor":2,"capacity":30,"amenities":[{"id":2,"name":"Aircon"},{"id":1,"name":"Outlets"}]}
```

`POST /availability` with `{"studentId":2,"roomId":6,"occupied":false}`

```json
{"id":1,"occupied":false,"reportedAt":"2026-11-26T22:27:10.484","verified":true,"roomId":6,"studentId":2}
```

`GET /notifications?studentId=4`

```json
[{"id":1,"message":"F-301 (Faura) is now vacant as of 10:27 PM.","sentAt":"2026-11-26T22:27:11","studentId":4}]
```

`POST /subscriptions` with `{"studentId":4,"roomId":6}`

```json
{"id":2,"roomId":6,"studentId":4}
```

### Errors

Every error is JSON: `{"status": 404, "error": "Not Found", "message": "Room with ID 999 does not exist."}`.

| Status | When |
|---|---|
| 400 | Body is not valid JSON, body is missing, or an id or query parameter is not a number |
| 404 | The row, or a student, room or amenity named in the body or query, does not exist |
| 409 | Duplicate email, duplicate amenity name, duplicate room name in a building, or duplicate subscription |
| 422 | A validation rule failed; the message lists each field |
| 500 | Unexpected server error |

## Postman

Import `postman/MagisSpace.postman_collection.json`. The variable `baseUrl` defaults to `http://localhost:9999/api`. Start MySQL, the mock SMS server and the app, then open the collection and click Run. One folder per controller, plus a last folder that deletes what the run created and checks the cascades. Every request has a status check. The collection passes on an empty database and again on the same database.

From a terminal:

```
npx newman run postman/MagisSpace.postman_collection.json
npx newman run postman/MagisSpace.postman_collection.json --env-var baseUrl=http://localhost:9998/api
```

## Project layout

```
config/application.properties      Spring and app settings
postman/                           Postman collection
src/app/SpringBootApp.java         Entry point, @EnableScheduling
src/app/config/                    JerseyConfig, RequestFilter (CORS template)
src/app/components/                DataLoader (@PostConstruct seed), ReportExpiryScheduler
src/app/entity/                    JPA entities
src/app/repository/                Spring Data repositories
src/app/service/                   Services the controllers forward to
src/app/notification/              Retrofit SmsClient and its config
src/app/rest/controllers/          Jersey controllers
src/app/rest/dto/                  Request bodies with validation
src/app/rest/errorhandlers/        JSON error bodies and exception mappers
src/mock/MockSmsServer.java        Fake Twilio, run separately
DEMO.md                            Demo script
```

## Troubleshooting

Start with the first red error in the Eclipse Console, not the last one.

| What you see | What to do |
|---|---|
| Many red errors in Eclipse, "The import app... cannot be resolved", or a red X on the project | The jars come from the `SpringBootDependencies` project. It must be in the same workspace, with exactly that name. Then Project > Clean... > Clean all projects. |
| "Unsupported major.minor version" or "release version 17 not supported" | The project needs Java 8. Project > Properties > Java Build Path > Libraries > JRE System Library > Edit, choose JavaSE-1.8. Check Java Compiler > compiler compliance level is 1.8. Then Project > Clean. |
| `CommunicationsException: Communications link failure` and `Connection refused` | MySQL is not running. Start it in the XAMPP control panel (port 3306). |
| `Access denied for user 'root'` | The app uses user `root` with no password, the XAMPP default. If you set a password, put it in `spring.datasource.password`. |
| `Port 9999 was already in use` | Another program uses the port, often an earlier run of this app. Press the red Terminate square in the Eclipse Console, or change `server.port` and use that port in the URL (Postman: `baseUrl`; newman: `--env-var baseUrl=http://localhost:9998/api`). |
| A 500 error or an SQL error about a missing column or value when saving a subscription | The database was created by an older version, whose subscription table had extra columns. Drop `magis_space_db` in phpMyAdmin and run the app again. |
| Notifications are saved but no SMS appears | Start the mock SMS server before the report. The app logs the failed SMS and carries on. |
| Mock SMS: `port 8080 is already in use` | See "Running the mock SMS server". |
| `Access restriction: The type 'HttpServer' is not API` | You have an older commit whose mock server used `com.sun.net.httpserver`. Pull the current branch. Then Project > Clean. |

To start from an empty database, drop `magis_space_db` in phpMyAdmin and run the app again. It creates the database, the tables and the seed data.
