package kct.co.id.skilltest;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kct.co.id.skilltest.dto.AddressDTO;
import kct.co.id.skilltest.dto.UserDTO;
import kct.co.id.skilltest.entity.Address;
import kct.co.id.skilltest.entity.User;
import kct.co.id.skilltest.enumerate.Gender;
import kct.co.id.skilltest.model.UserPayload;
import kct.co.id.skilltest.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerTests {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    @LocalServerPort
    private int port;
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private UserRepository userRepository;

    private static void assertionsValidation(String heading, ResponseEntity<String> response, Map<String, Object> abstractResponse) {
        Assertions.assertAll(heading,
                () -> Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Http Status Code must be BAD REQUEST"),
                () -> Assertions.assertTrue(abstractResponse.containsKey("status"), "Response Body must be contains status"),
                () -> Assertions.assertTrue(abstractResponse.containsKey("message"), "Response Body must be contains message"),
                () -> Assertions.assertTrue(abstractResponse.containsKey("data"), "Response Body must be contains data")
        );
    }

    private void clearData() {
        userRepository.deleteAll();
    }

    @Test
    public void get() {
        String BASE_URL = String.format("http://localhost:%s/users", port);
        ResponseEntity<String> response = restTemplate
                .getForEntity(
                        BASE_URL,
                        String.class
                );
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void checkActiveStartDate() throws Exception {
        createInvalidUser();

        List<UserDTO> users = getUsers();

        boolean isInvalidActiveStartDate = users
                .stream()
                .anyMatch(userDTO -> userDTO.getActiveStartDate().isAfter(LocalDate.now()));
        Assertions.assertFalse(isInvalidActiveStartDate, "Active Start Date must be less than equals today");
    }

    @Test
    public void checkActiveEndDate() throws Exception {
        createInvalidUser();

        List<UserDTO> users = getUsers();

        boolean isInvalidActiveEndDate = users
                .stream()
                .anyMatch(userDTO -> userDTO.getActiveEndDate().isBefore(LocalDate.now()));
        Assertions.assertFalse(isInvalidActiveEndDate, "Active End Date must be greater than equals today");
    }

    @Test
    public void checkFullName() throws Exception {
        createInvalidUser();

        List<UserDTO> users = getUsers();

        boolean isFullNameNull = users
                .stream()
                .anyMatch(userDTO -> userDTO.getFullName() == null);
        Assertions.assertFalse(isFullNameNull, "Full Name must be combine first name and last name");
    }

    private List<UserDTO> getUsers() throws Exception {
        String BASE_URL = String.format("http://localhost:%s/users", port);

        ResponseEntity<String> response = restTemplate
                .getForEntity(
                        BASE_URL,
                        String.class
                );

        return OBJECT_MAPPER.convertValue(getData(response.getBody()), new TypeReference<>() {
        });
    }

    @Test
    public void create() {
        clearData();

        ResponseEntity<String> response = createUser(createUserPayload());

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
    }

    @Test
    public void delete() {
        User user = createInvalidUser();

        String BASE_URL = String.format("http://localhost:%s/users/%s", port, user.getId());

        ResponseEntity<String> response = restTemplate
                .exchange(
                        BASE_URL,
                        HttpMethod.DELETE,
                        HttpEntity.EMPTY,
                        String.class
                );

        Assertions.assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode(), "HTTP Status code must be no content");
    }

    private ResponseEntity<String> createUser(UserPayload UserPayload) {
        String BASE_URL = String.format("http://localhost:%s/users", port);

        HttpEntity<UserPayload> requestBody = new HttpEntity<>(UserPayload);

        return restTemplate
                .exchange(
                        BASE_URL,
                        HttpMethod.POST,
                        requestBody,
                        String.class
                );
    }

    private ResponseEntity<String> updateUser(Long userId, UserPayload UserPayload) {
        String BASE_URL = String.format("http://localhost:%s/users/%s", port, userId);

        HttpEntity<UserPayload> requestBody = new HttpEntity<>(UserPayload);

        return restTemplate
                .exchange(
                        BASE_URL,
                        HttpMethod.PUT,
                        requestBody,
                        String.class
                );
    }

    @Test
    public void editDuplicatePhoneNumber() throws Exception {
        User user = createInvalidUser();

        UserPayload payload = createUserPayload();
        payload.setEmail("john@kct.co.id");

        ResponseEntity<String> response = updateUser(user.getId(), payload);

        Map<String, Object> abstractResponse = OBJECT_MAPPER.readValue(response.getBody(), new TypeReference<>() {
        });

        assertionsValidation("Edit Duplicate Phone Number", response, abstractResponse);
    }

    @Test
    public void duplicatePhoneNumber() throws Exception {
        createInvalidUser();

        UserPayload payload = createUserPayload();
        payload.setEmail("john@kct.co.id");

        ResponseEntity<String> response = createUser(payload);

        Map<String, Object> abstractResponse = OBJECT_MAPPER.readValue(response.getBody(), new TypeReference<>() {
        });

        assertionsValidation("Duplicate Phone Number", response, abstractResponse);
    }

    @Test
    public void duplicateEmail() throws Exception {
        createInvalidUser();

        UserPayload payload = createUserPayload();
        payload.setPhoneNumber("14022");
        payload.setEmail("JOHNDOE@kct.co.id");

        ResponseEntity<String> response = createUser(payload);

        Map<String, Object> abstractResponse = OBJECT_MAPPER.readValue(response.getBody(), new TypeReference<>() {
        });

        assertionsValidation("Duplicate Email", response, abstractResponse);
    }

    @Test
    public void editDuplicateEmail() throws Exception {
        User user = createInvalidUser();

        UserPayload payload = createUserPayload();
        payload.setPhoneNumber("14022");
        payload.setEmail("JOHNDOE@kct.co.id");

        ResponseEntity<String> response = updateUser(user.getId(), payload);

        Map<String, Object> abstractResponse = OBJECT_MAPPER.readValue(response.getBody(), new TypeReference<>() {
        });

        assertionsValidation("Duplicate Email", response, abstractResponse);
    }

    @Test
    public void invalidColumns() throws Exception {
        List<String> columns = List.of("firstName", "email", "phoneNumber", "activeStartDate", "activeEndDate");
        for (String column : columns) {
            invalidColumn(column);
        }
    }

    private void invalidColumn(String columnName) throws Exception {
        clearData();

        UserPayload payload = createInvalidPayload(columnName);

        ResponseEntity<String> response = createUser(payload);

        Map<String, Object> abstractResponse = OBJECT_MAPPER.readValue(response.getBody(), new TypeReference<>() {
        });

        assertionsValidation(String.format("Invalid Column %s", columnName), response, abstractResponse);
    }

    @Test
    public void invalidLengthColumn() throws Exception {
        clearData();

        UserPayload payload = createInvalidLengthPayload("firstName");

        ResponseEntity<String> response = createUser(payload);

        Map<String, Object> abstractResponse = OBJECT_MAPPER.readValue(response.getBody(), new TypeReference<>() {
        });

        assertionsValidation(String.format("Invalid Length Column %s", "firstName"), response, abstractResponse);
    }

    @Test
    public void invalidEditLengthColumn() throws Exception {
        User user = createInvalidUser();

        UserPayload payload = createInvalidLengthPayload("lastName");

        ResponseEntity<String> response = updateUser(user.getId(), payload);

        Map<String, Object> abstractResponse = OBJECT_MAPPER.readValue(response.getBody(), new TypeReference<>() {
        });

        assertionsValidation(String.format("Invalid Edit Length Column %s", "lastName"), response, abstractResponse);
    }

    @Test
    public void editInvalidColumns() throws Exception {
        List<String> columns = List.of("firstName", "email", "phoneNumber", "activeStartDate", "activeEndDate");
        for (String column : columns) {
            editInvalidColumn(column);
        }
    }

    private void editInvalidColumn(String columnName) throws Exception {
        User user = createInvalidUser();

        UserPayload payload = createInvalidPayload(columnName);

        ResponseEntity<String> response = updateUser(user.getId(), payload);

        Map<String, Object> abstractResponse = OBJECT_MAPPER.readValue(response.getBody(), new TypeReference<>() {
        });

        assertionsValidation(String.format("Invalid Column %s", columnName), response, abstractResponse);
    }

    private Object getData(String responseBody) throws Exception {
        Map<String, Object> abstractResponse = OBJECT_MAPPER.readValue(responseBody, new TypeReference<>() {
        });
        return abstractResponse.get("data");
    }

    private User createInvalidUser() {
        clearData();
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setDateOfBirth(LocalDate.now().minusYears(17L));
        user.setActiveStartDate(LocalDate.now().plusMonths(1L));
        user.setActiveEndDate(LocalDate.now().minusMonths(1L));
        user.setEmail("johndoe@kct.co.id");
        user.setPhoneNumber("14045");
        user.setGender(Gender.MALE);
        Address address = new Address();
        address.setStreetAddress("NY");
        address.setState("NY");
        address.setCity("NY");
        address.setPostalCode("NY");
        address.setCountry("NY");
        user.setAddress(address);
        return userRepository.save(user);
    }

    private UserPayload createUserPayload() {
        UserPayload payload = new UserPayload();
        payload.setFirstName("John");
        payload.setLastName("Doe");
        payload.setDateOfBirth(LocalDate.now().minusYears(17L));
        payload.setActiveStartDate(LocalDate.now().minusDays(1L));
        payload.setActiveEndDate(LocalDate.now().plusYears(1L));
        payload.setEmail("johndoe@kct.co.id");
        payload.setPhoneNumber("14045");
        payload.setGender(Gender.MALE);
        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setStreetAddress("NY");
        addressDTO.setState("NY");
        addressDTO.setCity("NY");
        addressDTO.setPostalCode("NY");
        addressDTO.setCountry("NY");
        payload.setAddress(addressDTO);
        return payload;
    }

    private UserPayload createInvalidPayload(String column) {
        UserPayload userPayload = createUserPayload();
        Map<String, Object> payload = OBJECT_MAPPER.convertValue(userPayload, new TypeReference<>() {
        });
        payload.put(column, null);
        return OBJECT_MAPPER.convertValue(payload, UserPayload.class);
    }

    private UserPayload createInvalidLengthPayload(String column) {
        UserPayload userPayload = createUserPayload();
        Map<String, Object> payload = OBJECT_MAPPER.convertValue(userPayload, new TypeReference<>() {
        });
        payload.put(column, "Z!huyv/T)+#ekDhL5_A;UJ-bvX[UC)UVUipH!+)(SG@[dPN-CiR]?f:M90Tv=zX$$+MU&y2[d4F}hg]_-hHX7h!AW!bM:cr5B@H(jJ7tvB&vph.j1xQv*.GW3}CnY=3*z9#8[1dWP7A3:TqM;hQtF5*[Sm;e%yN.FCTcvq!zhxX.*)qMkCYS_KLyEER*{Kn-a-b7CXXLN4Pemuvc:Tk]Qa)/)4$dw%](5rek7Nt/e@,4F0WY4$3:Dz.;r8_uJZG#1r&!59;nhR?2#H8_6L/{2T!/!a}g+zf)[Bf(rANBw&U:");
        return OBJECT_MAPPER.convertValue(payload, UserPayload.class);
    }
}
