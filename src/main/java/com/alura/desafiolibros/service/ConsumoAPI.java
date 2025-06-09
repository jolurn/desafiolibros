package com.alura.desafiolibros.service;

import java.io.IOException;
import java.net.ProxySelector;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class ConsumoAPI {
    public String obtenerDatos(String url) {
        try {

            HttpClient client = HttpClient.newBuilder()
                    .proxy(ProxySelector.getDefault())
                    .version(HttpClient.Version.HTTP_2)
                    .followRedirects(HttpClient.Redirect.NORMAL)
                    .connectTimeout(Duration.ofSeconds(20))
                    .build();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("User-Agent", "Mozilla/5.0")
                    .header("Accept", "application/json")
                    .GET()
                    .timeout(Duration.ofSeconds(20))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200 || response.body().isEmpty()) {
                throw new RuntimeException("Respuesta inválida: " + response.statusCode());
            }

            return response.body();

        } catch (Exception e) {
            throw new RuntimeException("Error al consumir API: " + e.getMessage(), e);
        }
    }

}
