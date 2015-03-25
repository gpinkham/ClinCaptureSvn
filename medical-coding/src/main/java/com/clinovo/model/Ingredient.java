package com.clinovo.model;

import javax.persistence.Column;
import javax.persistence.Entity;

import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Ingredient table bean.
 */
@Entity
@Table(name = "ingredient")
public class Ingredient {

    private int ingredientId = -1;
    private String createDate = null;
    private int substanceId = -1;
    private String quantity = "";
    private String quantity2 = "";
    private int unit = -1;
    private int pharmproductId = -1;
    private Substance sun;
    private MedicalProduct medicalProduct;

    @Id
    @Column(name = "ingredient_id")
    public int getIngredientId() {
        return ingredientId;
    }

    public void setIngredientId(int ingredientId) {
        this.ingredientId = ingredientId;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    @Column(name = "substance_id")
    public int getSubstanceId() {
        return substanceId;
    }

    public void setSubstanceId(int substanceId) {
        this.substanceId = substanceId;
    }

    @Column(name = "quantity")
    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    @Column(name = "quantity_2")
    public String getQuantity2() {
        return quantity2;
    }

    public void setQuantity2(String quantity2) {
        this.quantity2 = quantity2;
    }

    public int getUnit() {
        return unit;
    }

    public void setUnit(int unit) {
        this.unit = unit;
    }

    public int getPharmproductId() {
        return pharmproductId;
    }

    public void setPharmproductId(int pharmproductId) {
        this.pharmproductId = pharmproductId;
    }

    @ManyToOne
    @JoinColumn(name = "medicinalprod_id", referencedColumnName = "medicinalprod_id", insertable = false, updatable = false, nullable = false)
    public MedicalProduct getMedicalProduct() {
        return medicalProduct;
    }

    public void setMedicalProduct(MedicalProduct medicalProduct) {
        this.medicalProduct = medicalProduct;
    }

    @ManyToOne
    @JoinColumn(name = "substance_id", insertable = false, updatable = false, nullable = false)
    public Substance getSun() {
        return sun;
    }

    public void setSun(Substance sun) {
        this.sun = sun;
    }
}
