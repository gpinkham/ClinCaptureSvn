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
 \* If not, see <http://www.gnu.org/licenses/>. Modified by Clinovo Inc 01/29/2013.
 ******************************************************************************/

/* 
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).
 * For details see: http://www.openclinica.org/license
 *
 * Copyright 2003-2008 Akaza Research 
 */
package org.akaza.openclinica.domain.rule.expression;

import org.akaza.openclinica.exception.OpenClinicaSystemException;
import org.akaza.openclinica.service.rule.expression.ExpressionService;

/**
 * @author Krikor Krumlian
 * 
 */
public class ExpressionProcessorFactory {

	public static ExpressionProcessor createExpressionProcessor(ExpressionService expressionService) {
		ExpressionProcessor ep;
		switch (expressionService.getExpressionWrapper().getExpressionBean().getContext()) {
		case OC_RULES_V1: {
			ep = new OpenClinicaV1ExpressionProcessor(expressionService);
			break;
		}
		default:
			throw new OpenClinicaSystemException("Context : "
					+ expressionService.getExpressionWrapper().getExpressionBean().getContext() + " not Valid");
		}

		return ep;

	}

}
