package com.chat.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class HelperTest {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    public static String generateChars() {
        Random random = new Random();
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < 10; i++) {
            char l = (char) ('a' + random.nextInt(26));
            builder.append(l);
        }

        return builder.toString();
    }

    public static String generateRandomUnicode() {
        Random random = new Random();

        int start = 0x1F600;
        int end = 0x1F64F;

        int randomUnicode = start + random.nextInt(end - start + 1);
        return "U+" + Integer.toHexString(randomUnicode).toUpperCase();
    }

}
