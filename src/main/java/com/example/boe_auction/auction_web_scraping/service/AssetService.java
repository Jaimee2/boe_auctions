package com.example.boe_auction.auction_web_scraping.service;

import com.example.boe_auction.auction_web_scraping.dao.document.AuctionAsset;
import com.example.boe_auction.auction_web_scraping.dto.AssetMapInitDto;

import java.util.List;

public interface AssetService {
    List<AuctionAsset> getAllAsset();

    List<AssetMapInitDto> getAllAssetForMap(String city, String startDate, String endDate, String minimumBid,
                                            String minAppraisalValue, String maxAppraisalValue, String province,
                                            List<String> assetTypes
    );

    void deleteOldAsset() throws InterruptedException;
}
