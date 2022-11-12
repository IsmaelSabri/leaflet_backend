package com.maps.dao;

import com.maps.entities.Edificio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IEdificioRepository extends JpaRepository<Edificio, Long> {
}
