package com.chat.api.integration.contact;

import com.chat.api.HelperTest;
import com.chat.api.TestcontainersConfiguration;
import com.chat.api.helps.classes.UserResponse;
import com.chat.api.modules.contacts.dto.ContactDTO;
import com.chat.api.modules.contacts.dto.CreateContactDTO;
import com.chat.api.modules.contacts.dto.UpdateContactDTO;
import com.chat.api.modules.contacts.repository.ContactRepository;
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

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import({TestcontainersConfiguration.class})
public class ContactControllerTest {

    private final String URL = "/v1/contact";

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private HelperTest helper;
    @Autowired private ContactRepository repository;

    @BeforeEach
    void setup() {
        repository.deleteAll();
    }

    // create
    @Test
    void shouldCreateContact() throws Exception {
        var traceId = UUID.randomUUID().toString();
        UserResponse user = this.helper.createUser();
        UserResponse contact = this.helper.createUser();

        CreateContactDTO dto = new CreateContactDTO(
                "pochita",
                contact.tokens().user().id()
        );

        MvcResult result = this.mockMvc.perform(post(this.URL)
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + user.tokens().token())
                .header("X-Idempotency-Key", traceId)
        ).andExpect(status().isCreated()).andReturn();

        String registerJson = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<ContactDTO>> typeRef =
                new TypeReference<>() {};

        ResponseHttp<ContactDTO> response =
                objectMapper.readValue(registerJson, typeRef);

        assertThat(response.status()).isEqualTo(true);
        assertThat(response.traceId()).isNotBlank().isEqualTo(traceId);
        assertThat(response.message()).isNotBlank();
        assertThat(response.data()).isNotNull();

