package com.hththn.dev.department_manager.service;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.hththn.dev.department_manager.dto.request.UserLoginDTO;
import com.hththn.dev.department_manager.dto.request.UserSsoDTO;
import com.hththn.dev.department_manager.dto.response.ResLoginDTO;
import com.hththn.dev.department_manager.exception.UserInfoException;
import com.hththn.dev.department_manager.repository.UserRepository;
import com.nimbusds.jose.shaded.gson.Gson;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.hththn.dev.department_manager.entity.User;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class AuthService {
    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    String googleClientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    String googleClientSecret;

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    String googleRedirectUri;

    @Value("${spring.security.oauth2.client.registration.google.user-info-uri}")
    String googleUserInfoUri;

    static String RESPONSE_TYPE = "code";
    static String SCOPE = "email profile";
//    static String FACEBOOK_SCOPE = "email public_profile";
    static String GOOGLE_AUTH_URL = "https://accounts.google.com/o/oauth2/auth";
//    static String FACEBOOK_AUTH_URL = "https://www.facebook.com/v21.0/dialog/oauth";

    final AuthenticationManagerBuilder authenticationManagerBuilder;
    final UserService userService;
    final UserRepository userRepository;
    final SecurityUtil securityUtil;

//    public AuthService(AuthenticationManagerBuilder authenticationManagerBuilder, UserService userService, UserRepository userRepository, SecurityUtil securityUtil) {
//        this.authenticationManagerBuilder = authenticationManagerBuilder;
//        this.userService = userService;
//        this.userRepository = userRepository;
//        this.securityUtil = securityUtil;
//    }

    // get user info
    public ResLoginDTO getLogin(UserLoginDTO loginDto) {
        // Put input including username/password into Security
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginDto.getUsername(), loginDto.getPassword());

        // authenticate user => need to define loadUserByUsername method
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // Put information into securitycontext if user logins successfully. Spring has already done it, but we can config in here.
        SecurityContextHolder.getContext().setAuthentication(authentication);

        ResLoginDTO res = new ResLoginDTO();

        // return user's info
        User currentUserDB = this.userService.getUserByUsername(loginDto.getUsername());
        if (currentUserDB != null) {
            ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(
                    currentUserDB.getId(),
                    currentUserDB.getEmail(),
                    currentUserDB.getName());
            res.setUser(userLogin);
        }

        // create a token
        String access_token = this.securityUtil.createAccessToken(authentication.getName(), res.getUser(), authentication.getAuthorities());
        res.setAccessToken(access_token);

        return res;
    }

    // get current user from security context
    public ResLoginDTO.UserLogin getUserLogin() {
        String email = SecurityUtil.getCurrentUserLogin().isPresent()
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";

        User currentUserDB = this.userService.getUserByUsername(email);
        ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin();
        if (currentUserDB != null) {
            userLogin.setId(currentUserDB.getId());
            userLogin.setEmail(currentUserDB.getEmail());
            userLogin.setName(currentUserDB.getName());
        }
        return userLogin;
    }

    // logout
    public ResponseCookie handleLogout() throws UserInfoException {
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "";

        if (email.isEmpty()) {
            throw new UserInfoException("Access token isn't valid");
        }

        // update refresh token = null
        this.userService.updateUserToken(null, email);

        // remove refresh token cookie
        return ResponseCookie
                .from("refresh_token", null)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .build();
    }

    public Map<String,Object> authenticatedAndFetchFrofile(String code, String loginType) throws IOException {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
        String accessToken;
        Gson gson = new Gson();

        try {
            switch (loginType.trim().toLowerCase()) {
                case "google":
                    GoogleAuthorizationCodeTokenRequest tokenRequest = new GoogleAuthorizationCodeTokenRequest(
                            new NetHttpTransport(),
                            new GsonFactory(),
                            googleClientId,
                            googleClientSecret,
                            code,
                            googleRedirectUri
                    );

                    // Kiểm tra và ghi log chi tiết trước khi gọi execute()
                    System.out.println("Exchanging code for access token...");
                    GoogleTokenResponse tokenResponse = tokenRequest.execute();
                    accessToken = tokenResponse.getAccessToken();

                    // Log access token (hoặc kiểm tra xem có giá trị hợp lệ không)
                    System.out.println("Access token received: " + accessToken);

                    // Thêm header Authorization với token vào yêu cầu RestTemplate
                    restTemplate.getInterceptors().add((req, body, executionContext) -> {
                        req.getHeaders().set("Authorization", "Bearer " + accessToken);
                        return executionContext.execute(req, body);
                    });

                    // Truy vấn thông tin người dùng
                    String userInfo = restTemplate.getForEntity(googleUserInfoUri, String.class).getBody();
                    return new ObjectMapper().readValue(userInfo, new TypeReference<Map<String, Object>>() {});

                default:
                    throw new IllegalArgumentException("Unsupported login type: " + loginType);
            }
        } catch (Exception e) {
            // Ghi log lỗi nếu có
            System.err.println("Error during token exchange: " + e.getMessage());
            e.printStackTrace();
            throw e; // Ném lại lỗi để xử lý ở cấp cao hơn
        }
    }


    public String generateAuthUrl(String loginType) {
        return switch (loginType) {
            case "google" -> generateGoogleAuthUrl();
//            case "facebook" ->
//                return generateFacebookAuthUrl();
            default -> throw new IllegalArgumentException("Unsupported login type: " + loginType);
        };
    }

    String generateGoogleAuthUrl() {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("client_id", googleClientId);
        queryParams.put("redirect_uri", googleRedirectUri);
        queryParams.put("response_type", RESPONSE_TYPE);
        queryParams.put("scope", SCOPE);

        return (String) buildUrlWithParams(GOOGLE_AUTH_URL, queryParams);
    }

    String buildUrlWithParams(String baseUrl, Map<String, String> params) {
        StringBuilder urlBuilder = new StringBuilder(baseUrl);
        urlBuilder.append("?");
        params.forEach((key, value) ->
                urlBuilder.append(key).append("=").append(value).append("&")
        );
        return urlBuilder.substring(0, urlBuilder.length() - 1);
    }

    public String googleLogin(UserSsoDTO userSsoDTO) throws Exception {
        Optional<User> optionalUser = Optional.empty();
        String subject = null;

        if(userSsoDTO.getGoogleAccountId() != null){
            optionalUser = userRepository.findByGoogleAccountId(userSsoDTO.getGoogleAccountId());
            subject = "Google:" + userSsoDTO.getGoogleAccountId();
            if(optionalUser.isEmpty()){
                User newUser = User.builder()
                        .name(userSsoDTO.getName() != null ? userSsoDTO.getName() : "")
                        .email(userSsoDTO.getEmail() != null ? userSsoDTO.getEmail() : "")
                        .googleAccountId(userSsoDTO.getGoogleAccountId())
                        .password("")
                        .authType("google")
                        .isActive(1)
                        .build();
                newUser = userRepository.save(newUser);
                optionalUser = Optional.of(newUser);
            }

            Map<String,Object> attributes = new HashMap<>();
            attributes.put("email", userSsoDTO.getEmail());
            ResLoginDTO res = new ResLoginDTO();
            ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(userRepository.findByEmail(userSsoDTO.getEmail()).getId(),userSsoDTO.getEmail(),userSsoDTO.getName());
            res.setUser(userLogin);
            return this.securityUtil.createRefreshToken(userSsoDTO.getEmail(),res); // todo: Hung fix
        }
        else throw new RuntimeException("Account is not found");
    }
}
