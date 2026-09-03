package pl.edu.pja.pk_gorski_filip_16608.frontend;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
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

@Route(value = "assignhall", layout = MainView.class)
public class NewHallView extends VerticalLayout {

    private ConcertHall selectedHall;

    public NewHallView(ConcertService concertService) {


        Button assignButton = new Button("Przypisz salę");
        assignButton.setEnabled(false);

        Button cancelButton = new Button("Anuluj", e ->
                UI.getCurrent().navigate(MainView.class));
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);


        ComboBox<Concert> concertSelect = new ComboBox<>("Wybierz koncert");
        concertSelect.setItems(concertService.findConcertsWithoutHall());
        concertSelect.setItemLabelGenerator(concert -> concert.getName() + " (" + concert.getDate() + ")");
        concertSelect.setWidth("500px");


        Span gridLabel = new Span("Wybierz salę");
        Grid<ConcertHall> hallGrid = new Grid<>();
        hallGrid.setWidth("900px");
        hallGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        hallGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);

        hallGrid.addColumn(concertHall -> concertHall.getBuilding().getName())
                .setHeader("Budynek").setAutoWidth(true);
        hallGrid.addColumn(ConcertHall::getName)
                .setHeader("Sala").setAutoWidth(true);
        hallGrid.addColumn(ConcertHall::getCapacity)
                .setHeader("Pojemność").setAutoWidth(true);
        hallGrid.addColumn(concertHall -> concertHall.getStageSize() + " m²")
                .setHeader("Scena").setAutoWidth(true);
        hallGrid.addColumn(concertHall -> concertHall.getAcousticRating() + "/10")
                .setHeader("Akustyka").setAutoWidth(true);
        hallGrid.addColumn(concertHall -> concertHall.isLightingSystem() ? "Tak" : "Nie")
                .setHeader("Oświetlenie").setAutoWidth(true);
        hallGrid.addColumn(concertHall -> concertHall.isSoundSystem() ? "Tak" : "Nie")
                .setHeader("Nagłośnienie").setAutoWidth(true);
        hallGrid.addColumn(concertHall -> concertHall.getPricePerHour() + " zł/h")
                .setHeader("Cena").setAutoWidth(true);


        concertSelect.addValueChangeListener(event -> {
            Concert selectedConcert = event.getValue();
            if (selectedConcert != null) {
                hallGrid.setItems(concertService.findAvailableHalls(selectedConcert.getId()));
            } else {
                hallGrid.setItems();
            }
        });

        hallGrid.addSelectionListener(event -> {
            selectedHall = event.getFirstSelectedItem().orElse(null);
            assignButton.setEnabled(selectedHall != null && concertSelect.getValue() != null);
        });


        assignButton.addClickListener(event -> {
            Concert concert = concertSelect.getValue();

            try {
                concertService.assignConcertHall(concert.getId(), selectedHall.getId());
                Notification.show(
                        "Sala \"" + selectedHall.getName() + "\" przypisana do koncertu \"" + concert.getName() + "\"",
                        3000, Notification.Position.TOP_CENTER);

                selectedHall = null;
                hallGrid.deselectAll();
                concertSelect.setItems(concertService.findConcertsWithoutHall());

            } catch (IllegalStateException e) {
                Notification.show("Błąd: " + e.getMessage(), 5000, Notification.Position.TOP_CENTER);
            }
        });

        HorizontalLayout buttons = new HorizontalLayout(assignButton, cancelButton);
        add(concertSelect, gridLabel, hallGrid, buttons);
    }
}