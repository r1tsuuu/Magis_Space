# Demo guide: showing Magis Space in a terminal and in Postman

Use this script to present the API to the instructor. Every response below was recorded from the running app on a fresh database (seed data only). Ids, times and the random SMS id differ on your machine if the database is not fresh; the status codes and shapes are the same.

## Before you start (in this order)

1. **MySQL.** Open the XAMPP control panel and start MySQL (it must be green).
2. **Mock SMS server.** In Eclipse, right-click `src/mock/MockSmsServer.java`, Run As > Java Application. Keep its Console visible: this is where the SMS appears.
3. **The app.** Right-click `src/app/SpringBootApp.java`, Run As > Java Application. Wait for `Started SpringBootApp`.
4. For a clean demo, drop the database in phpMyAdmin (`magis_space_db`) before step 3 so the ids below match. The app recreates and seeds it.

## Terminal commands

Pick one variant and use it for the whole demo.

- **macOS / Linux:** any terminal with `curl`.
- **Windows PowerShell:** `Invoke-RestMethod` (shows objects) and the helper below for status codes, DELETE and error bodies.
- **Windows cmd:** use `curl.exe` (Windows 10 and later). In PowerShell, plain `curl` is an alias for another command, so always type `curl.exe`.

Windows PowerShell only: paste this once. It prints the HTTP status and the raw JSON body, also for 4xx errors, which `Invoke-RestMethod` would otherwise turn into a red exception.

```powershell
function Call($method, $path, $json) {
  $p = @{ Method = $method; Uri = "http://localhost:9999/api$path"; UseBasicParsing = $true }
  if ($json) { $p.ContentType = 'application/json'; $p.Body = $json }
  try { $r = Invoke-WebRequest @p; "HTTP $([int]$r.StatusCode)"; $r.Content }
  catch {
    "HTTP $([int]$_.Exception.Response.StatusCode)"
    if ($_.ErrorDetails.Message) { $_.ErrorDetails.Message }
    else { (New-Object IO.StreamReader($_.Exception.Response.GetResponseStream())).ReadToEnd() }
  }
}
```

The expected-response blocks show the macOS `curl -si` output (status line, `Content-Type`, body). In PowerShell, `Invoke-RestMethod` prints the same data as an object without a status line. The `curl.exe -si` variants print the same status line and body as macOS, plus more headers.

Run the steps in order. Later steps depend on earlier ones.


### 1. List all rooms

Every room with its amenities. The five seeded rooms are shown.

macOS / Linux:
```bash
curl -si http://localhost:9999/api/rooms
```

Windows PowerShell:
```powershell
Invoke-RestMethod http://localhost:9999/api/rooms | Format-Table id, name, building, floor, capacity
```

Windows cmd (`curl.exe`):
```bat
curl.exe -si "http://localhost:9999/api/rooms"
```

Expected response:
```
HTTP/1.1 200 OK
Content-Type: application/json

[{"id":4,"name":"CTC 102","building":"CTC","floor":1,"capacity":45,"amenities":[{"id":1,"name":"Outlets"},{"id":2,"name":"Aircon"}]},{"id":1,"name":"F-204","building":"Faura","floor":2,"capacity":30,"amenities":[{"id":1,"name":"Outlets"},{"id":2,"name":"Aircon"}]},{"id":2,"name":"F-227","building":"Faura","floor":2,"capacity":35,"amenities":[{"id":3,"name":"Whiteboard"},{"id":2,"name":"Aircon"}]},{"id":5,"name":"K-301","building":"Kostka","floor":3,"capacity":30,"amenities":[{"id":1,"name":"Outlets"},{"id":3,"name":"Whiteboard"}]},{"id":3,"name":"SEC-A 210","building":"SEC-A","floor":2,"capacity":40,"amenities":[{"id":1,"name":"Outlets"},{"id":3,"name":"Whiteboard"},{"id":2,"name":"Aircon"}]}]
```

### 2. List rooms in one building

The only filter on rooms. Not case sensitive.

macOS / Linux:
```bash
curl -si "http://localhost:9999/api/rooms?building=Faura"
```

Windows PowerShell:
```powershell
Invoke-RestMethod "http://localhost:9999/api/rooms?building=Faura" | Format-Table id, name, building, floor, capacity
```

Windows cmd (`curl.exe`):
```bat
curl.exe -si "http://localhost:9999/api/rooms?building=Faura"
```

