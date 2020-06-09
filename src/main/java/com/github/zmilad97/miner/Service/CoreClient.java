package com.github.zmilad97.miner.Service;

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

    @Value("${app.wallet.public.id}")
    private String walletPublicId;

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

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        if(response.statusCode() == 404)
            return null;
        //Converting response to Block Object
        Gson gson = new Gson();
        Block block = gson.fromJson(response.body(), Block.class);
        LOG.debug("Reward: {}", block.getReward());

        //add reward transaction to Block transactions list
       /* Transaction rewardTransaction = new Transaction();
        rewardTransaction.setTransactionId("1");
        rewardTransaction.setSource(null);
        rewardTransaction.setDestination(walletPublicId);
        rewardTransaction.setAmount(block.getReward());
        rewardTransaction.setTransactionHash(cryptography.toHexString(cryptography.getSha(rewardTransaction.getTransactionId()+rewardTransaction.getSource()+rewardTransaction.getDestination()+rewardTransaction.getAmount())));
        block.addTransaction(rewardTransaction);*/  // TODO : FIX reward Transaction
        return block;
    }


    //Sends block to CoreService to confirm mining
    public void sendBlock(Block block) {

        try {

            CloseableHttpClient httpClient = HttpClientBuilder.create().build();
            HttpPost httpPost = new HttpPost(coreAddress + "/pow");
            Gson gson = new Gson();
//            block.setDate(null);
            StringEntity params = new StringEntity(gson.toJson(block));
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

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        if(response.statusCode() == 404)
            return null;

        Gson gson = new Gson();
        List <Block> chain = gson.fromJson(response.body(), List.class);

        return chain;

    }
}
