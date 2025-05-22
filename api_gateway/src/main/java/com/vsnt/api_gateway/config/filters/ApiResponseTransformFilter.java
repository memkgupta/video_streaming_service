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
import org.springframework.cloud.gateway.filter.factory.rewrite.MessageBodyDecoder;
import org.springframework.cloud.gateway.filter.factory.rewrite.MessageBodyEncoder;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.server.HandlerStrategies;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.function.UnaryOperator.identity;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.ORIGINAL_RESPONSE_CONTENT_TYPE_ATTR;
@Component
public class ApiResponseTransformFilter extends AbstractGatewayFilterFactory<ApiResponseTransformFilter.Config> {
    private final Map<String,MessageBodyDecoder> decoders;
    private final Map<String,MessageBodyEncoder> encoders;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private APIResponse transform(Map<String,Object> object,Config config) {
        APIResponse apiResponse = new APIResponse();
        apiResponse.setData(object);
apiResponse.setSuccess(true);
        return apiResponse;
    };
    private APIResponse errorTransform(Map<String,Object> object,Config config) {
        APIResponse apiResponse = new APIResponse();
        apiResponse.setSuccess(false);
        apiResponse.setMessage(object.get("message").toString());
        if(object.get("errors") != null) {
            apiResponse.setData((Map<String, Object>) object.get("errors"));
        }
        return apiResponse;
    }
    public ApiResponseTransformFilter(Set<MessageBodyDecoder> decoders,Set<MessageBodyEncoder> encoders) {
        super(Config.class);
    this.decoders = decoders.stream().collect(Collectors.toMap(MessageBodyDecoder::encodingType, identity()));
    this.encoders = encoders.stream().collect(Collectors.toMap(MessageBodyEncoder::encodingType, identity()));
    }


    @Override
    public GatewayFilter apply(Config config) {
        return new OrderedGatewayFilter((exchange, chain) -> {
            ServerHttpResponse originalResponse = exchange.getResponse();
            DataBufferFactory bufferFactory = originalResponse.bufferFactory();
            ServerHttpResponseDecorator decorator = new ServerHttpResponseDecorator(originalResponse) {

                @Override
                public Mono<Void> writeWith(Publisher<? extends DataBuffer> body)
                {

                    String originalResponseContentType = exchange.getAttribute(ORIGINAL_RESPONSE_CONTENT_TYPE_ATTR);
                    HttpHeaders httpHeaders = new HttpHeaders();

                    httpHeaders.add(HttpHeaders.CONTENT_TYPE, originalResponseContentType);

                    Flux<? extends DataBuffer> fluxBody = Flux.from(body);

                    return DataBufferUtils.join(fluxBody)
                            .publishOn(Schedulers.boundedElastic())
                            .flatMap(dataBuffer -> {
                                byte[] content = new byte[dataBuffer.readableByteCount()];
                                dataBuffer.read(content);
                                DataBufferUtils.release(dataBuffer);

                                String responseBody = new String(content, StandardCharsets.UTF_8);

                                Map<String, Object> mapBody = JsonUtil.toMap(responseBody);

                                APIResponse apiResponse =null;
                                if(originalResponse.getStatusCode().isError()){
                                    apiResponse = errorTransform(mapBody,config);
                                }
                                else{
                                    apiResponse= transform(mapBody, config);
                                }

                                try {
                                    byte[] newContent = objectMapper.writeValueAsBytes(apiResponse);
                                    DataBuffer buffer = originalResponse.bufferFactory().wrap(newContent);
                                    return super.writeWith(Mono.just(buffer));
                                } catch (JsonProcessingException e) {
                                    e.printStackTrace();
                                    return super.writeWith(Flux.error(e));
                                }
                            });
                }



            };


            return chain.filter(exchange.mutate().response(decorator).build());
        }, NettyWriteResponseFilter.WRITE_RESPONSE_FILTER_ORDER - 1);
    }

    public static class Config {}
}
