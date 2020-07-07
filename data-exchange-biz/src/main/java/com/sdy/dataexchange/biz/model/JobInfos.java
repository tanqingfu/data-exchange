package com.sdy.dataexchange.biz.model;

import lombok.Data;
/**
 * <p>
 *所有作业信息
 * </p>
 *
 * @author 高连明
 * @since 2019-07-22
 */

@Data
public class JobInfos {
    private Integer jobId;

    private String jobName;

    private String jobDesc;

    private String validFlag;
    private String ip;

    private String jobState;
    private Integer jobStatus;

}
