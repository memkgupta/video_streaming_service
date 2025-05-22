package com.vsnt.channel_service.entities;

import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
public class Links {

    private String title;
    private String url;
    public Links(Links old)
    {
        this.title = old.title;
        this.url = old.url;
    }
}
