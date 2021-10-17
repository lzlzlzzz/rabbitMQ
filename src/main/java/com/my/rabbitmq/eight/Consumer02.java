package com.my.rabbitmq.eight;

import com.my.rabbitmq.common.ChannelUtils;
import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Consumer02 {
    public static final String DEAD_QUEUE = "dead_queue";

    public static void main(String[] args) {
        try {
            Channel channel = ChannelUtils.getChannel();
            System.out.println("等待接收消息");
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(),"UTF-8");
                System.out.println("C2接收到消息：" + message);
            };
            CancelCallback cancelCallback = consumerTag -> {
                System.out.println("消费消息失败了");
            };
            channel.basicConsume(DEAD_QUEUE, true, deliverCallback, cancelCallback);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }
}
