# Demo guide: showing Magis Space in a terminal and in Postman

Use this script to present the API to the instructor. Every response below was recorded from the running app on a fresh database (seed data only). Ids, times and the random SMS id differ on your machine; the status codes and shapes are the same.

## Before you start (in this order)

1. **MySQL.** Open the XAMPP control panel and start MySQL (it must be green).
2. **Mock SMS server.** In Eclipse, right-click `src/mock/MockSmsServer.java`, Run As > Java Application. Keep its Console visible: this is where the SMS appears.
3. **The app.** Right-click `src/app/SpringBootApp.java`, Run As > Java Application. Wait for `Started SpringBootApp`.
4. For a clean demo, drop the database in phpMyAdmin (`magis_space_db`) before step 3 so the ids below match. The app recreates and seeds it.

Open `http://localhost:9999/` in a browser too, if you want to show the map next to the terminal.

## Terminal commands

Pick one column and use it for the whole demo.

- **macOS / Linux:** any terminal with `curl`.
- **Windows PowerShell:** `Invoke-RestMethod` (shows objects) and the helper below for status codes and error bodies.
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

In the cmd commands, `-i` makes `curl.exe` print the status line and headers before the body. The expected responses show only the status line and `Content-Type`.

The expected-response blocks show the macOS `curl -i` output (status line, `Content-Type`, body). In PowerShell, `Invoke-RestMethod` prints the same data as an object without a status line; the `Call` helper prints the status and raw JSON. `Invoke-RestMethod` GET and POST commands raise a red error for 4xx statuses, so the error steps use `Call`.

Run the steps in order. Later steps depend on earlier ones.

### 1. List all rooms

Every room with its live status. `NO_DATA` means nobody has reported it yet.

macOS / Linux:
```bash
curl -s http://localhost:9999/api/rooms
```

Windows PowerShell:
```powershell
Invoke-RestMethod http://localhost:9999/api/rooms | Format-Table id, name, building, floor, capacity, status
```

Windows cmd (`curl.exe`):
```bat
curl.exe -s http://localhost:9999/api/rooms
```

Expected response:
```
[
  {
    "id": 6,
    "name": "B-204",
    "building": "Berchmans",
    "floor": 2,
    "capacity": 25,
    "mapX": 150.0,
    "mapY": 70.0,
    "latitude": 14.6397,
    "longitude": 121.0784,
    "amenities": [
      "Whiteboard"
    ],
    "status": "NO_DATA",
    "predictedVacant": true,
    "lastReportedAt": null,
    "minutesAgo": null
  },
  {
    "id": 5,
    "name": "CTC 102",
    "building": "CTC",
    "floor": 1,
    "capacity": 45,
    "mapX": 180.0,
    "mapY": 90.0,
    "latitude": 14.6386,
    "longitude": 121.0788,
    "amenities": [
      "Aircon",
      "Outlets"
    ],
    "status": "NO_DATA",
    "predictedVacant": true,
    "lastReportedAt": null,
    "minutesAgo": null
  },
  ... (5 more)
]
```

### 2. List rooms with filters

Rooms in Faura that seat 30 or more and have Outlets.

macOS / Linux:
```bash
curl -s "http://localhost:9999/api/rooms?building=Faura&minCapacity=30&amenity=Outlets"
```

Windows PowerShell:
```powershell
Invoke-RestMethod 'http://localhost:9999/api/rooms?building=Faura&minCapacity=30&amenity=Outlets' | Format-List
```

Windows cmd (`curl.exe`):
```bat
curl.exe -s "http://localhost:9999/api/rooms?building=Faura&minCapacity=30&amenity=Outlets"
```

Expected response:
```
[
  {
    "id": 1,
    "name": "F-204",
    "building": "Faura",
    "floor": 2,
    "capacity": 30,
    "mapX": 120.0,
    "mapY": 45.0,
    "latitude": 14.6395,
    "longitude": 121.078,
    "amenities": [
      "Aircon",
      "Outlets"
    ],
    "status": "NO_DATA",
    "predictedVacant": true,
    "lastReportedAt": null,
    "minutesAgo": null
  }
]
```

### 3. Campus map data

One row per building with vacant and total room counts (this feeds the pins on the web map). The second request lists the rooms of one building for the floor view.

macOS / Linux:
```bash
curl -s http://localhost:9999/api/map/buildings
curl -s "http://localhost:9999/api/map?building=Faura"
```

