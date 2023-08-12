package org.cst8277.twitter.services;

import java.nio.charset.Charset;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

@Service
public class UMSConnector {

    @Value("${ums.host}")
    private String uriUmsHost;

    @Value("${ums.port}")
    private String uriUmsPort;

    public Mono<Object> retrieveUmsData(String uri) {
        WebClient client = WebClient.builder().baseUrl(uriUmsHost + ":" + uriUmsPort)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).build();

        // verify user
        Mono<Object> authCheck = client.method(HttpMethod.GET).uri("/user").accept(MediaType.APPLICATION_JSON)
        .acceptCharset(Charset.forName("UTF-8")).retrieve().bodyToMono(Object.class);
        // if authCheck size is 1, then user is not authenticated
        // if authCheck size is more than 1, then user is authenticated
        if (authCheck.block().toString().length() <= 1) {
            return Mono.just("User is not authenticated");
        }
        
        Mono<Object> response = client.method(HttpMethod.GET).uri(uri).accept(MediaType.APPLICATION_JSON)
                .acceptCharset(Charset.forName("UTF-8")).retrieve().bodyToMono(Object.class);

        return response;
    }
}
