package com.example.boe_auction.auction_web_scraping.dao.repository;

import com.example.boe_auction.auction_web_scraping.dao.document.Auction;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AuctionRepository extends MongoRepository<Auction, String> {
}
