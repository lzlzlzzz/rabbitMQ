package com.my.rabbitmq.six;

import com.my.rabbitmq.common.ChannelUtils;
import com.rabbitmq.client.Channel;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class EmitLogDirect {
    private static final String EXCHANGE_NAME = "direct_logs";

    public static void main(String[] args) {
        try {
            Channel channel = ChannelUtils.getChannel();
            Map<String, String> map = new HashMap<>();
            map.put("info", "infoMessage");
            map.put("warning", "warningMessage");
            map.put("error", "errorMessage");

            for (Map.Entry<String, String> stringMap: map.entrySet()) {
                channel.basicPublish(EXCHANGE_NAME, stringMap.getKey(), null, stringMap.getValue().getBytes());
                System.out.println(stringMap.getValue() + "消息发送完毕");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }
}