Expected response:
```
HTTP/1.1 200 OK
Content-Type: application/json

[{"id":1,"name":"F-204","building":"Faura","floor":2,"capacity":30,"amenities":[{"id":2,"name":"Aircon"},{"id":1,"name":"Outlets"}]},{"id":2,"name":"F-227","building":"Faura","floor":2,"capacity":35,"amenities":[{"id":2,"name":"Aircon"},{"id":3,"name":"Whiteboard"}]}]
```

### 3. Get one room

Reads room 1 by id.

macOS / Linux:
```bash
curl -si http://localhost:9999/api/rooms/1
```

Windows PowerShell:
```powershell
Invoke-RestMethod http://localhost:9999/api/rooms/1
```

Windows cmd (`curl.exe`):
```bat
curl.exe -si "http://localhost:9999/api/rooms/1"
```

Expected response:
```
HTTP/1.1 200 OK
Content-Type: application/json

{"id":1,"name":"F-204","building":"Faura","floor":2,"capacity":30,"amenities":[{"id":2,"name":"Aircon"},{"id":1,"name":"Outlets"}]}
```

### 4. List amenities

The three seeded amenities.

macOS / Linux:
```bash
curl -si http://localhost:9999/api/amenities
```

Windows PowerShell:
```powershell
Invoke-RestMethod http://localhost:9999/api/amenities | Format-Table id, name
```

Windows cmd (`curl.exe`):
```bat
curl.exe -si "http://localhost:9999/api/amenities"
```

Expected response:
```
HTTP/1.1 200 OK
Content-Type: application/json

[{"id":2,"name":"Aircon"},{"id":1,"name":"Outlets"},{"id":3,"name":"Whiteboard"}]
```

### 5. Create an amenity (201)

Creates a row at runtime. The new id is 4.

macOS / Linux:
```bash
curl -si -X POST http://localhost:9999/api/amenities \
  -H 'Content-Type: application/json' \
  -d '{"name":"Projector"}'
```

Windows PowerShell:
```powershell
Invoke-RestMethod -Method Post -Uri http://localhost:9999/api/amenities -ContentType 'application/json' `
  -Body '{"name":"Projector"}'
```

Windows cmd (`curl.exe`):
```bat
curl.exe -si -X POST http://localhost:9999/api/amenities -H "Content-Type: application/json" -d "{\"name\":\"Projector\"}"
```

Expected response:
```
HTTP/1.1 201 Created
Content-Type: application/json

{"id":4,"name":"Projector"}
```

### 6. Create a student (201)

Creates the student who will subscribe. The new id is 4.

macOS / Linux:
```bash
curl -si -X POST http://localhost:9999/api/students \
  -H 'Content-Type: application/json' \
  -d '{"name":"Ana Lim","email":"ana.lim@example.com","phone":"09171112222"}'
```

Windows PowerShell:
```powershell
Invoke-RestMethod -Method Post -Uri http://localhost:9999/api/students -ContentType 'application/json' `
  -Body '{"name":"Ana Lim","email":"ana.lim@example.com","phone":"09171112222"}'
```

Windows cmd (`curl.exe`):
```bat
curl.exe -si -X POST http://localhost:9999/api/students -H "Content-Type: application/json" -d "{\"name\":\"Ana Lim\",\"email\":\"ana.lim@example.com\",\"phone\":\"09171112222\"}"
```

Expected response:
```
HTTP/1.1 201 Created
Content-Type: application/json

{"id":4,"name":"Ana Lim","email":"ana.lim@example.com","phone":"09171112222"}
```

### 7. Create a room with the new amenity (201)

Creates room 6 with amenities 1 (Outlets) and 4 (Projector).

macOS / Linux:
```bash
curl -si -X POST http://localhost:9999/api/rooms \
  -H 'Content-Type: application/json' \
  -d '{"name":"F-301","building":"Faura","floor":3,"capacity":20,"amenityIds":[1,4]}'
```

Windows PowerShell:
```powershell
Invoke-RestMethod -Method Post -Uri http://localhost:9999/api/rooms -ContentType 'application/json' `
  -Body '{"name":"F-301","building":"Faura","floor":3,"capacity":20,"amenityIds":[1,4]}'
```

Windows cmd (`curl.exe`):
```bat
curl.exe -si -X POST http://localhost:9999/api/rooms -H "Content-Type: application/json" -d "{\"name\":\"F-301\",\"building\":\"Faura\",\"floor\":3,\"capacity\":20,\"amenityIds\":[1,4]}"
```

Expected response:
```
HTTP/1.1 201 Created
Content-Type: application/json