Windows PowerShell:
```powershell
Invoke-RestMethod http://localhost:9999/api/map/buildings | Format-Table
Invoke-RestMethod 'http://localhost:9999/api/map?building=Faura' | Format-Table roomId, name, floor, status
```

Windows cmd (`curl.exe`):
```bat
curl.exe -s http://localhost:9999/api/map/buildings
curl.exe -s "http://localhost:9999/api/map?building=Faura"
```

Expected response:
```
[
  {
    "building": "Berchmans",
    "latitude": 14.6397,
    "longitude": 121.0784,
    "vacantCount": 0,
    "totalRooms": 1
  },
  {
    "building": "CTC",
    "latitude": 14.6386,
    "longitude": 121.0788,
    "vacantCount": 0,
    "totalRooms": 1
  },
  ... (4 more)
]

[
  {
    "roomId": 1,
    "name": "F-204",
    "building": "Faura",
    "floor": 2,
    "mapX": 120.0,
    "mapY": 45.0,
    "status": "NO_DATA",
    "predictedVacant": true,
    "lastReportedAt": null,
    "minutesAgo": null
  },
  {
    "roomId": 2,
    "name": "F-227",
    "building": "Faura",
    "floor": 2,
    "mapX": 135.0,
    "mapY": 45.0,
    "status": "NO_DATA",
    "predictedVacant": true,
    "lastReportedAt": null,
    "minutesAgo": null
  }
]
```

### 4. Create the reporting student

Juan (student 1) is subscribed to Faura rooms with Outlets, so a second student does the reporting. On a fresh database this student gets id 2.

macOS / Linux:
```bash
curl -s -i -X POST http://localhost:9999/api/students -H "Content-Type: application/json" -d '{"name":"Maria Reporter","email":"maria.reporter@example.com","phone":"09181234567"}'
```

Windows PowerShell:
```powershell
$body = @{ name = 'Maria Reporter'; email = 'maria.reporter@example.com'; phone = '09181234567' } | ConvertTo-Json
Invoke-RestMethod -Method Post -Uri http://localhost:9999/api/students -ContentType 'application/json' -Body $body
```

Windows cmd (`curl.exe`):
```bat
curl.exe -s -i -X POST http://localhost:9999/api/students -H "Content-Type: application/json" -d "{\"name\":\"Maria Reporter\",\"email\":\"maria.reporter@example.com\",\"phone\":\"09181234567\"}"
```

Expected response:
```
HTTP/1.1 201 Created
Content-Type: application/json

{
  "id": 2,
  "name": "Maria Reporter",
  "email": "maria.reporter@example.com",
  "phone": "09181234567"
}
```

### 5. Report room F-204 as vacant (201)

Room 1 is F-204. `occupied: false` means vacant. Status 201 Created.

macOS / Linux:
```bash
curl -s -i -X POST http://localhost:9999/api/availability -H "Content-Type: application/json" -d '{"roomId":1,"studentId":2,"occupied":false}'
```

Windows PowerShell:
```powershell
$body = @{ roomId = 1; studentId = 2; occupied = $false } | ConvertTo-Json
Call POST /availability $body
```

Windows cmd (`curl.exe`):
```bat
curl.exe -s -i -X POST http://localhost:9999/api/availability -H "Content-Type: application/json" -d "{\"roomId\":1,\"studentId\":2,\"occupied\":false}"
```

Expected response (your ids and times will differ):
```
HTTP/1.1 201 Created
Content-Type: application/json

{
  "id": 1,
  "occupied": false,
  "reportedAt": "2026-11-26T20:36:19.829",
  "verified": true,
  "studentId": 2,
  "roomId": 1
}
```

### 6. Report the same room again (409)

The same student cannot report the same room twice within 10 minutes.

macOS / Linux:
```bash
curl -s -i -X POST http://localhost:9999/api/availability -H "Content-Type: application/json" -d '{"roomId":1,"studentId":2,"occupied":false}'
```

Windows PowerShell:
```powershell
$body = @{ roomId = 1; studentId = 2; occupied = $false } | ConvertTo-Json
Call POST /availability $body
```

Windows cmd (`curl.exe`):
```bat
curl.exe -s -i -X POST http://localhost:9999/api/availability -H "Content-Type: application/json" -d "{\"roomId\":1,\"studentId\":2,\"occupied\":false}"
```

Expected response (your ids and times will differ):
```
HTTP/1.1 409 Conflict
Content-Type: application/json

{
  "status": 409,
  "error": "Conflict",
  "message": "You already reported this room less than 10 minutes ago."
}
```

### 7. Room now shows as vacant

