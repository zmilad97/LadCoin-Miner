package com.github.zmilad97.miner.Service;

import com.github.zmilad97.miner.Module.Block;
import com.github.zmilad97.miner.Module.Transaction.Transaction;
import com.github.zmilad97.miner.Module.Transaction.TransactionInput;
import com.github.zmilad97.miner.Module.Transaction.TransactionOutput;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.*;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Service
public class MinerService {
    private final static Logger LOG = LoggerFactory.getLogger(MinerService.class);
    @Value("${app.core.address}")
    private String coreAddress;

    @Value("${app.wallet.signature}")
    private String walletSignature;

    private CoreClient coreClient;
    private final Cryptography cryptography;
    private BlockService blockService;

    @Autowired
    public MinerService(Cryptography cryptography, CoreClient coreClient, BlockService blockService) {
        this.cryptography = cryptography;
        this.coreClient = coreClient;
        this.blockService = blockService;
    }

    public void computeHash(@NotNull Block block) {
        String hash;
        LOG.debug("diff : {}", block.getDifficultyLevel());
        long nonce = -1;
        StringBuilder transactionStringToHash = new StringBuilder();


        for (int i = 0; i < block.getTransactions().size(); i++)
            transactionStringToHash.append(block.getTransactions().get(i).getTransactionHash());

        do {
            nonce++;
            block.setDate(new java.util.Date().toString());
            String stringToHash = nonce + block.getIndex() + block.getDate() + block.getPreviousHash() + transactionStringToHash;
            Cryptography cryptography = new Cryptography();

            hash = cryptography.toHexString(cryptography.getSha(stringToHash));

            if (hash.startsWith(block.getDifficultyLevel())) {
                LOG.trace("string to hash {}", stringToHash);
                break;
            }
        } while (true);

        block.setNonce(nonce);
        block.setHash(hash);

    }

    public void setCurrentChain() {
        blockService.setChain(coreClient.currentChain());
    }

    public Block currentTransactions(Block block) {
        LOG.debug("current transaction start");
        //TODO  :  Think about this
//        for (int i = 0; i < block.getTransactions().size(); i++)
//            if (!verifyTransaction(blockService
//                    .findTransactionByTransactionHash(block.getTransactions().get(i)), block.getTransactions().get(i)))
//                block.getTransactions().remove(block.getTransactions().get(i));
        LOG.debug("current transaction end");
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
        rewardTransaction.setTransactionId("REWARD"+block.getIndex());
        rewardTransaction.setTransactionHash(cryptography.toHexString(cryptography.getSha(
                rewardTransaction.getTransactionId()+rewardTransaction.getTransactionOutput().getAmount())));
        block.addTransaction(rewardTransaction);
        return block;

    }

    private boolean verifyTransaction(Transaction outputTransaction, Transaction transaction) {
        boolean result ;

        try {
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
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | InvalidKeyException | SignatureException e) {
            LOG.error(e.getMessage(), e);
        }
        return false;
    }

    //TODO : Write A Method To Get All The Blocks And Save Transaction Snapshots


}
