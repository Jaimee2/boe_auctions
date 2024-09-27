package com.example.boe_auction.auction_web_scraping.dao.document;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document
public class AuctionAsset {

    @Id
    private String id;

    private String auctionId;

    private String startDate;
    private String endDate;
    private String assetLink;
    private String assetType;
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

    private float auctionValue;
    private float appraisalValue;
    private float minimumBid;
    private float bidIncrement;
    private float depositAmount;

}