        assertThat(response.data().id()).isNotNull();
        assertThat(response.data().contactId()).isEqualTo(contact.tokens().user().id());
    }

    @Test
    void shouldReturnConflictContactAlreadyExistsCreateContact() throws Exception {
        var traceId = UUID.randomUUID().toString();
        UserResponse user = this.helper.createUser();
        UserResponse contact = this.helper.createUser();
        this.helper.createContact(user, contact);

        CreateContactDTO dto = new CreateContactDTO(
                "pochita",
                contact.tokens().user().id()
        );

        MvcResult result = this.mockMvc.perform(post(this.URL)
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + user.tokens().token())
                .header("X-Idempotency-Key", traceId)
        ).andExpect(status().isConflict()).andReturn();

        String registerJson = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<Void>> typeRef =
                new TypeReference<>() {};

        ResponseHttp<Void> response =
                objectMapper.readValue(registerJson, typeRef);

        assertThat(response.status()).isEqualTo(false);
        assertThat(response.traceId()).isNotBlank().isEqualTo(traceId);
        assertThat(response.message()).isNotBlank();
        assertThat(response.data()).isNull();
    }

    @Test
    void shouldReturnBadRequestBecauseOwerEqualsCreateContact() throws Exception {
        var traceId = UUID.randomUUID().toString();
        UserResponse user = this.helper.createUser();

        CreateContactDTO dto = new CreateContactDTO(
                "pochita",
                user.tokens().user().id()
        );

        MvcResult result = this.mockMvc.perform(post(this.URL)
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + user.tokens().token())
                .header("X-Idempotency-Key", traceId)
        ).andExpect(status().isBadRequest()).andReturn();

        String registerJson = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<Void>> typeRef =
                new TypeReference<>() {};

        ResponseHttp<Void> response =
                objectMapper.readValue(registerJson, typeRef);

        assertThat(response.status()).isEqualTo(false);
        assertThat(response.traceId()).isNotBlank().isEqualTo(traceId);
        assertThat(response.message()).isNotBlank();
        assertThat(response.data()).isNull();
    }

    @Test
    void shouldReturnNotFoundBecauseContactNoExistsCreateContact() throws Exception {
        var traceId = UUID.randomUUID().toString();
        UserResponse user = this.helper.createUser();

        CreateContactDTO dto = new CreateContactDTO(
                "pochita",
                UUID.randomUUID()
        );

        MvcResult result = this.mockMvc.perform(post(this.URL)
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + user.tokens().token())
                .header("X-Idempotency-Key", traceId)
        ).andExpect(status().isNotFound()).andReturn();

        String registerJson = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<Void>> typeRef =
                new TypeReference<>() {};

        ResponseHttp<Void> response =
                objectMapper.readValue(registerJson, typeRef);

        assertThat(response.status()).isEqualTo(false);
        assertThat(response.traceId()).isNotBlank().isEqualTo(traceId);
        assertThat(response.message()).isNotBlank();
        assertThat(response.data()).isNull();
    }

    // GET
    @Test
    void shouldReturnContactWHenGetById() throws Exception {
        var traceId = UUID.randomUUID().toString();
        UserResponse user = this.helper.createUser();
        UserResponse contact = this.helper.createUser();
        ContactDTO contactDTO = this.helper.createContact(user, contact);

        MvcResult result = this.mockMvc.perform(get(this.URL + "/" + contactDTO.id())
                .header("Authorization", "Bearer " + user.tokens().token())
                .header("X-Idempotency-Key", traceId)
        ).andExpect(status().isOk()).andReturn();

        String registerJson = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<ContactDTO>> typeRef =
                new TypeReference<>() {};

        ResponseHttp<ContactDTO> response =
                objectMapper.readValue(registerJson, typeRef);

        assertThat(response.status()).isEqualTo(true);
        assertThat(response.message()).isNotBlank();
        assertThat(response.data().id()).isEqualTo(contactDTO.id());
    }

    @Test
    void shouldReturnNullWHenGetById() throws Exception {
        var traceId = UUID.randomUUID().toString();
        UserResponse user = this.helper.createUser();

        MvcResult result = this.mockMvc.perform(get(this.URL + "/" + traceId)
                .header("Authorization", "Bearer " + user.tokens().token())
                .header("X-Idempotency-Key", traceId)
        ).andExpect(status().isNotFound()).andReturn();

        String registerJson = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<ContactDTO>> typeRef =
                new TypeReference<>() {};

        ResponseHttp<ContactDTO> response =
                objectMapper.readValue(registerJson, typeRef);

        assertThat(response.status()).isEqualTo(false);
        assertThat(response.message()).isNotBlank();
        assertThat(response.data()).isNull();
    }

    // DELETE
    @Test
    void shouldReturnContactWHenDeleteById() throws Exception {
        var traceId = UUID.randomUUID().toString();
        UserResponse user = this.helper.createUser();
        UserResponse contact = this.helper.createUser();
        ContactDTO contactDTO = this.helper.createContact(user, contact);

        MvcResult result = this.mockMvc.perform(delete(this.URL + "/" + contactDTO.id())
                .header("Authorization", "Bearer " + user.tokens().token())
                .header("X-Idempotency-Key", traceId)
        ).andExpect(status().isOk()).andReturn();

        String registerJson = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<ContactDTO>> typeRef =
                new TypeReference<>() {};

        ResponseHttp<ContactDTO> response =
                objectMapper.readValue(registerJson, typeRef);

        assertThat(response.status()).isEqualTo(true);
        assertThat(response.message()).isNotBlank();
        assertThat(response.data()).isNull();
    }

    @Test
    void shouldReturnNullWHenDeleteById() throws Exception {
        var traceId = UUID.randomUUID().toString();
        UserResponse user = this.helper.createUser();

        MvcResult result = this.mockMvc.perform(delete(this.URL + "/" + traceId)
                .header("Authorization", "Bearer " + user.tokens().token())
                .header("X-Idempotency-Key", traceId)
        ).andExpect(status().isNotFound()).andReturn();

        String registerJson = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<ContactDTO>> typeRef =
                new TypeReference<>() {};

        ResponseHttp<ContactDTO> response =
                objectMapper.readValue(registerJson, typeRef);

        assertThat(response.status()).isEqualTo(false);
        assertThat(response.message()).isNotBlank();
        assertThat(response.data()).isNull();
    }

    // GET ALL
    @Test
    void shouldReturn200WhenGetAll() throws Exception {
        var traceId = UUID.randomUUID().toString();
        UserResponse user = this.helper.createUser();

        this.mockMvc.perform(get(this.URL)
                .header("Authorization", "Bearer " + user.tokens().token())
                .header("X-Idempotency-Key", traceId)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(0))
                .andExpect(jsonPath("$.content").isEmpty());
    }

    // update
    @Test
    void shouldReturnContactUpdateWhenUpdate() throws Exception {
        var traceId = UUID.randomUUID().toString();
        UserResponse user = this.helper.createUser();
        UserResponse contact = this.helper.createUser();
        ContactDTO contactDTO = this.helper.createContact(user, contact);

        UpdateContactDTO dto = new UpdateContactDTO("pochita-updated");

        MvcResult result = this.mockMvc.perform(patch(this.URL + "/" + contactDTO.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                .header("Authorization", "Bearer " + user.tokens().token())
                .header("X-Idempotency-Key", traceId)
        ).andExpect(status().isOk()).andReturn();

        String registerJson = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<ContactDTO>> typeRef =
                new TypeReference<>() {};

        ResponseHttp<ContactDTO> response =
                objectMapper.readValue(registerJson, typeRef);

        assertThat(response.status()).isEqualTo(true);
        assertThat(response.message()).isNotBlank();
        assertThat(response.traceId()).isNotBlank().isEqualTo(traceId);
        assertThat(response.data().id()).isEqualTo(contactDTO.id());
        assertThat(response.data().nickname()).isEqualTo(dto.nickname());

    }

    @Test
    void shouldReturnNullUpdateWhenUpdate() throws Exception {
        var traceId = UUID.randomUUID().toString();
        UserResponse user = this.helper.createUser();

        UpdateContactDTO dto = new UpdateContactDTO("pochita-updated");

        MvcResult result = this.mockMvc.perform(patch(this.URL + "/" + traceId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                .header("Authorization", "Bearer " + user.tokens().token())
                .header("X-Idempotency-Key", traceId)
        ).andExpect(status().isNotFound()).andReturn();

        String registerJson = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<ContactDTO>> typeRef =
                new TypeReference<>() {};

        ResponseHttp<ContactDTO> response =
                objectMapper.readValue(registerJson, typeRef);

        assertThat(response.status()).isEqualTo(false);
        assertThat(response.message()).isNotBlank();
        assertThat(response.traceId()).isNotBlank().isEqualTo(traceId);
        assertThat(response.data()).isNull();
    }


}
