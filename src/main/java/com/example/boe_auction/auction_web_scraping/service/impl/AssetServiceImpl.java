package com.example.boe_auction.auction_web_scraping.service.impl;

import com.example.boe_auction.auction_web_scraping.dao.document.AuctionAsset;
import com.example.boe_auction.auction_web_scraping.dao.repository.AuctionAssetRepository;
import com.example.boe_auction.auction_web_scraping.service.AssetService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class AssetServiceImpl implements AssetService {

    private AuctionAssetRepository auctionAssetRepository;

    @Override
    public List<AuctionAsset> getAllAsset() {
        return auctionAssetRepository.findAll();
    }

    @Override
    public List<AuctionAsset> getAllAssetForMap(
            String city, String startDate, String endDate, String minimumBid, String minAppraisalValue,
            String maxAppraisalValue, String province, List<String> assetTypes) {

        return auctionAssetRepository.findAssetBy(
                city, startDate, endDate, minimumBid,
                minAppraisalValue, maxAppraisalValue, province, assetTypes
        );
    }


}
