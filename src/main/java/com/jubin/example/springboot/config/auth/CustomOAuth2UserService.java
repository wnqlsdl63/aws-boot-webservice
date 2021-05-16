package com.jubin.example.springboot.config.auth;

import com.jubin.example.springboot.config.auth.dto.OAuthAttributes;
import com.jubin.example.springboot.config.auth.dto.SessionUser;
import com.jubin.example.springboot.domain.user.User;
import com.jubin.example.springboot.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.Collections;

@RequiredArgsConstructor
@Service
@Slf4j
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final UserRepository userRepository;
    private final HttpSession httpSession;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        System.out.println("loadUser in");
        OAuth2UserService delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        // 로그인 진행중인 서비스를 구분하는 코드(e.g google, naver, kakao etc..)
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        logger.debug("[CustomOAuth2UserService] registrationId = {}",registrationId);
        System.out.println("registrationId = "+registrationId);

        /* OAuth2 로그인시 PK값, 구글은 기본코드 'sub'를 지원하지만 네이버 카카오는 지원하지 않는다.
         이후 네이버 로그인과 구글 로그인을 동시 지원할떄 사용한다.*/
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails()
                .getUserInfoEndpoint().getUserNameAttributeName();
        System.out.println("userNameAttributeName = "+userNameAttributeName);

        // OAuth2UserSerivce를 통해 가져온 OAuth2User의 attribute를 담을 클래스
        OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName,
                oAuth2User.getAttributes());

        User user = saveOrupdate(attributes);

        // 세션에 사용자 정보를 저장하기 위한 DTO 클래스
        httpSession.setAttribute("user", new SessionUser(user));

        return new DefaultOAuth2User(Collections.singleton(new SimpleGrantedAuthority(user.getRoleKey())),
                attributes.getAttributes(),
                attributes.getNameAttributeKey());


    }

    private User saveOrupdate(OAuthAttributes attributes) {
        User user = userRepository.findByEmail(attributes.getEmail())
                .map(entity -> entity.update(attributes.getName(), attributes.getPicture()))
                .orElse(attributes.toEntity());

        return userRepository.save(user);
    }
}
