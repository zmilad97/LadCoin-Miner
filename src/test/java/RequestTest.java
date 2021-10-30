import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.zmilad97.miner.Module.Block;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeoutException;


@Slf4j
public class RequestTest {

    @SneakyThrows
    @Test
    void sendRequests() {
        List<String> coreAddresses = new ArrayList<>(Arrays.asList(
                "http://localhost:8181", "http://localhost:4040", "http://localhost:5050"));
        List<Block> transactions = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        for (String address : coreAddresses) {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(address + "/block")).build();
            client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).
                    thenAccept(response -> {
                        if (response.statusCode() == 200)

                            try {
                                transactions.add(mapper.readValue(response.body(), Block.class));
                            } catch (JsonProcessingException e) {
                                log.error(e.getMessage());
                            }

                    });
        }
        for (int i = 0; i < 20; i++) {
            if (transactions.size() == 0)
                Thread.sleep(500);
            else
                break;
        }
        if (transactions.size() == 0)
            throw new TimeoutException();
        Block block = Collections.max(
                transactions, (o1, o2) ->
                        Math.max(o1.getTransactions().size(), o2.getTransactions().size()));
        System.out.println(block);
    }

}
