package com.hiking.dublindoga.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hiking.dublindoga.domain.enumeration.Difficulty;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Event.
 */
@Entity
@Table(name = "event")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Event implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", length = 5000)
    private String description;


    @NotNull
    @Column(name = "location", nullable = false)
    private String location;

    @NotNull
    @Column(name = "date", nullable = false)
    private Instant date;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty", nullable = false)
    private Difficulty difficulty;

    @Lob
    @Column(name = "photo_1")
    private byte[] photo1;

    @Column(name = "photo_1_content_type")
    private String photo1ContentType;

    @Lob
    @Column(name = "photo_2")
    private byte[] photo2;

    @Column(name = "photo_2_content_type")
    private String photo2ContentType;

    @Lob
    @Column(name = "photo_3")
    private byte[] photo3;

    @Column(name = "photo_3_content_type")
    private String photo3ContentType;

    @Column(name = "jhi_limit")
    private Integer limit;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "rel_event__pending_joiner",
        joinColumns = @JoinColumn(name = "event_id"),
        inverseJoinColumns = @JoinColumn(name = "pending_joiner_id")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "internalUser", "pendingEvents", "aprovedEvents" }, allowSetters = true)
    private Set<Joiner> pendingJoiners = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "rel_event__approved_joiner",
        joinColumns = @JoinColumn(name = "event_id"),
        inverseJoinColumns = @JoinColumn(name = "approved_joiner_id")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "internalUser", "pendingEvents", "aprovedEvents" }, allowSetters = true)
    private Set<Joiner> approvedJoiners = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Event id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Event name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public Event description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return this.location;
    }

    public Event location(String location) {
        this.setLocation(location);
        return this;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Instant getDate() {
        return this.date;
    }

    public Event date(Instant date) {
        this.setDate(date);
        return this;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public Difficulty getDifficulty() {
        return this.difficulty;
    }

    public Event difficulty(Difficulty difficulty) {
        this.setDifficulty(difficulty);
        return this;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public byte[] getPhoto1() {
        return this.photo1;
    }

    public Event photo1(byte[] photo1) {
        this.setPhoto1(photo1);
        return this;
    }

    public void setPhoto1(byte[] photo1) {
        this.photo1 = photo1;
    }

    public String getPhoto1ContentType() {
        return this.photo1ContentType;
    }

    public Event photo1ContentType(String photo1ContentType) {
        this.photo1ContentType = photo1ContentType;
        return this;
    }

    public void setPhoto1ContentType(String photo1ContentType) {
        this.photo1ContentType = photo1ContentType;
    }

    public byte[] getPhoto2() {
        return this.photo2;
    }

    public Event photo2(byte[] photo2) {
        this.setPhoto2(photo2);
        return this;
    }

    public void setPhoto2(byte[] photo2) {
        this.photo2 = photo2;
    }

    public String getPhoto2ContentType() {
        return this.photo2ContentType;
    }

    public Event photo2ContentType(String photo2ContentType) {
        this.photo2ContentType = photo2ContentType;
        return this;
    }

    public void setPhoto2ContentType(String photo2ContentType) {
        this.photo2ContentType = photo2ContentType;
    }

    public byte[] getPhoto3() {
        return this.photo3;
    }

    public Event photo3(byte[] photo3) {
        this.setPhoto3(photo3);
        return this;
    }

    public void setPhoto3(byte[] photo3) {
        this.photo3 = photo3;
    }

    public String getPhoto3ContentType() {
        return this.photo3ContentType;
    }

    public Event photo3ContentType(String photo3ContentType) {
        this.photo3ContentType = photo3ContentType;
        return this;
    }

    public void setPhoto3ContentType(String photo3ContentType) {
        this.photo3ContentType = photo3ContentType;
    }

    public Integer getLimit() {
        return this.limit;
    }

    public Event limit(Integer limit) {
        this.setLimit(limit);
        return this;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Set<Joiner> getPendingJoiners() {
        return this.pendingJoiners;
    }

    public void setPendingJoiners(Set<Joiner> joiners) {
        this.pendingJoiners = joiners;
    }

    public Event pendingJoiners(Set<Joiner> joiners) {
        this.setPendingJoiners(joiners);
        return this;
    }

    public Event addPendingJoiner(Joiner joiner) {
        this.pendingJoiners.add(joiner);
        return this;
    }

    public Event removePendingJoiner(Joiner joiner) {
        this.pendingJoiners.remove(joiner);
        return this;
    }

    public Set<Joiner> getApprovedJoiners() {
        return this.approvedJoiners;
    }

    public void setApprovedJoiners(Set<Joiner> joiners) {
        this.approvedJoiners = joiners;
    }

    public Event approvedJoiners(Set<Joiner> joiners) {
        this.setApprovedJoiners(joiners);
        return this;
    }

    public Event addApprovedJoiner(Joiner joiner) {
        this.approvedJoiners.add(joiner);
        return this;
    }

    public Event removeApprovedJoiner(Joiner joiner) {
        this.approvedJoiners.remove(joiner);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Event)) {
            return false;
        }
        return getId() != null && getId().equals(((Event) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Event{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", location='" + getLocation() + "'" +
            ", date='" + getDate() + "'" +
            ", difficulty='" + getDifficulty() + "'" +
            ", photo1='" + getPhoto1() + "'" +
            ", photo1ContentType='" + getPhoto1ContentType() + "'" +
            ", photo2='" + getPhoto2() + "'" +
            ", photo2ContentType='" + getPhoto2ContentType() + "'" +
            ", photo3='" + getPhoto3() + "'" +
            ", photo3ContentType='" + getPhoto3ContentType() + "'" +
            ", limit=" + getLimit() +
            "}";
    }
}
