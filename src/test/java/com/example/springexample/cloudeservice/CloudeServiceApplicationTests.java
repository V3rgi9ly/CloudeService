package com.example.springexample.cloudeservice;

import com.example.springexample.cloudeservice.dto.UsersDTO;
import com.example.springexample.cloudeservice.dto.UsersSignUpDto;
import com.example.springexample.cloudeservice.exception.exceptionClass.DatabaseConnectionException;
import com.example.springexample.cloudeservice.exception.exceptionClass.NoUserException;
import com.example.springexample.cloudeservice.exception.exceptionClass.UserAlreadyExistsException;
import com.example.springexample.cloudeservice.model.Users;
import com.example.springexample.cloudeservice.repository.UsersRepository;
import com.example.springexample.cloudeservice.service.AuthService;
import jakarta.transaction.Transactional;
import org.apache.catalina.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
@Transactional
class CloudeServiceApplicationTests {

    @Container
    public static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17-alpine");


    @DynamicPropertySource
    static void configureProoerties(DynamicPropertyRegistry registry) {
        registry.add("jdbc:postgresql://localhost:5432/cloude", postgres::getJdbcUrl);
        registry.add("postgres", postgres::getUsername);
        registry.add("Serega9900", postgres::getPassword);
    }


    @Autowired
    private AuthService authService;

    @Autowired
    private UsersRepository usersRepository;


    @Test
    @DisplayName("check System Test")
    void contextLoads() {
        assertThat(postgres.isRunning()).isTrue();
        assertThat(postgres.getJdbcUrl()).isNotEmpty();
    }

    @Test
    @DisplayName("Sign Up Test")
    void signUpTest() {
        UsersSignUpDto user = new UsersSignUpDto("Pridok", "Pozalak");
        UsersDTO usersDTO = authService.signUp(user);

        Users users = usersRepository.findByUsername(user.username());

        assertEquals(users.getUsername(), usersDTO.username());
    }


    @Test
    @DisplayName("username is taken test")
    void usernameIsTakerTest() {
        UsersSignUpDto user = new UsersSignUpDto("Provodka", "Tidaun");
        UsersDTO usersDTO = authService.signUp(user);

        UsersSignUpDto user_two = new UsersSignUpDto("Provodka", "Tidaun");
        assertThrows(UserAlreadyExistsException.class, () -> {
            authService.signUp(user_two);
        });
    }


    @Test
    @DisplayName("sign in Test")
    void signInTest() {
        UsersSignUpDto user = new UsersSignUpDto("Provodka", "Tidaun");
        UsersDTO usersDTO = authService.signUp(user);


        UsersDTO usersDTO1 = authService.validateUser(user);

        assertEquals(usersDTO1.username(), usersDTO.username());
    }

    @Test
    @DisplayName("no user with this username Test")
    void noUserWithThisUsernameTest() {
        UsersSignUpDto user = new UsersSignUpDto("Provodka", "Tidaun");

        assertThrows(NoUserException.class, ()->{authService.validateUser(user);});

    }
}
