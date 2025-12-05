package org.example.springboot_hd.entity;

import lombok.Data;

@Data
public class User {

    private Long userId;
    private String userName;
    private String userCard;
    private Long userAge;
    private String userGender;
    private String userAccount;
    private String userPassword;
    private String userPhone;
    private Long userStatus;
    private Double userBalance;
    private String userIsDelete;
    private java.sql.Timestamp createTime;
    private Integer pageNum;
    private Integer pageSize;
    private Integer start;
    private Integer medicalExaminationNumber;
    private String userEmail;
    private String examinationNumber;//体检编号


}
