package com.example.boe_auction.auction_web_scraping.service.service.impl;

import com.example.boe_auction.auction_web_scraping.model.Auction;
import com.example.boe_auction.auction_web_scraping.service.service.AuctionWebScrapingService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class AuctionWebScrapingServiceImpl implements AuctionWebScrapingService {

    public List<Auction> performQuery() throws IOException {
        String url = "https://subastas.boe.es/subastas_ava.php";

        Document document = Jsoup.connect(url)
                .method(Connection.Method.POST)
                .data("campo[2]", "SUBASTA.ESTADO.CODIGO")
                .data("dato[2]", "EJ")
                .data("campo[3]", "BIEN.TIPO")
                .data("dato[3]", "I")
                .data("dato[4]", "")
                .data("campo[8]", "BIEN.COD_PROVINCIA")
                .data("dato[8]", "28") //28 -> Madrid
                .data("page_hits", "500")
                .data("sort_field[0]", "SUBASTA.FECHA_FIN")
                .data("sort_order[0]", "desc")
                .data("sort_field[1]", "SUBASTA.FECHA_FIN")
                .data("sort_order[1]", "asc")
                .data("accion", "Buscar")
                .execute().parse();

        List<Auction> auctions = new ArrayList<>();

        List<String> auctionDetailLinkList = document.select(".resultado-busqueda")
                .stream()
                .map(auctionElement -> getLink(auctionElement
                        .selectFirst("a.resultado-busqueda-link-defecto").attr("href"))
                ).toList();

        for (String auctionLink : auctionDetailLinkList) {
            auctions.add(scrapeDetailPage(auctionLink));
        }

        return auctions;
    }

    private Auction scrapeDetailPage(String url) throws IOException {
        Document detailDoc = Jsoup.connect(url).get();

        Element dataTable = detailDoc.getElementById("idBloqueDatos1").selectFirst("table tbody");

        Auction.AuctionBuilder auction = Auction.builder();

                auction.identifier(getTextFromTable(dataTable, "Identificador"))
                .auctionType(getTextFromTable(dataTable, "Tipo de subasta"))
                .startDate((getTextFromTable(dataTable, "Fecha de inicio").split("\\(ISO")[0].trim()))
                .endDate((getTextFromTable(dataTable, "Fecha de conclusión").split("\\(ISO")[0].trim()))
                .lots((getTextFromTable(dataTable, "Lotes")))
                .announcementBOE((getTextFromTable(dataTable, "Lotes")))
                .auctionValue(getTextFromTable(dataTable, "Anuncio BOE"))
                .appraisalValue(getTextFromTable(dataTable, "Valor subasta"))
                .minimumBid(getTextFromTable(dataTable, "Puja mínima"))
                .bidIncrement(getTextFromTable(dataTable, "Tramos entre pujas"))
                .depositAmount(getTextFromTable(dataTable, "Importe del depósito"))
                .build();

        detailDoc.select("td:contains(Identificador) + td strong").text();
        Elements linkElement = detailDoc.select("a[href*='ver=3']");
        String auctionUrl = getLink(linkElement.attr("href"));
        scrapeDetailPageGoods(auctionUrl);

        return auction.build();
    }

    private String getTextFromTable(Element table, String headerText) {
        Elements rows = table.select("tr");
        for (Element row : rows) {
            Element header = row.selectFirst("th");
            if (header != null && header.text().equalsIgnoreCase(headerText)) {
                return row.selectFirst("td").text();
            }
        }
        return "";
    }

    private String getLink(String hrefLink) {
        return "https://subastas.boe.es" + hrefLink.substring(1);
    }

    private Auction scrapeDetailPageGoods(String url) throws IOException {
        Document detailDoc = Jsoup.connect(url).get();

        return null;
    }

}
