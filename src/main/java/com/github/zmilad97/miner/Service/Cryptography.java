package com.github.zmilad97.miner.Service;

import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import javax.xml.bind.DatatypeConverter;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@Service
public class Cryptography {
    private final MessageDigest md;

    @SneakyThrows
    public Cryptography() {
        md = MessageDigest.getInstance("SHA-256");
    }

    public byte[] getSha(String input) {
        return md.digest(input.getBytes(StandardCharsets.UTF_8));
    }

    public String toHexString(byte[] hash) {
        return DatatypeConverter.printHexBinary(hash);

    }
}
