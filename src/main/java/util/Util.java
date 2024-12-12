package util;

import java.math.BigDecimal;

public class Util {
    public static boolean isCurrencyValid(String code, String name, String sign) {
        if (code == null || name == null || sign == null) {
            return false;
        }
        if (code.isEmpty() || name.isEmpty() || sign.isEmpty()) {
            return false;
        }
        if (code.length() != 3 || name.length() > 54 || sign.length() > 3){
            return false;
        }
        return true;
    }
    public static boolean isExchangeRateValid(String baseCurrency, String targetCurrency, String rate) {
        if (baseCurrency == null || targetCurrency == null || rate == null) {
            return false;
        }
        if (baseCurrency.isEmpty() || targetCurrency.isEmpty() || rate.isEmpty()) {
            return false;
        }
        if (baseCurrency.length() != 3 || targetCurrency.length() != 3){
            return false;
        }
        if (!isStringDouble(rate)){
            return false;
        }
        return true;
    }

    public static boolean isStringDouble(String d) {
        if (d == null || d.isEmpty()){
            return false;
        }
        try {
            new BigDecimal(d);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    public static boolean isCodeValid(String code){
        if (code.length() != 4){
            return false;
        }
        String currentCode = code.substring(1);
        for (char c : currentCode.toCharArray()) {
            if (!Character.isLetter(c)){
                return false;
            }
        }
        return true;
    }
    public static boolean isCurrencyPairValid(String string){
        if (string.length() != 7){
            return false;
        }
        String currentCode = string.substring(1);
        for (char c : currentCode.toCharArray()) {
            if (!Character.isLetter(c)){
                return false;
            }
        }
        return true;
    }
    public static String getFormattedCode(String code){
        String substring = code.substring(1);
        return substring.toUpperCase();
    }
}
