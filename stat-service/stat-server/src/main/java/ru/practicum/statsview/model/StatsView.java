package ru.practicum.statsview.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class StatsView {
    @NotBlank
    private String app;
    @NotBlank
    private String uri;
    @NotNull
    private Long hits;
}
