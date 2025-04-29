package org.example.icatch.repository;

import org.example.icatch.model.GestureAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GestureActionRepository extends JpaRepository<GestureAction, Integer> {
    List<GestureAction> findBySelectedFunction(GestureAction.SelectedFunction selectedFunction);
}