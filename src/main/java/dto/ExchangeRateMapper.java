package dto;


import model.ExchangeRate;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import java.util.List;

@Mapper(uses = CurrencyMapper.class)
public interface ExchangeRateMapper {
    ExchangeRateMapper INSTANCE = Mappers.getMapper(ExchangeRateMapper.class);
    ExchangeRateDTO toDTO(ExchangeRate exchangeRate);
    ExchangeRate toEntity(ExchangeRateDTO dto);
    List<ExchangeRateDTO> toDTOList(List<ExchangeRate> exchangeRateList);
}
