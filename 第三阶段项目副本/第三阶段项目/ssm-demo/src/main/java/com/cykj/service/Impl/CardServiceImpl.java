package com.cykj.service.Impl;

import com.cykj.dto.ResponseDTO;
import com.cykj.mapper.CardMapper;
import com.cykj.pojo.Card;
import com.cykj.service.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author: 夏日花店
 * @CreateTime: 2025-10-14 11:16:30
 * @Description: 卡片业务
 * @Version: 1.0
 */

@Service
public class CardServiceImpl implements CardService {
    @Autowired
    private CardMapper cardMapper;

    @Transactional(rollbackFor = Exception.class)
    public ResponseDTO importCards(String startCardNo, String endCardNo, String prefix, BigDecimal money) {
        try { // 捕获所有可能的异常
            List<Card> cardList = new ArrayList<>();
            // 可能抛出NumberFormatException的地方
            long start = Long.parseLong(startCardNo);
            long end = Long.parseLong(endCardNo);
            Date now = new Date();
            for (long i = start; i <= end; i++) {
                Card card = new Card();

                // 确保前缀不为null
                if (prefix == null) {
                    prefix = "";
                }

                card.setCardPrefix(prefix);


                int num = 10 - prefix.length();//卡号总位数
                String cardNumberSuffix = String.format("%0" + num + "d", i);  // 赋值给变量
                card.setCardNumber(prefix + cardNumberSuffix);  // 使用 i 生成的卡号

//                 人为制造异常 测试事务回滚
//                if (i == 2) {
//                    int res = 1 / 0;
//                }
                card.setCardMoney(money);
                card.setCardIsDelete(0); // 初始为未删除
                card.setCardStatus(0); // 初始状态
                card.setCardCreateTime(now);
                card.setCardUpdateTime(now);
                cardList.add(card);
            }
            // 可能抛出SQL异常的地方
            cardMapper.batchInsertCards(cardList);
            return ResponseDTO.success(200, "批量入库成功，共" + (end - start + 1) + "张卡", null);
        } catch (NumberFormatException e) {
            // 处理卡号无法转换为数字的异常
            return ResponseDTO.error(500, "卡号格式错误：必须为数字");
        } catch (Exception e) {
            // 处理其他所有异常（如数据库操作失败等）
            return ResponseDTO.error(500, "入库失败：" + e.getMessage());
        }
    }
}