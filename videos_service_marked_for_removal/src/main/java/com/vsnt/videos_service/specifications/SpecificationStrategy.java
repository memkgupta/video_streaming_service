package com.vsnt.videos_service.specifications;

import org.springframework.data.jpa.domain.Specification;

@FunctionalInterface
public interface SpecificationStrategy<T> {
    Specification<T> getSpecification(String value);
}
