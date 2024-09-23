package com.example.boe_auction.auction_web_scraping.restCalls.AzureGeocoding.dto;

import lombok.Data;

import java.util.List;

@Data
public class AzureGeocodingResponse {
    private List<Result> results;
}
