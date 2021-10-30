package com.github.zmilad97.miner.requestHandler;

import com.github.zmilad97.miner.Module.Block;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class PostRequest extends BlockRequest {
    private final String body;

    @SneakyThrows
    public PostRequest(Block body) {
        this.body = this.mapper.writeValueAsString(body);
    }

    @Override
    public Object performAsync(List<String> address) {
        return null;
    }

    @Override
    @SneakyThrows
    public HttpResponse<String> perform(String address) {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(address + "/validMine"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers
                        .ofString(body)).build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
