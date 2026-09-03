*[English version](README.md)*

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
