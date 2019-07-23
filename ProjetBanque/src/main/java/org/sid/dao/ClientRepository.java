package org.sid.dao;

import java.util.ArrayList;

import org.sid.entities.Client;
import org.sid.entities.Compte;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ClientRepository extends JpaRepository<Client, Long>{
	@Query ("select c from Client c order by nom")
	public Page<Client> listClient(Pageable pageabl);
	@Query ("select c from Client c order by code")
	public ArrayList<Client> listClient();
	@Query ("select c from Client c where c.nom like concat(concat('%',:x),'%') order by nom")
	public Page<Client> clientParNom(@Param("x")String nom,Pageable pageabl);


}
