package com.example.boe_auction.auction_web_scraping.dao.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

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

    private float auctionValue;
    private float appraisalValue;
    private float minimumBid;
    private float bidIncrement;
    private float depositAmount;

    private ManagingAuthority managingAuthority;

}
