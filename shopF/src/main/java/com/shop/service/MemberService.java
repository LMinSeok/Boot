package com.shop.service;

import com.shop.entity.Member;
import com.shop.entity.Order;
import com.shop.repository.MemberRepository;
import com.shop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class MemberService implements UserDetailsService {

    @Autowired
    private final MemberRepository memberRepository;

    @Autowired
    private final OrderRepository orderRepository;

//    public List<Member> getAllMembers() {
//        return memberRepository.findAll();
//    }

    public Page<Member> getMembers(Pageable pageable) {
        return memberRepository.findAll(pageable);
    }

    public Member getMemberById(Long id) {
        return memberRepository.findById(id).orElse(null);
    }

    public Member savaMember(Member member) {
        validdateDuplicatemember(member);
        return memberRepository.save(member);
    }



    public void deleteMember(String email) {
        Member member = memberRepository.findByEmail(email);

        if (member == null) {
            throw new IllegalArgumentException("Member not found with email: " + email);
        }

        memberRepository.delete(member);
    }

    private void validdateDuplicatemember(Member member) {
        Member findMember = memberRepository.findByEmail(member.getEmail());

        if (findMember != null) {
            throw new IllegalStateException("이미 가입된 회원입니다.");
        }
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info("--------------------loadUserByUsername---------------------------------");
        log.info(email);
        Member member = memberRepository.findByEmail(email);

        if (member == null) {
            throw new UsernameNotFoundException(email);
        }

        return User.builder()
                .username(member.getEmail())
                .password(member.getPassword())
                .roles(member.getRole().toString())
                .build();
    }

    public List<Order> findOrdersByMemberId2(Long memberId, Pageable pageable) {

        log.info("작동");
        List<Order> orders= orderRepository.findOrders2(memberId, pageable);
        log.info("orders멤버쪽:" + orders);
        return orders;
    }

    public Long countOrdersByMemberEmail(String email) {
        return orderRepository.countOrder(email);
    }

}
