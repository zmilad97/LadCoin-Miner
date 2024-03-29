package com.github.zmilad97.miner.Controller;

import com.github.zmilad97.miner.Module.Block;
import com.github.zmilad97.miner.Service.CoreClient;
import com.github.zmilad97.miner.Service.MinerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MinerController {
    private final MinerService minerService;
    private final CoreClient coreClient;

    @Autowired
    public MinerController(MinerService minerService, CoreClient coreClient) {
        this.minerService = minerService;
        this.coreClient = coreClient;
    }

    @GetMapping("/mine")
    public void mine() {
        minerService.mine();
    }

    @GetMapping("/confirmation")
    public ResponseEntity<Boolean> confirmation(@RequestBody Block block) {
        return minerService.getConfirmation(block);
    }
}
