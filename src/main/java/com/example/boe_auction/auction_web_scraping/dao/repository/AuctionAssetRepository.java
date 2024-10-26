package com.example.boe_auction.auction_web_scraping.dao.repository;

import com.example.boe_auction.auction_web_scraping.dao.document.AuctionAsset;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;
import java.util.List;

public interface AuctionAssetRepository extends MongoRepository<AuctionAsset, String>, AssetRepositoryCustom {
    List<AuctionAsset> findTopByEndDateBefore(Date now, PageRequest of);
}
