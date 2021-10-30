package com.github.zmilad97.miner.Module;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class BlockAddressPair {
    @Getter
    private final String address;
    @Getter
    private final Block block;
}
