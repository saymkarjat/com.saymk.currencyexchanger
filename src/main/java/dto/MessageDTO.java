package dto;

import lombok.Getter;

@Getter
public class MessageDTO {
    private String message;

    public MessageDTO(String message) {
        this.message = message;
    }
}
