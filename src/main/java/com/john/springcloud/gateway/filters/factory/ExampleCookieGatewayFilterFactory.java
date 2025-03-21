package com.john.springcloud.gateway.filters.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;

import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
public class ExampleCookieGatewayFilterFactory extends AbstractGatewayFilterFactory<ExampleCookieGatewayFilterFactory.ConfigurationCookie> {

    private final Logger logger = LoggerFactory.getLogger(ExampleCookieGatewayFilterFactory.class);

    public ExampleCookieGatewayFilterFactory() {
        super(ConfigurationCookie.class);
    }

    @Override
    public GatewayFilter apply(ConfigurationCookie config) {
        return new OrderedGatewayFilter((exchange, chain) -> {
            //PRE
            logger.info("Ejecutando pre gateway filter factory: "+ config.message);

            //POS
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                Optional.ofNullable(config.value).ifPresent(cookie -> {
                    exchange.getResponse().addCookie(ResponseCookie.from(config.name, cookie).build());
                });
                logger.info("Ejecutando pos gateway filter factory: "+ config.message);
            }));
        }, 100);
    }

    @Override
    public List<String> shortcutFieldOrder() {
        return Arrays.asList("message, name, value");
    }

    @Override
    public String name() {
        return "EjemploCookie";
    }

    public static class ConfigurationCookie{

        private String name;
        private String value;
        private String message;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }


    }
}
