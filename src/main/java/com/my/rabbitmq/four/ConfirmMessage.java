package com.my.rabbitmq.four;

import com.my.rabbitmq.common.ChannelUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmCallback;

import java.io.IOException;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.TimeoutException;

public class ConfirmMessage {
    private static final int MESSAGE_COUNT = 1000;
    private static final String QUEUE_NAME = "testConfirm";

    public static void main(String[] args) {
        //单个确认发布总耗时：299ms
        //ConfirmMessage.oneConfirm();
        //批量确认发布总耗时：102ms
        //ConfirmMessage.moreConfirm();
        //异步确认发布总耗时：87ms
        ConfirmMessage.asynchronousConfirm();

    }

    //单个确认发布
    public static void oneConfirm(){
        try {
            Channel channel = ChannelUtils.getChannel();
            //开启发布确认
            channel.confirmSelect();
            channel.queueDeclare(QUEUE_NAME,false,false,false,null);
            long begin = System.currentTimeMillis();
            for (int i = 1; i <= MESSAGE_COUNT; i++) {
                String message = "message" + i;
                channel.basicPublish("",QUEUE_NAME,null,message.getBytes());
                //发布确认
                boolean b = channel.waitForConfirms();
                if (b){
                    System.out.println("消息" + message + "发布成功");
                }
            }
            long end = System.currentTimeMillis();
            System.out.println("单个确认发布总耗时为：" + (end - begin) + "ms");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //批量确认发布
    public static void moreConfirm(){
        try {
            //定义发布确认的频率
            int sum = 100;
            Channel channel = ChannelUtils.getChannel();
            channel.confirmSelect();
            channel.queueDeclare(QUEUE_NAME,false,false,false,null);
            long begin = System.currentTimeMillis();
            for (int i = 1; i <= MESSAGE_COUNT; i++) {
                String message = "message" + i;
                channel.basicPublish("",QUEUE_NAME,null,message.getBytes());
                if (i%sum == 0){
                    //发布确认
                    channel.waitForConfirms();
                }
            }
            long end = System.currentTimeMillis();
            System.out.println("批量确认发布总耗时为：" + (end - begin) + "ms");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //异步确认发布
    public static void asynchronousConfirm(){
        try {
            Channel channel = ChannelUtils.getChannel();
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            //开启发布确认
            channel.confirmSelect();

            /**
             * 线程安全有序的一个哈希表，使用于高并发的情况下
             * 1.轻松将序号与消息进行关联
             * 2.轻松删除条目，只要给到序号
             * 3.支持高并发
             */
            ConcurrentSkipListMap<Long, String> messageMap = new ConcurrentSkipListMap<>();

            /**
             * 发布成功回调函数
             * deliveryTag：消息的标记
             * multiple：是否批量
             */
            ConfirmCallback confirmCallback = (deliveryTag, multiple) -> {
                System.out.println("消息：" + deliveryTag + "已确认发布成功");
                //已确认发布成功的消息从队列移除，剩下的就是未确认的消息
                if (multiple){
                    ConcurrentNavigableMap<Long, String> navigableMap = messageMap.headMap(deliveryTag);
                    navigableMap.clear();
                } else {
                    messageMap.remove(deliveryTag);
                }
            };
            ConfirmCallback nackCallback = (deliveryTag, multiple) -> {
                //打印未确认的消息
                String message = messageMap.get(deliveryTag);
                System.out.println("消息序号为：" + deliveryTag + "，消息为：" + message + "，未确认发布成功");
            };
            /**
             * 异步发布确认
             * confirmCallback：发布成功回调
             * nackCallback：发布失败回调
             */
            channel.addConfirmListener(confirmCallback, nackCallback);
            long begin = System.currentTimeMillis();
            for (int i = 1; i <= MESSAGE_COUNT ; i++) {
                String message = "message" + i;
                channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
                //发布消息时，将所有的消息都统计起来
                messageMap.put(channel.getNextPublishSeqNo(), message);
            }
            long end = System.currentTimeMillis();
            System.out.println("异步确认发布总耗时为：" + (end-begin) + "ms");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }
}
