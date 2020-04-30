package com.github.zmilad97.miner.Module;


public class Transaction {

    private String transactionId;
    private String source;
    private String destination;
    private double amount;

    private String transactionHash;

    public Transaction(Transaction transaction) {
        this.transactionId = transaction.transactionId;
        this.source = transaction.source;
        this.destination = transaction.destination;
        this.amount = transaction.amount;
        this.transactionHash =transaction.getTransactionHash();
    }

    public Transaction() {

    }

    public String getTransactionHash() {
        return transactionHash;
    }

    public void setTransactionHash(String transactionHash) {
        this.transactionHash = transactionHash;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}