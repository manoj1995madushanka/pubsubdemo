package com.pubsub.demo.configuration;

import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.cloud.spring.pubsub.integration.AckMode;
import com.google.cloud.spring.pubsub.integration.inbound.PubSubInboundChannelAdapter;
import com.google.cloud.spring.pubsub.integration.outbound.PubSubMessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

@Configuration
public class PubSubConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(PubSubConfig.class);

    /**
     * Create a message channel for messages arriving from the subscription `sub-one`.
     * */
    @Bean
    public MessageChannel inputMessageChannel() {
        return new PublishSubscribeChannel();
    }

    /**
     * Create an inbound channel adapter to listen to the subscription `sub-one`
     * and send messages to the input message channel.
     *
     * The inboundChannelAdapter asynchronously
     * pulls messages from sub-one using a PubSubTemplate and sends the messages to inputMessageChannel.
     *
     * The inboundChannelAdapter sets the acknowledgement mode to MANUAL so the
     * application can acknowledge messages after it processes them.
     * The default acknowledgment mode of PubSubInboundChannelAdapter types is AUTO.
     * */
    @Bean
    public PubSubInboundChannelAdapter inboundChannelAdapter(
            @Qualifier("inputMessageChannel") MessageChannel messageChannel,
            PubSubTemplate pubSubTemplate) {
        PubSubInboundChannelAdapter adapter =
                new PubSubInboundChannelAdapter(pubSubTemplate, "sub-one");
        adapter.setOutputChannel(messageChannel);
        adapter.setAckMode(AckMode.MANUAL);
        adapter.setPayloadType(String.class);
        return adapter;
    }

    /**
     * [START pubsub_spring_outbound_channel_adapter]
     * Create an outbound channel adapter to send messages from the input message channel to the
     * topic `topic-two`.
     * */
    @Bean
    @ServiceActivator(inputChannel = "inputMessageChannel")
    public MessageHandler messageSender(PubSubTemplate pubsubTemplate) {

        PubSubMessageHandler adapter = new PubSubMessageHandler(pubsubTemplate, "topic-two");

        adapter.setSuccessCallback(
                ((ackId, message) ->
                        LOGGER.info("Message was sent via the outbound channel adapter to topic-two!")));

        adapter.setFailureCallback(
                (cause, message) -> LOGGER.info("Error sending " + message + " due to " + cause));

        return adapter;
    }
}
