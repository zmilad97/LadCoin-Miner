package com.github.zmilad97.miner.Service;

import com.github.zmilad97.miner.Module.*;
import com.google.gson.Gson;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.MalformedInputException;
import java.util.ArrayList;
import java.util.List;

@Service
public class MinerService {
    private final static Logger LOG = LoggerFactory.getLogger(MinerService.class);

    @Value("${app.core.address}")
    private String coreAddress;

    @Value("${app.wallet.public.id}")
    private String walletPublicId;

    public Block findBlock() {

        final HttpClient httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .build();
        String address = coreAddress +"/block"; //todo: convey config params with each block here  :  !checked

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(address))
                .setHeader("User-Agent", "Miner")
                .build();

        HttpResponse<String> response = null;
        try {

            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

      //Converting response to Block Object
        Gson gson = new Gson();
        Block block = gson.fromJson(response.body(), Block.class);
        LOG.debug("Reward: {}", block.getReward());

        //add reward transaction to Block transactions list
        Cryptography cryptography = new Cryptography();
        Transaction rewardTransaction = new Transaction();
        rewardTransaction.setTransactionId("1");
        rewardTransaction.setSource(null);
        rewardTransaction.setDestination(walletPublicId);
        rewardTransaction.setAmount(block.getReward());
        rewardTransaction.setTransactionHash(cryptography.toHexString(cryptography.getSha(rewardTransaction.getTransactionId()+rewardTransaction.getSource()+rewardTransaction.getDestination()+rewardTransaction.getAmount())));
        block.addTransaction(rewardTransaction);
        return block;
    }

    public void computeHash( @NotNull Block block) {
        String hash = "null";
        LOG.debug("diff: {}",block.getDifficultyLevel());
        long nonce = -1L;

        String transactionStringToHash = ""; //TODO: StringBuffer would be a better choice

        for (int i = 0; i < block.getTransactions().size(); i++)
            transactionStringToHash += block.getTransactions().get(i).getTransactionHash();    //TODO : FIX TRANSACTION HASH ALGORITHM

        do {
            nonce++;
            block.setDate(new java.util.Date());
            String stringToHash = nonce + block.getIndex() + block.getDate().toString() + block.getPreviousHash() + transactionStringToHash;
            Cryptography cryptography = new Cryptography();

            hash = cryptography.toHexString(cryptography.getSha(stringToHash));

            if (hash.startsWith(block.getDifficultyLevel())) {
                LOG.trace("string to hash {}",stringToHash);
                break;
            }
        } while (true);

        block.setNonce(nonce);
        block.setHash(hash);

    }

    //Sends block to CoreService to confirm mining
    public void sendBlock(Block block) {

        try {

            CloseableHttpClient httpClient = HttpClientBuilder.create().build();
            HttpPost httpPost = new HttpPost(coreAddress + "/pow");
            Gson gson = new Gson();
            block.setDate(null);
            StringEntity params = new StringEntity(gson.toJson(block));
            httpPost.addHeader("Content-Type", "application/json");
            httpPost.setEntity(params);
            CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
            System.out.println(httpResponse.getStatusLine());
//            System.out.println(httpResponse.getEntity().getContent());
        } catch (IOException e) {
          LOG.error(e.getMessage(),e);
        }


    }

//    public Boolean mineStatus(Response response, Config config) {
//
//        try {
//
//            String body = "#" + response.getHash() + "#" + response.getNonce() + "#" + response.getRewardTransaction()
//                    .getTransactionId() + "#" + response.getRewardTransaction().getSource() + "#"
//                    + response.getRewardTransaction().getDestination() + "#" + response.getRewardTransaction().getAmount() + "#";
//
//            HttpClient httpClient = HttpClient.newHttpClient();
//            HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(config.getBlockChainCoreAddress() + "/testpost"))
//                    .POST(HttpRequest.BodyPublishers.ofString(body))
//                    .build();
//
//            HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
//
//            System.out.println(httpResponse);
//            return true;
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//
//        return false;
//    }

    public List<Transaction> getTransactionList() {
        List<Transaction> currentTransactions = new ArrayList<>();
        Gson gson = new Gson();

        try {

            URL url = new URL("http://localhost:8080/trx/current");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");

            if (connection.getResponseCode() != 200) {
                throw new RuntimeException("Failed , http error code : " + connection.getResponseCode());
            }

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            StringBuilder sb = new StringBuilder();
            int cb;
            while ((cb = bufferedReader.read()) != -1) {
                sb.append((char) cb);
            }

            //fixing Strings
            sb.deleteCharAt(sb.length() - 1);
            sb.deleteCharAt(0);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append('"');
            stringBuilder.append("transactionId");

            String[] strings = sb.toString().split(stringBuilder.toString());

            //converting to json
            for (int i = 1; i < strings.length; i++) {
                StringBuilder strb = new StringBuilder();
                strb.append('{');
                strb.append('"');
                strb.append("transactionId");
                strb.append(strings[i]);

                if (i != strings.length - 1) {
                    strb.deleteCharAt(strb.length() - 1);
                    strb.deleteCharAt(strb.length() - 1);
                }

                Transaction transaction = gson.fromJson(strb.toString(), Transaction.class);
                currentTransactions.add(transaction);

            }


//            sb.deleteCharAt(sb.length() - 1);
//            sb.deleteCharAt(0);

            return currentTransactions;

        } catch (IOException e) {
            LOG.error(e.getMessage(),e);
        }

      return null;
    }

    public List<Block> getBlocks() {
        List<Block> currentBlocks = new ArrayList<>();
        Gson gson = new Gson();

        try {

            URL url = new URL("http://localhost:8080/chain");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");

            if (connection.getResponseCode() != 200) {
                throw new RuntimeException("Failed , http error code : " + connection.getResponseCode());
            }
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            StringBuilder sb = new StringBuilder();
            int cb;
            while ((cb = bufferedReader.read()) != -1) {
                sb.append((char) cb);
            }

            //fixing Strings
            sb.deleteCharAt(sb.length() - 1);
            sb.deleteCharAt(0);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append('"');
            stringBuilder.append("index");

            String[] strings = sb.toString().split(stringBuilder.toString());
            //converting to json
            for (int i = 1; i < strings.length; i++) {
                StringBuilder strb = new StringBuilder();
                strb.append('{');
                strb.append('"');
                strb.append("index");
                strb.append(strings[i]);
                if (i != strings.length - 1) {
                    strb.deleteCharAt(strb.length() - 1);
                    strb.deleteCharAt(strb.length() - 1);
                }

                Block block = gson.fromJson(strb.toString(), Block.class);
                currentBlocks.add(block);

            }


            sb.deleteCharAt(sb.length() - 1);
            sb.deleteCharAt(0);


            return currentBlocks;

        } catch (MalformedInputException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


}
