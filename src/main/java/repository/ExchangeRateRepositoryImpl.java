package repository;

import dao.ExchangeRateDAO;
import model.ExchangeRate;

import java.util.List;
import java.util.Optional;

public class ExchangeRateRepositoryImpl implements ExchangeRateRepository {
    ExchangeRateDAO exchangeRateDAO = new ExchangeRateDAO();
    @Override
    public Optional<ExchangeRate> findExchangeRateById(Long id) {
        return exchangeRateDAO.findById(id);
    }

    @Override
    public List<ExchangeRate> findAllExchangeRates() {
        return exchangeRateDAO.findAll();
    }

    @Override
    public Optional<ExchangeRate> findExchangeRateByCodes(String baseCurrency, String targetCurrency) {
        return exchangeRateDAO.findByCurrencyPair(baseCurrency, targetCurrency);
    }

    @Override
    public void addExchangeRate(ExchangeRate exchangeRate) {
        exchangeRateDAO.save(exchangeRate);
    }

    @Override
    public void updateExchangeRate(ExchangeRate exchangeRate) {
        exchangeRateDAO.update(exchangeRate);
    }

    @Override
    public void deleteExchangeRate(Long id) {
        exchangeRateDAO.delete(id);
    }
}
