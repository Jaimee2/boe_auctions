package com.example.boe_auction.auction_web_scraping.service.service.impl;

import com.example.boe_auction.auction_web_scraping.model.Auction;
import com.example.boe_auction.auction_web_scraping.model.AuctionAsset;
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
            auctions.add(scrapeAuction(auctionLink));
        }

        return auctions;
    }

    private Auction scrapeAuction(String url) throws IOException {
        Document detailDoc = Jsoup.connect(url).get();

        Auction auction = getGeneralInformation(detailDoc);

        detailDoc.select("td:contains(Identificador) + td strong").text();
        Elements linkElement = detailDoc.select("a[href*='ver=3']");
        String auctionUrl = getLink(linkElement.attr("href"));

        auction.setAssets(List.of(getGoods(auctionUrl)));

        return auction;
    }

    private Auction getGeneralInformation(Document detailDoc) {
        Element dataTable = detailDoc.getElementById("idBloqueDatos1").selectFirst("table tbody");
        assert dataTable != null;
        return Auction.builder()
                .identifier(getTextFromTable(dataTable, "Identificador"))
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

    private AuctionAsset getGoods(String url) throws IOException {
        Document detailDoc = Jsoup.connect(url).get();
        Element assetTable = detailDoc.selectFirst("#idBloqueDatos3 .bloque table");
        assert assetTable != null;
        return AuctionAsset.builder()
                .description(getTextFromTable(assetTable, "Descripción"))
                .iDufir(getTextFromTable(assetTable, "IDUFIR"))
                .cadastralReference(getTextFromTable(assetTable, "Referencia catastral"))
                .address(getTextFromTable(assetTable, "Dirección"))
                .postalCode(getTextFromTable(assetTable, "Código Postal"))
                .city(getTextFromTable(assetTable, "Localidad"))
                .province(getTextFromTable(assetTable, "Provincia"))
                .isPrimaryResidence(getTextFromTable(assetTable, "Vivienda habitual").equalsIgnoreCase("Sí"))
                .possessionStatus(getTextFromTable(assetTable, "Situación posesoria"))
                .isVisitable(getTextFromTable(assetTable, "Visitable"))
                .encumbrances(getTextFromTable(assetTable, "Cargas"))
                .registryDetails(getTextFromTable(assetTable, "Inscripción registral"))
                .legalTitle(getTextFromTable(assetTable, "Título jurídico"))
                .build();
    }

}
