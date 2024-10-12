package com.metasoft.pointbarmetasoft.shared.exception;
import lombok.*;
import org.springframework.http.HttpStatus;

@Getter
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApplicationException extends RuntimeException {
    private HttpStatus httpStatus;

    public ApplicationException(HttpStatus httpStatus, String message) {
        super(message);
        this.httpStatus = httpStatus;
    }
}
