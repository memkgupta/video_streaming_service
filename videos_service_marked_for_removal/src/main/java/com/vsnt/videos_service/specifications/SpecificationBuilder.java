package com.vsnt.videos_service.specifications;

import com.vsnt.videos_service.entities.Video;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.Map;
@Component
public class SpecificationBuilder {
    private final SpecificationStrategyFactory specificationStrategyFactory;
    public SpecificationBuilder(SpecificationStrategyFactory specificationStrategyFactory) {
        this.specificationStrategyFactory = specificationStrategyFactory;
    }
    public Specification<Video> build(Map<String,String> params)
    {
        Specification<Video> specification = null;
        for(Map.Entry<String,String> entry : params.entrySet())
        {
            String key = entry.getKey();
            String value = entry.getValue();
            Specification<Video> specificationStrategy = specificationStrategyFactory.getSpecification(key,value);
            specification = specification == null ? specificationStrategy : specificationStrategy.and(specificationStrategy);

        }
        return specification;
    }
}
