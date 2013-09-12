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

package org.akaza.openclinica.domain.rule.action;

import org.akaza.openclinica.dao.hibernate.RuleActionRunLogDao;
import org.akaza.openclinica.domain.rule.RuleSetBean;
import org.akaza.openclinica.domain.rule.RuleSetRuleBean;
import org.akaza.openclinica.exception.OpenClinicaSystemException;
import org.akaza.openclinica.service.crfdata.DynamicsMetadataService;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import javax.sql.DataSource;
import java.sql.Connection;

public class ActionProcessorFacade {

    public static ActionProcessor getActionProcessor(ActionType actionType, DataSource ds,
                                                     JavaMailSenderImpl mailSender, DynamicsMetadataService itemMetadataService, RuleSetBean ruleSet,
                                                     RuleActionRunLogDao ruleActionRunLogDao, RuleSetRuleBean ruleSetRule) throws OpenClinicaSystemException {
        return getActionProcessor(actionType, null, ds,
                mailSender, itemMetadataService, ruleSet,
                ruleActionRunLogDao, ruleSetRule);
    }

	public static ActionProcessor getActionProcessor(ActionType actionType, Connection connection, DataSource ds,
			JavaMailSenderImpl mailSender, DynamicsMetadataService itemMetadataService, RuleSetBean ruleSet,
			RuleActionRunLogDao ruleActionRunLogDao, RuleSetRuleBean ruleSetRule) throws OpenClinicaSystemException {
		switch (actionType) {
		case FILE_DISCREPANCY_NOTE:
			return new DiscrepancyNoteActionProcessor(connection, ds, ruleActionRunLogDao, ruleSetRule);
		case EMAIL:
			return new EmailActionProcessor(connection, ds, mailSender, ruleActionRunLogDao, ruleSetRule);
		case SHOW:
			return new ShowActionProcessor(ds, itemMetadataService, ruleSet, connection);
		case HIDE:
			return new HideActionProcessor(ds, itemMetadataService, ruleSet, connection);
		case INSERT:
			return new InsertActionProcessor(ds, itemMetadataService, ruleActionRunLogDao, ruleSet, ruleSetRule, connection);
		default:
			throw new OpenClinicaSystemException("actionType", "Unrecognized action type!");
		}
	}
}
