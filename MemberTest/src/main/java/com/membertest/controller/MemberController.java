package com.membertest.controller;

import com.membertest.entity.Member;
import com.membertest.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/read/{id}")
    public String readOne(@PathVariable("id") Long id){
        Member member = memberService.readOne(id);

        log.info("member : " + member);
        return null;
    }

    @GetMapping("/list")
    public String readList(Model model){
        log.info("readList........");
        List<Member> members = memberService.readList();

        log.info(members);
        model.addAttribute("members", members);

        return "member/list";
    }

    @GetMapping("/new")
    public String newGet(Model model){
        model.addAttribute("member", new Member());
        return "member/newForm";
    }

    //멤버 등록 or 수정
    @PostMapping(value = {"/new", "/edit"})
    public String newPost(@ModelAttribute Member member){
        log.info("newPost : " + member);
        //회원 저장...
        memberService.register(member);

        return "redirect:/member/list";
    }

    //수정
    //<a th:href="@{/member/edit/{id}(id=${member.id})}">수정</a>&nbsp;&nbsp;
    @GetMapping("/edit/{id}")
    public String updateGet(@PathVariable("id")Long id, Model model){
        Member member = memberService.readOne(id);
        model.addAttribute("member", member);

        return "member/updateForm";
    }

    @PostMapping("/delete/{id}")
    public String deletePost(@PathVariable("id") Long id){
        memberService.delete(id);
        return "redirect:/member/list";
    }

    //페이징 처리
    @GetMapping("/list2")
    public String listPage(
         @RequestParam(name = "page",defaultValue = "0") int page,
         @RequestParam(name = "size", defaultValue = "5") int size,
         Model model
                           ) {
        Pageable pageable = PageRequest.of(page, size);

        Page<Member> memberPage = memberService.readPaging(pageable);

        model.addAttribute("memberPage", memberPage);

        return "member/list2";
    }

}
