package repository;

import dao.CurrencyDAO;
import model.Currency;

import java.util.List;
import java.util.Optional;

public class CurrencyRepositoryImpl implements CurrencyRepository {
    CurrencyDAO currencyDAO = new CurrencyDAO();

    @Override
    public List<Currency> getAllCurrencies() {
        return currencyDAO.findAll();
    }

    @Override
    public Optional<Currency> getCurrencyByCode(String code) {
        return currencyDAO.findByCode(code);
    }

    @Override
    public Optional<Currency> getCurrencyById(Long id) {
        return currencyDAO.findById(id);
    }

    @Override
    public void addCurrency(Currency currency) {
        currencyDAO.save(currency);
    }
}
