package pl.edu.pja.pk_gorski_filip_16608.frontend;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import pl.edu.pja.pk_gorski_filip_16608.model.Concert;
import pl.edu.pja.pk_gorski_filip_16608.model.ConcertHall;
import pl.edu.pja.pk_gorski_filip_16608.service.ConcertService;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Route(value = "assignhall", layout = MainView.class)
public class NewHallView extends VerticalLayout {

    private final ConcertService concertService;
    private final List<Checkbox> hallCheckboxes = new ArrayList<>();
    private ConcertHall selectedHall;

    public NewHallView(ConcertService concertService) {
        this.concertService = concertService;

        ComboBox<Concert> concertSelect = new ComboBox<>("Wybierz koncert");
        concertSelect.setItems(concertService.findConcertsWithoutHall());
        concertSelect.setItemLabelGenerator(c -> c.getName() + " (" + c.getDate() + ")");
        concertSelect.setWidth("500px");

        Span concertDetails = new Span();

        Grid<ConcertHall> hallGrid = new Grid<>();
        hallGrid.setWidth("900px");
        hallGrid.setSelectionMode(Grid.SelectionMode.NONE);
        hallGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);

        Button assignButton = new Button("Przypisz salę");
        assignButton.setEnabled(false);

        hallGrid.addComponentColumn(hall -> {
            Checkbox checkbox = new Checkbox();
            hallCheckboxes.add(checkbox);
            checkbox.addValueChangeListener(e -> {
                if (e.getValue()) {
                    selectedHall = hall;
                    hallCheckboxes.stream()
                            .filter(cb -> cb != checkbox)
                            .forEach(cb -> cb.setValue(false));
                    assignButton.setEnabled(concertSelect.getValue() != null);
                } else if (selectedHall == hall) {
                    selectedHall = null;
                    assignButton.setEnabled(false);
                }
            });
            return checkbox;
        }).setHeader("Wybierz").setWidth("80px").setFlexGrow(0);

        hallGrid.addColumn(h -> h.getBuilding().getName())
                .setHeader("Budynek").setAutoWidth(true);
        hallGrid.addColumn(h -> h.getName())
                .setHeader("Sala").setAutoWidth(true);
        hallGrid.addColumn(h -> h.getCapacity())
                .setHeader("Pojemność").setAutoWidth(true);
        hallGrid.addColumn(h -> h.getStageSize() + " m²")
                .setHeader("Scena").setAutoWidth(true);
        hallGrid.addColumn(h -> h.getAcousticRating() + "/10")
                .setHeader("Akustyka").setAutoWidth(true);
        hallGrid.addColumn(h -> h.isLightingSystem() ? "Tak" : "Nie")
                .setHeader("Oświetlenie").setAutoWidth(true);
        hallGrid.addColumn(h -> h.isSoundSystem() ? "Tak" : "Nie")
                .setHeader("Nagłośnienie").setAutoWidth(true);
        hallGrid.addColumn(h -> h.getPricePerHour() + " zł/h")
                .setHeader("Cena").setAutoWidth(true);

        concertSelect.addValueChangeListener(event -> {
            Concert selected = event.getValue();
            // reset stanu — grid odtwarza checkboxy przy nowych items, więc stare referencje stają się nieaktualne
            selectedHall = null;
            hallCheckboxes.clear();
            if (selected == null) {
                concertDetails.setText("");
                hallGrid.setItems();
                assignButton.setEnabled(false);
            } else {
                DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm");
                DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("dd.MM.yyyy");
                concertDetails.setText(selected.getDate().format(dateFmt)
                        + ", " + selected.getStart().format(timeFmt)
                        + " – " + selected.getEnd().format(timeFmt));
                // filtrowanie po stronie serwera — wyświetlane są tylko sale wolne w terminie wybranego koncertu
                hallGrid.setItems(concertService.findAvailableHalls(selected.getId()));
                assignButton.setEnabled(false);
            }
        });

        assignButton.addClickListener(event -> {
            Concert concert = concertSelect.getValue();
            if (concert == null || selectedHall == null) return;

            try {
                concertService.assignConcertHall(concert.getId(), selectedHall.getId());
                Notification.show(
                        "Sala \"" + selectedHall.getName() + "\" przypisana do koncertu \"" + concert.getName() + "\"",
                        3000, Notification.Position.TOP_CENTER);
                // pełny reset formularza — ponowne pobranie listy koncertów, bo przypisany koncert nie powinien już się w niej pojawiać
                selectedHall = null;
                hallCheckboxes.clear();
                concertSelect.setItems(concertService.findConcertsWithoutHall());
                concertSelect.clear();
                concertDetails.setText("");
                hallGrid.setItems();
                assignButton.setEnabled(false);
            } catch (IllegalStateException e) {
                Notification.show("Błąd: " + e.getMessage(), 5000, Notification.Position.TOP_CENTER);
            }
        });

        Button cancelButton = new Button("Anuluj", e ->
                UI.getCurrent().navigate(MainView.class));
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        HorizontalLayout buttons = new HorizontalLayout(assignButton, cancelButton);
        add(concertSelect, concertDetails, hallGrid, buttons);
    }
}

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
