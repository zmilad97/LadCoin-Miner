package com.github.zmilad97.miner.Module;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.MalformedInputException;
import java.util.ArrayList;
import java.util.List;

@Service
public class Config {

    private String blockChainCoreAddress;

    private String walletPublicId;

    private String difficultyLevel;

    private double reward;

    private String defaultAddress = "http://localhost:8181";

    public Config(String blockChainCoreAddress, String walletPublicId) {
        this.blockChainCoreAddress = blockChainCoreAddress;
        this.walletPublicId = walletPublicId;
    }

    public Config() {

    }

    public List<String> coreConfig(Config config) {
        List rewardDifficulty = new ArrayList();

        try {

            if (config.getBlockChainCoreAddress() == null)
                config.setBlockChainCoreAddress(defaultAddress);
            URL url = new URL(config.getBlockChainCoreAddress() + "/config");

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


            String[] strings = sb.toString().split(":");

            for (int i = 0; i < 2; i++) {
                StringBuilder strb = new StringBuilder();
                strb.append(strings[i]);
                strb.deleteCharAt(strb.length() - 1);
                strb.deleteCharAt(0);

                    rewardDifficulty.add(strb.toString());
                }


            config.reward = Double.parseDouble(rewardDifficulty.get(0).toString());
            config.difficultyLevel = rewardDifficulty.get(1).toString();

            return rewardDifficulty;
        } catch (MalformedInputException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getDifficultyLevel() {
        return difficultyLevel;
    }

    public void setDifficultyLevel(String difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }

    public double getReward() {
        return reward;
    }

    public void setReward(int reward) {
        this.reward = reward;
    }

    public String getBlockChainCoreAddress() {
        return blockChainCoreAddress;
    }

    public void setBlockChainCoreAddress(String blockChainCoreAddress) {
        this.blockChainCoreAddress = blockChainCoreAddress;
    }

    public String getWalletPublicId() {
        return walletPublicId;
    }

    public void setWalletPublicId(String walletPublicId) {
        this.walletPublicId = walletPublicId;
    }
}
