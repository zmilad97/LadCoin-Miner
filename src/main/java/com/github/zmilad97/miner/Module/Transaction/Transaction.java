package com.github.zmilad97.miner.Module.Transaction;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
public class Transaction {
    @Getter
    @Setter
    private String transactionId;
    @Getter
    @Setter
    private TransactionInput TransactionInput;
    @Getter
    @Setter
    private TransactionOutput TransactionOutput;
    @Getter
    @Setter
    private String transactionHash;
}