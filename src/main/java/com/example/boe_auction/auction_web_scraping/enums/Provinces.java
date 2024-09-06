package com.example.boe_auction.auction_web_scraping.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Getter
public enum Provinces {
    ARABA_ALAVA("01", "Araba/Álava"),
    ALBACETE("02", "Albacete"),
    ALICANTE_ALACANT("03", "Alicante/Alacant"),
    ALMERIA("04", "Almería"),
    AVILA("05", "Ávila"),
    BADAJOZ("06", "Badajoz"),
    ILLES_BALEARS("07", "Illes Balears"),
    BARCELONA("08", "Barcelona"),
    BURGOS("09", "Burgos"),
    CACERES("10", "Cáceres"),
    CADIZ("11", "Cádiz"),
    CASTELLON_CASTELLO("12", "Castellón/Castelló"),
    CIUDAD_REAL("13", "Ciudad Real"),
    CORDOBA("14", "Córdoba"),
    A_CORUNA("15", "A Coruña"),
    CUENCA("16", "Cuenca"),
    GIRONA("17", "Girona"),
    GRANADA("18", "Granada"),
    GUADALAJARA("19", "Guadalajara"),
    GIPUZKOA("20", "Gipuzkoa"),
    HUELVA("21", "Huelva"),
    HUESCA("22", "Huesca"),
    JAEN("23", "Jaén"),
    LEON("24", "León"),
    LLEIDA("25", "Lleida"),
    LA_RIOJA("26", "La Rioja"),
    LUGO("27", "Lugo"),
    MADRID("28", "Madrid"),
    MALAGA("29", "Málaga"),
    MURCIA("30", "Murcia"),
    NAVARRA("31", "Navarra"),
    OURENSE("32", "Ourense"),
    ASTURIAS("33", "Asturias"),
    PALENCIA("34", "Palencia"),
    LAS_PALMAS("35", "Las Palmas"),
    PONTEVEDRA("36", "Pontevedra"),
    SALAMANCA("37", "Salamanca"),
    SANTA_CRUZ_DE_TENERIFE("38", "Santa Cruz de Tenerife"),
    CANTABRIA("39", "Cantabria"),
    SEGOVIA("40", "Segovia"),
    SEVILLA("41", "Sevilla"),
    SORIA("42", "Soria"),
    TARRAGONA("43", "Tarragona"),
    TERUEL("44", "Teruel"),
    TOLEDO("45", "Toledo"),
    VALENCIA_VALENCIA("46", "Valencia/València"),
    VALLADOLID("47", "Valladolid"),
    BIZKAIA("48", "Bizkaia"),
    ZAMORA("49", "Zamora"),
    ZARAGOZA("50", "Zaragoza"),
    CEUTA("51", "Ceuta"),
    MELILLA("52", "Melilla"),
    NO_CONSTA("00", "No consta");

    private final String code;
    private final String name;

    public static List<String> getAllCodes() {
        List<String> codes = new ArrayList<>();
        for (Provinces provinces : Provinces.values()) {
            codes.add(provinces.getCode());
        }
        return codes;
    }
}
