/*******************************************************************************
 * CLINOVO RESERVES ALL RIGHTS TO THIS SOFTWARE, INCLUDING SOURCE AND DERIVED BINARY CODE. BY DOWNLOADING THIS SOFTWARE YOU AGREE TO THE FOLLOWING LICENSE:
 * 
 * Subject to the terms and conditions of this Agreement including, Clinovo grants you a non-exclusive, non-transferable, non-sublicenseable limited license without license fees to reproduce and use internally the software complete and unmodified for the sole purpose of running Programs on one computer. 
 * This license does not allow for the commercial use of this software except by IRS approved non-profit organizations; educational entities not working in joint effort with for profit business.
 * To use the license for other purposes, including for profit clinical trials, an additional paid license is required. Please contact our licensing department at http://www.clinovo.com/contact for pricing information.
 * 
 * You may not modify, decompile, or reverse engineer the software.
 * Clinovo disclaims any express or implied warranty of fitness for use. 
 * No right, title or interest in or to any trademark, service mark, logo or trade name of Clinovo or its licensors is granted under this Agreement.
 * THIS SOFTWARE IS PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND. CLINOVO FURTHER DISCLAIMS ALL WARRANTIES, EXPRESS AND IMPLIED, INCLUDING WITHOUT LIMITATION, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NONINFRINGEMENT.

 * LIMITATION OF LIABILITY. IN NO EVENT SHALL CLINOVO BE LIABLE FOR ANY INDIRECT, INCIDENTAL, SPECIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES, OR DAMAGES FOR LOSS OF PROFITS, REVENUE, DATA OR DATA USE, INCURRED BY YOU OR ANY THIRD PARTY, WHETHER IN AN ACTION IN CONTRACT OR TORT, EVEN IF ORACLE HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. CLINOVO’S ENTIRE LIABILITY FOR DAMAGES HEREUNDER SHALL IN NO EVENT EXCEED TWO HUNDRED DOLLARS (U.S. $200).
 *******************************************************************************/
package com.clinovo.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.clinovo.model.CodedItemsTableFactory;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.akaza.openclinica.view.StudyInfoPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.clinovo.coding.Search;
import com.clinovo.coding.model.Classification;
import com.clinovo.coding.source.impl.BioPortalSearchInterface;
import com.clinovo.model.CodedItem;
import com.clinovo.model.Status.CodeStatus;
import com.clinovo.service.CodedItemService;

@Controller
public class CodedItemsController {

	private Search search = new Search();

	@Autowired
	private CodedItemService codedItemService;

	private final Logger log = LoggerFactory.getLogger(getClass().getName());

	@RequestMapping("/codedItems")
	public ModelMap dictionaryHandler(HttpServletRequest request, HttpServletResponse response) throws Exception {

		ModelMap map = new ModelMap();
		ResourceBundleProvider.updateLocale(request.getLocale());

		List<CodedItem> items = codedItemService.findAll();
		List<CodedItem> codedItems = codedItemService.findCodedItemsByStatus(CodeStatus.CODED);
		List<CodedItem> unCodedItems = codedItemService.findCodedItemsByStatus(CodeStatus.NOT_CODED);

		CodedItemsTableFactory factory = new CodedItemsTableFactory();
		factory.setCodedItems(items);
		String codedItemsTable = factory.createTable(request, response).render();

		StudyInfoPanel panel = new StudyInfoPanel();
		panel.reset();

		// probably a bad idea?
		map.addAttribute("panel", panel);
		map.addAttribute("allItems", items);
		map.addAttribute("codedItems", codedItems.size());
		map.addAttribute("unCodedItems", unCodedItems.size());
		map.addAttribute("codedQuestionsHtml", codedItemsTable);

		return map;
	}

	@RequestMapping("/codedItem")
	public ModelMap codeItemHandler(HttpServletRequest request) throws Exception {

		ModelMap map = new ModelMap();
		ResourceBundleProvider.updateLocale(request.getLocale());

		String codedItemItemId = request.getParameter("item");
		String verbatimTerm = request.getParameter("verbatimTerm");

		CodedItem codedItem = codedItemService.findByItemId(Integer.parseInt(codedItemItemId));

		search.setSearchInterface(new BioPortalSearchInterface());

		try {

			List<Classification> classifications = search.getClassifications(verbatimTerm, codedItem.getDictionary());
			
			map.addAttribute("itemId", codedItem.getItemId());
			map.addAttribute("classification", classifications);
			map.addAttribute("itemDictionary", codedItem.getDictionary());

		} catch (Exception e) {

			log.error(e.getMessage());
		}

		return map;

	}
	
	@RequestMapping("/saveCodedItem")
	public String saveCodedItemHandler(HttpServletRequest request) throws Exception {
		
		ResourceBundleProvider.updateLocale(request.getLocale());
		
		String code = request.getParameter("code");
		String codedItemItemId = request.getParameter("item");
		
		CodedItem codedItem = codedItemService.findByItemId(Integer.parseInt(codedItemItemId));
		codedItem.setCodedTerm(code);
		
		codedItemService.saveCodedItem(codedItem);
		
		// Redirect to main
		return "codedItems";
	}
}
