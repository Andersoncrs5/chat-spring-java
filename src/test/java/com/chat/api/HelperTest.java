package com.chat.api;

import com.chat.api.helps.classes.UserResponse;
import com.chat.api.modules.user.dto.CreateUserDTO;
import com.chat.api.utils.res.ResponseHttp;
import com.chat.api.utils.res.ResponseToken;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Random;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Service
@RequiredArgsConstructor
public class HelperTest {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    public UserResponse createUser() {
        String key = UUID.randomUUID().toString();

        try {
            CreateUserDTO dto = new CreateUserDTO(
                    "name" + key,
                    "username" + key,
                    "user" + key + "@gmail.com",
                    "12345678",
                    null,
                    "AnyBio",
                    "8640028922"
            );

            MvcResult result = mockMvc.perform(post("/v1/auth/register")
                            .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isCreated())
                    .andReturn();

            String registerJson = result.getResponse().getContentAsString();
            TypeReference<ResponseHttp<ResponseToken>> typeRef =
                    new TypeReference<>() {};

            ResponseHttp<ResponseToken> response =
                    objectMapper.readValue(registerJson, typeRef);

            assertThat(response.status()).isEqualTo(true);
            assertThat(response.message()).isNotBlank();
            assertThat(response.data().token()).isNotBlank();
            assertThat(response.data().refreshToken()).isNotBlank();
            assertThat(response.data().user().id()).isNotNull();
            assertThat(response.data().user().name()).isEqualTo(dto.name());
            assertThat(response.data().user().username()).isEqualTo(dto.username());
            assertThat(response.data().user().email()).isEqualTo(dto.email());
            assertThat(response.data().user().phoneNumber()).isEqualTo(dto.phoneNumber());

            return new UserResponse(
                    response.data(),
                    dto
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

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
