package com.tistory.jaimemin.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Field {

    private String type;

    private boolean optional;

    private String field;
}
