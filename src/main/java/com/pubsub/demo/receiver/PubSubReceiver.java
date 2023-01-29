package com.pubsub.demo.receiver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.cloud.spring.pubsub.support.BasicAcknowledgeablePubsubMessage;
import com.google.cloud.spring.pubsub.support.GcpPubSubHeaders;

import java.util.Random;
import java.util.function.Consumer;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;


@Component
public class PubSubReceiver {

    private static Logger LOGGER = LoggerFactory.getLogger(PubSubReceiver.class);

    /**
    * Define what happens to the messages arriving in the message channel.
    * */
    @ServiceActivator(inputChannel = "inputMessageChannel")
    public void messageReceiver(
            String payload,
            @Header(GcpPubSubHeaders.ORIGINAL_MESSAGE) BasicAcknowledgeablePubsubMessage message) {
        LOGGER.info("Message arrived via an inbound channel adapter from sub-one! Payload: " + payload);
        message.ack();
    }

    /**
     * [START pubsub_spring_cloud_stream_input_binder]
     * Create an input binder to receive messages from `topic-two` using a Consumer bean.
     * */
    @Bean
    public Consumer<Message<String>> receiveMessageFromTopicTwo() {
        return message -> {
            LOGGER.info(
                    "Message arrived via an input binder from topic-two! Payload: " + message.getPayload());
        };
    }
}
