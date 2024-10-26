package com.example.boe_auction.auction_web_scraping.controller;

import com.example.boe_auction.auction_web_scraping.dao.document.AuctionAsset;
import com.example.boe_auction.auction_web_scraping.dto.AssetMapInitDto;
import com.example.boe_auction.auction_web_scraping.service.AssetService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@AllArgsConstructor
@RequestMapping("/asset")
public class AssetController {

    private AssetService assetService;

    @GetMapping
    public ResponseEntity<List<AuctionAsset>> getAllAsset() {
        return ResponseEntity.ok(assetService.getAllAsset());
    }

    @GetMapping("/map-filter")
    public ResponseEntity<List<AssetMapInitDto>> getAllAssetForMap(
            @RequestParam(required = false) List<String> assetTypes,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String province,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String minimumBid,
            @RequestParam(required = false) String minAppraisalValue,
            @RequestParam(required = false) String maxAppraisalValue
    ) {
        return ResponseEntity.ok(assetService.getAllAssetForMap(city, startDate, endDate, minimumBid,
                minAppraisalValue, maxAppraisalValue, province, assetTypes));
    }

    @DeleteMapping("/old")
    public ResponseEntity<String> deleteOldAuctions() throws InterruptedException {
        assetService.deleteOldAsset();
        return ResponseEntity.ok("Old auctions deleted successfully");
    }

}
