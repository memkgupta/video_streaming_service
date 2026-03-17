package com.vsnt.api_gateway.config.filters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vsnt.api_gateway.config.APIResponse;
import com.vsnt.api_gateway.utils.JsonUtil;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.NettyWriteResponseFilter;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.ORIGINAL_RESPONSE_CONTENT_TYPE_ATTR;

@Component
public class ApiResponseTransformFilter extends AbstractGatewayFilterFactory<ApiResponseTransformFilter.Config> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public ApiResponseTransformFilter() {
        super(Config.class);
    }

    private APIResponse transform(Map<String, Object> object, Config config) {
        APIResponse apiResponse = new APIResponse();
        apiResponse.setData(object);
        apiResponse.setSuccess(true);
        return apiResponse;
    }

    @Override

    public GatewayFilter apply(Config config) {
        return new OrderedGatewayFilter((exchange, chain) -> {

            ServerHttpResponse originalResponse = exchange.getResponse();
            DataBufferFactory bufferFactory = originalResponse.bufferFactory();

            ServerHttpResponseDecorator decorator = new ServerHttpResponseDecorator(originalResponse) {

                @Override
                public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {

                    String path = exchange.getRequest().getPath().toString();

                    // Skip swagger
                    if (path.contains("/v3/api-docs")) {
                        return super.writeWith(body);
                    }

                    HttpHeaders headers = getHeaders();
                    MediaType contentType = headers.getContentType();

                    // Only JSON responses
                    if (contentType == null || !MediaType.APPLICATION_JSON.isCompatibleWith(contentType)) {
                        return super.writeWith(body);
                    }

                    Flux<? extends DataBuffer> flux = Flux.from(body);

                    return DataBufferUtils.join(flux).flatMap(dataBuffer -> {

                        byte[] content = new byte[dataBuffer.readableByteCount()];
                        dataBuffer.read(content);
                        DataBufferUtils.release(dataBuffer);

                        String responseBody = new String(content, StandardCharsets.UTF_8);

                        Object parsedBody;

                        try {
                            parsedBody = objectMapper.readValue(responseBody, Object.class);
                        } catch (Exception e) {
                            parsedBody = responseBody; // fallback
                        }


                        if (parsedBody instanceof Map<?, ?> map && map.containsKey("success")) {
                            DataBuffer buffer = bufferFactory.wrap(content);
                            return super.writeWith(Mono.just(buffer));
                        }

                        int status = getStatusCode() != null ? getStatusCode().value() : 200;

                        APIResponse apiResponse;

                        if (status >= 400) {

                            apiResponse = APIResponse.error(parsedBody);
                        } else {
                            // ✅ SUCCESS CASE
                            apiResponse = APIResponse.success(parsedBody);
                        }

                        try {
                            byte[] newContent = objectMapper.writeValueAsBytes(apiResponse);
                            DataBuffer buffer = bufferFactory.wrap(newContent);
                            return super.writeWith(Mono.just(buffer));
                        } catch (JsonProcessingException e) {
                            return Mono.error(e);
                        }
                    });
                }
            };

            return chain.filter(exchange.mutate().response(decorator).build());

        }, NettyWriteResponseFilter.WRITE_RESPONSE_FILTER_ORDER - 1);
    }
    public static class Config {
        // Future config options
    }
}
