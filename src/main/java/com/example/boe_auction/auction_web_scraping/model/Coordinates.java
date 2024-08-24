package com.example.boe_auction.auction_web_scraping.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Coordinates {
    private String lat;
    private String lon;
}
