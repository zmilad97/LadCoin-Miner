package com.github.zmilad97.miner.Module;

import com.github.zmilad97.miner.Module.Transaction.Transaction;

import java.util.List;

public class Block {

    private int index;
    private String date;
    private String hash;
    private String previousHash;
    private long nonce;
    private List<Transaction> transactions;
    private String difficultyLevel;
    private Double reward;


    public Block(int index, String date, List<Transaction> transactions) {
        this.index = index;
        this.date = date;
        this.transactions = transactions;

    }

    public Block() {
    }



//    public String computeHash(String condition) {
//
//        String hash = "null";
//        nonce = -1;
//
//        try {
//            do {
//                nonce++;
//                String stringtohash = nonce + this.index + this.date.toString() + this.previousHash + this.transactions;
//
//                Cryptography cryptography = new Cryptography();
//                hash = cryptography.toHexString(cryptography.getSha(stringtohash));
//
//                if (hash.startsWith(condition))
//                    break;
//
//            } while (true);
//
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        }
//        setNonce(nonce);
//        return hash;
//    }


    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public void setPreviousHash(String previousHash) {
        this.previousHash = previousHash;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public long getNonce() {
        return nonce;
    }

    public void setNonce(long nonce) {
        this.nonce = nonce;
    }

    public String getDifficultyLevel() {
        return difficultyLevel;
    }

    public void setDifficultyLevel(String difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }

    public Double getReward() {
        return reward;
    }

    public void setReward(Double reward) {
        this.reward = reward;
    }

    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
    }
}
