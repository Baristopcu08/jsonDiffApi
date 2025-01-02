package com.JsonDiffApi.repository;

import com.JsonDiffApi.entity.JsonEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JsonRepository extends JpaRepository<JsonEntity, String> {
}
