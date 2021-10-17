package com.my.rabbitmq.three;

import com.my.rabbitmq.common.ChannelUtils;
import com.rabbitmq.client.Channel;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

public class Task02 {
    private static final String ACK_QUEUE = "ack_queue";

    public static void main(String[] args) {
        try {
            Channel channel = ChannelUtils.getChannel();
            //开启发布确认模式
            channel.confirmSelect();
            /**
             * 声明一个队列
             * 1.队列名称
             * 2.是否持久化
             * 3.消息是否私有化，true只能一个消费者进行消费
             * 4.队列是否自动删除
             * 5.其他参数
             */
            channel.queueDeclare(ACK_QUEUE,false,false,false,null);
            /**
             * 发送消息
             * 1.发送到哪个交换机
             * 2.路由key
             * 3.其他参数信息
             * 4.消息体
             */
            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNext()){
                String message = scanner.next();
                channel.basicPublish("",ACK_QUEUE,null,message.getBytes());
                System.out.println("消息发送完成：" + message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }
}
