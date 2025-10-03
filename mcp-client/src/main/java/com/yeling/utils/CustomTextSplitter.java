package com.yeling.utils;

import org.springframework.ai.transformer.splitter.TextSplitter;

import java.util.List;

/**
 * @author 夜凌
 * @Description: TODO
 * @ClassName CustomTextSplitter
 * @Date 2025/10/3 11:41
 * @Version 1.0
 */
public class CustomTextSplitter extends TextSplitter {
    @Override
    protected List<String> splitText(String text) {
        return List.of(split(text));
    }

    public String[] split(String text) {
        return text.split("\\s*\\R\\s*\\R\\s*");
    }


}
