package com.example.boe_auction.auction_web_scraping.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class Auction {

    private String identifier;

    private String auctionType;
    private String startDate;
    private String endDate;
    private String lots;
    private String announcementBOE;
    private String auctionValue;
    private String appraisalValue;
    private String minimumBid;
    private String bidIncrement;
    private String depositAmount;

    private ManagingAuthority managingAuthority;
    private List<AuctionAsset> assets;

}
