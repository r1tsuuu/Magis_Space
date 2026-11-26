# Magis Space
A REST API for finding vacant study rooms on the Ateneo campus. Students report whether a room is vacant or occupied, and other students can look rooms up by building, capacity, and amenity, or see them all on a live map feed. Every report is timestamped. A report older than an hour no longer counts as current, and the room shows as unverified. Students can subscribe to a condition (for example "a room in Faura with Outlets") and get a notification, saved and sent as an SMS to a mock SMS service, when a matching room becomes vacant.

This is the finals project for CSCI 181.05.

## Stack
- Java 8
- Spring Boot 2 (dependency injection, scheduling)
- Jersey (REST endpoints, JSON)
- Spring Data JPA with Hibernate, javax validation
- Retrofit with OkHttp (client for the mock SMS service)
- MySQL (XAMPP / phpMyAdmin)

## Prerequisites
- Eclipse with the class `SpringBootDependencies` project in the same workspace. `.classpath` references it for the Spring, Jersey, Jackson, Hibernate, and MySQL jars.
- No extra jars are needed beyond `SpringBootDependencies` (Spring Boot 2.1, Jersey 2.26, Jackson 2.9.7, `retrofit` 2.4.0, `okhttp` 3.10.0, `okio` 1.14.0, MySQL connector 8.0.13). The SMS client uses OkHttp request and response bodies and Jackson, so no Retrofit converter jar is required.
- XAMPP with MySQL running on port 3306, user `root`, no password.
- Optional: Node.js, to run the Postman collection from the command line with newman.

## Setup
1. Clone the repo and import it into Eclipse (File > Import > Existing Projects into Workspace).
2. Start MySQL from the XAMPP control panel.
3. Run `src/app/SpringBootApp.java` as a Java application.

The app creates the `magis_space_db` database and all tables on first run. `DataLoader` then seeds each empty table: 3 amenities (Outlets, Aircon, Whiteboard), 7 rooms (Faura F-204 and F-227, SEC-A 210, SEC-B 305, CTC 102, Berchmans B-204, Kostka K-301), 1 student (Juan Dela Cruz, `juan.delacruz@example.com`), 1 subscription (that student, building Faura, amenity Outlets), and 2 class schedules for F-204 (Thursday 13:00-14:30 async, Monday 09:00-10:30). Seeding only runs on empty tables, so restarting does not duplicate rows. The app does not need the seed data: everything can be created through the API.

