package com.github.zmilad97.miner.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.zmilad97.miner.Module.Block;
import com.google.gson.Gson;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

@Service
public class CoreClient {
    private static final Logger LOG = LoggerFactory.getLogger(CoreClient.class);
    @Value("${app.core.address}")
    private String coreAddress;


    private final Cryptography cryptography;

    @Autowired
    public CoreClient(Cryptography cryptography) {
        this.cryptography=  cryptography;
    }




    public Block findBlock() {

        final HttpClient httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .build();
        String address = coreAddress +"/block";

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(address))
                .setHeader("User-Agent", "Miner")
                .build();

        HttpResponse<String> response = null;
        try {

            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if(response.statusCode() == 404)
                return null;
            LOG.debug(response.body());
        } catch (IOException|NullPointerException | InterruptedException e) {
            e.printStackTrace();
        }

        //TODO : Fix Parsing json object via ObjectMapper.readValue()
        //Converting response to Block Object
        Block block = null;
        try {
            block = new ObjectMapper().readValue(response.body(), Block.class);
            LOG.debug(block.toString());
        } catch (NullPointerException|JsonProcessingException e) {
            LOG.error(e.getLocalizedMessage());
        }
//        Block block = gson.fromJson(response.body(), Block.class);
//        LOG.debug("Reward: {}", block.getReward());

        return block;
    }


    //Sends block to CoreService to confirm mining
    public void sendBlock(Block block) {

        try {
            ObjectMapper  objectMapper = new ObjectMapper();
            CloseableHttpClient httpClient = HttpClientBuilder.create().build();
            HttpPost httpPost = new HttpPost(coreAddress + "/validMine");
            Gson gson = new Gson();
//            StringEntity params = new StringEntity(gson.toJson(block));
            StringEntity params = new StringEntity(objectMapper.writeValueAsString(block));
            LOG.debug(params.getContent().toString());
            httpPost.addHeader("Content-Type", "application/json");
            httpPost.setEntity(params);
            CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
            LOG.info(String.valueOf(httpResponse.getStatusLine()));
//            System.out.println(httpResponse.getEntity().getContent());
        } catch (IOException e) {
            LOG.error(e.getMessage(),e);
        }
    }


    public List<Block> currentChain(){

        final HttpClient httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .build();
        String address = coreAddress +"/chain";

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(address))
                .setHeader("User-Agent", "Miner")
                .build();

        HttpResponse<String> response = null;
        try {

            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if(response.statusCode() == 404)
                return null;
        } catch (NullPointerException|IOException | InterruptedException e) {
            e.printStackTrace();
        }

        Gson gson = new Gson();
        assert response != null;
        return  gson.fromJson(response.body(), List.class);

    }
}
