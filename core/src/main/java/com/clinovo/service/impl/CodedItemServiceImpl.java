package com.clinovo.service.impl;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sql.DataSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.bean.submit.ItemFormMetadataBean;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.submit.ItemDataDAO;
import org.akaza.openclinica.dao.submit.ItemFormMetadataDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.clinovo.dao.CodedItemDAO;
import com.clinovo.exception.CodeException;
import com.clinovo.model.CodedItem;
import com.clinovo.model.Status.CodeStatus;
import com.clinovo.service.CodedItemService;

@Service
@Transactional
public class CodedItemServiceImpl implements CodedItemService {

	@Autowired
	private CodedItemDAO codeItemDAO;

	@Autowired
	private DataSource dataSource;

	public List<CodedItem> findAll() throws Exception {

		List<CodedItem> codedItems = codeItemDAO.findAll();

		if (codedItems.isEmpty()) {

			codedItems = initializeCodedItems();
		}

		return codedItems;
	}

	public CodedItem findById(int codedItemId) {
		return codeItemDAO.findById(codedItemId);
	}

	public CodedItem findCodedItem(int codedItemId) {
		return codeItemDAO.findById(codedItemId);
	}

	public List<CodedItem> findCodedItemsByVerbatimTerm(String verbatimTerm) {
		return codeItemDAO.findByVerbatimTerm(verbatimTerm);
	}

	public List<CodedItem> findCodedItemsByCodedTerm(String codedTerm) {
		return codeItemDAO.findByCodedTerm(codedTerm);
	}

	public List<CodedItem> findCodedItemsByDictionary(String dictionary) {
		return codeItemDAO.findByDictionary(dictionary);
	}

	public List<CodedItem> findCodedItemsByStatus(CodeStatus status) {
		return codeItemDAO.findByStatus(status);
	}

	public CodedItem findByItemId(int codedItemItemId) {
		return codeItemDAO.findByItemId(codedItemItemId);
	}

	public CodedItem saveCodedItem(CodedItem codedItem) throws Exception {
		
		ItemDataDAO itemDataDAO = new ItemDataDAO(dataSource);
		UserAccountDAO userDAO = new UserAccountDAO(dataSource);
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserAccountBean loggedInUser = (UserAccountBean) userDAO.findByUserName(authentication.getName());
		ItemDataBean itemData = (ItemDataBean) itemDataDAO.findByPK(codedItem.getItemId());
		
		if (itemData.getId() > 0) {

			itemData.setUpdater(loggedInUser);
			itemData.setUpdatedDate(new Date());
			itemData.setValue(codedItem.getCodedTerm());
			
			// persist
			itemDataDAO.updateValue(itemData);
			
		} else {
			
			throw new CodeException("ItemData for this Form not found. Has the CRF been completed?");
		}

		codedItem.setStatus("CODED");
		return codeItemDAO.saveOrUpdate(codedItem);
	}

	public void deleteCodedItem(CodedItem codedItem) {
		codeItemDAO.deleteCodedItem(codedItem);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private List<CodedItem> initializeCodedItems() throws Exception {

		List<CodedItem> codedItems = new ArrayList<CodedItem>();
		ItemFormMetadataDAO itemMetaDAO = new ItemFormMetadataDAO(dataSource);

		Collection<ItemFormMetadataBean> items = itemMetaDAO.findAll();

		for (ItemFormMetadataBean bean : items) {

			if (isItemCodable(bean)) {

				CodedItem codedItem = createCodeItem(bean);

				codedItems.add(codeItemDAO.saveOrUpdate(codedItem));
			}
		}
		return codedItems;
	}

	private CodedItem createCodeItem(ItemFormMetadataBean bean) throws Exception {

		CodedItem item = new CodedItem();
		Element element = createDocument(bean).getDocumentElement();

		item.setItemId(bean.getId());
		item.setDictionary(element.getAttribute("dictionary"));
		item.setVerbatimTerm(element.getAttribute("verbatimterm"));

		return item;
	}

	private boolean isItemCodable(ItemFormMetadataBean bean) throws Exception {

		String subHeader = bean.getSubHeader();
		
		Pattern pattern = Pattern.compile("^<.*>");
		Matcher matcher = pattern.matcher(subHeader);
		
		if (subHeader != null && !subHeader.isEmpty() && matcher.matches()) {

			Document document = createDocument(bean);
			Element root = document.getDocumentElement();

			return root.getAttribute("dictionary") != null;
		}

		return false;
	}

	private Document createDocument(ItemFormMetadataBean bean) throws Exception {

		// Make it valid xml
		String subHeader = bean.getSubHeader().replace(">", "/>");

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();

		Document document = builder.parse(new ByteArrayInputStream(subHeader.getBytes()));

		return document;
	}

}