macOS / Linux:
```bash
curl -s http://localhost:9999/api/rooms/1
```

Windows PowerShell:
```powershell
Invoke-RestMethod http://localhost:9999/api/rooms/1 | Format-List
```

Windows cmd (`curl.exe`):
```bat
curl.exe -s http://localhost:9999/api/rooms/1
```

Expected response (your ids and times will differ):
```
{
  "id": 1,
  "name": "F-204",
  "building": "Faura",
  "floor": 2,
  "capacity": 30,
  "mapX": 120.0,
  "mapY": 45.0,
  "latitude": 14.6395,
  "longitude": 121.078,
  "amenities": [
    "Aircon",
    "Outlets"
  ],
  "status": "VACANT",
  "predictedVacant": false,
  "lastReportedAt": "2026-11-26T20:36:20",
  "minutesAgo": 0
}
```

### 8. Look at the mock SMS server console

Switch to the mock SMS console (Eclipse Console tab, or the terminal where you started it). The report from step 5 produced this:

```
==================== SMS #1 received 20:36:19 ====================
  To:   09171234567
  Text: F-204 (Faura) is now vacant as of 8:36 PM.
  Sid:  SM00000000000000002aa02f3582cfbc47
====================================================================
```

The SMS text is the same text the server saved in the notification (next step).

### 9. Juan's notifications

The notification is saved by the server a moment after the report; wait a second before running this.

macOS / Linux:
```bash
sleep 1; curl -s http://localhost:9999/api/students/1/notifications
```

Windows PowerShell:
```powershell
Start-Sleep 1; Invoke-RestMethod http://localhost:9999/api/students/1/notifications | Format-List
```

Windows cmd (`curl.exe`):
```bat
curl.exe -s http://localhost:9999/api/students/1/notifications
```

Expected response (your ids and times will differ):
```
[
  {
    "id": 1,
    "message": "F-204 (Faura) is now vacant as of 8:36 PM.",
    "sentAt": "2026-11-26T20:36:20",
    "studentId": 1
  }
]
```

### 10. Validation error (422)

A blank name and capacity 0.

macOS / Linux:
```bash
curl -s -i -X POST http://localhost:9999/api/rooms -H "Content-Type: application/json" -d '{"name":"","building":"Faura","floor":1,"capacity":0}'
```

Windows PowerShell:
```powershell
$body = @{ name = ''; building = 'Faura'; floor = 1; capacity = 0 } | ConvertTo-Json
Call POST /rooms $body
```

Windows cmd (`curl.exe`):
```bat
curl.exe -s -i -X POST http://localhost:9999/api/rooms -H "Content-Type: application/json" -d "{\"name\":\"\",\"building\":\"Faura\",\"floor\":1,\"capacity\":0}"
```

Expected response (your ids and times will differ):
```
HTTP/1.1 422 
Content-Type: application/json

{
  "status": 422,
  "error": "Unprocessable Entity",
  "message": "capacity must be greater than or equal to 1; name must not be blank"
}
```

### 11. Bad parameter (400)

The room id is not a number.

macOS / Linux:
```bash
curl -s -i http://localhost:9999/api/rooms/abc
```

Windows PowerShell:
```powershell
Call GET /rooms/abc
```

Windows cmd (`curl.exe`):
```bat
curl.exe -s -i http://localhost:9999/api/rooms/abc
```

Expected response (your ids and times will differ):
```
HTTP/1.1 400 Bad Request
Content-Type: application/json

{
  "status": 400,
  "error": "Bad Request",
  "message": "Invalid path parameter 'id': value is not valid."
}
```

### 12. Not found (404)

macOS / Linux:
```bash
curl -s -i http://localhost:9999/api/rooms/999
```

Windows PowerShell:
```powershell
Call GET /rooms/999
```

Windows cmd (`curl.exe`):
```bat
curl.exe -s -i http://localhost:9999/api/rooms/999
```

Expected response (your ids and times will differ):
```
HTTP/1.1 404 Not Found
Content-Type: application/json

{
  "status": 404,
  "error": "Not Found",
  "message": "Room with ID 999 does not exist."
}
```

### 13. Create a room (201)

On a fresh database the new room gets id 8.

macOS / Linux:
```bash
curl -s -i -X POST http://localhost:9999/api/rooms -H "Content-Type: application/json" -d '{"name":"Demo Room","building":"Faura","floor":1,"capacity":20,"amenityId":1}'
```

Windows PowerShell:
```powershell
$body = @{ name = 'Demo Room'; building = 'Faura'; floor = 1; capacity = 20; amenityId = 1 } | ConvertTo-Json
Call POST /rooms $body
```

