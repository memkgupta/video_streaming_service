package com.vsnt.services;

import com.google.gson.Gson;
import com.vsnt.dtos.UpdateRequestDTO;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

public class APIService {
    private String url;
    public APIService(String url) {
        this.url = url;
    }
    public void sendUpdateRequest(UpdateRequestDTO updateRequestDTO)
    {
        try{
            Gson gson = new Gson();
         String json =   gson.toJson(updateRequestDTO, UpdateRequestDTO.class);
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))

                    .build();
           HttpResponse<String> res = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(res.body());
        }
        catch(Exception e)
        {
            e.printStackTrace();

        }

    }
}
