package pl.edu.pja.pk_gorski_filip_16608.model;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Booker extends Person {

    private static final int minComissionPercent = 2;

    @NotBlank
    private String contactPersonName;

    @NotNull
    private BigDecimal commissionPercent;

    @NotBlank
    private String companyName;

    @NotBlank
    @Size(max = 10)
    private String nip;

    @OneToMany(mappedBy = "booker")
    private Set<Concert> organizedConcerts = new HashSet<>();

    protected Booker() {}

    public Booker(
            String nick,
            String email,
            String country,
            String city,
            String phoneNumber,
            String contactPersonName,
            BigDecimal commissionPercent,
            String companyName,
            String nip
    ) {
        super(nick, email, country, city, phoneNumber);
        setContactPersonName(contactPersonName);
        setCommissionPercent(commissionPercent);
        setCompanyName(companyName);
        setNip(nip);
    }

    public String getContactPersonName() {
        return contactPersonName;
    }

    public void setContactPersonName(String contactPersonName) {
        if (contactPersonName == null || contactPersonName.isBlank()) {
            throw new IllegalArgumentException("Contact person name is required");
        }
        this.contactPersonName = contactPersonName;
    }

    public BigDecimal getCommissionPercent() {
        return commissionPercent;
    }

    public void setCommissionPercent(BigDecimal commissionPercent) {
        if (commissionPercent == null) {
            throw new IllegalArgumentException("Commission percent is required");
        }
        if (commissionPercent.compareTo(BigDecimal.valueOf(minComissionPercent)) < 0) {
            throw new IllegalArgumentException("Commission percent must be at least 2%");
        }
        this.commissionPercent = commissionPercent;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        if (companyName == null || companyName.isBlank()) {
            throw new IllegalArgumentException("Company name is required");
        }
        this.companyName = companyName;
    }

    public String getNip() {
        return nip;
    }

    public void setNip(String nip) {
        if (nip == null || !nip.matches("\\d{10}")) {
            throw new IllegalArgumentException("NIP must contain exactly 10 digits");
        }
        if (!isValidNip(nip)) {
            throw new IllegalArgumentException("Invalid NIP control sum");
        }
        this.nip = nip;
    }

    public Set<Concert> getOrganizedConcerts() {
        return organizedConcerts;
    }

    public void setOrganizedConcerts(Set<Concert> organizedConcerts) {
        this.organizedConcerts = organizedConcerts;
    }

    public void addOrganizedConcert(Concert concert) {
        if (concert == null) return;
        organizedConcerts.add(concert);
        concert.setBooker(this);
    }


    public void removeOrganizedConcert(Concert concert) {
        if (concert == null) return;

        if (organizedConcerts.remove(concert)) {
            concert.setBooker(null);
        }
    }

    private boolean isValidNip(String nip) {
        int[] weights = {6, 5, 7, 2, 3, 4, 5, 6, 7};
        int sum = 0;
        for (int i = 0; i < weights.length; i++) {
            sum += Character.getNumericValue(nip.charAt(i)) * weights[i];
        }
        int control = sum % 11;
        return control == Character.getNumericValue(nip.charAt(9));
    }
}