To present the API to an instructor (terminal commands for Windows and macOS, and Postman), follow [DEMO.md](DEMO.md).
## Running the mock SMS server
Notifications are sent as SMS through a small mock of a Twilio-style API in `src/mock/MockSmsServer.java`. It needs only the JDK, no other jar. It is in package `mock`, outside `app`, so Spring does not scan it.
In Eclipse: right-click `src/mock/MockSmsServer.java`, then Run As > Java Application. Leave its Console tab running; every SMS is printed there. Run the Spring Boot app in its own Console (Eclipse keeps one console per program; use the "Display Selected Console" button to switch).
From a terminal (Java 8), in the project folder:
```
javac -d out src/mock/MockSmsServer.java
java -cp out mock.MockSmsServer          # listens on port 8080; pass a port to change it
```
It accepts `POST /sms/send` with `{"to": "...", "body": "..."}`, answers `{"sid": "SM...", "status": "queued"}`, and prints each message:
```
==================== SMS #1 received 14:26:41 ====================
  To:   09171234567
  Text: F-204 (Faura) is now vacant as of 2:26 PM.
  Sid:  SM...
====================================================================
```
If port 8080 is already used (XAMPP Apache sometimes uses it, or an older mock server is still running), it prints an `ERROR: port 8080 is already in use` message and stops. Stop the other program, or give a different port (Run > Run Configurations > Arguments > Program arguments: `8081`) and set `notification.api.url=http://localhost:8081/` in `config/application.properties`. The API works without the mock: if the SMS server is down, the notification is still saved and the failure is only logged.
## Troubleshooting
Start with the first red error in the Eclipse Console, not the last one. Most failures are one of these.
| What you see | What to do |
|---|---|
| Many red errors in Eclipse, "The import app... cannot be resolved", or a red X on the project | The jars come from the `SpringBootDependencies` project. It must be imported into the same workspace, and its name must be exactly `SpringBootDependencies` (check Project Explorer; rename it if needed). Then Project > Clean... > Clean all projects, and wait for the build to finish. |
| "Unsupported major.minor version" or "release version 17 not supported" | The project needs Java 8. Right-click the project > Properties > Java Build Path > Libraries > JRE System Library > Edit, and choose JavaSE-1.8. Also check Java Compiler > compiler compliance level is 1.8. Then Project > Clean. |
| `package retrofit2.converter... does not exist` or `cannot be resolved` for a converter class | You have an older commit. Older commits needed an extra Retrofit converter jar. Run `git pull` on `main`: the current code needs no extra jar. Then Project > Clean. |
| `CommunicationsException: Communications link failure` and `Connection refused`, ending with `Application run failed` | MySQL is not running. Open the XAMPP control panel and click Start next to MySQL (it must turn green; the port is normally 3306). Run the app again. If MySQL uses another port, change `3306` in `spring.datasource.url`. |
| `Access denied for user 'root'` | The app uses user `root` with no password, the XAMPP default. If you set a MySQL password, put it in `spring.datasource.password`. |
| `APPLICATION FAILED TO START ... Port 9999 was already in use` | Another program uses the port, often a previous run of this app that is still going. In Eclipse, press the red Terminate square in the Console (or the Debug view) for the old run. Or change `server.port` in `config/application.properties` (for example `9998`) and use that port in the URL; for Postman set the `baseUrl` variable, for newman pass `--env-var baseUrl=http://localhost:9998/api`. |
| The browser shows 404 at `http://localhost:9999/` | Use `http://localhost:9999/` (not `/api`). Project > Clean makes Eclipse copy `src/static` again. Check that `src/static/index.html` exists. |
| `Cannot reach the Magis Space server` in the web page | The app is not running, or runs on another port. Check the Eclipse Console for "Started SpringBootApp". |
| Notifications are saved but no SMS appears | Start the mock SMS server (see above) before the report. The app logs the failed SMS and carries on. |
| Mock SMS: `port 8080 is already in use` | See "Running the mock SMS server". |
| `Access restriction: The type 'HttpServer' is not API`, or "Errors exist in required project" when launching | You have an older commit whose mock SMS server used `com.sun.net.httpserver`, which Eclipse blocks under a Java 8 JRE. Run `git pull` on `main`: the current mock server uses only `java.net`. Then Project > Clean. |
To start from an empty database, drop `magis_space_db` in phpMyAdmin and run the app again. It creates the database, the tables and the seed data.
## Web app
A browser front end is served by the API itself at `http://localhost:9999/` (files in `src/static/`: `index.html`, `app.js`, `style.css`; plain HTML/CSS/JS, no build step, no CDN, so it works offline). Under Eclipse, non-Java files in `src` are copied to the classpath, so Spring Boot serves them.
- Campus view with one pin per building (vacant / total rooms); click a pin for the floor view with colored room boxes (vacant, occupied, unverified, no data, "likely free" hint).
- Room list with building, capacity, amenity and status filters, and "Nearest vacant from" sorting.
- Pick who you are (or create a student; remembered in the browser), report a room vacant or occupied, and see API errors such as the 10-minute rule as a toast.
- "Notify me" subscriptions (add, list, delete) and a notifications panel with a badge.
- Refreshes every 30 seconds and shows the last update time.

The page also works when opened directly as a file (`file:///.../src/static/index.html`): it then calls `http://localhost:9999/api`. When served over http it uses the same origin as the page.

## Configuration
Settings are in `config/application.properties`.

| Property | Default | Notes |
|---|---|---|
| `server.port` | `9999` | |
| `spring.jersey.application-path` | `/api` | Prefix for every endpoint |
| `spring.datasource.url` | `jdbc:mysql://localhost:3306/magis_space_db?...` | Change if your MySQL runs elsewhere |
| `spring.datasource.username` / `password` | `root` / empty | XAMPP defaults |
| `spring.jpa.hibernate.ddl-auto` | `update` | Creates and updates tables |
| `spring.jpa.show-sql` | `false` | Set to `true` to print every SQL statement |
| `notification.api.url` | `http://localhost:8080/` | Base URL of the mock SMS service. If empty or invalid, `http://localhost:8080/` is used. |

## Endpoints
Base URL: `http://localhost:9999/api`. Requests and responses are JSON.

