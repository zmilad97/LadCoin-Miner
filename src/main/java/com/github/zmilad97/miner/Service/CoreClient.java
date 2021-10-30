package com.github.zmilad97.miner.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.zmilad97.miner.Module.Block;

import com.github.zmilad97.miner.Module.BlockAddressPair;
import com.github.zmilad97.miner.requestHandler.GetRequest;
import com.github.zmilad97.miner.requestHandler.PostRequest;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import java.net.http.HttpResponse;
import java.util.*;

@Service
@Slf4j
public class CoreClient {
    @Value("${app.core.addresses}")
    private List<String> coreAddresses;



    @SneakyThrows
    public BlockAddressPair findBlock() {
        List<BlockAddressPair> blocks = new GetRequest().performAsync(coreAddresses);

        return Collections.max(
                blocks, (o1, o2) ->
                        Math.max(o1.getBlock().getTransactions().size(),
                                o2.getBlock().getTransactions().size()));
    }

    //Sends block to CoreService to confirm mining
    @SneakyThrows
    public HttpResponse<String> sendBlock(Block block, String address) {
        return new PostRequest(block).perform(address);
    }


/*    public List<Block> currentChain() {

        final HttpClient httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .build();
        String address = coreAddress + "/chain";

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(address))
                .setHeader("User-Agent", "Miner")
                .build();

        HttpResponse<String> response = null;
        try {

            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 404)
                return null;
        } catch (NullPointerException | IOException | InterruptedException e) {
            e.printStackTrace();
        }

        Gson gson = new Gson();
        assert response != null;
        return gson.fromJson(response.body(), List.class);

    }*/
}
