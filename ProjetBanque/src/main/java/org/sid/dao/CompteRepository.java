package org.sid.dao;

import org.sid.entities.Compte;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CompteRepository extends JpaRepository<Compte, String>{
	@Query ("select c from Compte c where c.client.code=:x order by dateCreation")
	public Page<Compte> listCompte(@Param("x")Long codeClt,Pageable pageabl);
}
