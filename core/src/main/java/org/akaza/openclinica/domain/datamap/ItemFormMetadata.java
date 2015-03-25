package org.akaza.openclinica.domain.datamap;

import org.akaza.openclinica.domain.DataMapDomainObject;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * ItemFormMetadata.
 */
@Entity
@SuppressWarnings("serial")
@Table(name = "item_form_metadata")
@GenericGenerator(name = "id-generator", strategy = "native", parameters = {@Parameter(name = "sequence", value = "item_form_metadata_item_form_metadata_id_seq")})
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class ItemFormMetadata extends DataMapDomainObject {

    private int itemFormMetadataId;
    private ResponseSet responseSet;
    private Item item;
    private Section section;
    private Integer crfVersionId;
    private String header;
    private String subheader;
    private Integer parentId;
    private String parentLabel;
    private Integer columnNumber;
    private String pageNumberLabel;
    private String questionNumberLabel;
    private String leftItemText;
    private String rightItemText;
    private Integer decisionConditionId;
    private String regexp;
    private String regexpErrorMsg;
    private int ordinal;
    private Boolean required;
    private String defaultValue;
    private String responseLayout;
    private String widthDecimal;
    private Boolean showItem;

    @Id
    @Column(name = "item_form_metadata_id", unique = true, nullable = false)
    @GeneratedValue(generator = "id-generator")
    public int getItemFormMetadataId() {
        return this.itemFormMetadataId;
    }

    public void setItemFormMetadataId(int itemFormMetadataId) {
        this.itemFormMetadataId = itemFormMetadataId;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "response_set_id", nullable = false)
    public ResponseSet getResponseSet() {
        return this.responseSet;
    }

    public void setResponseSet(ResponseSet responseSet) {
        this.responseSet = responseSet;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    public Item getItem() {
        return this.item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id", nullable = false)
    public Section getSection() {
        return this.section;
    }

    public void setSection(Section section) {
        this.section = section;
    }

    @Column(name = "crf_version_id")
    public Integer getCrfVersionId() {
        return this.crfVersionId;
    }

    public void setCrfVersionId(Integer crfVersionId) {
        this.crfVersionId = crfVersionId;
    }

    @Column(name = "header", length = 2000)
    public String getHeader() {
        return this.header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    @Column(name = "subheader", length = 240)
    public String getSubheader() {
        return this.subheader;
    }

    public void setSubheader(String subheader) {
        this.subheader = subheader;
    }

    @Column(name = "parent_id")
    public Integer getParentId() {
        return this.parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    @Column(name = "parent_label", length = 120)
    public String getParentLabel() {
        return this.parentLabel;
    }

    public void setParentLabel(String parentLabel) {
        this.parentLabel = parentLabel;
    }

    @Column(name = "column_number")
    public Integer getColumnNumber() {
        return this.columnNumber;
    }

    public void setColumnNumber(Integer columnNumber) {
        this.columnNumber = columnNumber;
    }

    @Column(name = "page_number_label", length = 5)
    public String getPageNumberLabel() {
        return this.pageNumberLabel;
    }

    public void setPageNumberLabel(String pageNumberLabel) {
        this.pageNumberLabel = pageNumberLabel;
    }

    @Column(name = "question_number_label", length = 20)
    public String getQuestionNumberLabel() {
        return this.questionNumberLabel;
    }

    public void setQuestionNumberLabel(String questionNumberLabel) {
        this.questionNumberLabel = questionNumberLabel;
    }

    @Column(name = "left_item_text", length = 4000)
    public String getLeftItemText() {
        return this.leftItemText;
    }

    public void setLeftItemText(String leftItemText) {
        this.leftItemText = leftItemText;
    }

    @Column(name = "right_item_text", length = 2000)
    public String getRightItemText() {
        return this.rightItemText;
    }

    public void setRightItemText(String rightItemText) {
        this.rightItemText = rightItemText;
    }

    @Column(name = "decision_condition_id")
    public Integer getDecisionConditionId() {
        return this.decisionConditionId;
    }

    public void setDecisionConditionId(Integer decisionConditionId) {
        this.decisionConditionId = decisionConditionId;
    }

    @Column(name = "regexp", length = 1000)
    public String getRegexp() {
        return this.regexp;
    }

    public void setRegexp(String regexp) {
        this.regexp = regexp;
    }

    @Column(name = "regexp_error_msg")
    public String getRegexpErrorMsg() {
        return this.regexpErrorMsg;
    }

    public void setRegexpErrorMsg(String regexpErrorMsg) {
        this.regexpErrorMsg = regexpErrorMsg;
    }

    @Column(name = "ordinal", nullable = false)
    public int getOrdinal() {
        return this.ordinal;
    }

    public void setOrdinal(int ordinal) {
        this.ordinal = ordinal;
    }

    @Column(name = "required")
    public Boolean getRequired() {
        return this.required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    @Column(name = "default_value", length = 4000)
    public String getDefaultValue() {
        return this.defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Column(name = "response_layout")
    public String getResponseLayout() {
        return this.responseLayout;
    }

    public void setResponseLayout(String responseLayout) {
        this.responseLayout = responseLayout;
    }

    @Column(name = "width_decimal", length = 10)
    public String getWidthDecimal() {
        return this.widthDecimal;
    }

    public void setWidthDecimal(String widthDecimal) {
        this.widthDecimal = widthDecimal;
    }

    @Column(name = "show_item")
    public Boolean getShowItem() {
        return this.showItem;
    }

    public void setShowItem(Boolean showItem) {
        this.showItem = showItem;
    }
}
