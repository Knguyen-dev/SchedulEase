package com.knguyendev.api.domain.dto.error;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomErrorDTO {

    private int status;
    private String message;
    private Map<String, String> errors;
}
