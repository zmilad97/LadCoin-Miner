package com.github.zmilad97.miner.requestHandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.zmilad97.miner.Module.Block;
import com.github.zmilad97.miner.Module.BlockAddressPair;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;


@Slf4j
public class GetRequest extends BlockRequest {


    @SneakyThrows
    @Override
    public List<BlockAddressPair> performAsync(List<String> addresses) {
        if (addresses == null)
            throw new NullPointerException();

        List<BlockAddressPair> blocks = new ArrayList<>();
        for (String address : addresses) {
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(address + "/block")).build();
            client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).
                    thenAccept(response -> {
                        if (response.statusCode() == 200)
                            try {
                                blocks.add(new BlockAddressPair
                                        (address, mapper.readValue(response.body(), Block.class)));
                            } catch (JsonProcessingException e) {
                                log.error(e.getMessage());
                            }
                    });
        }
        for (int i = 0; i < 10; i++) {
            if (blocks.size() == 0)
                Thread.sleep(1000);
            else
                return blocks;
        }
        throw new TimeoutException();
    }

    @Override
    public Object perform(String address) {
        return null;
    }

}
