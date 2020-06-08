package com.github.zmilad97.miner.Controller;

import com.github.zmilad97.miner.Module.Block;
import com.github.zmilad97.miner.Service.CoreClient;
import com.github.zmilad97.miner.Service.Cryptography;
import com.github.zmilad97.miner.Service.MinerService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.Date;

class MinerStarterTest {

    @Test
    void testComputeHash() {
        Block block = new Block(1, new Date().toString(), Collections.emptyList());
        CoreClient client = Mockito.mock(CoreClient.class);
        Mockito.when(client.findBlock()).thenReturn(block);
        MinerService minerService = new MinerService(new Cryptography());
        ;
        block.setDifficultyLevel("ab");
        minerService.computeHash(block);
        Assertions.assertNotNull(block.getHash());

    }
}