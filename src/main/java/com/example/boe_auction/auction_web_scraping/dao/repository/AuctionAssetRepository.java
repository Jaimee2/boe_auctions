package com.example.boe_auction.auction_web_scraping.dao.repository;

import com.example.boe_auction.auction_web_scraping.dao.document.AuctionAsset;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AuctionAssetRepository extends MongoRepository<AuctionAsset, String>, AssetRepositoryCustom {
}
