package com.github.zmilad97.miner.Module.Transaction;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
public class TransactionOutput {
    @Getter
    @Setter
    private double amount;
    @Getter
    @Setter
    private String signature;
}
