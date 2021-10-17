package com.my.rabbitmq.eight;

import com.my.rabbitmq.common.ChannelUtils;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

//生产者
public class Producer {
    public static final String EXCHANGE_NAME = "normal_exchange";

    public static void main(String[] args) {
        try {
            Channel channel = ChannelUtils.getChannel();
            //设置消息TTL时间
            //AMQP.BasicProperties properties = new AMQP.BasicProperties().builder().expiration("10000").build();
            for (int i = 1; i <= 10; i++) {
                String message = "info " + i;
                channel.basicPublish(EXCHANGE_NAME, "zhangsan", null, message.getBytes("UTF-8"));
            }
            System.out.println("消息发送完毕");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }
}
