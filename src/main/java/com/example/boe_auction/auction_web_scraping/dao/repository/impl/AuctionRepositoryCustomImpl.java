package com.example.boe_auction.auction_web_scraping.dao.repository.impl;

import com.example.boe_auction.auction_web_scraping.dao.document.Auction;
import com.example.boe_auction.auction_web_scraping.dao.repository.AuctionRepositoryCustom;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

@Slf4j
@AllArgsConstructor
public class AuctionRepositoryCustomImpl implements AuctionRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    @Override
    public List<Auction> findAuctionsByCriteria(
            String auctionType, String city, String startDate, String endDate,
            String minimumBid, String minAppraisalValue, String maxAppraisalValue, String province,
            List<String> assetTypes) {

        Query query = new Query();

        if (auctionType != null && !auctionType.isEmpty()) {
            query.addCriteria(Criteria.where("auctionType").is(auctionType));
        }
        if (assetTypes != null && !assetTypes.isEmpty()) {
            query.addCriteria(Criteria.where("assets.assetType").in(assetTypes));
        }
        if (city != null && !city.isEmpty()) {
            query.addCriteria(Criteria.where("assets.city").is(city));
        }
        if (province != null && !province.isEmpty()) {
            query.addCriteria(Criteria.where("assets.province").is(province));
        }
        if (startDate != null && endDate != null && !startDate.isEmpty() && !endDate.isEmpty()) {
            query.addCriteria(Criteria.where("startDate").gte(startDate).lte(endDate));
        }
        if (minimumBid != null && !minimumBid.isEmpty()) {
            query.addCriteria(Criteria.where("minimumBid").is(minimumBid));
        }
        if (minAppraisalValue != null && !minAppraisalValue.isEmpty()) {
            query.addCriteria(Criteria.where("appraisalValue").gte(minAppraisalValue));
        }
        if (maxAppraisalValue != null && !maxAppraisalValue.isEmpty()) {
            query.addCriteria(Criteria.where("appraisalValue").lte(maxAppraisalValue));
        }
        log.info("Query use to find data in mongodb: {}",query);
        return mongoTemplate.find(query, Auction.class);
    }
}
