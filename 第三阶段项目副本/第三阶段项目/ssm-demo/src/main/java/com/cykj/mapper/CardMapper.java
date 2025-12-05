package com.cykj.mapper;

import com.cykj.pojo.Card;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CardMapper {
    // 批量插入卡片数据
    int batchInsertCards(@Param("cards") List<Card> cards);
}
