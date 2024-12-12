package dto;

import lombok.*;
import model.Currency;

import java.math.BigDecimal;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExchangeRateDTO {
    private Long id;
    private CurrencyDTO baseCurrency;
    private CurrencyDTO targetCurrency;
    private BigDecimal rate;
}
