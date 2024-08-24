package com.example.boe_auction.auction_web_scraping.service;

import com.example.boe_auction.auction_web_scraping.model.Auction;

import java.io.IOException;
import java.util.List;

public interface AuctionWebScrapingService {

    List<Auction> performQuery() throws IOException;

}
