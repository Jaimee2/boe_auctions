package com.example.boe_auction.auction_web_scraping.ollama.service.impl;

import com.example.boe_auction.auction_web_scraping.ollama.service.OllamaService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class OllamaServiceImpl implements OllamaService {

    private final OllamaChatModel chatModel;

    public String improveAddressForGeoDataApi(String address) {
        log.info("Ollama improving address -> {}", address);

        String prompt = String.format("""
               Necesito que me devuelvas la dirección mejorada "address",
               para que pueda ser mejor interpretada por las apis de geocoding.
               La respuesta debe de ser extrictamente la dirección.
               No des explicaciones ni nada, solo dame la dirección.
               Dame solo la calle y el número. Evita otros datos.
               La calle también puede ser otro tipo de vía,(por ejemplo, Calle, Avenida, Paseo, etc.)

               address: %s

               El patrón debe de ser: Calle, número.

               Ejemplo: Calle Cabo Suceso, 4
               """, address);

        try {
            return chatModel.call(prompt);
        } catch (Exception e) {
            log.error("Error improving address: {}", address, e);
            return null;
        }
    }
}
