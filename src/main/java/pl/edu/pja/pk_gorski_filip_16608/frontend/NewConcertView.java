package pl.edu.pja.pk_gorski_filip_16608.frontend;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.Route;
import lombok.Getter;
import pl.edu.pja.pk_gorski_filip_16608.model.*;
import pl.edu.pja.pk_gorski_filip_16608.service.BandService;
import pl.edu.pja.pk_gorski_filip_16608.service.ConcertService;
import pl.edu.pja.pk_gorski_filip_16608.service.GenreService;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Route(value = "concertform", layout = MainView.class)
public class NewConcertView extends VerticalLayout {

    private final GenreService genreService;
    private final BandService bandService;
    private final ConcertService concertService;

    private final Concert concert = new Concert();

    private final Binder<Concert> binder = new Binder<>(Concert.class);

    private final List<TimetableRow> timetableRows = new ArrayList<>();

    private final Button saveButton;

    private final Button cancelButton;


    public NewConcertView(GenreService genreService, BandService bandService, ConcertService concertService) {
        this.genreService = genreService;
        this.bandService = bandService;
        this.concertService = concertService;

        //PRZYCISK ZAPISU I ANULOWANIA

        saveButton = new Button("Zapisz", event -> {
            UI.getCurrent().navigate(MainView.class);
        });

        saveButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        saveButton.setEnabled(false);

        cancelButton = new Button("Anuluj", event -> {
            UI.getCurrent().navigate(MainView.class);
        });

        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        //FORMULARZ

        TextField title = new TextField("Nazwa koncertu");
        title.setWidth("500px");

        DateTimePicker startDateTime = new DateTimePicker("Rozpoczęcie koncertu");
        DateTimePicker endDateTime = new DateTimePicker("Zakończenie koncertu");

        TextField priceOfTicket = new TextField("Cena bilteu");
        priceOfTicket.setWidth("170px");

        //WALIDACJA FORMULARZA Z WYKORZYSTANIEM BINDERA (WALIDATOR ZWRACA TRUE)

        binder.forField(title).asRequired("Nazwa koncertu jest wymagana").bind(Event::getName, Event::setName);

        binder.forField(startDateTime).asRequired("Data i godzina startu są wymagane").withValidator(start -> endDateTime.getValue() == null || start.isBefore(endDateTime.getValue()), "Start koncertu musi być przed jego końcem").bind(Event::getStart, Event::setStart);

        binder.forField(endDateTime).asRequired("Data i godzina zakończenia są wymagane").withValidator(end -> startDateTime.getValue() == null || end.isAfter(startDateTime.getValue()), "Koniec koncertu musi być po jego starcie").bind(Event::getEnd, Event::setEnd);

        binder.forField(priceOfTicket).asRequired("Cena biletu jest wymagana").withValidator(price -> {
            try {
                return new BigDecimal(price).compareTo(BigDecimal.ZERO) >= 0;
            } catch (NumberFormatException ex) {
                return false;
            }
        }, "Cena biletu nie może być liczbą ujemną, ani napisem").bind(c -> c.getTicketPrice() != null ? c.getTicketPrice().toString() : "", (c, price) -> c.setTicketPrice(new BigDecimal(price)));

        //PRZYPISANIE WARTOŚCI Z FORMULARZA DO OBIEKTU CONCERT

        binder.setBean(concert);

        binder.addStatusChangeListener(event -> updateSaveButton());

        //UZUPEŁNIWNIE GATUNKÓW

        MultiSelectComboBox<Genre> genreSelect = new MultiSelectComboBox<>("Gatunek muzyczny");
        genreSelect.setItems(genreService.getAllGenres());
        genreSelect.setItemLabelGenerator(Genre::getName);
        genreSelect.setWidth("500px");

        //UTOWRZENIE COMBOBOX DLA ZESPOŁÓW

        MultiSelectComboBox<Band> bandSelect = new MultiSelectComboBox<>("Zespoły muzyczne");
        bandSelect.setItemLabelGenerator(Band::getName);
        bandSelect.setWidth("500px");
        bandSelect.setEnabled(false);
        bandSelect.setTooltipText("Wybierz gatunki muzyczne, aby zobaczyć dostępne zespoły");

        //ZAZNACZENIE GATUNKÓW I UZUPEŁNIENIE COMBOBOXA Z ZESP0ŁAMI

        genreSelect.addValueChangeListener(event -> {
            Set<Genre> selected = event.getValue();

            if (selected == null || selected.isEmpty()) {
                bandSelect.setEnabled(false);
            } else {
                Set<Band> availableBands = bandService.getBandsByGenres(selected);
                bandSelect.setItems(availableBands);
                bandSelect.setEnabled(true);
            }
        });


//        bandSelect.addValueChangeListener(event -> {
//            Set<Band> selectedBands = event.getValue();
//
//            List<BandConcertParticipation> existing = new ArrayList<>(concert.getParticipations());
//
//            LocalTime defaultStart = startDateTime.getValue() != null
//                    ? startDateTime.getValue().toLocalTime() : LocalTime.of(20, 0);
//            LocalTime defaultEnd = endDateTime.getValue() != null
//                    ? endDateTime.getValue().toLocalTime() : LocalTime.of(22, 0);
//
//
//            for (BandConcertParticipation bandConcertParticipation : existing) {
//                if (!selectedBands.contains(bandConcertParticipation.getBand())) {
//                    concert.getParticipations().remove(bandConcertParticipation);
//                }
//            }
//
//            Set<Band> existingBands = existing.stream()
//                    .map(BandConcertParticipation::getBand)
//                    .collect(Collectors.toSet());
//
//            for (Band band : selectedBands) {
//                if (!existingBands.contains(band)) {
//                    BandConcertParticipation bandConcertParticipation = new BandConcertParticipation(concert, band, defaultStart, defaultEnd);
//                    concert.getParticipations().add(bandConcertParticipation);
//                }
//            }
//
//            participationRows.clear();
//            for (BandConcertParticipation bandConcertParticipation : concert.getParticipations()) {
//                participationRows.add(new ParticipationRow(bandConcertParticipation));
//            }
//            grid.setItems(participationRows);
//            validateOverlaps(saveButton);
//        });


        //GRID DO UTWORZEBNIA TIMETABLE KONCERTU

        Grid<TimetableRow> grid = new Grid<>();
        grid.setWidth("800px");

        grid.addColumn(timetableRow -> timetableRow.bandConcertParticipation.getBand().getName()).setHeader("Zespół").setAutoWidth(true);

        grid.addComponentColumn(timetableRow -> {
            if (startDateTime.getValue() != null)
                timetableRow.startBandConcert.setMin(startDateTime.getValue().toLocalTime());
            if (endDateTime.getValue() != null)
                timetableRow.startBandConcert.setMax(endDateTime.getValue().toLocalTime());
            timetableRow.startBandConcert.addValueChangeListener(e -> {
                timetableRow.bandConcertParticipation.setStart(e.getValue());

                updateSaveButton();
            });


            return timetableRow.startBandConcert;
        }).setHeader("Godzina rozpoczęcia").setWidth("200px");

        grid.addComponentColumn(timetableRow -> {
            if (startDateTime.getValue() != null)
                timetableRow.endBandConcert.setMin(startDateTime.getValue().toLocalTime());
            if (endDateTime.getValue() != null)
                timetableRow.endBandConcert.setMax(endDateTime.getValue().toLocalTime());
            timetableRow.endBandConcert.addValueChangeListener(e -> {
                timetableRow.bandConcertParticipation.setEnd(e.getValue());

                updateSaveButton();
            });
            return timetableRow.endBandConcert;
        }).setHeader("Godzina zakończenia").setWidth("200px");

        grid.addComponentColumn(timetableRow -> {
            Button removeButton = new Button("Usuń");
            removeButton.addClickListener(e -> {
                Set<Band> bands = new HashSet<>(bandSelect.getValue());
                bands.remove(timetableRow.bandConcertParticipation.getBand());
                bandSelect.setValue(bands);
            });
            return removeButton;
        }).setWidth("120px");

        //USTAWIENIE MIN I MAX GODZINY W TIMETABLE DLA KAŻDEGO ZESPOŁU - WG USTALONEJ WCZEŚNIEJ GODZINY ROZOCZĘCIA I ZAKOŃCZEIA KONCERTU

        startDateTime.addValueChangeListener(e -> {
            LocalTime min = e.getValue() != null ? e.getValue().toLocalTime() : null;
            timetableRows.forEach(row -> {
                row.startBandConcert.setMin(min);
                row.endBandConcert.setMin(min);
            });
        });

        endDateTime.addValueChangeListener(e -> {
            LocalTime max = e.getValue() != null ? e.getValue().toLocalTime() : null;
            timetableRows.forEach(row -> {
                row.startBandConcert.setMax(max);
                row.endBandConcert.setMax(max);
            });
        });

        //UZUPEŁNEINIE GRIDA ZESPOŁAMI I GODZINAMI - TIMETABLE

        bandSelect.addValueChangeListener(event -> {
            LocalTime defaultStart = startDateTime.getValue() != null ? startDateTime.getValue().toLocalTime() : LocalTime.of(20, 0);
            LocalTime defaultEnd = endDateTime.getValue() != null ? endDateTime.getValue().toLocalTime() : LocalTime.of(22, 0);

            Set<Band> newSelectedBands = event.getValue();
            Set<Band> oldSelectedBands = event.getOldValue();

            for (Band band : newSelectedBands) {
                if (!oldSelectedBands.contains(band)) {
                    BandConcertParticipation bandConcertParticipation = new BandConcertParticipation(concert, band, defaultStart, defaultEnd);
                    concert.getParticipations().add(bandConcertParticipation);
                    timetableRows.add(new TimetableRow(bandConcertParticipation));
                }
            }

            for (Band band : oldSelectedBands) {
                if (!newSelectedBands.contains(band)) {
                    timetableRows.removeIf(timetableRow ->
                            timetableRow.bandConcertParticipation.getBand().equals(band));
                    concert.getParticipations().removeIf(bandConcertParticipation ->
                            bandConcertParticipation.getBand().equals(band));
                }
            }

            grid.setItems(timetableRows);

            updateSaveButton();
        });


        HorizontalLayout buttons = new HorizontalLayout(saveButton, cancelButton);
        add(title, startDateTime, endDateTime, priceOfTicket, genreSelect, bandSelect, grid, buttons);
    }

