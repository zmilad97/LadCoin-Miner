package com.github.zmilad97.miner.Service;

import com.github.zmilad97.miner.Module.Block;
import com.github.zmilad97.miner.Module.BlockAddressPair;
import com.github.zmilad97.miner.Module.Transaction.Transaction;
import com.github.zmilad97.miner.Module.Transaction.TransactionInput;
import com.github.zmilad97.miner.Module.Transaction.TransactionOutput;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.http.HttpResponse;
import java.security.*;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Service
@Slf4j
public class MinerService {
    @Value("${app.core.address}")
    private String coreAddress;


    @Value("${app.wallet.signature}")
    private String walletSignature;

    private final CoreClient coreClient;
    private final Cryptography cryptography;

    @Autowired
    public MinerService(Cryptography cryptography, CoreClient coreClient) {
        this.cryptography = cryptography;
        this.coreClient = coreClient;
    }

    public void computeHash(Block block) {
        String hash = "";
        log.debug("diff : {}", block.getDifficultyLevel());
        long nonce = -1;
        StringBuilder transactionStringToHash = new StringBuilder();


        for (int i = 0; i < block.getTransactions().size(); i++)
            transactionStringToHash.append(block.getTransactions().get(i).getTransactionHash());

        do {
            nonce++;
            String stringToHash = nonce + block.getIndex() + block.getDate() + block.getPreviousHash() + transactionStringToHash;

            hash = cryptography.toHexString(cryptography.getSha(stringToHash));
            log.debug("| Nonce {} | Hash {} | ", nonce, hash);
            if (hash.startsWith(block.getDifficultyLevel())) {
                log.trace("string to hash {}", stringToHash);
                break;
            }
        } while (true);

        block.setNonce(nonce);
        block.setHash(hash);

    }

/*    public void setCurrentChain() {
        blockService.setChain(coreClient.currentChain());
    }*/

    public Block currentTransactions(Block block) {
        log.debug("current transaction start");
        //TODO  :  Think about this
//        for (int i = 0; i < block.getTransactions().size(); i++)
//            if (!verifyTransaction(blockService
//                    .findTransactionByTransactionHash(block.getTransactions().get(i)), block.getTransactions().get(i)))
//                block.getTransactions().remove(block.getTransactions().get(i));
        log.debug("current transaction end");
        Transaction rewardTransaction = new Transaction();
        TransactionInput transactionInput = new TransactionInput();
        TransactionOutput transactionOutput = new TransactionOutput();

        //TODO :  FIX this
        transactionInput.addPreviousTransactionHash(0, "REWARD");
        transactionInput.setPubKey("REWARD");

        transactionOutput.setAmount(block.getReward());
        transactionOutput.setSignature(walletSignature);

        rewardTransaction.setTransactionInput(transactionInput);
        rewardTransaction.setTransactionOutput(transactionOutput);
        rewardTransaction.setTransactionId("REWARD" + block.getIndex());
        rewardTransaction.setTransactionHash(cryptography.toHexString(cryptography.getSha(
                rewardTransaction.getTransactionId() + rewardTransaction.getTransactionOutput().getAmount())));
        block.addTransaction(rewardTransaction);
        return block;

    }

    @SneakyThrows
    private boolean verifyTransaction(Transaction outputTransaction, Transaction transaction) {
        boolean result;

        Signature ecdsaVerify = Signature.getInstance("SHA256withECDSA");
        KeyFactory keyFactory = KeyFactory.getInstance("EC");

        EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(Base64.getDecoder()
                .decode(outputTransaction.getTransactionInput().getPubKey()));

        PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);
        ecdsaVerify.initVerify(publicKey);
//TODO:FIx here
//            ecdsaVerify.update(transaction.getTransactionInput()
//                    .getSignature().get("message").getBytes(StandardCharsets.UTF_8));

        result = ecdsaVerify.verify(Base64.getDecoder().decode(
                transaction.getTransactionOutput().getSignature()));

        return result;
    }


    public void mine() {
        BlockAddressPair pair = coreClient.findBlock();
        computeHash(pair.getBlock());
        HttpResponse<String> response = coreClient.sendBlock(pair.getBlock(), pair.getAddress());
        response.body();
    }

    //TODO : Write A Method To Get All The Blocks And Save Transaction Snapshots


}
