package com.github.zmilad97.miner.Module;

import com.github.zmilad97.miner.Module.Transaction.Transaction;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
public class Block {

    @Getter
    @Setter
    private int index;
    @Getter
    @Setter
    private String date;
    @Getter
    @Setter
    private String hash;
    @Getter
    @Setter
    private String previousHash;
    @Getter
    @Setter
    private long nonce;
    @Getter
    @Setter
    private List<Transaction> transactions;
    @Getter
    @Setter
    private String difficultyLevel;
    @Getter
    @Setter
    private Double reward;


    public Block(int index, String date, List<Transaction> transactions) {
        this.index = index;
        this.date = date;
        this.transactions = transactions;
    }

    public void addTransaction(Transaction transaction) {
        this.transactions.add(transaction);
    }
}
