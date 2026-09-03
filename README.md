*[English version](README.md)*

*[Wersja polska](README.pl.md)*

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

# BandBase

Aplikacja webowa do zarządzania bookingiem koncertów. Booker planuje wydarzenie, układa rozpiskę występów wybierając zespoły filtrowane po gatunkach muzycznych i rezerwuje salę koncertową z automatycznym wykrywaniem kolizji terminów.

Projekt powstał jako praca końcowa z przedmiotu *Modelowanie i Analiza Systemów informacyjnych* (PJATK), z naciskiem na poprawność modelu dziedzinowego, a nie na obudowę frameworkową.

**Stos:** Java 21 · Spring Boot 4.0.5 · Spring Data JPA / Hibernate · Vaadin Flow 25 · PostgreSQL · Maven

---

## Funkcjonalność

**Dodanie koncertu.** Booker wprowadza dane wydarzenia i wybiera gatunki muzyczne — lista zespołów wypełnia się wtedy tymi, które te gatunki grają. Zaznaczenie zespołu dodaje wiersz do rozpiski, gdzie można ustawić indywidualne godziny występu. Formularz waliduje się na bieżąco: przycisk zapisu pozostaje nieaktywny, dopóki dane nie są kompletne, każdy występ mieści się w ramach koncertu, a godziny zespołów na siebie nie nachodzą.

**Przypisanie sali.** Po wyborze koncertu aplikacja pokazuje wyłącznie sale faktycznie wolne w jego terminie. Dostępność wyliczana jest przez przejście po asocjacji sali z koncertami i test nachodzenia przedziałów czasowych; koncerty anulowane nie blokują terminu. Ta sama reguła jest egzekwowana ponownie w warstwie modelu przy zapisie, więc rozjechany stan interfejsu nie przepuści podwójnej rezerwacji.

**Przegląd koncertów.** Wybór koncertu odsłania jego szczegóły, salę z pełnym adresem oraz rozpiskę zespołów uporządkowaną chronologicznie.

## Model dziedzinowy

Piętnaście encji w trzech hierarchiach dziedziczenia, każda odwzorowana strategią JPA dobraną do jej charakteru:

| Hierarchia | Strategia | Uzasadnienie |
|---|---|---|
| `Person` → `Musician`, `Booker`, `Owner` | `SINGLE_TABLE` | Podtypy dzielą większość atrybutów; brak złączeń przy każdym odczycie |
| `Event` → `Concert`, `Rehearsal` | `JOINED` | Podtypy istotnie się różnią; schemat pozostaje znormalizowany |
| `Room` → `ConcertHall`, `RehearsalRoom` | `SINGLE_TABLE` | Niewiele podtypów, proste pobieranie |

### Decyzje projektowe

**Klasa asocjacyjna zamiast `@ManyToMany`.** Występ zespołu na koncercie niesie własne dane — godziny rozpoczęcia i zakończenia setu. `BandConcertParticipation` jest więc pełnoprawną encją pomiędzy `Concert` a `Band`, a nie tabelą łączącą. Dodanie honorarium czy pozycji na scenie oznacza dopisanie pola, a nie przebudowę mapowania.

**Kompozycja wymuszona na trzech poziomach.** `Room` nie może istnieć bez swojego `Building`, i jest to zagwarantowane strukturalnie, nie umownie: kaskadą z `orphanRemoval` na poziomie persystencji, kluczem obcym `NOT NULL` na poziomie schematu oraz pakietowymi konstruktorami na poziomie języka — salę da się utworzyć wyłącznie przez `Building.createConcertHall()`. Próba przeniesienia sali do innego budynku rzuca wyjątek.

**Reguły biznesowe w modelu, nie w interfejsie.** Wykrywanie nachodzenia terminów, walidacja NIP (rzeczywisty polski algorytm sumy kontrolnej — wagi cyfr modulo 11), minimalna prowizja i niezmienniki zakresów czasowych to metody encji. Warstwa Vaadina je wywołuje i prezentuje wynik; nigdy ich nie powiela. Wygaszenie przycisku jest wygodą, nie gwarancją.

**Spójność asocjacji dwukierunkowych.** Metody pomocnicze aktualizują obie strony każdej relacji, więc graf obiektów w pamięci nigdy nie zaprzecza temu, co trafi do bazy.

**Nawigacja zamiast zapytań.** Obiekty powiązane pobierane są przez przejście po asocjacjach — `concert.getParticipations()`, `genre.getBands()`, `hall.getConcerts()` — a nie zapytaniami filtrującymi. Problem encji odłączonych rozwiązywany jest przez ponowne pobranie w transakcji i inicjalizację tych kolekcji, których widok faktycznie użyje, dzięki czemu `LazyInitializationException` nie pojawia się w warstwie prezentacji.

Zaimplementowane również: atrybuty pochodne (`@Transient`), atrybuty wielowartościowe (`@ElementCollection`), atrybut klasowy z walidacją, naturalne klucze biznesowe oraz enumy utrwalane jako tekst.

## Uruchomienie

```bash
createdb pk_mas_s16608                    # PostgreSQL, dane dostępowe w application.properties
./mvnw spring-boot:run
```

Aplikacja startuje pod `http://localhost:8081`. Hibernate generuje schemat przy pierwszym uruchomieniu, a `data.sql` wgrywa komplet danych przykładowych — kluby, zespoły, gatunki, muzyków i zaplanowane koncerty — więc każdy ekran jest od razu wypełniony.

## Struktura

```
model/         15 encji, 5 enumów — asocjacje, dziedziczenie, reguły biznesowe
repository/    interfejsy Spring Data
service/       granice transakcji, orkiestracja przypadków użycia
frontend/      widoki Vaadin (powłoka AppLayout + trzy ekrany przypadków użycia)
resources/     konfiguracja i dane startowe
```

## Ograniczenia

Uwierzytelnianie wykracza poza zakres projektu — tożsamość bookera to stały identyfikator zastępujący podmiot z sesji, odizolowany do pojedynczego wywołania w każdym widoku. Dodanie warstwy bezpieczeństwa oznaczałoby pobranie tego identyfikatora z kontekstu bezpieczeństwa; reszta kodu pozostaje bez zmian.

Enum statusu koncertu przewiduje przejścia edycji, realizacji i anulowania, ale nadawany jest wyłącznie stan zaplanowany. Anulowanie jest już uwzględnione w sprawdzaniu dostępności sal, więc dołożenie pozostałych przejść sprowadza się do dopisania metod zmieniających stan.
