package com.yeling.service.audio;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author 夜凌
 * @Description: TODO
 * @ClassName AsrService
 * @Date 2025/10/9 18:18
 * @Version 1.0
 */
public interface AsrService {

    /**
     * @description: 将上传的音频文件识别为文字
     * @author: 夜凌
     * @date: 2025/10/10 07:53
     * @param: [audioFile, model]
     * @return: String
     **/
    String recognize(MultipartFile audioFile, String model);
    
    /**
     * @description: 将在线音频URL识别为文字
     * @author: 夜凌
     * @date: 2025/10/10 08:11
     * @param: [audioUrl, model]
     * @return: String
     **/
    String recognize(String audioUrl, String model);

}
