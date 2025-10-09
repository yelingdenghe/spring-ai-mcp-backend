package com.yeling.service.audio;

import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.exception.UploadFileException;

/**
 * @author 夜凌
 * @Description: TODO
 * @ClassName TTSService
 * @Date 2025/10/9 17:04
 * @Version 1.0
 */
public interface TTSService {

    byte[] tts(String content, String model) throws ApiException, NoApiKeyException, UploadFileException;

}
