package com.shop.controller;

import com.shop.entity.Member;
import com.shop.entity.Order;
import com.shop.service.MemberService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Log4j2
@Controller
@RequestMapping("/admin")
public class AdminMemberController {

    @Autowired
    private MemberService memberService;

@GetMapping("/members")
public String listMembers(@RequestParam(defaultValue = "0") int page, Model model) {
    int pageSize = 5;
    Pageable pageable = PageRequest.of(page, pageSize);
    Page<Member> memberPage = memberService.getMembers(pageable);

    model.addAttribute("members", memberPage.getContent());
    model.addAttribute("currentPage", page);
    model.addAttribute("totalPages", memberPage.getTotalPages());

    return "admin/members/list";
}



    @GetMapping("/members/detail/{page}")
    public String getMember(Model model, Principal principal, @PathVariable ("page") Optional<Integer> page) {
        log.info("principal:" + principal.getName());
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 4);
        List<Order> orders = memberService.findOrdersByMemberId2(11L, pageable);
        log.info("orders:" + orders);

        model.addAttribute("member", principal.getName());
        model.addAttribute("orders", orders);
        return "admin/members/detail";

    }

    @GetMapping("/new")
    public String newMemberForm(Model model) {
        model.addAttribute("member", new Member());
        return "admin/members/form";
    }

    @PostMapping
    public String saveMember(@ModelAttribute Member member) {
        memberService.savaMember(member);
        return "redirect:/admin/members";
    }

    @GetMapping("/{id}/edit")
    public String editMemberForm(@PathVariable Long id, Model model) {
        Member member = memberService.getMemberById(id);
        model.addAttribute("member", member);
        return "admin/members/form";
    }

    @PostMapping("/{id}/update")
    public String updateMember(@PathVariable Long id, @ModelAttribute Member member) {
        member.setId(id);
        memberService.savaMember(member);
        return "redirect:/admin/members";
    }

    @PostMapping("/members/{email}/delete")
    public String deleteMember(@PathVariable String email) {
        memberService.deleteMember(email);
        return "redirect:/admin/members";
    }


}
