package com.example.boe_auction.auction_web_scraping.mapper;

import com.example.boe_auction.auction_web_scraping.dao.document.AuctionAsset;
import com.example.boe_auction.auction_web_scraping.dto.AssetMapInitDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface AuctionAssetMapper {

    AuctionAssetMapper INSTANCE = Mappers.getMapper(AuctionAssetMapper.class);

    AssetMapInitDto daoToDto(AuctionAsset auctionAsset);

    List<AssetMapInitDto> daoToDto(List<AuctionAsset> auctionAsset);
}
