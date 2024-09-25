package com.example.boe_auction.auction_web_scraping.service;

import com.example.boe_auction.auction_web_scraping.dao.document.AuctionAsset;

import java.util.List;

public interface AssetService {
    List<AuctionAsset> getAllAsset();

    List<AuctionAsset> getAllAssetForMap(String city, String startDate, String endDate, String minimumBid,
                                         String minAppraisalValue, String maxAppraisalValue, String province,
                                         List<String> assetTypes
    );
}
