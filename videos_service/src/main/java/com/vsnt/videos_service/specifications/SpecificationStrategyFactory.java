package com.vsnt.videos_service.specifications;

import com.vsnt.videos_service.entities.Video;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
@Component
public class SpecificationStrategyFactory {
    private final Map<String, SpecificationStrategy<Video>> strategies;
    public SpecificationStrategyFactory(List<SpecificationStrategy<Video>> strategyList) {
        this.strategies = strategyList.stream().collect(Collectors.toMap(
                strategy -> strategy.getClass().getAnnotation(Component.class).value(),
                Function.identity()
        ));
    }
    public Specification<Video> getSpecification(String key, String value) {
        SpecificationStrategy<Video> strategy = strategies.get(key);
        return (strategy != null) ? strategy.getSpecification(value) : null;
    }
}
