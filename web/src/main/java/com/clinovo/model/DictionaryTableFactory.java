package com.clinovo.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.akaza.openclinica.control.AbstractTableFactory;
import org.jmesa.facade.TableFacade;
import org.jmesa.limit.Limit;
import org.jmesa.view.component.Row;

public class DictionaryTableFactory extends AbstractTableFactory {

	private List<Dictionary> dictionaries;
	
	@Override
	protected String getTableName() {
		return "dictionaryTable";
	}

	@Override
	protected void configureColumns(TableFacade tableFacade, Locale locale) {
		
		tableFacade.setColumnProperties("dictionary.name", "dictionary.type", "dictionary.version");
		
		Row row = tableFacade.getTable().getRow();
		
		configureColumn(row.getColumn("dictionary.name"), "Name", null, null);
		configureColumn(row.getColumn("dictionary.type"), "Type", null, null);
		configureColumn(row.getColumn("dictionary.version"), "Version", null, null);

	}

	@Override
	public void setDataAndLimitVariables(TableFacade tableFacade) {
		
        Limit limit = tableFacade.getLimit();

        if (!limit.isComplete()) {
            tableFacade.setTotalRows(dictionaries.size());
        }

        Collection<HashMap<Object, Object>> codedItemsResult = new ArrayList<HashMap<Object, Object>>();
        
        for (Dictionary dictionary : dictionaries) {
        	
            HashMap<Object, Object> h = new HashMap<Object, Object>();
            h.put("dictionary", dictionary);
            h.put("dictionary.id", dictionary.getId());
            h.put("dictionary.name", dictionary.getName());
            h.put("dictionary.type", dictionary.getType());
            h.put("dictionary.version", dictionary.getVersion());

            codedItemsResult.add(h);
        }

        tableFacade.setItems(codedItemsResult);
	}

	public void setDictionaries(List<Dictionary> dictionaries) {
		this.dictionaries = dictionaries;
	}
}
