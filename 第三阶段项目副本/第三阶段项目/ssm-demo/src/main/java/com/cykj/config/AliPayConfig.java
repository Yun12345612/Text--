package com.cykj.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
public class AliPayConfig {

    @Value("#{alipay['alipay.appId']}")
    private String appId;

    @Value("#{alipay['alipay.appPrivateKey']}")
    private String appPrivateKey;

    @Value("#{alipay['alipay.alipayPublicKey']}")
    private String alipayPublicKey;

    @Value("#{alipay['alipay.notifyUrl']}")
    private String notifyUrl;

    @Value("#{alipay['alipay.returnUrl']}")
    private String returnUrl;
}