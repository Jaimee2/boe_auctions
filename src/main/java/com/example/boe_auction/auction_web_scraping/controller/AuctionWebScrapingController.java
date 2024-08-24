package com.example.boe_auction.auction_web_scraping.controller;

import com.example.boe_auction.auction_web_scraping.model.Auction;
import com.example.boe_auction.auction_web_scraping.service.AuctionWebScrapingService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("boe-scraping/auctions")
public class AuctionWebScrapingController {

    private AuctionWebScrapingService auctionWebScrapingService;

    @GetMapping
    public ResponseEntity<List<Auction>> getAllAuctions() throws IOException {
        return ResponseEntity.ok(auctionWebScrapingService.performQuery());
    }

}
