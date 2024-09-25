package com.example.boe_auction.auction_web_scraping.restcall.azure.geocoding;

import com.example.boe_auction.auction_web_scraping.dao.document.Coordinates;

public interface GeoCodingAzureApi {

    Coordinates getLatLonAzureGeocodingApi(String address);
}
