package com.github.zmilad97.miner.Controller;

import com.github.zmilad97.miner.Module.Block;
import com.github.zmilad97.miner.Service.BlockService;
import com.github.zmilad97.miner.Service.CoreClient;
import com.github.zmilad97.miner.Service.MinerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class MinerStarter implements ApplicationRunner {
    private static final Logger LOG = LoggerFactory.getLogger(MinerStarter.class);
    private final MinerService minerService;
    private final CoreClient coreClient;
    private final BlockService blockService;
    @Value("${app.sleep-between-calls}")
    private int sleepBetweenCalls;


    @Autowired
    public MinerStarter(MinerService minerService, CoreClient coreClient, BlockService blockService) {
        this.minerService = minerService;
        this.coreClient = coreClient;
        this.blockService = blockService;
    }

    @Override
    public void run(ApplicationArguments args) {
        LOG.debug("application just started, start to mine...");

        while (!(Thread.currentThread().isInterrupted())) {
            Block block = coreClient.findBlock();
            if (block != null) {
                LOG.debug(String.valueOf(block));
                minerService.setCurrentChain();
//                minerService.currentTransactions(block);
                minerService.computeHash(block);
                coreClient.sendBlock(block);

            } else {
                LOG.debug("Waiting for next call "+ sleepBetweenCalls+" sec");
            }
            try {
                Thread.sleep(TimeUnit.SECONDS.toMillis(sleepBetweenCalls));
            } catch (InterruptedException e) {
                break;
            }
        }
    }


}
