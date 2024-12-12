package repository;

import model.Currency;

import java.util.List;
import java.util.Optional;

public interface CurrencyRepository {
    List<Currency> getAllCurrencies();
    Optional<Currency> getCurrencyByCode(String code);
    Optional<Currency> getCurrencyById(Long id);
    void addCurrency(Currency currency);
}
