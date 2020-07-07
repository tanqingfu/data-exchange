package com.sdy.dataexchange.biz.model;

import lombok.Data;

import java.util.List;
/**
 * <p>
 *所有作业信息
 * </p>
 *
 * @author 高连明
 * @since 2019-07-22
 */
@Data
public class TaskNames {
    private String jobName;
    private String jobDesc;
    private String jobState;
    private String ip;
    private List<String> taskName;


}
