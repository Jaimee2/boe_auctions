package com.example.boe_auction.auction_web_scraping.controller;

import com.example.boe_auction.auction_web_scraping.dao.document.Auction;
import com.example.boe_auction.auction_web_scraping.service.AuctionService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/auction")
public class AuctionController {

    private AuctionService auctionService;

    @GetMapping
    private ResponseEntity<List<Auction>> getAllAuctions() {
        return ResponseEntity.ok(auctionService.getAllAuctions());
    }

}
