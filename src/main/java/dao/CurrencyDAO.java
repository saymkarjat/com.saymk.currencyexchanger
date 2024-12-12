package dao;

import config.HikariConnectionPool;
import exception.CurrencyAlreadyExistException;
import exception.DatabaseException;
import model.Currency;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CurrencyDAO implements CrudDAO<Currency> {

    @Override
    public Optional<Currency> findById(Long id) {
        String query = "SELECT * FROM Currencies WHERE id = ?";
        try (Connection connection = HikariConnectionPool.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            Currency currency = null;
            if (resultSet.next()) {
                currency = mapStringToCurrency(resultSet);
                resultSet.close();
            }
            return Optional.ofNullable(currency);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<Currency> findByCode(String code) {
        final String query = "SELECT * FROM Currencies WHERE code = ?";

        try (Connection connection = HikariConnectionPool.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, code);
            Currency currency = null;
            statement.execute();
            ResultSet resultSet = statement.getResultSet();
            if (resultSet.next()) {
                currency = mapStringToCurrency(resultSet);
                resultSet.close();
            }
            return Optional.ofNullable(currency);

        } catch (SQLException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    @Override
    public List<Currency> findAll() {
        List<Currency> currencyList = new ArrayList<>();
        String query = "SELECT * FROM Currencies";
        try (Connection connection = HikariConnectionPool.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Currency currency = mapStringToCurrency(resultSet);
                currencyList.add(currency);
            }
            return currencyList;
        } catch (SQLException e) {
            throw new DatabaseException(e.getCause().toString());
        }
    }

    private Currency mapStringToCurrency(ResultSet resultSet) {
        try {
            Long id = resultSet.getLong("id");
            String code = resultSet.getString("code");
            String fullName = resultSet.getString("fullname");
            String sign = resultSet.getString("sign");
            Currency currency = new Currency(id, code, fullName, sign);
            return currency;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void save(Currency currency) {
        String query = "INSERT INTO Currencies (code, fullname, sign) VALUES (?, ?, ?)";
        try (Connection connection = HikariConnectionPool.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, currency.getCode());
            statement.setString(2, currency.getFullName());
            statement.setString(3, currency.getSign());
            statement.execute();
        } catch (SQLException e) {
            if (e.getErrorCode() == 19) { // SQLITE_CONSTRAINT
                throw new CurrencyAlreadyExistException("Валюта с таким именем уже существует");
            }
            throw new DatabaseException("Ошибка базы данных: " + e.getMessage());
        }
    }

    @Override
    public void update(Currency entity) {

    }

    @Override
    public void delete(Long id) {

    }
}
