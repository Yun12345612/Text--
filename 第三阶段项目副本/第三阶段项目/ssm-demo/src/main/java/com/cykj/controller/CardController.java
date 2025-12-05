package com.cykj.controller;

import com.cykj.dto.ResponseDTO;
import com.cykj.pojo.Card;
import com.cykj.service.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: 夏日花店
 * @CreateTime: 2025-10-20 19:51:40
 * @Description: 卡片控制器
 * @Version: 1.0
 */
@RestController
@RequestMapping("/card")
public class CardController {
    @Autowired
    private CardService cardService;

    @PostMapping("/import")
    public ResponseDTO importCards(@RequestBody Card cardForm) {
        try {
            // 调用服务层执行卡入库业务
            cardService.importCards(
                    cardForm.getStartCardNo(),  // 起始卡号
                    cardForm.getEndCardNo(),    // 截止卡号
                    cardForm.getCardPrefix(),   // 前缀
                    cardForm.getCardMoney()     // 金额
            );
            // 入库成功：使用ResponseDTO的success方法返回成功信息
            return ResponseDTO.success(200, "卡入库成功", null);
        } catch (Exception e) {
            e.printStackTrace();
            // 入库失败：使用ResponseDTO的error方法返回错误信息
            return ResponseDTO.error(500, "卡入库失败：" + e.getMessage());
        }
    }
}