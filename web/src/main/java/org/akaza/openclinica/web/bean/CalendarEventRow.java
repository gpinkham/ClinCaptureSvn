/*
 * ******************************************************************************
 *  * ClinCapture, Copyright (C) 2009-2013 Clinovo Inc.
 *  *
 *  * This program is free software: you can redistribute it and/or modify it under the terms of the Lesser GNU General Public License
 *  * as published by the Free Software Foundation, either version 2.1 of the License, or(at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 *  * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the Lesser GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the Lesser GNU General Public License along with this program.
 *  \* If not, see <http://www.gnu.org/licenses/>. Modified by Clinovo Inc 01/29/2013.
 *  *****************************************************************************
 *
 *
 *  * OpenClinica is distributed under the
 *  * GNU Lesser General Public License (GNU LGPL).
 *
 *  * For details see: http://www.openclinica.org/license
 *  * copyright 2003-2005 Akaza Research
 *
 */

package org.akaza.openclinica.web.bean;

import com.clinovo.i18n.LocaleResolver;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.akaza.openclinica.service.calendar.CalendarFuncBean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * User: Vitaly
 * Date: 7/26/13
 */
public class CalendarEventRow extends EntityBeanRow {

    public static final int COL_EVENT_NAME = 0;
    public static final int COL_MIN_MAX_RANGE = 1;
    public static final int COL_SCHEDULE_DATE = 2;
    public static final int COL_USER_EMAIL_DATE = 3;
    public static final int COL_REFERENCE_VISIT = 4;
    public static final int COL_REFERENCE_EVENT_FOR_VISIT = 5;
    public SimpleDateFormat sdf = new SimpleDateFormat(ResourceBundleProvider.getFormatBundle().getString(
            "date_format_string"), LocaleResolver.getLocale());

    @Override
    protected int compareColumn(Object row, int sortingColumn) {
        if (!row.getClass().equals(CalendarEventRow.class)) {
            return 0;
        }

        CalendarFuncBean thisEvent = (CalendarFuncBean) bean;
        CalendarFuncBean argEvent = (CalendarFuncBean) ((CalendarEventRow) row).bean;

        int answer = 0;
        switch (sortingColumn) {
            case COL_EVENT_NAME:
                answer = thisEvent.getEventName().toLowerCase().compareTo(argEvent.getEventName().toLowerCase());
                break;
            case COL_MIN_MAX_RANGE:
                answer = compareDate(thisEvent.getDateMin(), argEvent.getDateMin());
                break;
            case COL_SCHEDULE_DATE:
                answer = compareDate(thisEvent.getDateSchedule(), argEvent.getDateSchedule());
                break;
            case COL_USER_EMAIL_DATE:
                answer = compareDate(thisEvent.getDateEmail(), argEvent.getDateEmail());
                break;
            case COL_REFERENCE_VISIT:
                answer = thisEvent.getReferenceVisit().compareTo(argEvent.getReferenceVisit());
                break;
            case COL_REFERENCE_EVENT_FOR_VISIT:
                answer = thisEvent.getReferenceVisit().compareTo(argEvent.getReferenceVisit());
                break;
        }

        return answer;
    }

    @Override
    public String getSearchString() {
        CalendarFuncBean thisEvent = (CalendarFuncBean) bean;
        return thisEvent.getEventName() + " " + thisEvent.getDateMin() + " " + sdf.format(thisEvent.getDateSchedule()) + " "
                + sdf.format(thisEvent.getDateEmail()) + " " + thisEvent.getReferenceVisit() + " " + thisEvent.getEventsReferenceVisit();
    }

    @SuppressWarnings("rawtypes")
	@Override
    public ArrayList generatRowsFromBeans(ArrayList beans) {
        return CalendarEventRow.generateRowsFromBeans(beans);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
	public static ArrayList generateRowsFromBeans(ArrayList beans) {
        ArrayList answer = new ArrayList();

        for (int i = 0; i < beans.size(); i++) {
            try {
                CalendarEventRow row = new CalendarEventRow();
                row.setBean((CalendarFuncBean) beans.get(i));
                answer.add(row);
            } catch (Exception e) {
            }
        }
        return answer;
    }

}
