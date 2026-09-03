# BandBase

A booking-management web application for the live music industry, built with Spring Boot and Vaadin Flow. A booker schedules concerts, builds a stage timetable from bands filtered by genre, and reserves venue halls with automatic conflict detection.

Originally developed as a final project for *Modelling and Analysis of Information Systems* (PJATK, Warsaw), with the emphasis on getting the domain model right rather than on framework plumbing.

**Stack:** Java 21 · Spring Boot 4.0.5 · Spring Data JPA / Hibernate · Vaadin Flow 25 · PostgreSQL · Maven

---

## What it does

**Schedule a concert.** The booker enters the event details, picks one or more genres, and the band selector populates itself with the bands playing those genres. Selecting a band adds a row to the concert timetable where its individual set times can be adjusted. The form validates continuously — the save button stays disabled until the data is complete, every set falls inside the concert window, and no two bands overlap on stage.

**Reserve a hall.** After choosing a concert, the app lists only the halls that are genuinely free in that time slot. Availability is computed by walking the hall's own concert associations and testing for interval overlap; cancelled events don't block a slot. The rule is enforced again in the domain layer at save time, so a stale UI can't push through a double booking.

**Review concerts.** Selecting a concert reveals its details, venue with full address, and the band-by-band running order, all sorted chronologically.

<img width="2560" height="1440" alt="Screenshot 2026-09-03 165925" src="https://github.com/user-attachments/assets/3d26338c-82cd-4180-a55a-19878c1a84fd" />

<img width="2560" height="1440" alt="Screenshot 2026-09-03 165941" src="https://github.com/user-attachments/assets/0a56fdb9-ab1b-428f-a962-69f7976ea010" />

<img width="2560" height="1440" alt="image" src="https://github.com/user-attachments/assets/4343629b-e198-4dee-bf22-10c62bddc030" />

## Domain model

Fifteen entities across three inheritance hierarchies, each mapped with the JPA strategy that fits its shape:

| Hierarchy | Strategy | Why |
|---|---|---|
| `Person` → `Musician`, `Booker`, `Owner` | `SINGLE_TABLE` | Subtypes share most attributes; avoids joins on every read |
| `Event` → `Concert`, `Rehearsal` | `JOINED` | Subtypes diverge substantially; keeps the schema normalised |  
| `Room` → `ConcertHall`, `RehearsalRoom` | `SINGLE_TABLE` | Few subtypes, simple retrieval |

### Design decisions worth pointing at

**Association class instead of `@ManyToMany`.** A band's appearance at a concert carries its own data — the set's start and end time. `BandConcertParticipation` is therefore a first-class entity sitting between `Concert` and `Band`, not a join table. Adding a fee or a stage position later means adding a field, not restructuring the mapping.

**Composition enforced on three levels.** A `Room` cannot exist without its `Building`, and this is guaranteed structurally rather than by convention: cascading with `orphanRemoval` at the persistence level, a non-nullable foreign key at the schema level, and package-private constructors at the language level — rooms can only be created through `Building.createConcertHall()`. Reassigning a room to another building throws.

**Business rules live in the domain, not the UI.** Interval-overlap detection, NIP checksum validation (the real Polish algorithm, weighted digits modulo 11), commission floors, and time-range invariants are all methods on entities. The Vaadin layer calls them and renders the outcome; it never reimplements them. Disabling a button is a convenience, not a guarantee.

**Bidirectional associations kept consistent.** Helper methods update both ends of every relationship, so the in-memory object graph never contradicts what would be written to the database.

**Navigation over queries.** Related objects are reached by walking associations — `concert.getParticipations()`, `genre.getBands()`, `hall.getConcerts()` — rather than by issuing filtered queries. Detached-entity pitfalls are handled by re-attaching inside a transaction and initialising the collections that the view will actually touch, which keeps `LazyInitializationException` out of the presentation layer.

Also implemented: derived transient attributes, multi-valued attributes via `@ElementCollection`, class-level constants with validation, natural business keys, and enums persisted as strings.

## Running it

```bash
createdb pk_mas_s16608                    # PostgreSQL, credentials in application.properties
./mvnw spring-boot:run
```

The app starts on `http://localhost:8081`. Hibernate generates the schema on first run and `data.sql` seeds a working dataset — venues, bands, genres, musicians and a few scheduled concerts — so every screen is populated immediately.

## Layout

```
model/         15 entities, 5 enums — associations, inheritance, business rules
repository/    Spring Data interfaces
service/       transaction boundaries, use-case orchestration
frontend/      Vaadin views (AppLayout shell + three use-case screens)
resources/     configuration and seed data
```

## Notes and limits

Authentication is out of scope — the booker's identity is a fixed ID standing in for a session principal, isolated to a single call in each view. Adding a security layer would mean sourcing that ID from the security context; nothing else changes.

The concert status enum anticipates edit, complete and cancel transitions, but only the planned state is assigned. Cancellation is already accounted for in the availability check, so wiring up the remaining transitions is a matter of adding the state-changing methods.
