package com.quocbao.projectmanager.elasticsearch.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import com.quocbao.projectmanager.elasticsearch.entity.ProjectES;

@Repository
public interface ProjectESRepository extends ElasticsearchRepository<ProjectES, String> {

	@Query("{\"bool\" : {\"must\": [" +
	        "{\"term\" : {\"userId\": \"?0\"}}," +
	        "{\"term\" : {\"status\": \"?1\"}}" +
	        "]}}")
	Page<ProjectES> findByUserIdAndStatusAndPageable(String userId, String status, Pageable pageable);
	
	@Query("{\"bool\" : {\"must\": [" +
	        "{\"term\" : {\"userId\": \"?0\"}}," +
	        "{\"term\" : {\"status\": \"?1\"}}," +
	        "{ \"wildcard\": { \"name\": \"*?2*\"}}" +
	        "]}}")
	Page<ProjectES> findByUserIdAndStatusAndPageable(String userId, String status, String keySearch, Pageable pageable);
	
	@Query("{\"bool\" : {\"prefix\": {\"name\": \"?0\"}}}")
	Page<ProjectES> findBySearch(String keySearch, Pageable pageable);
}
