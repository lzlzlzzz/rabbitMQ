package com.my.rabbitmq.seven;

import com.my.rabbitmq.common.ChannelUtils;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * 生产者
 * quick.orange.rabbit 被队列 Q1Q2 接收到
 * lazy.orange.elephant 被队列 Q1Q2 接收到
 * quick.orange.fox 被队列 Q1 接收到
 * lazy.brown.fox 被队列 Q2 接收到
 * lazy.pink.rabbit 虽然满足两个绑定但只被队列 Q2 接收一次
 * quick.brown.fox 不匹配任何绑定不会被任何队列接收到会被丢弃
 * quick.orange.male.rabbit 是四个单词不匹配任何绑定会被丢弃
 * lazy.orange.male.rabbit 是四个单词但匹配 Q2
 */
public class EmitLogTopic {
    public static final String EXCHANGE_NAME = "topic_logs";

    public static void main(String[] args) {
        try {
            Channel channel = ChannelUtils.getChannel();
            channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);

            Map<String, String> routingKeyMap = new HashMap<>();
            routingKeyMap.put("quick.orange.rabbit", "被队列 Q1Q2 接收到");
            routingKeyMap.put("lazy.orange.elephant", "被队列 Q1Q2 接收到");
            routingKeyMap.put("quick.orange.fox", "被队列 Q1 接收到");
            routingKeyMap.put("lazy.brown.fox", "被队列 Q2 接收到");
            routingKeyMap.put("lazy.pink.rabbit", "虽然满足两个绑定但只被队列 Q2 接收一次");
            routingKeyMap.put("quick.brown.fox", "不匹配任何绑定不会被任何队列接收到会被丢弃");
            routingKeyMap.put("quick.orange.male.rabbit", "是四个单词不匹配任何绑定会被丢弃");
            routingKeyMap.put("lazy.orange.male.rabbit", "是四个单词但匹配 Q2");

            /**
             * Q1-->绑定的是
             * 中间带 orange 带 3 个单词的字符串(*.orange.*)
             * Q2-->绑定的是
             * 最后一个单词是 rabbit 的 3 个单词(*.*.rabbit)
             * 第一个单词是 lazy 的多个单词(lazy.#)
             */
            for (Map.Entry<String, String> routingKey : routingKeyMap.entrySet()) {
                channel.basicPublish(EXCHANGE_NAME, routingKey.getKey(), null, routingKey.getValue().getBytes("UTF-8"));
                System.out.println("消息发送完毕：" + routingKey.getValue());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }
}