{"id":6,"name":"F-301","building":"Faura","floor":3,"capacity":20,"amenities":[{"id":1,"name":"Outlets"},{"id":4,"name":"Projector"}]}
```

### 8. Subscribe the student to the room (201)

A subscription is a student and a room. The new id is 2 (the seed holds subscription 1).

macOS / Linux:
```bash
curl -si -X POST http://localhost:9999/api/subscriptions \
  -H 'Content-Type: application/json' \
  -d '{"studentId":4,"roomId":6}'
```

Windows PowerShell:
```powershell
Invoke-RestMethod -Method Post -Uri http://localhost:9999/api/subscriptions -ContentType 'application/json' `
  -Body '{"studentId":4,"roomId":6}'
```

Windows cmd (`curl.exe`):
```bat
curl.exe -si -X POST http://localhost:9999/api/subscriptions -H "Content-Type: application/json" -d "{\"studentId\":4,\"roomId\":6}"
```

Expected response:
```
HTTP/1.1 201 Created
Content-Type: application/json

{"id":2,"roomId":6,"studentId":4}
```

### 9. Subscribe again (409)

The same pair cannot be added twice.

macOS / Linux:
```bash
curl -si -X POST http://localhost:9999/api/subscriptions \
  -H 'Content-Type: application/json' \
  -d '{"studentId":4,"roomId":6}'
```

Windows PowerShell (the `Call` helper):
```powershell
Call POST '/subscriptions' '{"studentId":4,"roomId":6}'
```

Windows cmd (`curl.exe`):
```bat
curl.exe -si -X POST http://localhost:9999/api/subscriptions -H "Content-Type: application/json" -d "{\"studentId\":4,\"roomId\":6}"
```

Expected response:
```
HTTP/1.1 409 Conflict
Content-Type: application/json

{"status":409,"error":"Conflict","message":"Student 4 is already subscribed to room 6."}
```

### 10. List the student's subscriptions

Optional studentId filter.

macOS / Linux:
```bash
curl -si "http://localhost:9999/api/subscriptions?studentId=4"
```

Windows PowerShell:
```powershell
Invoke-RestMethod "http://localhost:9999/api/subscriptions?studentId=4" | Format-Table id, studentId, roomId
```

Windows cmd (`curl.exe`):
```bat
curl.exe -si "http://localhost:9999/api/subscriptions?studentId=4"
```

Expected response:
```
HTTP/1.1 200 OK
Content-Type: application/json

[{"id":2,"roomId":6,"studentId":4}]
```

### 11. Report the room vacant (201)

Student 2 (Maria) reports room 6 as vacant. The app sends an SMS to every subscriber of the room through the Retrofit client and saves a notification for each.

macOS / Linux:
```bash
curl -si -X POST http://localhost:9999/api/availability \
  -H 'Content-Type: application/json' \
  -d '{"studentId":2,"roomId":6,"occupied":false}'
```

Windows PowerShell:
```powershell
Invoke-RestMethod -Method Post -Uri http://localhost:9999/api/availability -ContentType 'application/json' `
  -Body '{"studentId":2,"roomId":6,"occupied":false}'
```

Windows cmd (`curl.exe`):
```bat
curl.exe -si -X POST http://localhost:9999/api/availability -H "Content-Type: application/json" -d "{\"studentId\":2,\"roomId\":6,\"occupied\":false}"
```

Expected response:
```
HTTP/1.1 201 Created
Content-Type: application/json

{"id":1,"occupied":false,"reportedAt":"2026-11-26T22:27:10.484","verified":true,"roomId":6,"studentId":2}
```

### 12. Look at the mock SMS server console

Switch to the Eclipse Console of `MockSmsServer`. The SMS to Ana's phone is printed there. Expected output (the time and the Sid differ):

```
==================== SMS #1 received 22:27:10 ====================
  To:   09171112222
  Text: F-301 (Faura) is now vacant as of 10:27 PM.
  Sid:  SMfd814ff2ae292bb2e6e53cd363517ac2
