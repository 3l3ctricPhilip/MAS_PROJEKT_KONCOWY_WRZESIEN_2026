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

import java.util.ArrayList;
import java.util.List;

@Route(value = "assignhall", layout = MainView.class)
public class NewHallView extends VerticalLayout {

    private ConcertHall selectedHall;

    public NewHallView(ConcertService concertService) {

        //PRZYCISK PRZYPISANIA SALI I ANULOWANIA

        Button assignButton = new Button("Przypisz salę");
        assignButton.setEnabled(false);

        Button cancelButton = new Button("Anuluj", e ->
                UI.getCurrent().navigate(MainView.class));
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        //UTOWRZENIE COMBOBOXA DLA ZAPISANYCH KONCERTÓW BEZ PRZYDZIELONEJ SALI KONCERTOWEJ

        ComboBox<Concert> concertSelect = new ComboBox<>("Wybierz koncert");
        concertSelect.setItems(concertService.findConcertsWithoutHall());
        concertSelect.setItemLabelGenerator(concert -> concert.getName() + " (" + concert.getDate() + ")");
        concertSelect.setWidth("500px");

        //UTWORZENIE GRIDA DLA SAL KONCERTOWYCH

        Span gridLabel = new Span("Wybierz salę");
        Grid<ConcertHall> hallGrid = new Grid<>();
        hallGrid.setWidth("900px");
        hallGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        hallGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);

        //DODAWANIE KOLUMN DO GRIDA

//        hallGrid.addComponentColumn(concertHall -> {
//            Checkbox checkbox = new Checkbox();
//            hallCheckboxes.add(checkbox);
//
//            checkbox.addValueChangeListener(event -> {
//                if(event.getValue()){
//                    selectedHall = concertHall;
//                    hallCheckboxes.stream()
//                            .filter(cb -> cb != checkbox)
//                            .forEach(cb -> cb.setValue(false));
//                    assignButton.setEnabled(concertSelect.getValue() != null);
//                } else if (selectedHall == concertHall){
//                    selectedHall = null;
//                    assignButton.setEnabled(false);
//                }
//            });
//
//            return checkbox;
//        }).setHeader("Wybierz").setAutoWidth(true);

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

        //LISTENER DLA WIERSZY Z SALAMI

        hallGrid.addSelectionListener(event -> {
            selectedHall = event.getFirstSelectedItem().orElse(null);
            assignButton.setEnabled(selectedHall != null && concertSelect.getValue() != null);
        });

        //LISTENER DLA WYBRANEGO KONCERTU Z COMBOBOXA I UZUPEŁNIENIE GRIDA

        concertSelect.addValueChangeListener(event -> {
            Concert selected = event.getValue();
            // reset stanu — grid odtwarza checkboxy przy nowych items, więc stare referencje stają się nieaktualne
            selectedHall = null;
            hallGrid.deselectAll();
            if (selected == null) {
                hallGrid.setItems();
                assignButton.setEnabled(false);
            } else {
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
                hallGrid.deselectAll();
                concertSelect.setItems(concertService.findConcertsWithoutHall());
                concertSelect.clear();
                hallGrid.setItems();
                assignButton.setEnabled(false);
            } catch (IllegalStateException e) {
                Notification.show("Błąd: " + e.getMessage(), 5000, Notification.Position.TOP_CENTER);
            }
        });

        HorizontalLayout buttons = new HorizontalLayout(assignButton, cancelButton);
        add(concertSelect, gridLabel, hallGrid, buttons);
    }
}