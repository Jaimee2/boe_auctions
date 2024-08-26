package com.example.boe_auction.auction_web_scraping.service.impl;

import com.example.boe_auction.auction_web_scraping.dao.document.Auction;
import com.example.boe_auction.auction_web_scraping.dao.repository.AuctionRepository;
import com.example.boe_auction.auction_web_scraping.service.AuctionService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class AuctionServiceImpl implements AuctionService {

    private AuctionRepository auctionRepository;

    public List<Auction> getAllAuctions() {
        return auctionRepository.findAll();
    }

}
