package pl.edu.pja.pk_gorski_filip_16608;

import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.theme.aura.Aura;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@StyleSheet(Aura.STYLESHEET)
@StyleSheet("styles.css")
public class PkGorskiFilip16608Application implements AppShellConfigurator {

    public static void main(String[] args) {

        SpringApplication.run(PkGorskiFilip16608Application.class, args);
    }
}

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