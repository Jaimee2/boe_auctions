package com.example.boe_auction.auction_web_scraping.controller;

import com.example.boe_auction.auction_web_scraping.dao.document.Auction;
import com.example.boe_auction.auction_web_scraping.service.AuctionService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@AllArgsConstructor
@RequestMapping("/auction")
public class AuctionController {

    private AuctionService auctionService;

    @GetMapping
    public ResponseEntity<List<Auction>> getAllAuctions() {
        return ResponseEntity.ok(auctionService.getAllAuctions());
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAllAuctions() {
        auctionService.deleteAllAuctions();
        return ResponseEntity.ok().build();
    }

}
