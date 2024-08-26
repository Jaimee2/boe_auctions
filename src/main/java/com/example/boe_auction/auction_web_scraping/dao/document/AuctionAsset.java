package com.example.boe_auction.auction_web_scraping.dao.document;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuctionAsset {

    private String assetLink;
    private String description;
    private String iDufir;
    private String cadastralReference;
    private String address;
    private String addressIA;
    private String fullAddress;
    private String fullAddressWithIA;
    private Coordinates coordinates;
    private String postalCode;
    private String city;
    private String province;
    private boolean isPrimaryResidence;
    private String possessionStatus;
    private String isVisitable;
    private String encumbrances;
    private String registryDetails;
    private String legalTitle;

}
