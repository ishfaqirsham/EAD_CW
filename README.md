🏨 Grand Palm Hotel — Room Reservation Management System

A desktop hotel reservation and billing system built with Java Swing and
MySQL, following an MVC-style layered architecture (UI → Controller →
DAO → Database). Built as coursework for Enterprise Application Development.


✨ Features


Login system with role-based access (Admin / Receptionist)
Customer management — add, update, search customers (name, phone, email,
NIC/passport, nationality, address) with input validation
Room management — fixed room inventory across multiple categories
(Standard, Deluxe, Suite), each with its own nightly rate
Reservations — book a room for a customer over a date range, with:

Automatic price calculation (nights × room rate)
Strict date validation (rejects impossible dates, e.g. day 88 of a month)
Conflict checking so a room can't be double-booked
Live status tracking: Active → Completed (paid) or Cancelled



Payments — record a payment against an active reservation:

Amount due auto-fills from the reservation total (no manual lookup/typing)
Recording a payment automatically marks the reservation Completed and
frees the room



Reports — a JasperReports-generated Reservation Summary Report showing
only Completed (paid) reservations, with a total revenue figure that
reflects real, settled income
Dashboard — live counts of customers, rooms, availability, reservations,
and total revenue



🗂️ Project structure

HotelReservationSystem/
├── src/com/hotel/
│   ├── ui/            → Swing screens (Login, Dashboard, Customer, Room,
│   │                     Reservation, Payment, Report)
│   ├── controller/    → Business logic / validation layer between UI and DAO
│   ├── dao/           → Data access objects (raw SQL against MySQL)
│   ├── model/         → Plain model classes (Customer, Room, Reservation, Payment)
│   ├── exception/      → Custom exceptions (e.g. InvalidBookingException)
│   ├── report/        → JasperReports .jrxml report design(s)
│   └── util/          → DBConnection (singleton MySQL connection helper)
├── sql/
│   ├── hotel_db.sql              → creates the database schema
│   └── sample_data_clean_v2.sql  → demo data (5 customers, mixed reservation statuses)
└── dist/                          → build output (JAR + libraries)


🛠️ Tech stack


Java (Swing, NetBeans GUI Builder / Matisse)
MySQL (via JDBC — mysql-connector-j)
JasperReports for the printable reservation summary report
NetBeans IDE with an Ant-based build (no Maven)



🚀 Getting started

1. Database setup


Start MySQL (e.g. via XAMPP) and open phpMyAdmin.
Run sql/hotel_db.sql to create the database and tables.
Run sql/sample_data_clean_v2.sql to load demo data:

5 customers, 10 fixed rooms across 3 categories
5 reservations: 2 Completed (paid), 2 Active (awaiting payment), 1 Cancelled





2. Project setup in NetBeans


Open the project in NetBeans (File → Open Project).
Check Properties → Libraries and confirm these are attached:

mysql-connector-j (JDBC driver)
jasperreports + its dependencies (commons-collections4, jackson-*,
itextpdf, poi, poi-ooxml, stax2-api, woodstox-core, xercesImpl)



Open src/com/hotel/util/DBConnection.java and confirm the DB URL,
username, and password match your local MySQL setup.
Right-click the project → Clean and Build.
Run the project — it opens on the Login screen.


3. Login

RoleUsernamePasswordAdminadminadmin123Receptionistreceptionistreception123

(Adjust to match your actual seeded login credentials if different.)


🧾 How reservation status works

StatusMeaningActiveRoom is booked, but payment hasn't been recorded yetCompletedPayment has been recorded in full — counts toward revenue and appears in reportsCancelledBooking was cancelled — room is freed back to Available, excluded from revenue/reports

Only Completed reservations are counted in the Dashboard's revenue figure
and included in the Reservation Summary Report — this ensures the numbers
shown always reflect real, settled income rather than pending or cancelled
bookings.


📊 Reports

The Reservation Summary Report is generated using JasperReports:


Design file: src/com/hotel/report/ReservationSummaryReport.jrxml
Compiled at runtime into a .jasper file and rendered in a JasperViewer window
Query filters to WHERE res.status = 'Completed' so cancelled/unpaid
bookings never appear in the printed report or its revenue total



✅ Validation implemented


Customer name cannot be empty
Phone number must be exactly 10 digits
Email must match a proper name@domain.tld pattern
Check-in/check-out dates must be valid real calendar dates (strict parsing)
Check-out date must be after check-in date
Payment amount cannot be empty, non-numeric, zero, or negative
Room booking conflict check prevents double-booking the same room for
overlapping dates



🧩 Design notes


Layered architecture: UI screens never talk to the database directly —
they call a Controller, which applies validation/business rules and calls
a DAO, which runs the actual SQL.
Singleton pattern: DBConnection provides one shared database connection
across the app.
Custom exception: InvalidBookingException is thrown from the
reservation controller for invalid dates or booking conflicts, and caught
in the UI to show a friendly error dialog instead of crashing.



📌 Known limitations


Each reservation currently supports exactly one full payment (no partial/
deposit payments) — recording a payment immediately marks the reservation
as Completed.
No automated tests included; validation is manual/UI-level only.



Coursework project — for academic use.
