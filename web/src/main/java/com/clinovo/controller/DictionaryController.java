package com.clinovo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.clinovo.service.DictionaryService;

@Controller
@RequestMapping("/dictionary")
public class DictionaryController {
	
	@Autowired
	private DictionaryService dictionaryService;
	
	@RequestMapping(method = RequestMethod.GET)
	public String dictionaryHandler(ModelMap model) {
		
		model.addAttribute("message", "WTF");
		return "dictionary";
	}
}
