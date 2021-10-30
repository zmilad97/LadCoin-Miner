package com.github.zmilad97.miner.Module.Transaction;


import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;

public class TransactionInput {
    @Getter
    @Setter
    private HashMap<Integer,String> previousTransactionHash;
    @Getter
    @Setter
    private int indexReferenced;
    @Getter
    @Setter
    private String pubKey;

    public TransactionInput() {
        this.previousTransactionHash = new HashMap<>();
    }

    public void addPreviousTransactionHash(int i, String s ){
        previousTransactionHash.put(i,s);
    }

}
