package pl.edu.pja.pk_gorski_filip_16608.frontend;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import pl.edu.pja.pk_gorski_filip_16608.model.BandConcertParticipation;
import pl.edu.pja.pk_gorski_filip_16608.model.Concert;
import pl.edu.pja.pk_gorski_filip_16608.model.ConcertHall;
import pl.edu.pja.pk_gorski_filip_16608.service.ConcertService;

import java.time.format.DateTimeFormatter;
import java.util.Comparator;

@Route(value = "myconcerts", layout = MainView.class)
public class MyConcertsView extends VerticalLayout {

    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private Concert selectedConcert;

    public MyConcertsView(ConcertService concertService) {
        setSpacing(true);
        setPadding(true);

        Grid<Concert> concertsGrid = new Grid<>();
        concertsGrid.setWidth("700px");
        concertsGrid.setAllRowsVisible(true);
        concertsGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        concertsGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);

        concertsGrid.addColumn(Concert::getName).setHeader("Nazwa").setAutoWidth(true);
        concertsGrid.addColumn(c -> c.getDate().format(DATE_FMT)).setHeader("Data").setAutoWidth(true);
        concertsGrid.addColumn(c -> c.getStart().format(TIME_FMT) + " – " + c.getEnd().format(TIME_FMT)).setHeader("Godziny").setAutoWidth(true);


        concertsGrid.setItems(concertService.findConcertsByBooker(1L));

        VerticalLayout detailsPanel = new VerticalLayout();
        detailsPanel.setVisible(false);
        detailsPanel.setPadding(false);
        detailsPanel.setSpacing(true);

        Grid<Concert> concertDetailsGrid = new Grid<>();
        concertDetailsGrid.setWidth("700px");
        concertDetailsGrid.setAllRowsVisible(true);
        concertDetailsGrid.addColumn(Concert::getName).setHeader("Nazwa").setAutoWidth(true);
        concertDetailsGrid.addColumn(Concert::getDate).setHeader("Dzień").setAutoWidth(true);
        concertDetailsGrid.addColumn(Concert::getTicketPrice).setHeader("Cena").setAutoWidth(true);
        concertDetailsGrid.addColumn(concert -> concert.getStart().format(TIME_FMT)).setHeader("Start").setAutoWidth(true);
        concertDetailsGrid.addColumn(concert -> concert.getEnd().format(TIME_FMT)).setHeader("Koniec").setAutoWidth(true);
        concertDetailsGrid.addColumn(concert -> concert.getStatus().getDisplayName()).setHeader("Status").setAutoWidth(true);


        Grid<ConcertHall> hallDetailsGrid = new Grid<>();
        hallDetailsGrid.setWidth("700px");
        hallDetailsGrid.setAllRowsVisible(true);
        hallDetailsGrid.addColumn(concertHall -> concertHall.getBuilding().getName()).setHeader("Klub").setAutoWidth(true);

        hallDetailsGrid.addColumn(concertHall -> concertHall.getBuilding().getStreet()
                + " " + concertHall.getBuilding().getNumber() + ", " + concertHall.getBuilding().getCity()
                + " " + concertHall.getBuilding().getCountry()).setHeader("Adres").setAutoWidth(true);

        hallDetailsGrid.addColumn(ConcertHall::getName).setHeader("Sala").setAutoWidth(true);
        hallDetailsGrid.addColumn(ConcertHall::getCapacity).setHeader("Pojemność").setAutoWidth(true);


        Grid<BandConcertParticipation> lineupGrid = new Grid<>();
        lineupGrid.setWidth("700px");
        lineupGrid.setAllRowsVisible(true);

        lineupGrid.addColumn(bandConcertParticipation -> bandConcertParticipation.getBand().getName()
        ).setHeader("Zespół").setAutoWidth(true);
        lineupGrid.addColumn(bandConcertParticipation -> bandConcertParticipation.getStart().format(TIME_FMT))
                .setHeader("Start").setAutoWidth(true);
        lineupGrid.addColumn(bandConcertParticipation -> bandConcertParticipation.getEnd().format(TIME_FMT))
                .setHeader("Koniec").setAutoWidth(true);


        detailsPanel.add(
                sectionHeader("Szczegóły wydarzenia"), concertDetailsGrid,
                sectionHeader("Lokalizacja"), hallDetailsGrid,
                sectionHeader("Lineup"), lineupGrid
        );


        concertsGrid.addSelectionListener(event ->{
           selectedConcert = event.getFirstSelectedItem().orElse(null);
           if(selectedConcert != null){
               showDetails(concertService, selectedConcert, concertDetailsGrid, hallDetailsGrid, lineupGrid, detailsPanel);
           }
        });

        add(sectionHeader("Twoje koncerty"), concertsGrid, detailsPanel);
    }

    private void showDetails(ConcertService concertService, Concert selected,
                             Grid<Concert> detailsGrid, Grid<ConcertHall> hallDetailsGrid,
                             Grid<BandConcertParticipation> lineupGrid, VerticalLayout detailsPanel) {

        // osobne pobranie koncertu w ramach transakcji — wymusza inicjalizację LAZY kolekcji (gatunki zespołów), które poza transakcją rzuciłyby LazyInitializationException
        Concert concert = concertService.findConcertWithDetails(selected.getId());

        detailsGrid.setItems(concert);

        ConcertHall hall = concert.getConcertHall();
        if (hall != null) {
            hallDetailsGrid.setItems(hall);
        } else {
            hallDetailsGrid.setItems();
        }

        lineupGrid.setItems(concert.getParticipations().stream()
                        .sorted(Comparator.comparing(BandConcertParticipation::getStart))
                        .toList());


        detailsPanel.setVisible(true);
    }

    private H4 sectionHeader(String text) {
        H4 header = new H4(text);
        header.getStyle()
                .set("margin-top", "var(--lumo-space-l)")
                .set("margin-bottom", "var(--lumo-space-xs)");
        return header;
    }
}