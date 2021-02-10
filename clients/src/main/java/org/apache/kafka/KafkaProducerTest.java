package org.apache.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

public class KafkaProducerTest {

    private static String topicName;
    private static int msgNum;
    private static int key;
    public static void main(String[] args) {

        Properties props = new Properties();
        props.put("bootstrap.servers", "127.0.0.1:9092");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        topicName = "test";
        msgNum = 500; // 发送的消息数

        Producer<String,String> producer = new KafkaProducer<>(props);
        for (int i = 0; i < msgNum; i++) {
            String msg = i + " This is matt's blog.";

            //发送消息
            producer.send(new ProducerRecord<String, String>(topicName, msg));
        }

        producer.close();

    }

}
