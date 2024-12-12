package service;

import dto.CurrencyDTO;
import dto.ExchangeDTO;
import dto.ExchangeRateDTO;
import exception.ExchangeRateNotFoundException;
import exception.InvalidExchangeRateArgsException;
import jakarta.servlet.http.HttpServletRequest;
import util.Util;


import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class ExchangeService {
    ExchangeRateService serviceE = new ExchangeRateService();
    CurrencyService serviceC = new CurrencyService();

    public ExchangeDTO getExchange(HttpServletRequest req) throws IOException {
        String from = req.getParameter("from");
        String to = req.getParameter("to");
        String amount = req.getParameter("amount");

        if (!Util.isExchangeRateValid(from, to, amount)){
            throw new InvalidExchangeRateArgsException("Некорректные данные");
        }
        CurrencyDTO base = serviceC.getCurrencyByCode(from);
        CurrencyDTO target = serviceC.getCurrencyByCode(to);
        BigDecimal amountBase = BigDecimal.valueOf(Double.parseDouble(amount));
        BigDecimal convertedAmount;
        ExchangeRateDTO exRate;
        ExchangeRateDTO exRateInverse;
        try {
            exRate  = serviceE.findExchangeRateByCodes(from, to);
        }
        catch (ExchangeRateNotFoundException e){
            exRate = null;
        }
        try {
            exRateInverse = serviceE.findExchangeRateByCodes(to, from);
        }
        catch (ExchangeRateNotFoundException e){
            exRateInverse = null;
        }

        if (exRate != null){
            convertedAmount = exRate.getRate().multiply(amountBase).setScale(2, RoundingMode.HALF_UP);
            return new ExchangeDTO(base, target, exRate.getRate(), amountBase, convertedAmount);
        }
        if (exRateInverse != null){
            BigDecimal rate = BigDecimal.ONE.divide(exRateInverse.getRate(),4, RoundingMode.HALF_UP);
            convertedAmount = rate.multiply(amountBase).setScale(2, RoundingMode.HALF_UP);
            return  new ExchangeDTO(base, target, rate, amountBase, convertedAmount);
        }
        ExchangeRateDTO usdToBaseCurrency = serviceE.findExchangeRateByCodes("USD", from);
        ExchangeRateDTO usdToTargetCurrency = serviceE.findExchangeRateByCodes("USD", to);
        BigDecimal usdToBaseCurrencyRate = usdToBaseCurrency.getRate();
        BigDecimal usdToTargetCurrencyRate = usdToTargetCurrency.getRate();
        BigDecimal rate = usdToTargetCurrencyRate.divide(usdToBaseCurrencyRate,4, RoundingMode.HALF_UP);
        convertedAmount = rate.multiply(amountBase).setScale(2, RoundingMode.HALF_UP);
        return new ExchangeDTO(base, target, rate, amountBase, convertedAmount);
    }

}
