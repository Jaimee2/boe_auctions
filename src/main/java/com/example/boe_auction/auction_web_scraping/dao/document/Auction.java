package com.example.boe_auction.auction_web_scraping.dao.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Builder
@Document
@AllArgsConstructor
public class Auction {

    @Id
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
