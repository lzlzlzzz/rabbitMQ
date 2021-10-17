package com.my.rabbitmq.five;

import com.my.rabbitmq.common.ChannelUtils;
import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class ReceiveLogs01 {
    private static final String EXCHANGE_NAME = "logs";

    public static void main(String[] args) {
        try {
            Channel channel = ChannelUtils.getChannel();
            /**
             * 声明一个交换机
             * 1.交换机名称
             * 2.交换机类型
             */
            channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
            //创建一个临时队列
            String queue = channel.queueDeclare().getQueue();
            /**
             * 绑定交换机与队列
             * 1.队列名称
             * 2.绑定的交换机名称
             * 3.路由routingKey
             */
            channel.queueBind(queue, EXCHANGE_NAME, "");
            //接收消息
            DeliverCallback deliverCallback =  (consumerTag, delivery) -> {
                System.out.println("ReceiveLogs01接收到消息：" + new String(delivery.getBody()));
            };
            //接收消息失败的回调
            CancelCallback cancelCallback = consumerTag -> {
                System.out.println("接收消息失败了");
            };
            channel.basicConsume(queue, true, deliverCallback, cancelCallback);
            System.out.println("ReceiveLogs01等待接收消息......");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }
}
