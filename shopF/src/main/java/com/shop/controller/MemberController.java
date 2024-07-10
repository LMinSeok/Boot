package com.shop.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.shop.dto.MemberFormDto;
import com.shop.entity.Member;
import com.shop.service.MemberService;
import com.shop.service.oAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller
@Log4j2
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;
    private final oAuthService oAuthService;


    @GetMapping("/members/new")
    public String newMember(Model model) {
        model.addAttribute("memberFormDto", new MemberFormDto());

        return "member/memberForm";
    }

    //PRG방식
    @PostMapping("/members/new")
    public String memberForm(@Valid MemberFormDto memberFormDto,
                             BindingResult bindingResult , Model model) {

        if(bindingResult.hasErrors()) {
            return "member/memberForm";
        }

        try {
            //MemberFormDto >>> Member Entity 변환
            Member member = Member.createMember(memberFormDto, passwordEncoder);
            //member 객체 저장(회원저장)
            memberService.savaMember(member);
        }catch (IllegalStateException e){  //중복회원 입력시 예외 발생
            model.addAttribute("errorMessage", e.getMessage());
            return "member/memberForm";
        }

        return "redirect:/";
    }

    @GetMapping("/members/login")
    public String loginMember(){
        return "member/memberLoginForm";
    }

    @GetMapping("/members/login/error")
    public String loginError(Model model){
        model.addAttribute("loginErrorMsg", "아이디 또는 비밀번호를 확인해주세요.");
        return "member/memberLoginForm";
    }

    @GetMapping(value="/oauth/kakao")
    public String kakaoConnect() {

        String redirect_uri = "http://localhost:8181/oauth/kakao/callback";

        log.info("카카오로 로그인 작동");
        StringBuffer url = new StringBuffer();
        url.append("https://kauth.kakao.com/oauth/authorize?");
        url.append("client_id=99d7689ed042fc6f9df444ad8dd8214c");
        url.append("&redirect_uri=" + redirect_uri);
        url.append("&response_type=code");
        return "redirect:" + url.toString();
    }

    @GetMapping("/oauth/kakao/callback")
    public String kakaoCallback(String code, HttpSession session) {
        log.info("콜백작동...");
        log.info("code:" + code);
        // SETP1 : 인가코드 받기
        // (카카오 인증 서버는 서비스 서버의 Redirect URI로 인가 코드를 전달합니다.)
        // System.out.println(code);

        // STEP2: 인가코드를 기반으로 토큰(Access Token) 발급
        String accessToken = null;
        try {
            accessToken = oAuthService.getAccessToken(code);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        //System.out.println("엑세스 토큰  "+accessToken);

        // STEP3: 토큰를 통해 사용자 정보 조회
        Member member = null;
        try {
            member = oAuthService.getKakaoInfo(accessToken);

            log.info("member값! :" + member);

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        //System.out.println("이메일 확인 "+kakaoInfo.getEmail());

        // STEP4: 카카오 사용자 정보 확인
        Member member1 = oAuthService.ifNeedKakaoInfo(member);

        log.info("member1값!" + member1);

        // STEP5: 강제 로그인
        // 세션에 회원 정보 저장 & 세션 유지 시간 설정
        if (member1 != null) {
            session.setAttribute("loginMember", member1);
            // session.setMaxInactiveInterval( ) : 세션 타임아웃을 설정하는 메서드
            // 로그인 유지 시간 설정 (1800초 == 30분)
            session.setMaxInactiveInterval(60 * 30);
            // 로그아웃 시 사용할 카카오토큰 추가
            session.setAttribute("kakaoToken", accessToken);
        }

        return "redirect:/";
    }

}
