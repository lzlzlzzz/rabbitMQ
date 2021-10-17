package com.my.rabbitmq.one;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Consumer {
    private static final String QUEUE_NAME = "hello";

    public static void main(String[] args) {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");

        try {
            Connection connection = connectionFactory.newConnection();
            Channel channel = connection.createChannel();
            System.out.println("等待接收消息");
            //接收消息
            DeliverCallback deliverCallback =  (consumerTag, delivery) -> {
                System.out.println(new String(delivery.getBody()));
            };

            //接收消息失败的回调
            CancelCallback cancelCallback = consumerTag -> {
                System.out.println("接收消息失败了");
            };

            /**
             * 消费者消费消息
             * 1.消费的队列
             * 2.消费成功之后是否自动应答
             * 3.接收消息
             * 4.消息消费失败之后的回调
             */
            channel.basicConsume(QUEUE_NAME, true, deliverCallback, cancelCallback);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }
}
