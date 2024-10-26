package com.example.boe_auction.auction_web_scraping.dao.repository;

import com.example.boe_auction.auction_web_scraping.dao.document.Auction;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;
import java.util.List;

public interface AuctionRepository extends MongoRepository<Auction, String> {
    List<Auction> findTopByEndDateBefore(Date date, PageRequest pageRequest);
}
