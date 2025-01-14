package com.quocbao.projectmanager.entity;

import java.io.Serializable;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "member_group")
@Setter
@Getter
@AllArgsConstructor
@Builder
public class Member implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;
    
    @CreationTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "joined_at")
	private LocalDateTime createdAt;
    
    public enum Role {
    	ADMIN,
    	MEMBER
    }
    
    public Member() {
    	
    }
    
    public Member(UUID groupId, UUID userId, Role role) {
    	this.group = Group.builder().id(groupId).build();
    	this.user = User.builder().id(userId).build();
    	this.role = role;
    }
}