| Method | Path | Description |
|---|---|---|
| GET | `/rooms?building=&minCapacity=&amenity=` | List rooms with live status, sorted by building, then name. All filters are optional. |
| GET | `/rooms/{id}` | One room |
| POST | `/rooms` | Create a room |
| DELETE | `/rooms/{id}` | Delete a room with its reports and schedules |
| GET | `/rooms/{id}/history` | Reports per hour of the day: `{hour, vacantCount, totalCount}` |
| GET | `/rooms/{id}/availability` | Reports of a room, newest first |
| GET | `/rooms/{id}/schedules` | Class schedules of a room |
| GET | `/availability?roomId=` | List reports, newest first. `roomId` is optional. |
| GET | `/availability/{id}` | One report |
| POST | `/availability` | Report a room as occupied or vacant |
| DELETE | `/availability/{id}` | Delete a report |
| GET | `/amenities` | List amenities |
| GET | `/amenities/{id}` | One amenity |
| POST | `/amenities` | Create an amenity |
| DELETE | `/amenities/{id}` | Delete an amenity. It is removed from rooms, and subscriptions that require it are deleted. |
| GET | `/students` | List students |
| GET | `/students/{id}` | One student |
| POST | `/students` | Create a student |
| DELETE | `/students/{id}` | Delete a student with their reports, subscriptions and notifications |
| GET | `/students/{id}/subscriptions` | Subscriptions of a student |
| GET | `/students/{id}/notifications` | Notifications of a student, newest first |
| GET | `/subscriptions` | List subscriptions |
| GET | `/subscriptions/{id}` | One subscription |
| POST | `/subscriptions` | Create a subscription |
| DELETE | `/subscriptions/{id}` | Delete a subscription |
| GET | `/notifications` | List notifications |
| GET | `/notifications/{id}` | One notification |
| POST | `/notifications` | Save a notification for a student and send it as an SMS |
| DELETE | `/notifications/{id}` | Delete a notification |
| GET | `/schedules?roomId=&day=` | List class schedules. Filters are optional. `day` is e.g. `MONDAY`, any letter case. |
| GET | `/schedules/{id}` | One class schedule |
| POST | `/schedules` | Create a class schedule |
| DELETE | `/schedules/{id}` | Delete a class schedule |
| GET | `/map?building=&floor=&fromBuilding=` | Every room with live status for a map. `fromBuilding` sorts nearest first. |
| GET | `/map/buildings` | One row per building with its vacant and total room counts |

Request bodies. Room name and building, amenity name, student email and subscription building are trimmed before the duplicate check and saving.

| Endpoint | Fields |
|---|---|
| `POST /rooms` | `name` (max 50), `building` (max 100), `floor` (0 or more), `capacity` (1 or more). Optional: `mapX`, `mapY`, `latitude`, `longitude`, and amenities as `amenityId` (one id), `amenityIds` (list of ids) or `amenities` (list of names). |
| `POST /availability` | `roomId`, `studentId`, `occupied` (true or false) |
| `POST /amenities` | `name` (max 50) |
| `POST /students` | `name` (max 100), `email` (max 100), `phone` (`09XXXXXXXXX` or `+639XXXXXXXXX`) |
| `POST /subscriptions` | `studentId`, `building` (max 100). Optional: `minCapacity` (1 or more), `amenityId`. |
| `POST /notifications` | `studentId`, `message` (not blank, max 300) |
| `POST /schedules` | `roomId`, `dayOfWeek` (`MONDAY` to `SUNDAY`, any letter case), `startTime`, `endTime` (`HH:mm`, end after start). Optional: `isAsync` (default false). |

## Usage
These are real responses from the running app.

Create a room:
```
POST http://localhost:9999/api/rooms
Content-Type: application/json

{ "name": "SEC-C 101", "building": "SEC-C", "floor": 1, "capacity": 30, "amenityId": 1, "latitude": 14.6405, "longitude": 121.0770 }
```
Response (`201 Created`):
```json
{
  "id": 11,
  "name": "SEC-C 101",
  "building": "SEC-C",
  "floor": 1,
  "capacity": 30,
  "mapX": null,
  "mapY": null,
  "latitude": 14.6405,
  "longitude": 121.077,
  "amenities": ["Outlets"],
  "status": "NO_DATA",
  "predictedVacant": true,
  "lastReportedAt": null,
  "minutesAgo": null
}
```

