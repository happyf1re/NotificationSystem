package com.muravlev.notificationsystem.invitation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvitationRepository extends JpaRepository<Invitation, Integer> {
    List<Invitation> findByInviteeId(Integer userId);

    List<Invitation> findAllByInviteeId(Integer userId);
}
