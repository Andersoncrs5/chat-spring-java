package com.chat.api.integration.user;

import com.chat.api.HelperTest;
import com.chat.api.NoTransactionConfig;
import com.chat.api.helps.classes.UserResponse;
import com.chat.api.modules.user.dto.UpdateUserDTO;
import com.chat.api.modules.user.dto.UserDTO;
import com.chat.api.modules.user.repository.UserRepository;
import com.chat.api.utils.res.ResponseHttp;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import({com.chat.api.TestcontainersConfiguration.class})
public class UserControllerTest {

    private final String URL = "/v1/user";

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserRepository userRepository;
    @Autowired private HelperTest helper;

    @BeforeEach
    public void setup() {
        userRepository.deleteAll();
    }

    @Test
    void shouldGetUserMe() throws Exception {
        UserResponse userResponse = this.helper.createUser();

        MvcResult result = this.mockMvc.perform(get(this.URL + "/me")
                .header("Authorization", "Bearer " + userResponse.tokens().token())
        ).andExpect(status().isOk()).andReturn();

        String registerJson = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<UserDTO>> typeRef =
                new TypeReference<>() {};

        ResponseHttp<UserDTO> response =
                objectMapper.readValue(registerJson, typeRef);

        assertThat(response.status()).isEqualTo(true);
        assertThat(response.message()).isNotBlank();
        assertThat(response.data().id()).isEqualTo(userResponse.tokens().user().id());
    }

    @Test
    void shouldDeleteUserMe() throws Exception {
        UserResponse userResponse = this.helper.createUser();

        MvcResult result = this.mockMvc.perform(delete(this.URL)
                .header("Authorization", "Bearer " + userResponse.tokens().token())
        ).andExpect(status().isOk()).andReturn();

        String registerJson = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<UserDTO>> typeRef =
                new TypeReference<>() {};

        ResponseHttp<UserDTO> response =
                objectMapper.readValue(registerJson, typeRef);

        assertThat(response.status()).isEqualTo(true);
        assertThat(response.message()).isNotBlank();
        assertThat(response.data()).isNull();
    }

    @Test
    void shouldUpdateAllField() throws Exception {
        UserResponse userResponse = this.helper.createUser();

        UpdateUserDTO dto = new UpdateUserDTO(
                "user updated",
                "userupdated",
                "12345678",
                "https://www.pinterest.com/pin/pochita-chainsaw-man-wall-and-art-print--853009985699119362/",
                "AnyBioAnyBioAnyBioAnyBioAnyBioAnyBioAnyBioAnyBioAnyBio",
                "86854356345694"
        );

        MvcResult result = this.mockMvc.perform(patch(this.URL)
                .header("Authorization", "Bearer " + userResponse.tokens().token())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        ).andExpect(status().isOk()).andReturn();

        String registerJson = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<UserDTO>> typeRef =
                new TypeReference<>() {};

        ResponseHttp<UserDTO> response =
                objectMapper.readValue(registerJson, typeRef);

        assertThat(response.status()).isEqualTo(true);
        assertThat(response.message()).isNotBlank();
        assertThat(response.data().id()).isEqualTo(userResponse.tokens().user().id());

        assertThat(response.data().name()).isEqualTo(dto.name());
        assertThat(response.data().username()).isEqualTo(dto.username());
        assertThat(response.data().bannerUrl()).isEqualTo(dto.bannerUrl());
        assertThat(response.data().bio()).isEqualTo(dto.bio());
        assertThat(response.data().phoneNumber()).isEqualTo(dto.phoneNumber());
    }

    @Test
    void shouldUpdateJustFieldName() throws Exception {
        UserResponse userResponse = this.helper.createUser();

        UpdateUserDTO dto = new UpdateUserDTO(
                "user updated",
                null,
                null,
                null,
                null,
                null
        );

        MvcResult result = this.mockMvc.perform(patch(this.URL)
                .header("Authorization", "Bearer " + userResponse.tokens().token())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        ).andExpect(status().isOk()).andReturn();

        String registerJson = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<UserDTO>> typeRef =
                new TypeReference<>() {};

        ResponseHttp<UserDTO> response =
                objectMapper.readValue(registerJson, typeRef);

        assertThat(response.status()).isEqualTo(true);
        assertThat(response.message()).isNotBlank();
        assertThat(response.data().id()).isEqualTo(userResponse.tokens().user().id());

        assertThat(response.data().name()).isEqualTo(dto.name());
        assertThat(response.data().username()).isEqualTo(userResponse.tokens().user().username());
        assertThat(response.data().bannerUrl()).isEqualTo(userResponse.tokens().user().bannerUrl());
        assertThat(response.data().bio()).isEqualTo(userResponse.tokens().user().bio());
        assertThat(response.data().phoneNumber()).isEqualTo(userResponse.tokens().user().phoneNumber());
    }

