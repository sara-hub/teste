package org.sid;

import org.sid.dao.ClientRepository;
import org.sid.dao.CompteRepository;
import org.sid.dao.OperationRepository;
import org.sid.metier.IBanqueMetier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ProjetBanqueApplication implements CommandLineRunner{

	@Autowired
	private ClientRepository clientRepository;
	@Autowired
	private CompteRepository compteRepository;
	@Autowired
	private OperationRepository operationRepository;
	
	@Autowired
	private IBanqueMetier banqueMetier;
	public static void main(String[] args) {
		SpringApplication.run(ProjetBanqueApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		/*Client c1=clientRepository.save(new Client("sara", "sarajbaida@gmail.com"));
		Client c2=clientRepository.save(new Client("chaimae", "chaimaejbaida@gmail.com"));
		Client c3=clientRepository.save(new Client("zakariae", "zakariajbaida@gmail.com"));
		Client c4=clientRepository.save(new Client("redouane", "redouanejbaida@gmail.com"));
		
		Compte cp1=compteRepository.save(new CompteCourant("c1",new Date(),90000,c1,6000));
		Compte cp2=compteRepository.save(new CompteEpargne("c4",new Date(),6000,c4,5.5));
		
		operationRepository.save(new Versement(new Date(), 9000, cp1));
		operationRepository.save(new Versement(new Date(), 6000, cp1));
		operationRepository.save(new Versement(new Date(), 4000, cp1));
		operationRepository.save(new Retrait(new Date(), 9000, cp1));
		operationRepository.save(new Versement(new Date(), 9000, cp2));
		operationRepository.save(new Versement(new Date(), 6000, cp2));
		operationRepository.save(new Versement(new Date(), 4000, cp2));
		operationRepository.save(new Retrait(new Date(), 9000, cp2));
	
		banqueMetier.verser("c1", 111);
		banqueMetier.verser("c4", 111);*/
	
	}

}

