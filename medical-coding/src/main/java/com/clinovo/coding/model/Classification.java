package com.clinovo.coding.model;

import java.util.ArrayList;

public class Classification {
	
	private String httpPath;

	private ArrayList<ClassificationElement> classificationElement;

    public Classification() {
        this.httpPath = "";
        this.classificationElement = new ArrayList<ClassificationElement>();
    }

    public ArrayList<ClassificationElement> getClassificationElement() {
        return classificationElement;
    }

    public void setClassificationElement(ArrayList<ClassificationElement> classificationElement) {
        this.classificationElement = classificationElement;
    }

    public void addClassificationElement(ClassificationElement classElement) {
        this.classificationElement.add(classElement);
    }

    public String getHttpPath() {
        return httpPath;
    }

    public void setHttpPath(String httpPath) {
        this.httpPath = httpPath;
    }
}