Report the room as vacant:
```
POST http://localhost:9999/api/availability
Content-Type: application/json

{ "roomId": 11, "studentId": 1, "occupied": false }
```
Response (`201 Created`):
```json
{ "id": 3, "occupied": false, "reportedAt": "2026-11-23T14:26:38.351", "verified": true, "roomId": 11, "studentId": 1 }
```
`GET /api/rooms/11` now shows `"status": "VACANT"`, `"lastReportedAt": "2026-11-23T14:26:38.351"` and `"minutesAgo": 0`. `GET /api/rooms/11/history` returns:
```json
[ { "hour": 14, "vacantCount": 1, "totalCount": 1 } ]
```

The seeded subscription watches Faura rooms with Outlets. When another student reports Faura room F-204 as vacant, `GET /api/students/1/notifications` returns (and the mock SMS server prints):
```json
[ { "id": 1, "message": "F-204 (Faura) is now vacant as of 3:52 PM.", "sentAt": "2026-11-23T15:52:18", "studentId": 1 } ]
```
### Notification rules
- A vacant report notifies matching subscribers only when it is a change to vacant: the room's status just before the report was not `VACANT` (`OCCUPIED`, `UNVERIFIED` and `NO_DATA` all count as a change). A second vacant report on a room that is already `VACANT` notifies nobody. Occupied reports never notify.
- The student who filed the report is not notified about it.
- A student gets at most one notification per event, even when several of their subscriptions match.
- Creating an async class schedule (`"isAsync": true`) notifies matching subscribers only when that class is in session right now (today's day of the week, `startTime <= now < endTime`) and the room's current status is not `OCCUPIED`. An async class on another day or time notifies nobody.
- Messages are cut to 300 characters at most. A failed notification or SMS never fails the report or schedule request.

Create a subscription (a condition, not a room):
```
POST http://localhost:9999/api/subscriptions
{ "studentId": 1, "building": "SEC-C", "minCapacity": 20 }
```
Response (`201 Created`):
```json
{ "id": 4, "building": "SEC-C", "minCapacity": 20, "amenity": null, "studentId": 1 }
```

Class schedules of a room, `GET /api/schedules?roomId=1`:
```json
[
  { "id": 1, "dayOfWeek": "THURSDAY", "startTime": "13:00", "endTime": "14:30", "isAsync": true, "roomId": 1 },
  { "id": 2, "dayOfWeek": "MONDAY", "startTime": "09:00", "endTime": "10:30", "isAsync": false, "roomId": 1 }
]
```

Live map, `GET /api/map?building=Faura&fromBuilding=SEC-A` (nearest to SEC-A first):
```json
[
  { "roomId": 1, "name": "F-204", "building": "Faura", "floor": 2, "mapX": 120.0, "mapY": 45.0, "status": "NO_DATA", "predictedVacant": true, "lastReportedAt": null, "minutesAgo": null },
  { "roomId": 2, "name": "F-227", "building": "Faura", "floor": 2, "mapX": 135.0, "mapY": 45.0, "status": "NO_DATA", "predictedVacant": true, "lastReportedAt": null, "minutesAgo": null }
]
```

Building summary, `GET /api/map/buildings` (first entries; `latitude`/`longitude` are the average of the building's rooms that have coordinates):
```json
[
  { "building": "Berchmans", "latitude": 14.6397, "longitude": 121.0784, "vacantCount": 0, "totalRooms": 1 },
  { "building": "CTC", "latitude": 14.6386, "longitude": 121.0788, "vacantCount": 0, "totalRooms": 1 },
  { "building": "Faura", "latitude": 14.63935, "longitude": 121.078, "vacantCount": 0, "totalRooms": 2 }
]
```

## Room status
Each room and each map entry carries these fields, worked out from the room's latest report and its class schedule:

| Status | Meaning |
|---|---|
| `VACANT` | The latest report is verified, under 60 minutes old and says vacant |
| `OCCUPIED` | The latest report is verified, under 60 minutes old and says occupied |
| `UNVERIFIED` | The latest report is unverified (`verified` is false) or is 60 minutes old or more (exact age in seconds) |
| `NO_DATA` | The room has no reports |

`predictedVacant` is only computed for `UNVERIFIED` and `NO_DATA` rooms. It is `true` when no class is in progress in the room right now (server day and time), or every class in progress is async. For `VACANT` and `OCCUPIED` rooms it is always `false`. `lastReportedAt` and `minutesAgo` are `null` when the room has no reports.

A scheduled job runs every 5 minutes and sets `verified` to `false` on reports older than 60 minutes. Reports are kept, so room history stays intact.

A student can report the same room again only after 10 minutes. Another student, or the same student on another room, is not blocked.

## Errors
Errors come back as JSON:
```json
{ "status": 404, "error": "Not Found", "message": "Room with ID 99 does not exist." }
```
| Status | When |
|---|---|
| 400 | A path or query parameter cannot be converted, for example `GET /rooms/abc`, `GET /schedules?roomId=abc` or an id too large for a number. The message names the parameter. `minCapacity` is not a positive number. `floor` on `/map` is not a number. `day` is not a day name. The body is not valid JSON, or a value cannot be read (`"floor": "x"`, `"dayOfWeek": "FUNDAY"`, `"startTime": "8am"`). A POST has no body. |
| 404 | Student, room, amenity, report, subscription, notification or schedule not found. A building given to `/map` does not exist. An unknown URL (a malformed id is a 400, not a 404). |
| 405 | A method the path does not support |
| 409 | A room with the same name already exists in that building. Amenity name or student email already exists. The same student reported the same room less than 10 minutes ago ("You already reported this room less than 10 minutes ago."). A class schedule overlaps another class in the same room and day. A database unique constraint is violated. |
| 422 | A field fails validation: blank name, a text longer than its limit (the limits are listed under request bodies), a null inside `amenityIds` or `amenities` ("amenityIds must not contain null"), capacity below 1, invalid email or phone, missing required fields, `minCapacity` below 1, blank message, `endTime` not after `startTime`. The message lists the fields. |
| 500 | Anything unexpected. The body is `"Unexpected server error."`; the stack trace is written to the server log. |

## Postman collection
`postman/MagisSpace.postman_collection.json` (Postman collection v2.1) has a request for every endpoint, error examples (for example "Create Room - duplicate (409)"), a description on each request, and test scripts that check the status code and key fields.

To use it:
1. In Postman, choose Import and select the file.
2. The collection variable `baseUrl` is `http://localhost:9999/api`. Change it if the app runs elsewhere.
3. Start the app with the seeded data and the mock SMS server, then run the collection with the Collection Runner (Run collection), top to bottom.

Folders: Setup (looks up seeded ids and creates a reporter student), Rooms, Availability, Amenities, Students, Subscriptions, Notifications, Schedules, Notification rules, Map, and Cleanup. Requests are ordered so the whole run passes on a freshly seeded database. Created ids are stored in collection variables and reused, names and emails carry a per-run suffix, and everything the run creates is deleted again. Only the seeded student and room F-204 are read, never changed. The class schedule request uses the current day of the week of the machine running Postman, so Postman and the app should be on the same machine or time zone.

From the command line:
```
npx newman run postman/MagisSpace.postman_collection.json
```
Add `--env-var baseUrl=http://localhost:PORT/api` to use another port. The last verified run, on a fresh database with the mock SMS server running, executed 144 requests with 293 assertions and 0 failures, and a second run against the same database gave the same result.

## Project layout
```
config/application.properties    database, server and SMS settings
postman/                         Postman collection
src/app/SpringBootApp.java       entry point
src/mock/MockSmsServer.java      mock SMS service (port 8080), run as a Java Application
src/app/entity/                  JPA entities
src/app/repository/              Spring Data repositories
src/app/service/                 business logic
src/app/validation/              custom validators (report interval, class time range)
src/app/notification/            Retrofit client for the SMS service
src/app/rest/controllers/        Jersey resources
src/app/rest/dto/                request and response objects
src/app/rest/errorhandlers/      exception to JSON mapping
src/static/                      web app (index.html, app.js, style.css)
src/app/components/              seed data (DataLoader) and the report expiry scheduler
```

## Known limitations
- Room latitude/longitude in the seed data are approximate points on the Ateneo campus, not surveyed. `mapX`/`mapY` are arbitrary demo values: there is no campus map image or scale, and rooms created without them have `null`.
- `fromBuilding` sorts by straight-line distance on raw latitude/longitude (or mapX/mapY when a building has no coordinates). Rooms without coordinates sort last. It is not walking distance.
- SMS goes to a mock service, not a real provider. It is sent asynchronously and failures are only logged, so a notification can be saved without the SMS arriving.
- There is no login or authorization. Anyone can create or delete any record, and a report is trusted as sent.
- There are no update (PUT) endpoints. To change a record, delete and create it again.
- Rooms, schedules and the day/time used for `predictedVacant` use the server's local time zone.
- An `endTime` that is not after `startTime` is rejected with 422 by validation before the service's own 400 check is reached.
- A class cannot end at midnight: `endTime` must be after `startTime` on the same day, so use `23:59` as the latest end.
