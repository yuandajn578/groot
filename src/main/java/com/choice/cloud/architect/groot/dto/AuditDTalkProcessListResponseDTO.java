package com.choice.cloud.architect.groot.dto;


import java.util.List;

import lombok.Data;

/**
 * <p>
 * 审核单对应的钉钉审批流进度响应体
 * </p>
 *
 * @author LZ
 * @date Created in 2020/6/10 14:19
 */
@Data
public class AuditDTalkProcessListResponseDTO {

    /**
     * 操作记录列表
     */
    private List<OperationRecordDTO> operationRecordDTOList;

    /**
     * 已审批任务列表（可以通过此列表获取已审批人）
     */
    private List<TaskDTO> taskDTOList;


    @Data
    public static class OperationRecordDTO {
        /**
         * attachments : null
         * date : 1591762245000
         * operationResult : NONE
         * operationType : START_PROCESS_INSTANCE
         * remark : null
         * userid : 142737334338190766
         */
        private Object attachments;
        private String date;
        private String operationResult;
        private String operationType;
        private String remark;
        private String userid;
        private String userName;
    }

    @Data
    public static class TaskDTO {
        /**
         * createTime : null
         * finishTime : null
         * taskResult : NONE
         * taskStatus : NEW
         * taskid : 64409298217
         * url : ?procInsId=c1afada6-d1b8-4757-97eb-542d5e37ff37&taskId=64409298217&businessId=202006101131000460012
         * userid : 1966204526676887
         */
        private String createTime;
        private String finishTime;
        private String taskResult;
        private String taskStatus;
        private String taskid;
        private String url;
        private String userid;
        private String userName;
        //private String remark;
    }
}
