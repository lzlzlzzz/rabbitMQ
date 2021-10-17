package com.my.rabbitmq.five;

import com.my.rabbitmq.common.ChannelUtils;
import com.rabbitmq.client.Channel;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

public class EmitLog {
    private static final String EXCHANGE_NAME = "logs";

    public static void main(String[] args) {
        try {
            Channel channel = ChannelUtils.getChannel();

            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNext()){
                String message = scanner.next();
                channel.basicPublish(EXCHANGE_NAME, "", null, message.getBytes());
                System.out.println("消息发送完成");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }
}
