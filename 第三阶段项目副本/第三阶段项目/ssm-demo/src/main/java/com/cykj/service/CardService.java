package com.cykj.service;

import com.cykj.dto.ResponseDTO;

import java.math.BigDecimal;

public interface CardService {
    // 卡入库业务方法
    ResponseDTO importCards(String startCardNo, String endCardNo, String prefix, BigDecimal money);

}
