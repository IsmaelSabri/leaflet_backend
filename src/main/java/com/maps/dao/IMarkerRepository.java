package com.maps.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.maps.entities.Marker;
@Repository
public interface IMarkerRepository extends JpaRepository<Marker, Long>{

}
