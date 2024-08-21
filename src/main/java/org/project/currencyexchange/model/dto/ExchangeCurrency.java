package org.project.currencyexchange.model.dto;

import lombok.Getter;

@Getter
public enum ExchangeCurrency {
    AED("United Arab Emirates Dirham"),
    ARS("Argentine Peso"),
    AUD("Australian Dollar"),
    BDT("Bangladeshi Taka"),
    BGN("Bulgarian Lev"),
    BRL("Brazilian Real"),
    CAD("Canadian Dollar"),
    CHF("Swiss Franc"),
    CNY("Chinese Yuan"),
    CZK("Czech Republic Koruna"),
    DKK("Danish Krone"),
    EGP("Egyptian Pound"),
    EUR("Euro"),
    GBP("British Pound Sterling"),
    HKD("Hong Kong Dollar"),
    HUF("Hungarian Forint"),
    IDR("Indonesian Rupiah"),
    ILS("Israeli New Sheqel"),
    INR("Indian Rupee"),
    JPY("Japanese Yen"),
    KES("Kenyan Shilling"),
    KRW("South Korean Won"),
    MAD("Moroccan Dirham"),
    MXN("Mexican Peso"),
    MYR("Malaysian Ringgit"),
    NGN("Nigerian Naira"),
    NOK("Norwegian Krone"),
    NZD("New Zealand Dollar"),
    PHP("Philippine Peso"),
    PLN("Polish Zloty"),
    QAR("Qatari Rial"),
    RON("Romanian Leu"),
    RUB("Russian Ruble"),
    SAR("Saudi Riyal"),
    SEK("Swedish Krona"),
    SGD("Singapore Dollar"),
    THB("Thai Baht"),
    TRY("Turkish Lira"),
    TWD("New Taiwan Dollar"),
    UAH("Ukrainian Hryvnia"),
    USD("United States Dollar"),
    UYU("Uruguayan Peso"),
    VND("Vietnamese Dong"),
    ZAR("South African Rand");

    private final String currencyName;

    ExchangeCurrency(String currencyName) {
        this.currencyName = currencyName;
    }

}
