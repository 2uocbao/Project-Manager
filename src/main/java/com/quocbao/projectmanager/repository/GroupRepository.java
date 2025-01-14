package com.quocbao.projectmanager.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.quocbao.projectmanager.entity.Group;

import io.lettuce.core.dynamic.annotation.Param;
import jakarta.persistence.Tuple;

@Repository
public interface GroupRepository extends JpaRepository<Group, UUID>, JpaSpecificationExecutor<Group> {

	@Query(value = """
			SELECT
			    HEX(gm.id) AS uuid_str,
			    (
			        SELECT GROUP_CONCAT(u2.first_name SEPARATOR ' ')
			        FROM user u2
			        WHERE u2.id = (
			            SELECT mg.user_id
			            FROM member_group mg, group_member gm
			            WHERE mg.user_id != u.id
			        )
			    ) AS first_names,
			    (
			        SELECT m.message
			        FROM message m, group_member gm
			        WHERE m.group_id = gm.id
			        ORDER BY m.created_at DESC
			        LIMIT 1
			    ) AS last_message,
			    (
			        SELECT m.created_at
			        FROM message m, group_member gm
			        WHERE m.group_id = gm.id
			        ORDER BY m.created_at DESC
			        LIMIT 1
			    ) AS last_message_time
			FROM user u, group_member gm
			WHERE u.id = UNHEX(REPLACE(:userId, '-', ''))
			""", nativeQuery = true)

	Page<Tuple> getGroupsOfUser(@Param("userId") String userId, Pageable pageable);

}
