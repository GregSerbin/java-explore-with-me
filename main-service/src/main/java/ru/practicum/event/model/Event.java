package ru.practicum.event.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import ru.practicum.category.model.Category;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @NotBlank
    @Column
    String annotation;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    Category category;

    @NotNull
    @Column
    LocalDateTime createdOn;

    @NotBlank
    @Column
    String description;

    @NotNull
    @Column
    LocalDateTime eventDate;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initiator_id")
    User initiator;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "location_id")
    Location location;

    @NotNull
    @Column
    Boolean paid;

    @NotNull
    @Column
    Long participantLimit;

    @NotNull
    @Column
    LocalDateTime publishedOn;

    @NotNull
    @Column
    Boolean requestModeration;

    @NotNull
    @Column
    @Enumerated(value = EnumType.STRING)
    State state;

    @NotBlank
    @Column
    String title;

    @Column(name = "confirmed_requests")
    Long confirmedRequests;

    @Transient
    Long views;
}
