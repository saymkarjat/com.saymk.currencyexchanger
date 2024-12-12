package repository;

import model.ExchangeRate;

import java.util.List;
import java.util.Optional;

public interface ExchangeRateRepository {
    Optional<ExchangeRate> findExchangeRateById(Long id);
    List<ExchangeRate> findAllExchangeRates();
    Optional<ExchangeRate> findExchangeRateByCodes(String baseCurrency, String targetCurrency);
    void addExchangeRate(ExchangeRate exchangeRate);
    void updateExchangeRate(ExchangeRate exchangeRate);
    void deleteExchangeRate(Long id);
}
