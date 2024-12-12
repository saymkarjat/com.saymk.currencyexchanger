package model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
@Data
public class Currency {
    private Long id;
    @NonNull
    private String code;
    @NonNull
    private String fullName;
    @NonNull
    private String sign;

    public Currency(Long id, @NonNull String code, @NonNull String fullName, @NonNull String sign) {
        this.fullName = fullName;
        this.id = id;
        this.code = code;
        this.sign = sign;
    }
}
