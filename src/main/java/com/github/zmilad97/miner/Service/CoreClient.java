package com.github.zmilad97.miner.Service;

import com.github.zmilad97.miner.Module.Block;
import com.github.zmilad97.miner.Module.BlockAddressPair;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import java.net.http.HttpResponse;
import java.util.*;
import java.util.concurrent.TimeoutException;

@Service
@Slf4j
public class CoreClient {
    @Value("${app.core.addresses}")
    private List<String> coreAddresses;
    private final RequestHandler requestHandler = new RequestHandler();


    public BlockAddressPair findBlock() throws TimeoutException, InterruptedException {
        List<BlockAddressPair> blocks = requestHandler.getBlockAddressPairAsync(coreAddresses);

        return Collections.max(
                blocks, (o1, o2) ->
                        Math.max(o1.getBlock().getTransactions().size(),
                                o2.getBlock().getTransactions().size()));
    }

    //Sends block to CoreService to confirm mining
    @SneakyThrows
    public HttpResponse<String> sendBlock(Block block, String address) {
        return requestHandler.sendMinedBlock(address, block);
    }

}
