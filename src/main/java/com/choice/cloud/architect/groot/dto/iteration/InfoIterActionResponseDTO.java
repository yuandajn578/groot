package com.choice.cloud.architect.groot.dto.iteration;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * <p>
 * 迭代详情响应体
 * </p>
 *
 * @author LZ
 * @date Created in 2020/8/19 16:51
 */
@Data
public class InfoIterActionResponseDTO {
    /**
     * 业务id
     */
    private String oid;
    /**
     * 项目oid
     */
    private String projectId;
    /**
     * 项目名称
     */
    private String projectName;
    /**
     * 迭代名称
     */
    private String iterationName;
    /**
     * 迭代描述
     */
    private String iterationDesc;
    /**
     * 预计完成时间
     */
    //@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String exceptCompleteTime;
    /**
     * 实际完成时间
     */
    //@JsonFormat(pattern = "YYYY-MM-dd HH:mm:ss")
    private String actualCompleteTime;
    /**
     * 迭代状态(未执行unexecuted  迭代中iteration  已完成completed  已提前关闭stopped)
     */
    private String iterationStatus;
}
