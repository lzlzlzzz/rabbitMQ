package com.my.rabbitmq.two;

import com.my.rabbitmq.common.ChannelUtils;
import com.rabbitmq.client.Channel;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

//生产者，发送大量消息
public class Task01 {
    //队列名称
    private static final String QUEUE_NAME = "hello";

    public static void main(String[] args) {
        try {
            Channel channel = ChannelUtils.getChannel();
            /**
             * 声明一个队列
             * 1.队列名称
             * 2.队列消息是否持久化
             * 3.消息是否共享
             * 4.消费后消息是否自动删除
             * 5.其他参数
             */
            channel.queueDeclare(QUEUE_NAME,false,false,false,null);
            /**
             * 发送消息,从控制台接收信息
             * 1.发送到那个交换机
             * 2.路由key
             * 3.其他参数
             * 4.消息体
             */
            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNext()){
                String message = scanner.next();
                channel.basicPublish("",QUEUE_NAME,null,message.getBytes());
                System.out.println("消息发送完成：" + message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }
}
