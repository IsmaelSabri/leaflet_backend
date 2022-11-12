package com.maps.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.maps.entities.Usuario;

@Repository
public interface IUsuarioRepository extends JpaRepository<Usuario, Long> {
	Usuario findUsuarioByUsername(String nombre); // username

	Usuario findUsuarioByEmail(String email); //
}
