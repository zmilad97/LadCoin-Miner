package com.github.zmilad97.miner.requestHandler;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.net.http.HttpClient;
import java.util.List;

public abstract class BlockRequest {

    protected final ObjectMapper mapper = new ObjectMapper();
    protected final HttpClient client = HttpClient.newHttpClient();


    public abstract Object performAsync(List<String> address);

    public abstract Object perform(String address);


}
