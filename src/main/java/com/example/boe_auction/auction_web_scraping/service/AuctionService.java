package com.example.boe_auction.auction_web_scraping.service;

import com.example.boe_auction.auction_web_scraping.dao.document.Auction;

import java.util.List;

public interface AuctionService {

    List<Auction> getAllAuctions();

    void deleteAllAuctions();
}
