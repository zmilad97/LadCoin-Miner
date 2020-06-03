package com.github.zmilad97.miner.Controller;

import com.github.zmilad97.miner.Module.Block;
import com.github.zmilad97.miner.Service.MinerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class MinerStarter implements ApplicationRunner {
  private static final Logger LOG = LoggerFactory.getLogger(MinerStarter.class);
  private final MinerService minerService;

  @Autowired
  public MinerStarter(MinerService minerService) {
    this.minerService = minerService;
  }

  @Override
  public void run(ApplicationArguments args) {
    LOG.debug("application just started, start to mine...");
    Block block = minerService.findBlock();
    minerService.computeHash(block);
    minerService.sendBlock(block);
  }
}
