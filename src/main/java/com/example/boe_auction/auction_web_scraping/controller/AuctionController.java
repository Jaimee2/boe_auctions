package com.example.boe_auction.auction_web_scraping.controller;

import com.example.boe_auction.auction_web_scraping.dao.document.Auction;
import com.example.boe_auction.auction_web_scraping.service.AuctionService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/auction")
@CrossOrigin
public class AuctionController {

    private AuctionService auctionService;

    @GetMapping
    public ResponseEntity<List<Auction>> getAllAuctions(
            @RequestParam(required = false) String auctionType,
            @RequestParam(required = false) String assetType,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String province,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String minimumBid,
            @RequestParam(required = false) String minAppraisalValue,
            @RequestParam(required = false) String maxAppraisalValue
    ) {
        return ResponseEntity.ok(auctionService.getAllAuctions(
                auctionType, city, startDate, endDate, minimumBid,
                minAppraisalValue, maxAppraisalValue, province, assetType
        ));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAllAuctions() {
        auctionService.deleteAllAuctions();
        return ResponseEntity.ok().build();
    }

}
