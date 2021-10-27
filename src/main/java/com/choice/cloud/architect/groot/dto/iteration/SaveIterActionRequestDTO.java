package com.choice.cloud.architect.groot.dto.iteration;

import java.time.LocalDateTime;

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * <p>
 * 编辑保存迭代请求体
 * </p>
 *
 * @author LZ
 * @date Created in 2020/8/19 16:53
 */
@Data
public class SaveIterActionRequestDTO {
    /**
     * 业务id
     */
    @NotBlank(message = "迭代业务主键不能为空")
    private String oid;
    /**
     * 预计完成时间
     */
    //@JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private String exceptCompleteTime;
    /**
     * 实际完成时间
     */
    //@JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private String actualCompleteTime;
    /**
     * 迭代状态(未执行unexecuted  迭代中iteration  已完成completed  已提前关闭stopped)
     */
    private String iterationStatus;
}
