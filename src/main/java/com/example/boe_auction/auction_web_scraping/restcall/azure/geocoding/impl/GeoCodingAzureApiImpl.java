package com.example.boe_auction.auction_web_scraping.restcall.azure.geocoding.impl;

import com.example.boe_auction.auction_web_scraping.dao.document.Coordinates;
import com.example.boe_auction.auction_web_scraping.restcall.azure.geocoding.GeoCodingAzureApi;
import com.example.boe_auction.auction_web_scraping.restcall.azure.geocoding.dto.AzureGeocodingResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Slf4j
@Service
public class GeoCodingAzureApiImpl implements GeoCodingAzureApi {

    public Coordinates getLatLonAzureGeocodingApi(String address) {
        if (address == null || address.isEmpty()) return null;
        String url = "https://atlas.microsoft.com/search/address/json";

        RestClient restClient = RestClient.create();

        URI uri = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("query", address)
                .queryParam(
                        "subscription-key",
                        "XRKf4AGk19X05G3NDvSmYinOvIHsyGaJiVkNijAwtsvKWJCqVmOMJQQJ99AHAC5RqLJ2gwi4AAAgAZMP2t9J")
                .queryParam("top", 1)
                .build()
                .toUri();

        log.info(uri.toString());

        AzureGeocodingResponse response =
                restClient.get().uri(uri).retrieve().body(AzureGeocodingResponse.class);

        if (response == null || response.getResults() == null || response.getResults().isEmpty()) return null;

        return new Coordinates(
                String.valueOf(response.getResults().get(0).getPosition().getLat()),
                String.valueOf(response.getResults().get(0).getPosition().getLon())
        );

    }

}
