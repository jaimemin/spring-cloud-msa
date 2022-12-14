package com.tistory.jaimemin.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KafkaOrderDto implements Serializable {

    private Schema schema;

    private Payload payload;
}
