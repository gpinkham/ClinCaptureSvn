package com.clinovo.coding.source;

import java.util.List;

import com.clinovo.coding.model.Classification;

public interface SearchInterface {
	
	public List<Classification> search(String term, String dictionary) throws Exception;

    public void getClassificationCodes (Classification classification, String dictionary) throws Exception;

}
