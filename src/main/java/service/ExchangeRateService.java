package service;

import dto.ExchangeRateDTO;
import dto.ExchangeRateMapper;
import exception.ExchangeRateNotFoundException;
import model.ExchangeRate;
import repository.ExchangeRateRepository;
import repository.ExchangeRateRepositoryImpl;

import java.util.List;
import java.util.Optional;

public class ExchangeRateService {
    ExchangeRateRepository repository = new ExchangeRateRepositoryImpl();

    public ExchangeRateDTO findExchangeRateById(Long id) {
        Optional<ExchangeRate> exchangeRateById = repository.findExchangeRateById(id);
        if (exchangeRateById.isPresent()){
            return ExchangeRateMapper.INSTANCE.toDTO(exchangeRateById.get());
        }
        else {
            throw new ExchangeRateNotFoundException("Обменного курса с таким id не найдено.");
        }
    }


    public List<ExchangeRateDTO> findAllExchangeRates() {
        List<ExchangeRate> allExchangeRates = repository.findAllExchangeRates();
        return ExchangeRateMapper.INSTANCE.toDTOList(allExchangeRates);
    }


    public ExchangeRateDTO findExchangeRateByCodes(String baseCurrency, String targetCurrency) {
        Optional<ExchangeRate> exchangeRateByCodes = repository.findExchangeRateByCodes(baseCurrency, targetCurrency);
        if (exchangeRateByCodes.isPresent()){
            return ExchangeRateMapper.INSTANCE.toDTO(exchangeRateByCodes.get());
        }
        else {
            throw new ExchangeRateNotFoundException("Обменного курса не найдено.");
        }
    }


    public void addExchangeRate(ExchangeRateDTO exchangeRateDTO) {
        ExchangeRate exchangeRate = ExchangeRateMapper.INSTANCE.toEntity(exchangeRateDTO);
        repository.addExchangeRate(exchangeRate);
    }


    public void updateExchangeRate(ExchangeRateDTO exchangeRateDTO) {
        ExchangeRate exchangeRate = ExchangeRateMapper.INSTANCE.toEntity(exchangeRateDTO);
        repository.updateExchangeRate(exchangeRate);
    }


    public void deleteExchangeRate(Long id) {
        repository.deleteExchangeRate(id);
    }
}
