package org.sid.metier;

import org.sid.entities.Compte;
import org.sid.entities.Operation;

import java.util.ArrayList;

import org.sid.entities.Client;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

public interface IBanqueMetier {
	public Compte consulterCompte(String codeCpte);
	public Client consulterClient(Long code);
	public void verser(String codeCpte,double montant);
	public void retirer(String codeCpte,double montant);
	public void virement(String codeCpte1,String codeCpte2,double montant);
	public Page<Operation> listOperation(String codeCpte,int page,int size); 
	public Page<Compte> listCompte(Long codeClt,int page,int size);
	public Page<Client> listClient(int page,int size);
	public ArrayList<Client> listClient();
	public void insertClient(String nom,String mail);
	public void modifierClient(Long codeClient,String nvnom,String nvmail);
	public void ajouterCompteCourant(String codeCpte,Long codeClt,double decouvert);
	public void ajouterCompteEpargne(String codeCpte,Long codeClt,double taux);
	public Page<Client> clientParNom(String nom,int page,int size);
	public boolean compteExistant(String c);
}
