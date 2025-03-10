package com.hiking.dublindoga.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hiking.dublindoga.domain.enumeration.Gender;
import com.hiking.dublindoga.domain.enumeration.JoinStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Joiner.
 */
@Entity
@Table(name = "joiner")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Joiner implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "full_name", nullable = false)
    private String fullName;

    @NotNull
    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "phone")
    private String phone;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private JoinStatus status;

    @Lob
    @Column(name = "photo_1")
    private byte[] photo1;

    @Column(name = "photo_1_content_type")
    private String photo1ContentType;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    @Column(name = "point")
    private Integer point;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(unique = true)
    private User internalUser;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "pendingJoiners")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "pendingJoiners", "approvedJoiners" }, allowSetters = true)
    private Set<Event> pendingEvents = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "approvedJoiners")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "pendingJoiners", "approvedJoiners" }, allowSetters = true)
    private Set<Event> aprovedEvents = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Joiner id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return this.fullName;
    }

    public Joiner fullName(String fullName) {
        this.setFullName(fullName);
        return this;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return this.email;
    }

    public Joiner email(String email) {
        this.setEmail(email);
        return this;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return this.phone;
    }

    public Joiner phone(String phone) {
        this.setPhone(phone);
        return this;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public JoinStatus getStatus() {
        return this.status;
    }

    public Joiner status(JoinStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(JoinStatus status) {
        this.status = status;
    }

    public byte[] getPhoto1() {
        return this.photo1;
    }

    public Joiner photo1(byte[] photo1) {
        this.setPhoto1(photo1);
        return this;
    }

    public void setPhoto1(byte[] photo1) {
        this.photo1 = photo1;
    }

    public String getPhoto1ContentType() {
        return this.photo1ContentType;
    }

    public Joiner photo1ContentType(String photo1ContentType) {
        this.photo1ContentType = photo1ContentType;
        return this;
    }

    public void setPhoto1ContentType(String photo1ContentType) {
        this.photo1ContentType = photo1ContentType;
    }

    public Gender getGender() {
        return this.gender;
    }

    public Joiner gender(Gender gender) {
        this.setGender(gender);
        return this;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public Integer getPoint() {
        return this.point;
    }

    public Joiner point(Integer point) {
        this.setPoint(point);
        return this;
    }

    public void setPoint(Integer point) {
        this.point = point;
    }

    public User getInternalUser() {
        return this.internalUser;
    }

    public void setInternalUser(User user) {
        this.internalUser = user;
    }

    public Joiner internalUser(User user) {
        this.setInternalUser(user);
        return this;
    }

    public Set<Event> getPendingEvents() {
        return this.pendingEvents;
    }

    public void setPendingEvents(Set<Event> events) {
        if (this.pendingEvents != null) {
            this.pendingEvents.forEach(i -> i.removePendingJoiner(this));
        }
        if (events != null) {
            events.forEach(i -> i.addPendingJoiner(this));
        }
        this.pendingEvents = events;
    }

    public Joiner pendingEvents(Set<Event> events) {
        this.setPendingEvents(events);
        return this;
    }

    public Joiner addPendingEvents(Event event) {
        this.pendingEvents.add(event);
        event.getPendingJoiners().add(this);
        return this;
    }

    public Joiner removePendingEvents(Event event) {
        this.pendingEvents.remove(event);
        event.getPendingJoiners().remove(this);
        return this;
    }

    public Set<Event> getAprovedEvents() {
        return this.aprovedEvents;
    }

    public void setAprovedEvents(Set<Event> events) {
        if (this.aprovedEvents != null) {
            this.aprovedEvents.forEach(i -> i.removeApprovedJoiner(this));
        }
        if (events != null) {
            events.forEach(i -> i.addApprovedJoiner(this));
        }
        this.aprovedEvents = events;
    }

    public Joiner aprovedEvents(Set<Event> events) {
        this.setAprovedEvents(events);
        return this;
    }

    public Joiner addAprovedEvents(Event event) {
        this.aprovedEvents.add(event);
        event.getApprovedJoiners().add(this);
        return this;
    }

    public Joiner removeAprovedEvents(Event event) {
        this.aprovedEvents.remove(event);
        event.getApprovedJoiners().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Joiner)) {
            return false;
        }
        return getId() != null && getId().equals(((Joiner) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Joiner{" +
            "id=" + getId() +
            ", fullName='" + getFullName() + "'" +
            ", email='" + getEmail() + "'" +
            ", phone='" + getPhone() + "'" +
            ", status='" + getStatus() + "'" +
            ", photo1='" + getPhoto1() + "'" +
            ", photo1ContentType='" + getPhoto1ContentType() + "'" +
            ", gender='" + getGender() + "'" +
            ", point=" + getPoint() +
            "}";
    }
}
