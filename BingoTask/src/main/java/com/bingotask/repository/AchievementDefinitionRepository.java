package com.bingotask.repository;

import com.bingotask.model.AchievementDefinition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AchievementDefinitionRepository extends JpaRepository<AchievementDefinition, Long> {

    List<AchievementDefinition> findByIsActiveTrue();

    List<AchievementDefinition> findByCategory(String category);

    List<AchievementDefinition> findByIsRareTrue();

    List<AchievementDefinition> findByCriteriaType(String criteriaType);

    AchievementDefinition findByName(String name);
}