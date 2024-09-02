package com.example.boe_auction.auction_web_scraping.service;

import com.example.boe_auction.auction_web_scraping.dao.document.Auction;

import java.util.List;

public interface AuctionService {

    List<Auction> getAllAuctions(
            String auctionType, String city, String startDate, String endDate, String minimumBid,
            String minAppraisalValue, String maxAppraisalValue, String province, String assetType
    );

    void deleteAllAuctions();
}
