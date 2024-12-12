package dao;

import config.HikariConnectionPool;
import exception.*;
import model.Currency;
import model.ExchangeRate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExchangeRateDAO implements CrudDAO<ExchangeRate>{
    CurrencyDAO currencyDAO = new CurrencyDAO();

    @Override
    public Optional<ExchangeRate> findById(Long id) {
        String query = "SELECT * FROM ExchangeRates WHERE id = ?";
        try (Connection connection = HikariConnectionPool.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            ExchangeRate exchangeRate = null;
            if (resultSet.next()) {
                exchangeRate = mapStringToExchangeRate(resultSet);
                resultSet.close();
            }
            return Optional.ofNullable(exchangeRate);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    private ExchangeRate mapStringToExchangeRate(ResultSet resultSet) {
        try {
            Long id = resultSet.getLong("id");
            Currency baseCurrency = currencyDAO
                    .findById(resultSet.getLong("baseCurrencyId"))
                    .orElseThrow(()-> new CurrencyNotFoundException("Валюта не найдена"));
            Currency targetCurrency = currencyDAO
                    .findById(resultSet.getLong("targetCurrencyId"))
                    .orElseThrow(()-> new CurrencyNotFoundException("Валюта не найдена"));
            BigDecimal rate = resultSet.getBigDecimal("rate");
            return new ExchangeRate(id, baseCurrency, targetCurrency, rate);
        } catch (SQLException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    @Override
    public List<ExchangeRate> findAll() {
        List<ExchangeRate> exchangeRates = new ArrayList<>();
        String query = "SELECT * FROM ExchangeRates";
        try (Connection connection = HikariConnectionPool.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                ExchangeRate exchangeRate = mapStringToExchangeRate(resultSet);
                exchangeRates.add(exchangeRate);
            }
            return exchangeRates;
        } catch (SQLException e) {
            throw new DatabaseException(e.getCause().toString());
        }
    }
    public Optional<ExchangeRate> findByCurrencyPair(String baseCurrency, String targetCurrency) {
        String query = "SELECT ExchangeRates.ID, ExchangeRates.BaseCurrencyId, ExchangeRates.TargetCurrencyId, ExchangeRates.Rate\n" +
                "FROM ExchangeRates\n" +
                "         JOIN main.Currencies C on C.ID = ExchangeRates.BaseCurrencyId\n" +
                "         JOIN main.Currencies C2 on C2.ID = ExchangeRates.TargetCurrencyId\n" +
                "WHERE C.Code = ? AND C2.Code = ?";
        try (Connection connection = HikariConnectionPool.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, baseCurrency);
            statement.setString(2, targetCurrency);
            ResultSet resultSet = statement.executeQuery();
            ExchangeRate exchangeRate = null;
            if (resultSet.next()) {
                exchangeRate = mapStringToExchangeRate(resultSet);
                resultSet.close();
            }
            return Optional.ofNullable(exchangeRate);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void save(ExchangeRate entity) {
        String query = "INSERT INTO ExchangeRates (baseCurrencyId, targetCurrencyId, rate) VALUES (?, ?, ?)";
        try (Connection connection = HikariConnectionPool.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, entity.getBaseCurrency().getId());
            statement.setLong(2, entity.getTargetCurrency().getId());
            statement.setBigDecimal(3, entity.getRate());
            statement.execute();
        } catch (SQLException e) {
            if (e.getErrorCode() == 19) { // SQLITE_CONSTRAINT
                throw new ExchangeRateAlreadyExistException("Такой курс уже существует");
            }
            throw new DatabaseException("Ошибка базы данных: " + e.getMessage());
        }
    }

    @Override
    public void update(ExchangeRate entity) {
        String query = "UPDATE ExchangeRates SET basecurrencyid = ?, targetcurrencyid = ?, rate = ? WHERE id = ?";

        try (Connection connection = HikariConnectionPool.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, entity.getBaseCurrency().getId());
            statement.setLong(2, entity.getTargetCurrency().getId());
            statement.setBigDecimal(3, entity.getRate());
            statement.setLong(4, entity.getId());
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected == 0) {
                // Если строка не обновлена, бросаем исключение
                throw new ExchangeRateNotFoundException("Строка с указанными валютами не найдена, либо не обновлена");
            }
        } catch (SQLException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    @Override
    public void delete(Long id) {
        String query = "DELETE FROM ExchangeRates WHERE id = ?";
        try (Connection connection = HikariConnectionPool.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            statement.execute();
        } catch (SQLException e) {
            throw new DatabaseException(e.getMessage());
        }
    }
    private void updateReverseExchangeRate(ExchangeRate exchangeRate) {
        Optional<ExchangeRate> reverseExchangeRate = findByCurrencyPair(
                exchangeRate.getTargetCurrency().getCode(), exchangeRate.getBaseCurrency().getCode());
        if (reverseExchangeRate.isPresent()) {
            reverseExchangeRate.get().setRate(new BigDecimal(1).divide(exchangeRate.getRate(), RoundingMode.CEILING));
            update(reverseExchangeRate.get());
        }
    }
}
