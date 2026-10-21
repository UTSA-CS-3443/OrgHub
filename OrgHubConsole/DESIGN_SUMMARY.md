# OrgHub – Console Prototype (Option 2) Design Summary

**Goal:** a console version of OrgHub that covers the proposal's scope – create, view, update and delete events, officer vs. member access, a dashboard, and file-based storage in `events.csv`. The layering is chosen so the JavaFX version can reuse the model, data and controller packages unchanged and only replace `ui`.

## How to run
Java 17+. From the project folder:
```
javac -d out $(find src -name "*.java")
java -cp out orghub.Main
```
(In IntelliJ: mark `src` as Sources Root, run `orghub.Main` with the project folder as the working directory.)
Log in with an email from `data/members.csv`, e.g. `clara@example.edu` (Officer) or `jordan@example.edu` (Member).

## Package overview
```
orghub
├── Main                    wires everything together
├── model                   data + rules (no I/O)
│   ├── Event (abstract) ── MeetingEvent, WorkshopEvent, SocialEvent, VolunteerEvent
│   ├── EventType (enum + factory)
│   ├── User (abstract)  ── Officer, Member
│   └── Organization
├── data                    storage
│   ├── Repository<T> (interface)
│   ├── CsvRepository<T> (abstract) ── CsvEventRepository, CsvUserRepository
│   └── CsvUtil
├── controller              application logic
│   ├── EventController
│   └── UserController
└── ui
    └── ConsoleUI           menus, prompts, printing
```

## Model classes
- **Event (abstract)** – private fields `id, title, date, time, location, description, organizer`; getters; validating setters (blank/invalid values are rejected); `parseDate/parseTime`; `toCsvFields()`; `compareTo` (chronological). Abstract methods `getType()` and `getReminder()` are implemented by each subclass.
- **MeetingEvent / WorkshopEvent / SocialEvent / VolunteerEvent** – each overrides `getType()` and `getReminder()`.
- **EventType** – enum with a label for each type; `fromLabel()` reads the CSV text and `create(...)` builds the right Event subclass.
- **User (abstract)** – private `id, name, email`; abstract `getRole()` and `canManageEvents()`; `User.create(...)` builds an Officer or Member from the CSV role.
- **Officer / Member** – Officer can manage events, Member is view-only.
- **Organization** – name and contact email loaded from `organization.txt`.

## Controller classes
- **EventController** – holds the event list and a `Repository<Event>`. Methods: `getAllEvents`, `findById`, `search(keyword, type)`, `getUpcoming`, `countByType`, `createEvent`, `updateEvent`, `deleteEvent`. It validates input, assigns new IDs, and never prints. Every change is built on a copy and kept only after the file saved successfully.
- **UserController** – holds the members and looks them up by email for login.
- **ConsoleUI** (the console "view") – reads input and prints the Dashboard, View, Search, Create, Update and Delete screens from the proposal; asks the model whether the current user `canManageEvents()`; delete asks for confirmation.

## How data is stored and loaded
- **Load:** at startup `CsvEventRepository`/`CsvUserRepository` read their files line by line, split each row (quote-aware) and rebuild objects. Bad rows are skipped with a warning.
- **Save:** after each create/update/delete, `EventController` calls `saveAll`, which rewrites `events.csv` (header + all events) through a temp file and replaces the original.
- **Update/Delete flow** matches the proposal: the event is selected, the list is modified, and the whole file is rewritten with the remaining/updated events.
- See `data/Data_Files_Description.md` for the file formats.

## OOP principles (where to find them)
| Principle | Where |
|---|---|
| **Encapsulation** | All fields are `private`; access through getters and validating setters (`Event`, `User`, `Organization`). Controllers expose only what the UI needs. |
| **Inheritance** | `Event` → 4 event types; `User` → `Officer`/`Member`; `CsvRepository` → `CsvEventRepository`/`CsvUserRepository` (shared file code written once). |
| **Abstraction** | `Repository<T>` interface hides how data is stored; `ConsoleUI` only talks to controllers; controllers never print. Model, data and presentation are separate packages. |
| **Polymorphism** | `getType()`, `getReminder()`, `getRole()`, `canManageEvents()` are overridden per subclass; the UI and controller call them on `Event`/`User` references without checking the concrete class. `Repository<T>` is used through the interface. |

## Team contributions
_(Fill in before submitting: who worked on which part, and any participation concerns per the team agreement.)_
