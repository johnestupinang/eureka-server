package com.john.springcloud.gateway.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
//import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Component
public class ExampleGlobalFilter implements GlobalFilter, Ordered {

    private final Logger logger = LoggerFactory.getLogger(ExampleGlobalFilter.class);

    /*
    * exchange = contiene el request y response
    * chain = contiene el listado de rutas
    * */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        logger.info("----------------------------- Ejecutando el filtro PRE Request  -----------------------------");

        //Crear un request mutado
        ServerHttpRequest mutatedRequest = exchange.getRequest().mutate().headers(headers -> headers.add("token","ABCDF12345")).build();

        //Crear un nuevo exchange con el request mutado
        ServerWebExchange mutatedExchange = exchange.mutate().request(mutatedRequest).build();


        //Ejecución de la cadena de filtros
        return chain.filter(mutatedExchange).then(Mono.fromRunnable(() -> {
            logger.info("----------------------------- Ejecutando el filtro POS Response -----------------------------");
            //Programación normal
            String token = mutatedExchange.getRequest().getHeaders().getFirst("token");
            if(token != null){
                logger.info("token: "+token);
            }

            //Programación funcional
            Optional.ofNullable(mutatedExchange.getRequest().getHeaders().getFirst("token")).ifPresent(valueToken -> {
                logger.info("valueToken: "+valueToken);
                mutatedExchange.getResponse().getHeaders().add("token", valueToken);
            });

            mutatedExchange.getResponse().getCookies().add("Color", ResponseCookie.from("Color","Red").build());
            //mutatedExchange.getResponse().getHeaders().setContentType(MediaType.TEXT_PLAIN);

        }));

    }

    @Override
    public int getOrder() {
        return 100;
    }
}
