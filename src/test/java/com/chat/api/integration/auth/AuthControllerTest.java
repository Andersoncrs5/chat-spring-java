package com.chat.api.integration.auth;

import com.chat.api.HelperTest;
import com.chat.api.NoTransactionConfig;
import com.chat.api.helps.classes.UserResponse;
import com.chat.api.modules.auth.dto.LoginRequestDTO;
import com.chat.api.modules.user.dto.CreateUserDTO;
import com.chat.api.modules.user.repository.UserRepository;
import com.chat.api.utils.res.ResponseHttp;
import com.chat.api.utils.res.ResponseToken;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MediaType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import({com.chat.api.TestcontainersConfiguration.class})
public class AuthControllerTest {

    private final String URL = "/v1/auth/";

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserRepository userRepository;
    @Autowired private HelperTest helper;

    @BeforeEach
    public void setup() {
        userRepository.deleteAll();
    }

    @Test
    void shouldCreateUser() throws Exception {
        String key = UUID.randomUUID().toString();

        CreateUserDTO dto = new CreateUserDTO(
                "name" + key,
                "username" + key,
                "user" + key + "@gmail.com",
                "12345678",
                null,
                "AnyBio",
                "8640028922"
        );

        MvcResult result = mockMvc.perform(post(URL + "/register")
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

    }

    @Test
    void shouldReturnConflictBecauseEmailAlreadyExists() throws Exception {
        String key = UUID.randomUUID().toString();

        UserResponse userResponse = this.helper.createUser();

        CreateUserDTO dto = new CreateUserDTO(
                "name" + key,
                "username" + key,
                userResponse.dto().email(),
                "12345678",
                null,
                "AnyBio",
                "8640028922"
        );

        MvcResult result = mockMvc.perform(post(URL + "/register")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict())
                .andReturn();

        String registerJson = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<Void>> typeRef =
                new TypeReference<>() {};

        ResponseHttp<Void> response =
                objectMapper.readValue(registerJson, typeRef);

        assertThat(response.status()).isEqualTo(false);
        assertThat(response.message()).isNotBlank();
        assertThat(response.data()).isNull();
    }

    @Test
    void shouldReturnConflictBecauseUsernameAlreadyExists() throws Exception {
        String key = UUID.randomUUID().toString();

        UserResponse userResponse = this.helper.createUser();

        CreateUserDTO dto = new CreateUserDTO(
                "name" + key,
                userResponse.dto().username(),
                "user" + key + "@gmail.com",
                "12345678",
                null,
                "AnyBio",
                "8640028922"
        );

        MvcResult result = mockMvc.perform(post(URL + "/register")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict())
                .andReturn();

        String registerJson = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<Void>> typeRef =
                new TypeReference<>() {};

        ResponseHttp<Void> response =
                objectMapper.readValue(registerJson, typeRef);

        assertThat(response.status()).isEqualTo(false);
        assertThat(response.message()).isNotBlank();
        assertThat(response.data()).isNull();
    }

    @Test
    void shouldLogUser() throws Exception {
        UserResponse userResponse = this.helper.createUser();

        LoginRequestDTO dto = new LoginRequestDTO(
                userResponse.dto().email(),
                userResponse.dto().password()
        );

        MvcResult result = mockMvc.perform(post(URL + "/login")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
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
        assertThat(response.data().user().id()).isEqualTo(userResponse.tokens().user().id());
        assertThat(response.data().user().email()).isEqualTo(dto.email());
    }

    @Test
    void shouldFailBecauseUserNotFoundLogUser() throws Exception {
        UserResponse userResponse = this.helper.createUser();

        LoginRequestDTO dto = new LoginRequestDTO(
                "user@gmail.com",
                userResponse.dto().password()
        );

        MvcResult result = mockMvc.perform(post(URL + "/login")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound())
                .andReturn();

        String registerJson = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<Void>> typeRef =
                new TypeReference<>() {};

        ResponseHttp<Void> response =
                objectMapper.readValue(registerJson, typeRef);

        assertThat(response.status()).isEqualTo(false);
        assertThat(response.message()).isNotBlank();
        assertThat(response.data()).isNull();
    }

    @Test
    void shouldFailBecausePasswordWrongLogUser() throws Exception {
        UserResponse userResponse = this.helper.createUser();

        LoginRequestDTO dto = new LoginRequestDTO(
                userResponse.dto().email(),
                "56467436735567"
        );

        MvcResult result = mockMvc.perform(post(URL + "/login")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized())
                .andReturn();

        String registerJson = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<Void>> typeRef =
                new TypeReference<>() {};

        ResponseHttp<Void> response =
                objectMapper.readValue(registerJson, typeRef);

        assertThat(response.status()).isEqualTo(false);
        assertThat(response.message()).isNotBlank();
        assertThat(response.data()).isNull();
    }

    @Test
    void shouldRefreshTokens() throws Exception {
        UserResponse userResponse = this.helper.createUser();

        MvcResult result = mockMvc.perform(
                get(URL + "/refresh-token/" + userResponse.tokens().refreshToken())
                )
                .andExpect(status().isOk())
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
        assertThat(response.data().user().id()).isEqualTo(userResponse.tokens().user().id());
    }

    @Test
    void shouldReturnNotFoundTheRefreshTokensBecauseUserNotFound() throws Exception {
        MvcResult result = mockMvc.perform(get(URL + "refresh-token/fjsufbsfusfgrdigidugfiod"))
                .andExpect(status().isNotFound())
                .andReturn();

        String registerJson = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<Void>> typeRef =
                new TypeReference<>() {};

        ResponseHttp<Void> response =
                objectMapper.readValue(registerJson, typeRef);

        assertThat(response.status()).isEqualTo(false);
        assertThat(response.message()).isNotBlank();
        assertThat(response.data()).isNull();
    }


}
