package com.example.boe_auction.auction_web_scraping.dto;

import com.example.boe_auction.auction_web_scraping.dao.document.Coordinates;
import lombok.Data;

@Data
public class AssetMapInitDto {

    private String auctionId;

    private String startDate;
    private String endDate;

    private String assetLink;
    private String assetType;

    private Coordinates coordinates;
    private String possessionStatus;
    private String encumbrances;

    private float auctionValue;
    private float appraisalValue;
    private float minimumBid;
    private float bidIncrement;
    private float depositAmount;

}
