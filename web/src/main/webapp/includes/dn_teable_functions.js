/**
 * User: Pavel Lukashenka
 * Date: 04.10.12
 */

function applyEmptyStatusAndTypeFilter() {
    applyStatusAndTypeFilter("", "")
}

function applyStatusFilter(status) {
    applyStatusAndTypeFilter(status, "")
}

function applyTypeFilter(type) {
    applyStatusAndTypeFilter("", type)
}

function applyStatusAndTypeFilter(status, type) {
    jQuery.jmesa.getTableFacade("listNotes").limit.filterSet = [];
    setStatusFilter(status);
    setTypeFilter(type);
    createHiddenInputFieldsForLimitAndSubmit("listNotes");
}

function setStatusFilter(status) {
    setFilterValue("discrepancyNoteBean.resolutionStatus", status);
}

function setTypeFilter(noteType) {
    setFilterValue("discrepancyNoteBean.disType", noteType);
}

function setFilterValue(filterName, filterValue) {
    var filterSet = jQuery.jmesa.getTableFacade("listNotes").limit.filterSet;
    var isFilterInSet = false;

    jQuery.each(filterSet, function(index, value) {
        if(value.property == filterName) {
            value.value = filterValue;
            isFilterInSet = true;
        }
    });

    if(!isFilterInSet) {
        filterSet.push({property: filterName, value: filterValue});
    }
}