Windows cmd (`curl.exe`):
```bat
curl.exe -s -i -X POST http://localhost:9999/api/rooms -H "Content-Type: application/json" -d "{\"name\":\"Demo Room\",\"building\":\"Faura\",\"floor\":1,\"capacity\":20,\"amenityId\":1}"
```

Expected response (your ids and times will differ):
```
HTTP/1.1 201 Created
Content-Type: application/json

{
  "id": 8,
  "name": "Demo Room",
  "building": "Faura",
  "floor": 1,
  "capacity": 20,
  "mapX": null,
  "mapY": null,
  "latitude": null,
  "longitude": null,
  "amenities": [
    "Outlets"
  ],
  "status": "NO_DATA",
  "predictedVacant": true,
  "lastReportedAt": null,
  "minutesAgo": null
}
```

### 14. Delete the room (204)

macOS / Linux:
```bash
curl -s -i -X DELETE http://localhost:9999/api/rooms/8
```

Windows PowerShell:
```powershell
Call DELETE /rooms/8
```

Windows cmd (`curl.exe`):
```bat
curl.exe -s -i -X DELETE http://localhost:9999/api/rooms/8
```

Expected response (your ids and times will differ):
```
HTTP/1.1 204 No Content
```

### 15. Class schedules of F-204

macOS / Linux:
```bash
curl -s "http://localhost:9999/api/schedules?roomId=1"
```

Windows PowerShell:
```powershell
Invoke-RestMethod 'http://localhost:9999/api/schedules?roomId=1' | Format-Table
```

Windows cmd (`curl.exe`):
```bat
curl.exe -s "http://localhost:9999/api/schedules?roomId=1"
```

Expected response (your ids and times will differ):
```
[
  {
    "id": 1,
    "dayOfWeek": "THURSDAY",
    "startTime": "13:00",
    "endTime": "14:30",
    "isAsync": true,
    "roomId": 1
  },
  {
    "id": 2,
    "dayOfWeek": "MONDAY",
    "startTime": "09:00",
    "endTime": "10:30",
    "isAsync": false,
    "roomId": 1
  }
]
```

### 16. Room history (reports per hour of the day)

The hour is the server clock hour of the report.

macOS / Linux:
```bash
curl -s http://localhost:9999/api/rooms/1/history
```

Windows PowerShell:
```powershell
Invoke-RestMethod http://localhost:9999/api/rooms/1/history | Format-Table
```

Windows cmd (`curl.exe`):
```bat
curl.exe -s http://localhost:9999/api/rooms/1/history
```

Expected response (your ids and times will differ):
```
[
  {
    "hour": 20,
    "vacantCount": 1,
    "totalCount": 1
  }
]
```

### 17. Availability reports of a room

Newest first. The unfiltered list is `GET /availability`.

macOS / Linux:
```bash
curl -s "http://localhost:9999/api/availability?roomId=1"
```

Windows PowerShell:
```powershell
Invoke-RestMethod 'http://localhost:9999/api/availability?roomId=1' | Format-Table
```

Windows cmd (`curl.exe`):
```bat
curl.exe -s "http://localhost:9999/api/availability?roomId=1"
```

Expected response (your ids and times will differ):
```
[
  {
    "id": 1,
    "occupied": false,
    "reportedAt": "2026-11-26T20:36:20",
    "verified": true,
    "studentId": 2,
    "roomId": 1
  }
]
```

## Show it in Postman

1. Open Postman and choose Import. Select `postman/MagisSpace.postman_collection.json` from the repository.
2. The collection variable `baseUrl` is `http://localhost:9999/api`. Change it (collection > Variables) if the app uses another port.
3. **One request:** open the folder Rooms, click "List Rooms" and press Send. The response body, the status code and the response time are in the lower pane. The Tests tab of the response shows the checks that passed. The description of each request explains what it does.
4. **Whole collection:** right-click the collection > Run collection (or the Runner), keep every request ticked and press Run. It runs top to bottom, about 144 requests. The result screen lists every request with a green pass or red fail, and each assertion. Click a request in that list to see its request and response.
5. Run it against a database that has the seed data. The run creates its own records and deletes them again, so it can be run twice in a row.
6. To show the same flow from a terminal: `npx newman run postman/MagisSpace.postman_collection.json` (needs Node.js).

Where to look at responses in Postman: the Body tab of the response (Pretty), the status code in the top right of the response pane, and Console (View > Show Postman Console) for the raw request and response.
