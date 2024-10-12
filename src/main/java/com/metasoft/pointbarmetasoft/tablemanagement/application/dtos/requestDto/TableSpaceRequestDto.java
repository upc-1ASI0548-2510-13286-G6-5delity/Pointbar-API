package com.metasoft.pointbarmetasoft.tablemanagement.application.dtos.requestDto;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TableSpaceRequestDto {
    @NotBlank(message = "El nombre del espacio de mesa es requerido")
    private String name;

    @NotNull(message = "El número de mesas es requerido")
    @Min(value = 1, message = "El número de mesas debe ser al menos 1")
    private Integer numberOfTables;

    private MultipartFile image;
}
