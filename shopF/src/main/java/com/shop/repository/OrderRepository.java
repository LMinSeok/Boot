package com.shop.repository;

import com.shop.entity.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("select o from Order o where o.member.email = :email order by o.orderDate desc")
    List<Order> findOrders(@Param("email")String email, Pageable pageable);

    List<Order>findOrdersByMemberEmail(String email);

    @Query("select o.id, o.regTime, o.orderDate from Order o where o.member.id = :memberId")
    List<Order>findOrders2(@Param("memberId")Long memberId, Pageable pageable);

    @Query("select count(o) from Order o where o.member.email = :email")
    Long countOrder(@Param("email")String email);

}
