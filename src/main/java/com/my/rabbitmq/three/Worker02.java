package com.my.rabbitmq.three;

import com.my.rabbitmq.common.ChannelUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Worker02 {
    private static final String ACK_QUEUE = "ack_queue";

    public static void main(String[] args) {
        try {
            Channel channel = ChannelUtils.getChannel();
            /**
             * 1.队列名称
             * 2.是否自动应答
             * 3.消息处理完成后的回调函数
             * 4.消息处理失败后的回调函数
             */
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                System.out.println("消息消费成功：" + new String(delivery.getBody()));
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                System.out.println("消息成功应答");
            };
            channel.basicQos(1);
            channel.basicConsume(ACK_QUEUE, false,deliverCallback,consumer -> {
                System.out.println("消息消费失败了");
            });
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }
}
