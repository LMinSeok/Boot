package com.membertest.service;

import com.membertest.entity.Member;
import com.membertest.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Log4j2
public class MemberService {

    private final MemberRepository memberRepository;

    //CRUD -> insert,update
    public void register(Member member){
        log.info("register..or update......");
        memberRepository.save(member);
    }

    //CRUD -> delete
    public void delete(long memberId){
        log.info("delete........");
        memberRepository.deleteById(memberId);
    }

    //CRUD -> readOne
    public Member readOne(long memberId){
        log.info("readOne.........");

        Member member =
                memberRepository.findById(memberId).orElseThrow(() -> new EntityNotFoundException());
        return member;
    }

    //ReadList
    public List<Member> readList(){
       log.info("ReadList.............");

        return memberRepository.findAll();
    }

    //paging
    public Page<Member> readPaging(Pageable pageable){

        log.info("readpagging..........");

        return memberRepository.findAll(pageable);

    }

}
