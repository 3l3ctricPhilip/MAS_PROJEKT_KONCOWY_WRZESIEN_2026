package pl.edu.pja.pk_gorski_filip_16608.frontend;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import pl.edu.pja.pk_gorski_filip_16608.model.BandConcertParticipation;
import pl.edu.pja.pk_gorski_filip_16608.model.Concert;
import pl.edu.pja.pk_gorski_filip_16608.model.ConcertHall;
import pl.edu.pja.pk_gorski_filip_16608.service.ConcertService;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Route(value = "myconcerts", layout = MainView.class)
public class MyConcertsView extends VerticalLayout {

    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private final List<Checkbox> concertCheckboxes = new ArrayList<>();
    private Concert selectedConcert;

    public MyConcertsView(ConcertService concertService) {
        setSpacing(false);
        setPadding(true);

        Grid<Concert> concertGrid = new Grid<>();
        concertGrid.setWidth("700px");
        concertGrid.setAllRowsVisible(true);
        // NONE + ręczne checkboxy zamiast SINGLE — wbudowana selekcja Vaadin nie pozwala na odznaczenie kliknięciem tego samego wiersza
        concertGrid.setSelectionMode(Grid.SelectionMode.NONE);
        concertGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);

        concertGrid.setItems(concertService.findConcertsByBooker(1L));

        VerticalLayout detailsPanel = new VerticalLayout();
        detailsPanel.setVisible(false);
        detailsPanel.setPadding(false);
        detailsPanel.setSpacing(false);
        detailsPanel.getStyle().set("gap", "var(--lumo-space-xs)");

        H3 concertName = new H3();
        Span dateSpan = new Span();
        Span timeSpan = new Span();
        Span statusSpan = new Span();

        H4 hallHeader = new H4();
        Span hallNameSpan = new Span();
        Span buildingSpan = new Span();
        Span addressSpan = new Span();
        Span capacitySpan = new Span();

        H4 lineupHeader = new H4("Lineup");
        Grid<BandConcertParticipation> lineupGrid = new Grid<>();
        lineupGrid.setWidth("700px");
        lineupGrid.addColumn(cp -> {
            String name = cp.getBand().getName();
            String genres = cp.getBand().getGenres().stream()
                    .map(g -> g.getName())
                    .sorted()
                    .collect(Collectors.joining(", "));
            return genres.isEmpty() ? name : name + " (" + genres + ")";
        }).setHeader("Zespół").setAutoWidth(true);
        lineupGrid.addColumn(cp -> cp.getStart().format(TIME_FMT))
                .setHeader("Start").setAutoWidth(true);
        lineupGrid.addColumn(cp -> cp.getEnd().format(TIME_FMT))
                .setHeader("Koniec").setAutoWidth(true);

        detailsPanel.add(concertName, dateSpan, timeSpan, statusSpan,
                hallHeader, hallNameSpan, buildingSpan, addressSpan, capacitySpan,
                lineupHeader, lineupGrid);


        concertGrid.addComponentColumn(concert -> {
            Checkbox checkbox = new Checkbox();
            concertCheckboxes.add(checkbox);
            checkbox.addValueChangeListener(e -> {
                if (e.getValue()) {
                    selectedConcert = concert;
                    concertCheckboxes.stream()
                            .filter(cb -> cb != checkbox)
                            .forEach(cb -> cb.setValue(false));
                    showDetails(concertService, concert,
                            concertName, dateSpan, timeSpan, statusSpan,
                            hallHeader, hallNameSpan, buildingSpan, addressSpan, capacitySpan,
                            lineupGrid, detailsPanel);
                } else if (selectedConcert == concert) {
                    selectedConcert = null;
                    detailsPanel.setVisible(false);
                }
            });
            return checkbox;
        }).setHeader("Wybierz").setWidth("80px").setFlexGrow(0);

        concertGrid.addColumn(Concert::getName)
                .setHeader("Nazwa").setAutoWidth(true);
        concertGrid.addColumn(c -> c.getDate().format(DATE_FMT))
                .setHeader("Data").setAutoWidth(true);
        concertGrid.addColumn(c -> c.getStart().format(TIME_FMT)
                + " – " + c.getEnd().format(TIME_FMT))
                .setHeader("Godziny").setAutoWidth(true);

        add(concertGrid, detailsPanel);
    }

    private void showDetails(ConcertService concertService, Concert selected,
                             H3 concertName, Span dateSpan, Span timeSpan, Span statusSpan,
                             H4 hallHeader, Span hallNameSpan, Span buildingSpan,
                             Span addressSpan, Span capacitySpan,
                             Grid<BandConcertParticipation> lineupGrid,
                             VerticalLayout detailsPanel) {

        // osobne pobranie koncertu w ramach transakcji — wymusza inicjalizację LAZY kolekcji (gatunki zespołów), które poza transakcją rzuciłyby LazyInitializationException
        Concert concert = concertService.findConcertWithDetails(selected.getId());

        concertName.setText(concert.getName());
        dateSpan.setText("Data: " + concert.getDate().format(DATE_FMT));
        timeSpan.setText("Godziny: " + concert.getStart().format(TIME_FMT)
                + " – " + concert.getEnd().format(TIME_FMT));
        statusSpan.setText("Status: " + concert.getStatus().getDisplayName());

        ConcertHall hall = concert.getConcertHall();
        if (hall != null) {
            hallHeader.setText("Sala koncertowa");
            hallNameSpan.setText("Sala: " + hall.getName());
            buildingSpan.setText("Budynek: " + hall.getBuilding().getName());
            addressSpan.setText("Adres: " + hall.getBuilding().getStreet() + " "
                    + hall.getBuilding().getNumber() + ", "
                    + hall.getBuilding().getCity() + ", "
                    + hall.getBuilding().getCountry());
            capacitySpan.setText("Pojemność: " + hall.getCapacity());
            hallNameSpan.setVisible(true);
            buildingSpan.setVisible(true);
            addressSpan.setVisible(true);
            capacitySpan.setVisible(true);
        } else {
            hallHeader.setText("Sala koncertowa: nie przypisano");
            hallNameSpan.setVisible(false);
            buildingSpan.setVisible(false);
            addressSpan.setVisible(false);
            capacitySpan.setVisible(false);
        }

        // jawne sortowanie — Hibernate nie gwarantuje kolejności elementów w Set ładowanym z bazy
        lineupGrid.setItems(concert.getParticipations().stream()
                .sorted((a, b) -> a.getStart().compareTo(b.getStart()))
                .toList());
        detailsPanel.setVisible(true);
    }
}