package com.speakeasy.controller.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.speakeasy.domain.User;
import com.speakeasy.repository.UserRepository;
import com.speakeasy.request.UserSignIn;
import com.speakeasy.request.UserSignUp;
import com.speakeasy.service.SignService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class SignControllerTest {


    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SignService signService;

    @BeforeEach
    void clean(){
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("로그인을 실행한다")
    void signIn() throws Exception{
        UserSignUp user = UserSignUp
                .builder()
                .email("id@naver.com")
                .password("1234")
                .name("이름")
                .build();
        signService.join(user);
        Assertions.assertEquals(1, userRepository.count());
        UserSignIn request = UserSignIn
                .builder()
                .email("id@naver.com")
                .password("1234")
                .build();
        String json = objectMapper.writeValueAsString(request);
        mockMvc .perform(post("/signin")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("로그아웃을 실행한다")
    void signOut() throws Exception{
        //회원가입
        UserSignUp user = UserSignUp
                .builder()
                .email("id@naver.com")
                .password("1234")
                .name("이름")
                .build();
        signService.join(user);
        Assertions.assertEquals(1, userRepository.count());
        UserSignIn request = UserSignIn
                .builder()
                .email("id@naver.com")
                .password("1234")
                .build();
        String json = objectMapper.writeValueAsString(request);

        //로그인
        MvcResult result = mockMvc .perform(post("/signin")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isOk())
                .andReturn();

        //로그아웃
        mockMvc .perform(post("/signout")
                        .cookie(result.getResponse().getCookies()))
                .andExpect(status().isOk())
                .andDo(print());


    }

    @Test
    @DisplayName("회원가입을 실행한다")
    void signup() throws Exception{
        UserSignUp request = UserSignUp
                .builder()
                .email("id@naver.com")
                .password("1234")
                .name("이름")
                .build();
        String json = objectMapper.writeValueAsString(request);
        mockMvc .perform(post("/signup")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isOk())
                .andDo(print());
        Assertions.assertEquals(1, userRepository.count());
        User user = userRepository.findAll().get(0);

        assertEquals("id@naver.com",user.getEmail());
        assertEquals("이름",user.getName());

    }

    @Test
    @DisplayName("access token 재발급을 실행한다")
    void reissue() throws Exception{
        UserSignUp user = UserSignUp
                .builder()
                .email("id@naver.com")
                .password("1234")
                .name("이름")
                .build();
        signService.join(user);
        UserSignIn request = UserSignIn
                .builder()
                .email("id@naver.com")
                .password("1234")
                .build();
        String json = objectMapper.writeValueAsString(request);

        MvcResult result = mockMvc .perform(post("/signin")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isOk())
                .andReturn();

        //reissue
        mockMvc .perform(get("/reissue")
                        .cookie(result.getResponse().getCookies()))
                .andExpect(status().isOk())
                .andDo(print());

    }

    @Test
    @DisplayName("이메일 중복을 확인한다")
    void checkEmail() throws Exception{
        UserSignUp user = UserSignUp
                .builder()
                .email("id@naver.com")
                .password("1234")
                .name("이름")
                .build();
        signService.join(user);
        mockMvc .perform(get("/signup/check-email?email=id@naver.com"))
                .andExpect(status().isConflict())
                .andDo(print());


    }
}