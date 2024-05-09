package com.bslecommerce.springbackend.util;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class SecretKeyReader {
    @Value("${secretKey}")
    private String secretKey;
}
