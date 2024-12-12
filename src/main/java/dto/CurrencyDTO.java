package dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonPropertyOrder({"id", "name", "code", "sign"})
public class CurrencyDTO {
    private Long id;
    private String name;
    private String code;
    private String sign;
}
