package com.nour.enseignant.controllers;

import java.text.ParseException;



import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.nour.enseignant.entities.Enseignant;
import com.nour.enseignant.entities.University;
import com.nour.enseignant.services.EnseignantService;


import java.util.List;
import java.util.Objects;

import javax.validation.Valid;

@Controller
public class EnseignantController {
    @Autowired
    EnseignantService enseignantService;

    @RequestMapping("/showCreate")
    public String showCreate(ModelMap modelMap,
    @RequestParam(name = "page", defaultValue = "0") int page,
    @RequestParam(name = "size", defaultValue = "2") int size) 
    {
    	List<University> univs = enseignantService.getAllUniversities();
    	Enseignant ens = new Enseignant();
    	University univ = new University();
        univ = univs.get(0); // prendre la première university de la liste
        ens.setUniversity(univ); //affedter une university par défaut au Enseignant pour éviter le pb avec une university null
    	
    Page<Enseignant> enseignants = enseignantService.getAllEnseignantParPage(page, size);
    modelMap.addAttribute("enseignant",ens);
    modelMap.addAttribute("university",univ);
    modelMap.addAttribute("mode", "new");
    modelMap.addAttribute("universities", univs);
    modelMap.addAttribute("pages", new int[enseignants.getTotalPages()]);
    modelMap.addAttribute("currentPage", page);
    modelMap.addAttribute("size", size);
    modelMap.addAttribute("page",0);
    return "formEnseignant";
    }

    
    @RequestMapping("/saveEnseignant")
    public String saveEnseignant(@Valid Enseignant enseignant,  BindingResult bindingResult,
    	    @ModelAttribute("page") int pageFromPrevious,
    	    @RequestParam (name="size", defaultValue = "2") int size,
    	    RedirectAttributes redirectAttributes)
    
    {
    	int page;
    if (bindingResult.hasErrors()) return "formEnseignant";
    if (enseignant.getIdEnseignant()==null)//adding
        page = enseignantService.getAllEnseignant().size()/size; // calculer le nbr de pages
    else //updating
        page = pageFromPrevious;
   
    enseignantService.saveEnseignant(enseignant);
    
    redirectAttributes.addAttribute("page", page);
    
    return "redirect:/listeEnseignants";
    }

   
 
    @GetMapping("/listeEnseignants")
    public String listeEnseignants(ModelMap modelMap, @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "2") int size) {
        Page<Enseignant> enseignants = enseignantService.getAllEnseignantParPage(page, size);
        modelMap.addAttribute("enseignants", enseignants);
        modelMap.addAttribute("pages", new int[enseignants.getTotalPages()]);
        modelMap.addAttribute("currentPage", page);
        return "listeEnseignant";
    }

	
	
    @GetMapping("/supprimerEnseignant")
    public String supprimerEnseignant(@RequestParam("id") Long id, ModelMap modelMap,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "2") int size) {
        enseignantService.deleteEnseignantById(id);
        Page<Enseignant> enseignants = enseignantService.getAllEnseignantParPage(page, size);
        modelMap.addAttribute("enseignants", enseignants);
        modelMap.addAttribute("pages", new int[enseignants.getTotalPages()]);
        modelMap.addAttribute("currentPage", page);
        modelMap.addAttribute("size", size);
        return "listeEnseignant";
    }

    @GetMapping("/modifierEnseignant")
    public String editerEnseignant(@RequestParam("id") Long id,@RequestParam("page") int page, ModelMap modelMap) {
        Enseignant e = enseignantService.getEnseignant(id);
        List<University> univs = enseignantService.getAllUniversities();
        
        modelMap.addAttribute("enseignant", e);
        modelMap.addAttribute("mode", "edit");
        modelMap.addAttribute("page",page);
		modelMap.addAttribute("universities", univs);
        return "formEnseignant";
    }
    
    
  
    @GetMapping("/updateEnseignant")
    public String updateEnseignant(@ModelAttribute("enseignant") Enseignant enseignant, ModelMap modelMap) 
    {
    	enseignantService.updateEnseignant(enseignant);
    	List<Enseignant> enseignants = enseignantService.getAllEnseignant();
    	modelMap.addAttribute("enseignants", enseignants);
    	return "listeEnseignant";
    }
}