====================================================================
```

### 13. Ana's notifications

The notification the vacant report created for the subscriber.

macOS / Linux:
```bash
curl -si "http://localhost:9999/api/notifications?studentId=4"
```

Windows PowerShell:
```powershell
Invoke-RestMethod "http://localhost:9999/api/notifications?studentId=4" | Format-Table id, studentId, message, sentAt
```

Windows cmd (`curl.exe`):
```bat
curl.exe -si "http://localhost:9999/api/notifications?studentId=4"
```

Expected response:
```
HTTP/1.1 200 OK
Content-Type: application/json

[{"id":1,"message":"F-301 (Faura) is now vacant as of 10:27 PM.","sentAt":"2026-11-26T22:27:11","studentId":4}]
```

### 14. Send a notification by hand (201)

Sends one SMS through the Retrofit client and saves the record.

macOS / Linux:
```bash
curl -si -X POST http://localhost:9999/api/notifications \
  -H 'Content-Type: application/json' \
  -d '{"studentId":4,"message":"Study group at 3 PM in F-301"}'
```

Windows PowerShell:
```powershell
Invoke-RestMethod -Method Post -Uri http://localhost:9999/api/notifications -ContentType 'application/json' `
  -Body '{"studentId":4,"message":"Study group at 3 PM in F-301"}'
```

Windows cmd (`curl.exe`):
```bat
curl.exe -si -X POST http://localhost:9999/api/notifications -H "Content-Type: application/json" -d "{\"studentId\":4,\"message\":\"Study group at 3 PM in F-301\"}"
```

Expected response:
```
HTTP/1.1 201 Created
Content-Type: application/json

{"id":2,"message":"Study group at 3 PM in F-301","sentAt":"2026-11-26T22:27:12.119","studentId":4}
```

### 15. Reports of the room, newest first

Optional roomId filter on the report list.

macOS / Linux:
```bash
curl -si "http://localhost:9999/api/availability?roomId=6"
```

Windows PowerShell:
```powershell
Invoke-RestMethod "http://localhost:9999/api/availability?roomId=6" | Format-Table id, roomId, studentId, occupied, verified, reportedAt
```

Windows cmd (`curl.exe`):
```bat
curl.exe -si "http://localhost:9999/api/availability?roomId=6"
```

Expected response:
```
HTTP/1.1 200 OK
Content-Type: application/json

[{"id":1,"occupied":false,"reportedAt":"2026-11-26T22:27:10","verified":true,"roomId":6,"studentId":2}]
```

### 16. Validation error (422)

A blank name and a bad email.

macOS / Linux:
```bash
curl -si -X POST http://localhost:9999/api/students \
  -H 'Content-Type: application/json' \
  -d '{"name":"","email":"nope","phone":"09171112222"}'
```

Windows PowerShell (the `Call` helper):
```powershell
Call POST '/students' '{"name":"","email":"nope","phone":"09171112222"}'
```

Windows cmd (`curl.exe`):
```bat
curl.exe -si -X POST http://localhost:9999/api/students -H "Content-Type: application/json" -d "{\"name\":\"\",\"email\":\"nope\",\"phone\":\"09171112222\"}"
```

Expected response:
```
HTTP/1.1 422 
Content-Type: application/json

{"status":422,"error":"Unprocessable Entity","message":"email must be a well-formed email address; name must not be blank"}
```

### 17. Malformed JSON (400)

The body is not JSON.

macOS / Linux:
```bash
curl -si -X POST http://localhost:9999/api/rooms \
  -H 'Content-Type: application/json' \
  -d '{ "name": '
```

Windows PowerShell (the `Call` helper):
```powershell
Call POST '/rooms' '{ "name": '
```

Windows cmd (`curl.exe`):
```bat
curl.exe -si -X POST http://localhost:9999/api/rooms -H "Content-Type: application/json" -d "{ \"name\": "
```

Expected response:
```
HTTP/1.1 400 Bad Request
Content-Type: application/json

{"status":400,"error":"Bad Request","message":"Malformed request body: Unexpected end-of-input within/between Object entries"}
```

### 18. Bad id (400)

The id is not a number.

macOS / Linux:
```bash
curl -si http://localhost:9999/api/rooms/abc
```

Windows PowerShell (the `Call` helper):
```powershell
Call GET '/rooms/abc'
```

Windows cmd (`curl.exe`):
```bat
curl.exe -si "http://localhost:9999/api/rooms/abc"
```

Expected response:
```
HTTP/1.1 400 Bad Request
Content-Type: application/json

