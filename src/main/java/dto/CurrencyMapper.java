package dto;

import model.Currency;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface CurrencyMapper {
    CurrencyMapper INSTANCE = Mappers.getMapper(CurrencyMapper.class);
    @Mapping(source = "fullName", target = "name")
    CurrencyDTO toDTO(Currency currency);
    @Mapping(source = "name", target = "fullName")
    Currency toEntity(CurrencyDTO dto);
    List<CurrencyDTO> toDTOList(List<Currency> currencies);
}
