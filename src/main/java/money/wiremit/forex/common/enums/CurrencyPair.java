package money.wiremit.forex.common.enums;

public enum CurrencyPair { USD_GBP, USD_ZAR, ZAR_GBP;
    public static CurrencyPair of(String s){
        return switch (s.toUpperCase()) {
            case "USD-GBP","USD_GBP" -> USD_GBP;
            case "USD-ZAR","USD_ZAR" -> USD_ZAR;
            case "ZAR-GBP","ZAR_GBP" -> ZAR_GBP;
            default -> throw new IllegalArgumentException("Unsupported pair: " + s);
        };
    }
    public String asCode(){ return name().replace('_','-'); }
}
