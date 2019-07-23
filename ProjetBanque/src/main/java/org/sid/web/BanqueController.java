package org.sid.web;

import java.util.ArrayList;

import org.sid.dao.CompteRepository;
import org.sid.entities.Client;
import org.sid.entities.Compte;
import org.sid.entities.Operation;
import org.sid.metier.IBanqueMetier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class BanqueController {
	@Autowired
	private IBanqueMetier banqueMetier;

	@RequestMapping("/operations")
	public String index() {
		return "comptes";
	}

	/*
	 * cette méthode permet d'ajouter un compte, pour les numéros des clients, ils
	 * sont présentés sous forme d'une liste déroulante pour empêcher toute
	 * association a un client inexistant
	 */ @RequestMapping("/ajouterCompte")
	public String ajouterCompte(Model model, String cCompte, Long cClient, String typeCompte,
			@RequestParam(name = "valeurDecouvert", defaultValue = "600") Double valeurDecouvert,
			@RequestParam(name = "valeurTaux", defaultValue = "5.5") Double valeurTaux) {
		ArrayList<Client> pc = banqueMetier.listClient();// recuperer la liste de tous les clients
		model.addAttribute("cl", pc.clone());// mettre la liste des clients dans un attribut cl
		try {
			if (banqueMetier.compteExistant(cCompte)) {
				model.addAttribute("existant", "Echec d'ajout, compte existant");
				return "ajouterCompte";
			}
		} catch (Exception e) {
		}
		try {
			if (typeCompte.equals("CC"))// choix du compte courant: afficher champs du texte pour saisir le decouvert
			{
				model.addAttribute("msg", "Compte courant ajouté avec succès");
				banqueMetier.ajouterCompteCourant(cCompte, cClient, (double) valeurDecouvert);
			}
			if (typeCompte.equals("CE")) {
				model.addAttribute("msg", "Compte epargne ajouté avec succès");// choix du compte courant: afficher
																				// champs du texte pour saisir le taux
				banqueMetier.ajouterCompteEpargne(cCompte, cClient, (double) valeurTaux);
			}
		} catch (Exception e) {
		}

		return "ajouterCompte";
	}

	/*
	 * cette méthode permet de chercher un compte
	 */ @RequestMapping(value = "/consulterCompte")
	public String consulter(Model model, String codeCompte, @RequestParam(name = "page", defaultValue = "0") int page,
			@RequestParam(name = "size", defaultValue = "5") int size) {
		model.addAttribute("codeCompte", codeCompte);
		try {
			Compte cp = banqueMetier.consulterCompte(codeCompte);
			Page<Operation> pageOperation = banqueMetier.listOperation(codeCompte, page, size);
			model.addAttribute("compte", cp);
			model.addAttribute("listOperations", pageOperation.getContent());
			int[] pages = new int[pageOperation.getTotalPages()];
			model.addAttribute("pages", pages);
		} catch (Exception e) {
			model.addAttribute("exception", e);
		}
		return "comptes";
	}

	/*
	 * Méthode permet de chercher un client par code,et à droite on affiche la liste
	 * de tous les clients
	 */
	@RequestMapping(value = "/consulterClient")
	public String consulterClient(Model model, Long codeClient,
			@RequestParam(name = "page", defaultValue = "0") int page,
			@RequestParam(name = "size", defaultValue = "10") int size) {
		model.addAttribute("codeClient", codeClient);
		Page<Client> pc = banqueMetier.listClient(page, size);// liste de tous les clients
		model.addAttribute("allClient", pc.getContent());
		int[] pages = new int[pc.getTotalPages()];
		model.addAttribute("pagesClient", pages);
		try {
			Client cl = banqueMetier.consulterClient(codeClient);// l'objet client associé au code client
			Page<Compte> pageClient = banqueMetier.listCompte(codeClient, page, size);// liste des comptes de ce client
			model.addAttribute("client", cl);
			model.addAttribute("listComptes", pageClient.getContent());
		} catch (Exception e) {
			model.addAttribute("ex", e);
		}
		/*
		 * Dans la même div, on va afficher le résultat de la recherche d’un client dont
		 * le nom contient un mot clé, cet attribut ‘tout’ désigne qu’on veut la liste
		 * de tous les clients, dans la méthode permettant la recherche d’un client
		 * spécifique, on envoie un autre attribut 'parNom', voir ligne 189
		 */ model.addAttribute("tous", "   ");
		return "consulterClient";
	}

	@RequestMapping(value = "/saveOperation", method = RequestMethod.POST)
	public String saveOperation(Model model, String typeOperation, String codeCompte, double montant,
			String codeCompte2) {
		if(montant<=0)
		{
			model.addAttribute("e","Montant doit être supérieur à 0");
			return "redirect:/consulterCompte?codeCompte=" + codeCompte;
		}
		try {
			if (typeOperation.equals("VERS")) {
				banqueMetier.verser(codeCompte, montant);
			} else if (typeOperation.equals("RETR")) {
				banqueMetier.retirer(codeCompte, montant);
			} else if (typeOperation.equals("VIR")) {
				banqueMetier.virement(codeCompte, codeCompte2, montant);
			}

		} catch (Exception e) {
			model.addAttribute("erreur", e);
			return "redirect:/consulterCompte?codeCompte=" + codeCompte + "&erreur=" + e.getMessage();
		}

		return "redirect:/consulterCompte?codeCompte=" + codeCompte;
	}

	/*
	 * Méthode permettant l’ajout d’un client
	 */ @RequestMapping(value = "/ajouterClient", method = RequestMethod.GET)
	public String ajouterClient(String nomClt, String mail, RedirectAttributes ra) {

		if (nomClt.trim().length() < 4)
			ra.addFlashAttribute("invalide", "Le nom du client doit contenir au moins 4 lettres");
		else if (!mailValide(mail))
			ra.addFlashAttribute("invalide", "Adresse mail invalide");
		else {
			banqueMetier.insertClient(nomClt, mail);
			ra.addFlashAttribute("valide", "Client ajouté avec succès");
		}

		return "redirect:/consulterClient?nomClt=" + nomClt + "&mail=" + mail;
	}

	/*
	 * Méthode permet de verifier si l'email est valide, Récupéré la fomule du forum
	 * suivant : https://www.tutorialspoint.com/validate-email-address-in-java
	 */ public boolean mailValide(String mail) {
		String syntaxe = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
		return mail.matches(syntaxe);
	}

	/*
	 * Méthode pour modifier un client, on ne peut y acceder qu'apres une recherche
	 * de client
	 */ @RequestMapping(value = "/modifierClient", method = RequestMethod.POST)
	public String modifier(Model model, Long codeClient, String nvnom, String nvmail,
			RedirectAttributes redirectAttributes) {
		try {
			if (!nvnom.isEmpty() || !nvmail.isEmpty()) {
				redirectAttributes.addFlashAttribute("succes",
						"Le client avec code= " + codeClient + " a été modifié avec succès");
				banqueMetier.modifierClient(codeClient, nvnom, nvmail);
			} else
				redirectAttributes.addFlashAttribute("succes", "Aucune modification n'a été apportée");

		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("introuvable", e);
			return "redirect:/consulterClient?codeClient=" + codeClient;
		}
		return "redirect:/consulterClient?codeClient=" + codeClient;
	}

	/*
	 * Méthode permettant de Consulter les clients dont le nom contient un mot clé.
	 */
	@RequestMapping(value = "/consulterClientParNom")
	public String consulterClientParNom(Model model, String nom,
			@RequestParam(name = "page", defaultValue = "0") int page,
			@RequestParam(name = "size", defaultValue = "10") int size) {
		Page<Client> pc = banqueMetier.clientParNom(nom, page, size);
		model.addAttribute("allClient", pc.getContent());
		int[] pages = new int[pc.getTotalPages()];
		model.addAttribute("pagesClient", pages);
		model.addAttribute("parNom", "   ");
		model.addAttribute("nom", nom);
		if (pages.length == 0)
			model.addAttribute("vide", "Aucun résultat trouvé");
		return "consulterClient";
	}

}
