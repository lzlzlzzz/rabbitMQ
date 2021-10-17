package com.my.rabbitmq.seven;

import com.my.rabbitmq.common.ChannelUtils;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

//消费者Q2
public class ReceiveLogsTopic02 {
    public static final String QUEUE_NAME ="Q2";
    public static final String EXCHANGE_NAME = "topic_logs";

    public static void main(String[] args) {
        try {
            Channel channel = ChannelUtils.getChannel();
            channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, "*.*.rabbit");
            channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, "lazy.#");
            System.out.println("Q2等待接收消息");
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody());
                System.out.println("Q2消费成功，消息体：" + message + "，路由key：" + delivery.getEnvelope().getRoutingKey());
            };
            CancelCallback cancelCallback = consumerTag -> {
                System.out.println("消费失败了");
            };
            channel.basicConsume(QUEUE_NAME, true, deliverCallback, cancelCallback);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }
}
