# OrgHub – Data Files (Part 1)

All files live in the `data/` folder and are read when the program starts. The program is run from the project folder (or pass the data folder as the first argument).

| File | Type | What it is used for | Read / written by |
|---|---|---|---|
| `events.csv` | CSV | Stores every organization event so events survive closing the app. One row per event. | Read on startup; rewritten after every create, update or delete (`CsvEventRepository`) |
| `members.csv` | CSV | Stores the people in the organization and their role (Officer or Member). The role decides who may create/update/delete events. | Read on startup (`CsvUserRepository`) |
| `organization.txt` | Text (`key=value`) | Basic organization info shown on the dashboard: organization name and contact email. | Read on startup (`Organization.load`) |

## events.csv
First line is a header. Columns:

`id,title,date,time,location,description,type,organizer`

| Column | Format | Example |
|---|---|---|
| id | Unique whole number, assigned automatically (highest ID + 1) | `3` |
| title | Text | `Fall Campus Cleanup` |
| date | `yyyy-MM-dd` | `2026-10-29` |
| time | `h:mm AM/PM` | `9:00 AM` |
| location | Text | `Central Quad` |
| description | Text (single line) | `Spend the morning giving back to campus.` |
| type | `Meeting`, `Workshop`, `Social` or `Volunteer` | `Volunteer` |
| organizer | Text | `Alex Morgan` |

Values that contain a comma or a quote are wrapped in double quotes (a quote inside a value is written as `""`), e.g. `"Student Union, Room 2.02"`.

## members.csv
Header: `id,name,email,role` – `role` is `Officer` or `Member`. The email is what a user types to log in.

## organization.txt
Lines starting with `#` are comments. Keys: `name` and `contactEmail`.

## Robustness
- If `events.csv` or `members.csv` is missing, it is created with just its header row.
- A malformed row is skipped and a warning is printed at startup; the rest of the file still loads.
- Saving writes to a temporary file first and then replaces the real file, so a crash mid-save cannot corrupt the data.
