package com.pubsub.demo.sender;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.function.Supplier;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

@Component
public class PubSubSender {

    private static final Logger LOGGER = LoggerFactory.getLogger(PubSubSender.class);
    private static final Random rand = new Random(2020);

    /**
     * [START pubsub_spring_cloud_stream_output_binder]
     * Create an output binder to send messages to `topic-one` using a Supplier bean.
     * */
    @Bean
    public Supplier<Flux<Message<String>>> sendMessageToTopicOne() {
        return () ->
                Flux.<Message<String>>generate(
                                sink -> {
                                    try {
                                        Thread.sleep(10000);
                                    } catch (InterruptedException e) {
                                        // Stop sleep earlier.
                                    }

                                    Message<String> message =
                                            MessageBuilder.withPayload("message-" + rand.nextInt(1000)).build();
                                    LOGGER.info(
                                            "Sending a message via the output binder to topic-one! Payload: "
                                                    + message.getPayload());
                                    sink.next(message);
                                })
                        .subscribeOn(Schedulers.boundedElastic());
    }
}
