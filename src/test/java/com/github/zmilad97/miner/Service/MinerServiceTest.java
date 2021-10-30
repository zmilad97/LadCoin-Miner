package com.github.zmilad97.miner.Service;

import com.github.zmilad97.miner.Module.Block;
import com.github.zmilad97.miner.Module.BlockAddressPair;
import com.github.zmilad97.miner.Module.Transaction.Transaction;
import com.github.zmilad97.miner.Module.Transaction.TransactionInput;
import com.github.zmilad97.miner.Module.Transaction.TransactionOutput;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@Slf4j
class MinerServiceTest {

    private final MinerService minerService = new MinerService(new Cryptography(), new CoreClient());

    @Test
    void computeHash() {
        String signature = "MFYwEAYHKoZIzj0CAQYFK4EEAAoDQgAEF25QbMKZV5wJ/tw9BjBvx137bIQwbJR76bYkwAQeKbn9xRPPaMNpu0hWRlZt8MUxvGvn/ln5PxPHB+cmbmacZw==";
        Transaction transaction = new Transaction();
        transaction.setTransactionHash("test");
        TransactionInput transactionInput = new TransactionInput();
        TransactionOutput transactionOutput = new TransactionOutput();
        transactionInput.setIndexReferenced(12);
        transactionInput.addPreviousTransactionHash(0, "test");
        transactionInput.setPubKey("null");
        transactionOutput.setAmount(50);
        transactionOutput.setSignature(signature);
        transaction.setTransactionId("80");
        transaction.setTransactionInput(transactionInput);
        transaction.setTransactionOutput(transactionOutput);
        List<Transaction> transactionList = new ArrayList<>();
        transactionList.add(transaction);
        log.debug(String.valueOf(transaction));
        Block block = new Block(10, String.valueOf(new java.util.Date()), transactionList);
        BlockAddressPair pair = new BlockAddressPair("test", block);
        block.setDifficultyLevel("000");
        log.debug("Nonce before compute :" + pair.getBlock().getNonce());
        minerService.computeHash(pair.getBlock());
        log.debug("Nonce after compute" + pair.getBlock().getNonce());
    }
}