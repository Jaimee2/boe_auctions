package com.example.boe_auction.auction_web_scraping.dao.repository;

import com.example.boe_auction.auction_web_scraping.dao.document.Auction;

import java.util.List;

public interface AuctionRepositoryCustom {

    List<Auction> findAuctionsByCriteria(
            String auctionType, String city, String startDate, String endDate,
            String minimumBid, String minAppraisalValue, String maxAppraisalValue, String province,
            List<String> assetType);
}
