package com.github.zmilad97.miner.Controller;

import com.github.zmilad97.miner.Module.Block;
import com.github.zmilad97.miner.Module.Config;
import com.github.zmilad97.miner.Service.MinerService;
import com.google.gson.Gson;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@RestController
public class MinerController {

    MinerService minerService;
    Config config;

    @Autowired
    public MinerController(MinerService minerService, Config config) {
        this.minerService = minerService;
        this.config = config;

    }

   /* @RequestMapping(value = "/config", method = RequestMethod.POST)
    public void configMiner(@RequestBody Config config) {
        this.config = config;
        this.config.coreConfig(this.config);
    }*/

    //TODO :MAKE CURRENT CONFIG & TEST CONNECTION
    @RequestMapping("/mine")
    public Block mine() {
        Block block = minerService.findBlock();
        minerService.computeHash(block);
        minerService.sendBlock(block);
        return block;
    }

    @RequestMapping(value = "/minerTest")
    public void minerTest() {
        try {

            CloseableHttpClient httpClient = HttpClientBuilder.create().build();
            HttpPost httpPost = new HttpPost("http://localhost:8181/test");
            StringEntity params = new StringEntity("{\"transactionId\":\"1\"}");

            httpPost.addHeader("Content-Type", "application/json");
            httpPost.setEntity(params);

            CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
            System.out.println(httpResponse.getEntity().getContent());

        } catch (IOException e) {
            e.printStackTrace();
            e.printStackTrace();
        }

    }

    @RequestMapping("/test")
    public Block testBlock() {

        HttpClient httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:8181/trx"))
                .setHeader("User-Agent", "Miner")
                .build();
        HttpResponse<String> response = null;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println(response.body());

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Gson gson = new Gson();

        Block[] block = gson.fromJson(response.body(), Block[].class);

        System.out.println(response.body());
        System.out.println(block[1]);
        System.out.println(block[1].getIndex());
        System.out.println(block[1].getPreviousHash());
        System.out.println(block[1].getTransactions().get(1).getAmount());

        return null;
    }

    @RequestMapping(value = "/testMine")
    public Object miner() {

        Block block = new Block(minerService.getBlocks().size(), new java.util.Date(), minerService.getTransactionList());
        minerService.computeHash(block);

        return null;
    }


}
