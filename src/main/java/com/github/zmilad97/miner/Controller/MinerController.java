package com.github.zmilad97.miner.Controller;

import com.github.zmilad97.miner.Service.MinerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;


//TODO : this class better to be deleted
@RestController
public class MinerController {
    private final static Logger LOG = LoggerFactory.getLogger(MinerController.class);

    private final MinerService minerService;

    @Autowired
    public MinerController(MinerService minerService) {
        this.minerService = minerService;
    }


}
