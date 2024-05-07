package org.testtask.cryptodealbot.fetchcryptopairprice.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/crypto")
public class CryptoController {

    private final WebClient client;
    private final String apiUrl;

    public CryptoController(WebClient.Builder webClientBuilder, @Value("${binance.api.url}") String apiUrl) {
        this.client = webClientBuilder.baseUrl(apiUrl).build();
        this.apiUrl = apiUrl;
    }

    @GetMapping("/price/{pair}")
    public Mono<String> getCurrentPrice(@PathVariable String pair) {
        String url = "?symbol=" + pair;

        return client.get()
                .uri(url)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> Mono.error(new ResponseStatusException(response.statusCode(), "Client error: " + response.statusCode())))
                .onStatus(HttpStatusCode::is5xxServerError, response -> Mono.error(new ResponseStatusException(response.statusCode(), "Server error: " + response.statusCode())))
                .bodyToMono(String.class);
    }
}
