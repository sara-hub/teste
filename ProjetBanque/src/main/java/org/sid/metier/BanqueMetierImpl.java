package org.sid.metier;

import java.util.ArrayList;
import java.util.Date;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

import org.sid.dao.ClientRepository;
import org.sid.dao.CompteRepository;
import org.sid.dao.OperationRepository;
import org.sid.entities.Client;
import org.sid.entities.Compte;
import org.sid.entities.CompteCourant;
import org.sid.entities.CompteEpargne;
import org.sid.entities.Operation;
import org.sid.entities.Retrait;
import org.sid.entities.Versement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
@Transactional
public class BanqueMetierImpl implements IBanqueMetier{
	@Autowired
	private CompteRepository compteRepository;
	@Autowired
	private OperationRepository operationRepository;
	@Autowired
	private ClientRepository clientRepository;
	@Override
	public Compte consulterCompte(String codeCpte) {
		Compte cp=compteRepository.findOne(codeCpte);
		if(cp==null)throw new RuntimeException("compte introuvable");
		return cp;
	}
	@Override
	public void verser(String codeCpte, double montant) {
		Compte cp=consulterCompte(codeCpte);
		Versement v=new Versement(new Date(), montant, cp);
		operationRepository.save(v);
		cp.setSolde(cp.getSolde()+montant);
		compteRepository.save(cp);
		
	}

	@Override
	public void retirer(String codeCpte, double montant) {
		Compte cp=consulterCompte(codeCpte);
		double fc=0;
		if (cp instanceof CompteCourant)
			fc=((CompteCourant)cp).getDecouvert();
		if(cp.getSolde()+fc<montant)
			throw new RuntimeException("solde insuffisant");
		Retrait r=new Retrait(new Date(), montant, cp);
		operationRepository.save(r);
		cp.setSolde(cp.getSolde()-montant);
		compteRepository.save(cp);	
	}
	@Override
	public void virement(String codeCpte1, String codeCpte2, double montant) {
		if(codeCpte1.equals(codeCpte2))
			throw new RuntimeException("On ne peut pas effectuer un virement sur le même compte!");
		retirer(codeCpte1, montant);
		verser(codeCpte2, montant);
		
	}

	@Override
	public Page<Operation> listOperation(String codeCpte, int page, int size) {
		return operationRepository.listOperation(codeCpte,new PageRequest(page, size));
	}
	@Override
	public Client consulterClient(Long code) {
		Client cl=clientRepository.findOne(code);
		if(cl==null)throw new RuntimeException("le client "+code+" n'existe pas");
		return cl;
	}

	@Override
	public Page<Compte> listCompte(Long codeClt, int page, int size) {
		return compteRepository.listCompte(codeClt,new PageRequest(page, size));
	}

	@Override
	public Page<Client> listClient(int page, int size) {
		return clientRepository.listClient(new PageRequest(page, size));
	}

	@Override
	public void insertClient(String nom, String mail) {
			Client c=new Client(nom,mail);
			clientRepository.save(c);}
	@Override
	public void modifierClient(Long codeClient, String nvnom, String nvmail) {
		Client c=clientRepository.findOne(codeClient);
		if(!nvnom.isEmpty())
			c.setNom(nvnom);
		if(!nvmail.isEmpty())
			c.setMail(nvmail);			}
	@Override
	public void ajouterCompteCourant(String codeCpte, Long codeClt,double decouvert) {
		Client c=clientRepository.findOne(codeClt);
		CompteCourant cc=new CompteCourant(codeCpte, new Date(), 0, c, decouvert);
		compteRepository.save(cc);	}
	@Override
	public void ajouterCompteEpargne(String codeCpte, Long codeClt, double taux) {
		Client c=clientRepository.findOne(codeClt);
		CompteEpargne ce=new CompteEpargne(codeCpte, new Date(), 0, c, taux);
		compteRepository.save(ce);
	}

	@Override
	public Page<Client> clientParNom(String nom, int page, int size) {
		return clientRepository.clientParNom(nom, new PageRequest(page, size));

	}

	@Override
	public ArrayList<Client> listClient() {
		return clientRepository.listClient();
	}

	@Override
	public boolean compteExistant(String c) {
		Compte cpt=compteRepository.findOne(c);
		if(cpt==null)
			return false;
		return true;
	}
}
