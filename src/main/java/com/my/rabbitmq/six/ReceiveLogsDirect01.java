package com.my.rabbitmq.six;

import com.my.rabbitmq.common.ChannelUtils;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class ReceiveLogsDirect01 {
    private static final String EXCHANGE_NAME = "direct_logs";
    private static final String QUEUE_NAME = "console";

    public static void main(String[] args) {
        try {
            Channel channel = ChannelUtils.getChannel();
            //声明交换机和交换机类型
            channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
            //声明队列
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            //绑定交换机
            channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, "info");
            channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, "warning");
            DeliverCallback deliverCallback =  (consumerTag, delivery) -> {
                System.out.println("ReceiveLogsDirect01接收到消息：" + new String(delivery.getBody()));
            };
            //接收消息失败的回调
            CancelCallback cancelCallback = consumerTag -> {
                System.out.println("接收消息失败了");
            };
            channel.basicConsume(QUEUE_NAME, true, deliverCallback, cancelCallback);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }
}