{"status":400,"error":"Bad Request","message":"Invalid path parameter 'id': value is not valid."}
```

### 19. Not found (404)

No room has this id.

macOS / Linux:
```bash
curl -si http://localhost:9999/api/rooms/999
```

Windows PowerShell (the `Call` helper):
```powershell
Call GET '/rooms/999'
```

Windows cmd (`curl.exe`):
```bat
curl.exe -si "http://localhost:9999/api/rooms/999"
```

Expected response:
```
HTTP/1.1 404 Not Found
Content-Type: application/json

{"status":404,"error":"Not Found","message":"Room with ID 999 does not exist."}
```

### 20. Delete the room (204)

The room's reports and subscriptions are deleted with it.

macOS / Linux:
```bash
curl -si -X DELETE http://localhost:9999/api/rooms/6
```

Windows PowerShell (the `Call` helper):
```powershell
Call DELETE '/rooms/6'
```

Windows cmd (`curl.exe`):
```bat
curl.exe -si -X DELETE http://localhost:9999/api/rooms/6
```

Expected response:
```
HTTP/1.1 204 No Content
```

### 21. The room is gone (404)

macOS / Linux:
```bash
curl -si http://localhost:9999/api/rooms/6
```

Windows PowerShell (the `Call` helper):
```powershell
Call GET '/rooms/6'
```

Windows cmd (`curl.exe`):
```bat
curl.exe -si "http://localhost:9999/api/rooms/6"
```

Expected response:
```
HTTP/1.1 404 Not Found
Content-Type: application/json

{"status":404,"error":"Not Found","message":"Room with ID 6 does not exist."}
```

### 22. The student's subscriptions are gone

The subscription went with the room.

macOS / Linux:
```bash
curl -si "http://localhost:9999/api/subscriptions?studentId=4"
```

Windows PowerShell:
```powershell
Invoke-RestMethod "http://localhost:9999/api/subscriptions?studentId=4" | Format-Table id, studentId, roomId
```

Windows cmd (`curl.exe`):
```bat
curl.exe -si "http://localhost:9999/api/subscriptions?studentId=4"
```

Expected response:
```
HTTP/1.1 200 OK
Content-Type: application/json

[]
```

### 23. Delete the student (204)

Their notifications are deleted with them.

macOS / Linux:
```bash
curl -si -X DELETE http://localhost:9999/api/students/4
```

Windows PowerShell (the `Call` helper):
```powershell
Call DELETE '/students/4'
```

Windows cmd (`curl.exe`):
```bat
curl.exe -si -X DELETE http://localhost:9999/api/students/4
```

Expected response:
```
HTTP/1.1 204 No Content
```

### 24. The student's notifications (404)

The student no longer exists.

macOS / Linux:
```bash
curl -si "http://localhost:9999/api/notifications?studentId=4"
```

Windows PowerShell (the `Call` helper):
```powershell
Call GET '/notifications?studentId=4'
```

Windows cmd (`curl.exe`):
```bat
curl.exe -si "http://localhost:9999/api/notifications?studentId=4"
```

Expected response:
```
HTTP/1.1 404 Not Found
Content-Type: application/json

{"status":404,"error":"Not Found","message":"Student with ID 4 does not exist."}
```

### 25. Delete the amenity (204)

It is removed from every room that had it.

macOS / Linux:
```bash
curl -si -X DELETE http://localhost:9999/api/amenities/4
```

Windows PowerShell (the `Call` helper):
```powershell
Call DELETE '/amenities/4'
```

Windows cmd (`curl.exe`):
```bat
curl.exe -si -X DELETE http://localhost:9999/api/amenities/4
```

Expected response:
```
HTTP/1.1 204 No Content
```

## Show it in Postman

1. In Postman, choose Import and select `postman/MagisSpace.postman_collection.json`.
2. The collection variable `baseUrl` is `http://localhost:9999/api`. Change it if the app runs on another port.
3. Open the collection, click Run, leave every request selected, and click Run Magis Space API. The mock SMS server must be running.
4. Show the green results. Folders: Amenities, Students, Rooms, Subscriptions, Availability, Notifications, and Deletes and cascades. Each request has a status check and a description. The request `Availability > Report room vacant (notifies subscribers)` is the vacant report that sends the SMS, and `Notifications > List notifications - by student` shows the notification it created.
5. Run it again: it passes again, because every run creates its own uniquely named rows and deletes them at the end.

From a terminal, with Node.js installed:

```
npx newman run postman/MagisSpace.postman_collection.json
```
