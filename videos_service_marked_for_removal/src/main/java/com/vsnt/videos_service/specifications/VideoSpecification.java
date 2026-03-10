package com.vsnt.videos_service.specifications;

import com.vsnt.videos_service.entities.Video;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Configuration
public class VideoSpecification {
    @Component("title")
    public class TitleSpecification implements SpecificationStrategy<Video>{

        @Override
        public Specification<Video> getSpecification(String value) {
            return (root,query,cb)->
                    cb.like(root.get("title"), "%"+value+"%");
        }

    }
    @Component("channelId")
    public class ChannelSpecification implements SpecificationStrategy<Video>{

        @Override
        public Specification<Video> getSpecification(String value) {
            return (root,query,cb)->cb.equal(root.get("channelId"), value);
        }

    }
    @Component("isChannelRequest")
    public class IsChannelRequestSpecification implements SpecificationStrategy<Video>{
        @Override
        public Specification<Video> getSpecification(String value) {
            return (root,query,cb)->{
                return cb.equal(root.get("visibilityStatus"), Boolean.parseBoolean(value));
            };
        }
    }

}
