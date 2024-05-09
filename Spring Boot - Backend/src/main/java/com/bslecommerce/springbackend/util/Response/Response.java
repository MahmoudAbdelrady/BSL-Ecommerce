package com.bslecommerce.springbackend.util.Response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
@AllArgsConstructor
@Getter
@Setter
public class Response<T> {
    private String status;
    private String message;
    private T body;
}
