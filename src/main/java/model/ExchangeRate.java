package model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

import java.math.BigDecimal;
@Data
@AllArgsConstructor
public class ExchangeRate {
    private Long id;
    @NonNull
    private Currency baseCurrency;
    @NonNull
    private Currency targetCurrency;
    @NonNull
    private BigDecimal rate;
}
