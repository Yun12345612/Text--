package org.example.springboot_hd.entity;

import lombok.Data;

@Data
public class ExaminationDetailSummary {
    private Long id;
    private Long summaryId;
    private Long detailId;
    private String checkResult;
    private String resultDescription;
    private String doctorName;
    private Long adminId;
    private String configValue;
    private String orderOn;
}