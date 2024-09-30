package ru.practicum;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TestController {

    private final StatClient statClient;

    @PostMapping
    void post(HttpServletRequest request) {
        statClient.saveHit("main", request);
    }
}
