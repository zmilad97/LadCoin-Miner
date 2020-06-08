package com.github.zmilad97.miner.Service;

import com.github.zmilad97.miner.Module.Block;
import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Service
public class MinerService {
    private final static Logger LOG = LoggerFactory.getLogger(MinerService.class);

    @Value("${app.core.address}")
    private String coreAddress;

    private final Cryptography cryptography;

    @Autowired
    public MinerService(Cryptography cryptography) {
      this.cryptography = cryptography;
    }

    public void computeHash( @NotNull Block block) {
        String hash = "null";
        LOG.debug("diff : {}",block.getDifficultyLevel());
        long nonce = -1L;

        StringBuffer transactionStringToHash = new StringBuffer();

        for (int i = 0; i < block.getTransactions().size(); i++)
            transactionStringToHash.append(block.getTransactions().get(i).getTransactionHash());

        do {
            nonce++;
            block.setDate(new java.util.Date().toString());
            String stringToHash = nonce + block.getIndex() + block.getDate() + block.getPreviousHash() + transactionStringToHash;
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

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


}
