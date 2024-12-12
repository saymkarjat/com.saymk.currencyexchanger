package service;

import dto.CurrencyDTO;
import dto.CurrencyMapper;
import exception.CurrencyNotFoundException;
import jakarta.servlet.annotation.WebServlet;
import model.Currency;
import repository.CurrencyRepositoryImpl;

import java.util.List;
import java.util.Optional;

public class CurrencyService {
    CurrencyRepositoryImpl repository = new CurrencyRepositoryImpl();

    public List<CurrencyDTO> getAllCurrencies() {
        List<Currency> allCurrencies = repository.getAllCurrencies();
        return CurrencyMapper.INSTANCE.toDTOList(allCurrencies);
    }

    public CurrencyDTO getCurrencyByCode(String code) {
        Optional<Currency> currencyByCode = repository.getCurrencyByCode(code);
        if (currencyByCode.isPresent()){
            Currency currency = currencyByCode.get();
            return CurrencyMapper.INSTANCE.toDTO(currency);
        }
        else {
            throw new CurrencyNotFoundException("The currency with this code was not found");
        }
    }
    public CurrencyDTO getCurrencyById(Long id) {
        Optional<Currency> currencyById = repository.getCurrencyById(id);
        if (currencyById.isPresent()){
            Currency currency = currencyById.get();
            return CurrencyMapper.INSTANCE.toDTO(currency);
        }
        else {
            throw new CurrencyNotFoundException("The currency with this id was not found");
        }
    }

    public void addCurrency(CurrencyDTO currencyDTO) {
        repository.addCurrency(CurrencyMapper.INSTANCE.toEntity(currencyDTO));
    }
}
