package pl.edu.pja.pk_gorski_filip_16608.frontend;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.Route;
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


        saveButton = new Button("Zapisz", event -> {
            try {
                concert.setStatus(ConcertStatus.PLANNED);
                // brak mechanizmu autentykacji — booker ID hardcoded
                concertService.saveConcert(concert, 1L);
                Notification.show("Koncert został zapisany", 3000, Notification.Position.TOP_CENTER);
                UI.getCurrent().navigate(MainView.class);
            } catch (Exception e) {
                Notification.show("Błąd: " + e.getMessage(), 5000, Notification.Position.TOP_CENTER);
            }
        });

        saveButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        saveButton.setEnabled(false);

        cancelButton = new Button("Anuluj", event -> {
            UI.getCurrent().navigate(MainView.class);
        });

        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);


        TextField title = new TextField("Nazwa koncertu");
        title.setWidth("500px");

        DateTimePicker startDateTime = new DateTimePicker("Rozpoczęcie koncertu");
        DateTimePicker endDateTime = new DateTimePicker("Zakończenie koncertu");

        TextField priceOfTicket = new TextField("Cena biletu");
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


        MultiSelectComboBox<Genre> genreSelect = new MultiSelectComboBox<>("Gatunek muzyczny");
        genreSelect.setItems(genreService.getAllGenres());
        genreSelect.setItemLabelGenerator(Genre::getName);
        genreSelect.setWidth("500px");


        MultiSelectComboBox<Band> bandSelect = new MultiSelectComboBox<>("Zespoły muzyczne");
        bandSelect.setItemLabelGenerator(Band::getName);
        bandSelect.setWidth("500px");
        bandSelect.setEnabled(false);
        bandSelect.setTooltipText("Wybierz gatunki muzyczne, aby zobaczyć dostępne zespoły");


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


        Grid<TimetableRow> timetableGrid = new Grid<>();
        timetableGrid.setWidth("800px");

        timetableGrid.addColumn(timetableRow -> timetableRow.bandConcertParticipation.getBand().getName()).setHeader("Zespół").setAutoWidth(true);

        timetableGrid.addComponentColumn(timetableRow -> {
            if (startDateTime.getValue() != null)
                timetableRow.startBandConcert.setMin(startDateTime.getValue().toLocalTime());
            if (endDateTime.getValue() != null)
                timetableRow.startBandConcert.setMax(endDateTime.getValue().toLocalTime());
            timetableRow.startBandConcert.addValueChangeListener(event -> {
                if (event.getValue() != null) {
                    timetableRow.bandConcertParticipation.setStart(event.getValue());
                }
                updateSaveButton();
            });
            return timetableRow.startBandConcert;
        }).setHeader("Godzina rozpoczęcia").setWidth("200px");

        timetableGrid.addComponentColumn(timetableRow -> {
            if (startDateTime.getValue() != null)
                timetableRow.endBandConcert.setMin(startDateTime.getValue().toLocalTime());
            if (endDateTime.getValue() != null)
                timetableRow.endBandConcert.setMax(endDateTime.getValue().toLocalTime());
            timetableRow.endBandConcert.addValueChangeListener(event -> {
                if (event.getValue() != null) {
                    timetableRow.bandConcertParticipation.setEnd(event.getValue());
                }
                updateSaveButton();
            });
            return timetableRow.endBandConcert;
        }).setHeader("Godzina zakończenia").setWidth("200px");

        timetableGrid.addComponentColumn(timetableRow -> {
            Button removeButton = new Button("Usuń");
            removeButton.addClickListener(e -> {
                Set<Band> bands = new HashSet<>(bandSelect.getValue());
                bands.remove(timetableRow.bandConcertParticipation.getBand());
                bandSelect.setValue(bands);
            });
            return removeButton;
        }).setWidth("120px");

        //USTAWIENIE MIN I MAX GODZINY W TIMETABLE DLA KAŻDEGO ZESPOŁU - WG USTALONEJ WCZEŚNIEJ GODZINY ROZPOCZĘCIA I ZAKOŃCZENIA KONCERTU

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

        //UZUPEŁNIENIE GRIDA TIMETABLE ZESPOŁAMI I GODZINAMI

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
            timetableGrid.setItems(timetableRows);

            updateSaveButton();
        });

        HorizontalLayout buttons = new HorizontalLayout(saveButton, cancelButton);
        add(title, startDateTime, endDateTime, priceOfTicket, genreSelect, bandSelect, timetableGrid, buttons);
    }

    boolean hasInvalidTimeRange() {
        for (TimetableRow timetableRow : timetableRows) {
            LocalTime start = timetableRow.startBandConcert.getValue();
            LocalTime end = timetableRow.endBandConcert.getValue();
            if (start == null || end == null || end.isBefore(start)) {
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

    void updateSaveButton() {
        saveButton.setEnabled(
                binder.isValid()
                        && !concert.getParticipations().isEmpty()
                        && !hasInvalidTimeRange()
                        && !hasOverlaps()
        );
    }

    private static class TimetableRow {

        final BandConcertParticipation bandConcertParticipation;
        final TimePicker startBandConcert = new TimePicker();
        final TimePicker endBandConcert = new TimePicker();

        TimetableRow(BandConcertParticipation participation) {
            this.bandConcertParticipation = participation;
            startBandConcert.setValue(participation.getStart());
            endBandConcert.setValue(participation.getEnd());
        }

    }
}