    boolean hasInvalidTimeRange() {
        for (TimetableRow timetableRow : timetableRows) {
            if (!timetableRow.endBandConcert.getValue().isAfter(timetableRow.startBandConcert.getValue())) {
                return true;
            }
        }
        return false;
    }

    boolean hasOverlaps() {
        for (int i = 0; i < timetableRows.size(); i++) {
            for (int j = i + 1; j < timetableRows.size(); j++) {
                if (timetableRows.get(i).startBandConcert.getValue().isBefore(timetableRows.get(j).endBandConcert.getValue())
                        && timetableRows.get(i).endBandConcert.getValue().isAfter(timetableRows.get(j).startBandConcert.getValue())) {
                    return true;
                }
            }
        }
        return false;
    }

    void updateSaveButton(){
        saveButton.setEnabled(
                binder.isValid()
                && !concert.getParticipations().isEmpty()
                && !hasOverlaps()
                && !hasInvalidTimeRange()
        );
    }

    private static class TimetableRow {
        @Getter
        final BandConcertParticipation bandConcertParticipation;
        final TimePicker startBandConcert = new TimePicker();
        final TimePicker endBandConcert = new TimePicker();

        TimetableRow(BandConcertParticipation participation) {
            this.bandConcertParticipation = participation;
            startBandConcert.setValue(participation.getStart());
            endBandConcert.setValue(participation.getEnd());
        }

    }


//    private final GenreService genreService;
//    private final BandService bandService;
//    private final ConcertService concertService;
//
//    private final Concert concert = new Concert();
//    private final Binder<Concert> binder = new Binder<>(Concert.class);
//    private final List<ParticipationRow> participationRows = new ArrayList<>();
//    // flaga zapobiegająca cyklicznemu wywołaniu listenera bandSelect, gdy genreSelect programowo zmienia jego items/value
//    private boolean suppressBandListener = false;
//
//    public NewConcertView(GenreService genreService, BandService bandService,
//                          ConcertService concertService) {
//
//        this.genreService = genreService;
//        this.bandService = bandService;
//        this.concertService = concertService;
//
//        TextField title = new TextField("Nazwa koncertu");
//        title.setWidth("500px");
//
//        DateTimePicker startTime = new DateTimePicker("Start");
//        DateTimePicker endTime = new DateTimePicker("Koniec");
//
//        TextField priceOfTicket = new TextField("Cena biletu");
//        priceOfTicket.setWidth("300px");
//
//        MultiSelectComboBox<Genre> genreSelect = new MultiSelectComboBox<>("Gatunek muzyczny");
//        genreSelect.setItems(genreService.getAllGenres());
//        genreSelect.setItemLabelGenerator(Genre::getName);
//        genreSelect.setWidth("500px");
//
//        MultiSelectComboBox<Band> bandSelect = new MultiSelectComboBox<>("Zespoły");
//        bandSelect.setItemLabelGenerator(Band::getName);
//        bandSelect.setWidth("500px");
//        bandSelect.setEnabled(false);
//        bandSelect.setTooltipText("Wybierz gatunki, aby zobaczyć dostępne zespoły");
//
//        Button saveButton = new Button("Zapisz", e ->
//                UI.getCurrent().navigate(MainView.class));
//        saveButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
//        saveButton.setEnabled(false);
//
//        Grid<ParticipationRow> grid = new Grid<>();
//        grid.setWidth("800px");
//
//        grid.addColumn(row -> row.participation.getBand().getName())
//                .setHeader("Zespół").setAutoWidth(true);
//
//        grid.addComponentColumn(row -> {
//            if (startTime.getValue() != null) row.startPicker.setMin(startTime.getValue().toLocalTime());
//            if (endTime.getValue() != null) row.startPicker.setMax(endTime.getValue().toLocalTime());
//            row.startPicker.addValueChangeListener(e -> {
//                row.participation.setStart(e.getValue());
//                validateParticipationTimeRange(row);
//                validateOverlaps(saveButton);
//            });
//            return row.startPicker;
//        }).setHeader("Godzina rozpoczęcia").setWidth("200px");
//
//        grid.addComponentColumn(row -> {
//            if (startTime.getValue() != null) row.endPicker.setMin(startTime.getValue().toLocalTime());
//            if (endTime.getValue() != null) row.endPicker.setMax(endTime.getValue().toLocalTime());
//            row.endPicker.addValueChangeListener(e -> {
//                row.participation.setEnd(e.getValue());
//                validateParticipationTimeRange(row);
//                validateOverlaps(saveButton);
//            });
//            return row.endPicker;
//        }).setHeader("Godzina zakończenia").setWidth("200px");
//
//        grid.addComponentColumn(row -> {
//            // usunięcie zespołu przez zmianę wartości bandSelect, a nie bezpośrednio z listy — deleguje do bandSelect listenera, który spójnie aktualizuje participations i grid
//            Button removeBtn = new Button("Usuń");
//            removeBtn.addClickListener(e -> {
//                Set<Band> current = new HashSet<>(bandSelect.getValue());
//                current.remove(row.participation.getBand());
//                bandSelect.setValue(current);
//            });
//            return removeBtn;
//        }).setWidth("120px");
//
//        startTime.addValueChangeListener(e -> {
//            LocalTime min = e.getValue() != null ? e.getValue().toLocalTime() : null;
//            participationRows.forEach(row -> {
//                row.startPicker.setMin(min);
//                row.endPicker.setMin(min);
//            });
//        });
//
//        endTime.addValueChangeListener(e -> {
//            LocalTime max = e.getValue() != null ? e.getValue().toLocalTime() : null;
//            participationRows.forEach(row -> {
//                row.startPicker.setMax(max);
//                row.endPicker.setMax(max);
//            });
//        });
//
//
//        genreSelect.addValueChangeListener(event -> {
//            Set<Genre> selected = event.getValue();
//            suppressBandListener = true;
//            if (selected == null || selected.isEmpty()) {
//                bandSelect.setItems();
//                bandSelect.setEnabled(false);
//
//                concert.getParticipations().clear();
//                participationRows.clear();
//                grid.setItems(participationRows);
//                validateOverlaps(saveButton);
//            } else {
//                Set<Band> previouslySelected = new HashSet<>(bandSelect.getValue());
//                Set<Band> availableBands = bandService.getBandsByGenres(selected);
//
//                bandSelect.setItems(availableBands);
//                bandSelect.setEnabled(true);
//
//                Set<Band> retained = previouslySelected.stream()
//                        .filter(availableBands::contains)
//                        .collect(Collectors.toSet());
//                bandSelect.setValue(retained);
//
//                concert.getParticipations().removeIf(cp -> !retained.contains(cp.getBand()));
//                participationRows.clear();
//                for (BandConcertParticipation cp : concert.getParticipations()) {
//                    participationRows.add(new ParticipationRow(cp));
//                }
//                grid.setItems(participationRows);
//                validateOverlaps(saveButton);
//            }
//            suppressBandListener = false;
//        });
//
//
//        bandSelect.addValueChangeListener(event -> {
//            if (suppressBandListener) return;
//            Set<Band> selectedBands = event.getValue();
//
//            List<BandConcertParticipation> existing = new ArrayList<>(concert.getParticipations());
//
//
//            LocalTime defaultStart = startTime.getValue() != null
//                    ? startTime.getValue().toLocalTime() : LocalTime.of(20, 0);
//            LocalTime defaultEnd = endTime.getValue() != null
//                    ? endTime.getValue().toLocalTime() : LocalTime.of(22, 0);
//
//
//            for (BandConcertParticipation bandConcertParticipation : existing) {
//                if (!selectedBands.contains(bandConcertParticipation.getBand())) {
//                    concert.getParticipations().remove(bandConcertParticipation);
//                }
//            }
//
//            Set<Band> existingBands = existing.stream()
//                    .map(BandConcertParticipation::getBand)
//                    .collect(Collectors.toSet());
//
//            for (Band band : selectedBands) {
//                if (!existingBands.contains(band)) {
//                    BandConcertParticipation bandConcertParticipation = new BandConcertParticipation(concert, band, defaultStart, defaultEnd);
//                    concert.getParticipations().add(bandConcertParticipation);
//                }
//            }
//
//            participationRows.clear();
//            for (BandConcertParticipation bandConcertParticipation : concert.getParticipations()) {
//                participationRows.add(new ParticipationRow(bandConcertParticipation));
//            }
//            grid.setItems(participationRows);
//            validateOverlaps(saveButton);
//        });
//
//        binder.forField(title)
//                .asRequired("Nazwa koncertu jest wymagana")
//                .bind(Event::getName, Event::setName);
//
//        binder.forField(startTime)
//                .asRequired("Data i godzina startu są wymagane")
//                .withValidator(
//                        s -> endTime.getValue() == null || s.isBefore(endTime.getValue()),
//                        "Start musi być przed końcem"
//                )
//                .bind(Event::getStart, Event::setStart);
//
//        binder.forField(endTime)
//                .asRequired("Data i godzina zakończenia są wymagane")
//                .withValidator(
//                        e -> startTime.getValue() == null || e.isAfter(startTime.getValue()),
//                        "Koniec musi być po starcie"
//                )
//                .bind(Event::getEnd, Event::setEnd);
//
//        binder.forField(priceOfTicket)
//                .asRequired("Cena biletu jest wymagana")
//                .withValidator(price -> {
//                    try {
//                        return new BigDecimal(price).compareTo(BigDecimal.ZERO) > 0;
//                    } catch (NumberFormatException ex) {
//                        return false;
//                    }
//                }, "Cena biletu musi być liczbą większą od 0")
//                .bind(
//                        c -> c.getTicketPrice() != null ? c.getTicketPrice().toString() : "",
//                        (c, price) -> c.setTicketPrice(new BigDecimal(price))
//                );
//
//        binder.addStatusChangeListener(event -> updateSaveButton(saveButton));
//
//        saveButton.addClickListener(event -> {
//            if (hasOverlaps()) {
//                Notification.show("Nie można zapisać — godziny zespołów nakładają się",
//                        3000, Notification.Position.TOP_CENTER);
//                return;
//            }
//            try {
//                concert.setName(title.getValue());
//                concert.setStart(startTime.getValue());
//                concert.setEnd(endTime.getValue());
//                concert.setDate(startTime.getValue().toLocalDate());
//                concert.setTicketPrice(new BigDecimal(priceOfTicket.getValue()));
//                concert.setStatus(ConcertStatus.PLANNED);
//
//                // brak mechanizmu autentykacji — booker ID hardcoded
//                concertService.saveConcert(concert, 1L);
//                Notification.show("Koncert został zapisany", 3000, Notification.Position.TOP_CENTER);
//            } catch (Exception e) {
//                Notification.show("Błąd: " + e.getMessage(), 5000, Notification.Position.TOP_CENTER);
//            }
//        });
//
//        Button cancelButton = new Button("Anuluj", e ->
//                UI.getCurrent().navigate(MainView.class));
//        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
//
//        HorizontalLayout buttons = new HorizontalLayout(saveButton, cancelButton);
//        add(title, startTime, endTime, priceOfTicket, genreSelect, bandSelect, grid, buttons);
//    }
//
//    private void validateOverlaps(Button saveButton) {
//        List<BandConcertParticipation> list = new ArrayList<>(concert.getParticipations());
//        for (int i = 0; i < list.size(); i++) {
//            for (int j = i + 1; j < list.size(); j++) {
//                BandConcertParticipation a = list.get(i);
//                BandConcertParticipation b = list.get(j);
//                if (a.getStart().isBefore(b.getEnd()) && a.getEnd().isAfter(b.getStart())) {
//                    Notification.show(
//                            "Godziny zespołów " + a.getBand().getName() + " i " + b.getBand().getName() + " nakładają się",
//                            3000, Notification.Position.TOP_CENTER);
//                    saveButton.setEnabled(false);
//                    return;
//                }
//            }
//        }
//        updateSaveButton(saveButton);
//    }
//
//    private boolean hasOverlaps() {
//        List<BandConcertParticipation> list = new ArrayList<>(concert.getParticipations());
//        for (int i = 0; i < list.size(); i++) {
//            for (int j = i + 1; j < list.size(); j++) {
//                BandConcertParticipation a = list.get(i);
//                BandConcertParticipation b = list.get(j);
//                if (a.getStart().isBefore(b.getEnd()) && a.getEnd().isAfter(b.getStart())) {
//                    return true;
//                }
//            }
//        }
//        return false;
//    }
//
//    private void validateParticipationTimeRange(ParticipationRow row) {
//        boolean invalid = row.participation.getStart() != null && row.participation.getEnd() != null
//                && !row.participation.getStart().isBefore(row.participation.getEnd());
//        String error = invalid ? "Start musi być przed końcem" : null;
//        row.startPicker.setInvalid(invalid);
//        row.startPicker.setErrorMessage(error);
//        row.endPicker.setInvalid(invalid);
//        row.endPicker.setErrorMessage(error);
//    }
//
//    private boolean hasInvalidTimeRanges() {
//        for (BandConcertParticipation cp : concert.getParticipations()) {
//            if (cp.getStart() != null && cp.getEnd() != null
//                    && !cp.getStart().isBefore(cp.getEnd())) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    private void updateSaveButton(Button saveButton) {
//        saveButton.setEnabled(binder.isValid() && !concert.getParticipations().isEmpty()
//                && !hasOverlaps() && !hasInvalidTimeRanges());
//    }
//
//    private static class ParticipationRow {
//        final BandConcertParticipation participation;
//        final TimePicker startPicker = new TimePicker();
//        final TimePicker endPicker = new TimePicker();
//
//        ParticipationRow(BandConcertParticipation participation) {
//            this.participation = participation;
//            startPicker.setValue(participation.getStart());
//            endPicker.setValue(participation.getEnd());
//        }
//    }
}