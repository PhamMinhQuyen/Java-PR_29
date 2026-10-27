package com.library.dto.response;

import com.library.domain.model.Member;

import java.time.LocalDateTime;

public class MemberResponse {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private boolean active;
    private LocalDateTime registeredAt;

    public MemberResponse() {
    }

    public static MemberResponse fromEntity(Member member) {
        if (member == null) return null;
        MemberResponse res = new MemberResponse();
        res.id = member.getId();
        res.name = member.getName();
        res.email = member.getEmail();
        res.phone = member.getPhone();
        res.active = member.isActive();
        res.registeredAt = member.getRegisteredAt();
        return res;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(LocalDateTime registeredAt) {
        this.registeredAt = registeredAt;
    }
}
