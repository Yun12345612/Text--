package com.cykj.pojo;

import lombok.Data;

@Data
public class User {

    private Long userId;
    private String userName;
    private String userCard;
    private Integer userAge = 0;
    private String userGender;
    private String userPhone;
    private String userAccount;
    private String userPassword;
    private java.sql.Timestamp createTime;
    private Long userStatus;
    private Double userBalance;
    private String userIsDelete;
    private Long medicalExaminationNumber;
    private String userEmail;
    private String salt;
    private String code;

}
