package com.github.zmilad97.miner.Module;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

@Service
public class Config {

    private String blockChainCoreAddress;

    private String walletPublicId;

    private String defaultAddress = "http://localhost:8181";
    private ObjectMapper mapper = new ObjectMapper();

    public Config( String walletPublicId) {
        this.blockChainCoreAddress = defaultAddress;
        this.walletPublicId = walletPublicId;
    }

    public Config() {
        this.blockChainCoreAddress = defaultAddress;
    }

//    public List<String> coreConfig(Config config) {
//        List rewardDifficulty = new ArrayList();
//
//        try {
//
//            if (config.getBlockChainCoreAddress() == null)
//                config.setBlockChainCoreAddress(defaultAddress);
//            URL url = new URL(config.getBlockChainCoreAddress() + "/config");
//
//            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//            connection.setRequestMethod("GET");
//            connection.setRequestProperty("Accept", "application/json");
//            if (connection.getResponseCode() != 200) {
//                throw new RuntimeException("Failed , http error code : " + connection.getResponseCode());
//            }
//
////            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
////            String res = IOUtils.toString(connection.getInputStream(), StandardCharsets.UTF_8);
////            StringBuilder sb = new StringBuilder(res);
////            int cb;
////            while ((cb = bufferedReader.read()) != -1) {
////                sb.append((char) cb);
////            }
//
//
//            //fixing Strings
////            sb.deleteCharAt(sb.length() - 1);
////            sb.deleteCharAt(0);
//
//
//            Map<String, String> map = mapper.readValue(connection.getInputStream(), new TypeReference<>() {
//            });
//            config.reward = Double.valueOf(map.get("reward"));
//            config.difficultyLevel = map.get("difficulty");
////            String[] strings = sb.toString().split(":");
////
////            for (int i = 0; i < 2; i++) {
////                StringBuilder strb = new StringBuilder();
////                strb.append(strings[i]);
////                strb.deleteCharAt(strb.length() - 1);
////                strb.deleteCharAt(0);
////
////                    rewardDifficulty.add(strb.toString());
////                }
////
////
////            config.reward = Double.parseDouble(rewardDifficulty.get(0).toString());
////            config.difficultyLevel = rewardDifficulty.get(1).toString();
//
//            return rewardDifficulty;
//        } catch (MalformedInputException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }


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
