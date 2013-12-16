package com.clinovo.coding;

import java.util.ArrayList;
import java.util.List;

import com.clinovo.coding.model.Classification;
import com.clinovo.coding.source.SearchInterface;

public class Search {

	private SearchInterface searchInterface = null;

	public List<Classification> getClassifications(String term, String dictionary) throws Exception {

		if (searchInterface != null)
			return searchInterface.search(term, dictionary);

		return new ArrayList<Classification>();
	}

    public void getClassificationWithCodes (Classification classification, String dictionary) throws Exception {

        if (searchInterface != null)
            searchInterface.getClassificationCodes(classification, dictionary);

    }

	public void setSearchInterface(SearchInterface searchInterface) {

		this.searchInterface = searchInterface;
	}
}
