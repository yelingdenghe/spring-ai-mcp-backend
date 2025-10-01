package com.yeling.enums;

/**
 * @author 夜凌
 * @ClassName SSEMsgType
 * @Date 2025/10/1 09:58
 * @Version 1.0
 */
public enum SSEMsgType {

    MESSAGE("message", "单词发送的普通类型消息"),
    ADD("add", "消息追加，适用于流式的消息推送"),
    FINISH("finish", "消息完成"),
    CUSTOM_EVENT("custom_event", "单词发送的普通类型消息"),
    DONE("done", "单词发送的普通类型消息");

    public final String type;
    public final String desc;

    SSEMsgType(String type, String desc) {
        this.type = type;
        this.desc = desc;
    }

}
