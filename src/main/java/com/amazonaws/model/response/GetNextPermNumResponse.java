package com.amazonaws.model.response;

import com.amazonaws.model.Order;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@JsonAutoDetect
public class GetNextPermNumResponse {
    private final String nextPermNum;
}
