package com.my.rabbitmq.eight;

import com.my.rabbitmq.common.ChannelUtils;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

//消费者
public class Consumer01 {
    //普通队列名称
    public static final String QUEUE_NAME = "normal_queue";
    //普通交换机名称
    public static final String EXCHANGE_NAME = "normal_exchange";
    //死信队列名称
    public static final String DEAD_QUEUE = "dead_queue";
    //死信交换机名称
    public static final String DEAD_EXCHANGE = "dead_exchange";

    public static void main(String[] args) {
        try {
            Channel channel = ChannelUtils.getChannel();
            channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
            channel.exchangeDeclare(DEAD_EXCHANGE, BuiltinExchangeType.DIRECT);
            Map<String, Object> argumentsMap = new HashMap<>();
            //设置消息过期时间，单位ms。一般不在消息队列设置，生产发送消息就会带上过期时间
//            argumentsMap.put("x-message-ttl", 10000);
            //正常队列设置死信交换机
            argumentsMap.put("x-dead-letter-exchange", DEAD_EXCHANGE);
            //设置死信routingKey
            argumentsMap.put("x-dead-letter-routing-key", "lisi");
            //设置队列长度
            //argumentsMap.put("x-max-length", 6);
            channel.queueDeclare(QUEUE_NAME, false, false, false, argumentsMap);
            channel.queueDeclare(DEAD_QUEUE, false, false, false, null);
            channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, "zhangsan");
            channel.queueBind(DEAD_QUEUE, DEAD_EXCHANGE, "lisi");
            System.out.println("等待接收消息");
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody());
                if (message.equals("info 2")){
                    System.out.println("C1消费消息：" + message + "失败,consumerTag：" + consumerTag);
                    channel.basicReject(delivery.getEnvelope().getDeliveryTag(), false);
                } else {
                    System.out.println("C1消费消息：" + message + "成功");
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                }
            };
            CancelCallback cancelCallback = consumerTag -> {
                System.out.println("消息消费失败了");
            };
            channel.basicConsume(QUEUE_NAME, false, deliverCallback, cancelCallback);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }
}
