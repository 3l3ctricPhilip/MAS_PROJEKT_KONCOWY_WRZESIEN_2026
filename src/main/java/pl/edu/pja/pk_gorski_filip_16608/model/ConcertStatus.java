package pl.edu.pja.pk_gorski_filip_16608.model;

public enum ConcertStatus {
    PLANNED("Zaplanowany"),
    EDITED("Edytowany"),
    COMPLETED("Zakończony"),
    CANCELLED("Anulowany");

    private final String displayName;

    ConcertStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
