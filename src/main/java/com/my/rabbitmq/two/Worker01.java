package com.my.rabbitmq.two;

import com.my.rabbitmq.common.ChannelUtils;
import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

//这是一个工作线程（相当于一个消费者）
public class Worker01 {
    private static final String QUEUE_NAME = "hello";

    public static void main(String[] args) {
        try {
            Channel channel = ChannelUtils.getChannel();
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                System.out.println("消息消费成功：" + new String(delivery.getBody()));
            };
            CancelCallback cancelCallback = consumerTag -> {
                System.out.println("消息消费失败了");
            };
            System.out.println("消费者c2开始消费消息");
            channel.basicConsume(QUEUE_NAME,true,deliverCallback,cancelCallback);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }
}
