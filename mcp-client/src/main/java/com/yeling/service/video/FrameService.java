package com.yeling.service.video;

import com.yeling.entity.FrameRequest;
import com.yeling.entity.TaskStatusResponse;

/**
 * @author 夜凌
 * @Description: TODO
 * @ClassName FrameService
 * @Date 2025/10/13 09:13
 * @Version 1.0
 */
public interface FrameService {
    
    /**
     * @description: 提交异步视频生成任务，并立即返回任务ID
     * @author: 夜凌
     * @date: 2025/10/13 17:13
     * @param: [frameRequest]
     * @return: String
     **/
    String asyncCall(FrameRequest frameRequest);

    /**
     * @description: 根据任务ID获取任务状态和结果
     * @author: 夜凌
     * @date: 2025/10/13 17:13
     * @param: [taskId]
     * @return: TaskStatusResponse
     **/
    TaskStatusResponse getTaskResult(String taskId);
}
