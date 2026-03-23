package com.Sorensen.FitMark.repository;

import com.Sorensen.FitMark.entity.Split;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SplitRepository extends JpaRepository<Split, UUID> {

    public List<Split> findByUserId(UUID userId);

}
