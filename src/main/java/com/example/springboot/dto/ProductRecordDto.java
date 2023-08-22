package com.example.springboot.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/*essa classe serve para que os campos String name, e BidDecimal value, vão como Json na minha requisição e estou dizeno q eles n podem ser vazios*/
public record ProductRecordDto(@NotBlank String name, @NotNull BigDecimal value) {

}