    @Test
    void shouldUpdateJustFieldUsername() throws Exception {
        UserResponse userResponse = this.helper.createUser();

        UpdateUserDTO dto = new UpdateUserDTO(
                null,
                "userupdated",
                null,
                null,
                null,
                null
        );

        MvcResult result = this.mockMvc.perform(patch(this.URL)
                .header("Authorization", "Bearer " + userResponse.tokens().token())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        ).andExpect(status().isOk()).andReturn();

        String registerJson = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<UserDTO>> typeRef =
                new TypeReference<>() {};

        ResponseHttp<UserDTO> response =
                objectMapper.readValue(registerJson, typeRef);

        assertThat(response.status()).isEqualTo(true);
        assertThat(response.message()).isNotBlank();
        assertThat(response.data().id()).isEqualTo(userResponse.tokens().user().id());

        assertThat(response.data().name()).isEqualTo(userResponse.tokens().user().name());
        assertThat(response.data().username()).isEqualTo(dto.username());
        assertThat(response.data().bannerUrl()).isEqualTo(userResponse.tokens().user().bannerUrl());
        assertThat(response.data().bio()).isEqualTo(userResponse.tokens().user().bio());
        assertThat(response.data().phoneNumber()).isEqualTo(userResponse.tokens().user().phoneNumber());
    }

    @Test
    void shouldUpdateJustFieldBannerUrl() throws Exception {
        UserResponse userResponse = this.helper.createUser();

        UpdateUserDTO dto = new UpdateUserDTO(
                null,
                null,
                null,
                "https://www.pinterest.com/pin/pochita-chainsaw-man-wall-and-art-print--853009985699119362/",
                null,
                null
        );

        MvcResult result = this.mockMvc.perform(patch(this.URL)
                .header("Authorization", "Bearer " + userResponse.tokens().token())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        ).andExpect(status().isOk()).andReturn();

        String registerJson = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<UserDTO>> typeRef =
                new TypeReference<>() {};

        ResponseHttp<UserDTO> response =
                objectMapper.readValue(registerJson, typeRef);

        assertThat(response.status()).isEqualTo(true);
        assertThat(response.message()).isNotBlank();
        assertThat(response.data().id()).isEqualTo(userResponse.tokens().user().id());

        assertThat(response.data().name()).isEqualTo(userResponse.tokens().user().name());
        assertThat(response.data().username()).isEqualTo(userResponse.tokens().user().username());
        assertThat(response.data().bannerUrl()).isEqualTo(dto.bannerUrl());
        assertThat(response.data().bio()).isEqualTo(userResponse.tokens().user().bio());
        assertThat(response.data().phoneNumber()).isEqualTo(userResponse.tokens().user().phoneNumber());
    }

    @Test
    void shouldUpdateJustFieldBio() throws Exception {
        UserResponse userResponse = this.helper.createUser();

        UpdateUserDTO dto = new UpdateUserDTO(
                null,
                null,
                null,
                null,
                "BioUpdated",
                null
        );

        MvcResult result = this.mockMvc.perform(patch(this.URL)
                .header("Authorization", "Bearer " + userResponse.tokens().token())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        ).andExpect(status().isOk()).andReturn();

        String registerJson = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<UserDTO>> typeRef =
                new TypeReference<>() {};

        ResponseHttp<UserDTO> response =
                objectMapper.readValue(registerJson, typeRef);

        assertThat(response.status()).isEqualTo(true);
        assertThat(response.message()).isNotBlank();
        assertThat(response.data().id()).isEqualTo(userResponse.tokens().user().id());

        assertThat(response.data().name()).isEqualTo(userResponse.tokens().user().name());
        assertThat(response.data().username()).isEqualTo(userResponse.tokens().user().username());
        assertThat(response.data().bio()).isEqualTo(dto.bio());
        assertThat(response.data().bannerUrl()).isEqualTo(userResponse.tokens().user().bannerUrl());
        assertThat(response.data().phoneNumber()).isEqualTo(userResponse.tokens().user().phoneNumber());
    }

    @Test
    void shouldUpdateJustFieldPhoneNumber() throws Exception {
        UserResponse userResponse = this.helper.createUser();

        UpdateUserDTO dto = new UpdateUserDTO(
                null,
                null,
                null,
                null,
                null,
                "464545765756756"
        );

        MvcResult result = this.mockMvc.perform(patch(this.URL)
                .header("Authorization", "Bearer " + userResponse.tokens().token())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        ).andExpect(status().isOk()).andReturn();

        String registerJson = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<UserDTO>> typeRef =
                new TypeReference<>() {};

        ResponseHttp<UserDTO> response =
                objectMapper.readValue(registerJson, typeRef);

        assertThat(response.status()).isEqualTo(true);
        assertThat(response.message()).isNotBlank();
        assertThat(response.data().id()).isEqualTo(userResponse.tokens().user().id());

        assertThat(response.data().name()).isEqualTo(userResponse.tokens().user().name());
        assertThat(response.data().username()).isEqualTo(userResponse.tokens().user().username());
        assertThat(response.data().phoneNumber()).isEqualTo(dto.phoneNumber());
        assertThat(response.data().bio()).isEqualTo(userResponse.tokens().user().bio());
        assertThat(response.data().phoneNumber()).isEqualTo(dto.phoneNumber());
    }



}
