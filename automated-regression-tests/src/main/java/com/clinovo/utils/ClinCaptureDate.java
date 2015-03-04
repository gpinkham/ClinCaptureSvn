package com.clinovo.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Created by Anton on 22.07.2014.
 */
public class ClinCaptureDate {

    public static String getTodayDateInCCFormat() {
        DateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);

        return df.format(new Date());
    }

    // pass date parameter in the following format: dd-mm-yyyy (e.g. 01-01-1979)
    public static String getDateInCCFormat(String date) {
        String formattedDate;

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);
        String[] ss = date.split("-");
        Integer[] i = new Integer[ss.length];

        for (int j = 0; j < ss.length; j++) {
            i[j] = Integer.parseInt(ss[j]);
        }

        Calendar calendar = new GregorianCalendar(i[2],i[1],i[0]);
        formattedDate = sdf.format(calendar.getTime());

        return formattedDate;
    }
}
