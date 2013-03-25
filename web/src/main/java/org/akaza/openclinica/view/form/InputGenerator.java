/*******************************************************************************
 * ClinCapture, Copyright (C) 2009-2013 Clinovo Inc.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the Lesser GNU General Public License 
 * as published by the Free Software Foundation, either version 2.1 of the License, or(at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the Lesser GNU General Public License for more details.
 * 
 * You should have received a copy of the Lesser GNU General Public License along with this program.  
 * If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package org.akaza.openclinica.view.form;

import org.akaza.openclinica.bean.submit.ResponseSetBean;
import org.jdom.Element;

import java.util.List;

/**
 * This interface defines the methods for creating HTML input types. The inputs represent the content for table cells or
 * TD tags, which are implemented as JDOM Elements. 
 */
@SuppressWarnings({ "rawtypes" })
public interface InputGenerator {
	Element createTextInputTag(Element tdCell, Integer itemId, Integer tabNumber, String defaultValue,
			boolean isDateType, String dbValue, boolean hasSavedData);

	Element createTextareaTag(Element tdCell, Integer itemId, Integer tabNumber, String dbValue, String defaultValue,
			boolean hasSavedData);

	Element createCheckboxTag(Element tdCell, Integer itemId, List options, Integer tabNumber, boolean includeLabel,
			String dbValue, String defaultValue, boolean isHorizontal, boolean hasSavedData);

	Element createRadioButtonTag(Element tdCell, Integer itemId, List options, Integer tabNumber, boolean includeLabel,
			String dbValue, String defaultValue, boolean isHorizontal, boolean hasSavedData);

	Element createSingleSelectTag(Element tdCell, Integer itemId, List options, Integer tabNumber);

	Element createMultiSelectTag(Element tdCell, Integer itemId, List options, Integer tabNumber, String dbValue,
			String defaultValue, boolean hasSavedData);

	Element createCaculationTag(Element tdCell, Integer itemId, ResponseSetBean responseSet, boolean isDateType,
			String dbValue, boolean hasSavedData);

	Element createInstantTag(Element tdCell, Integer itemId, Integer tabNumber, String dbValue, boolean hasSavedData);
}
