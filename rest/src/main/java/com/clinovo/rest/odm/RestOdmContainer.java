package com.clinovo.rest.odm;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.cdisc.ns.odm.v130.FileType;
import org.cdisc.ns.odm.v130.ODM;

import com.clinovo.rest.model.RestData;
import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;

/**
 * RestOdmContainer.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "ODM", namespace = "http://www.cdisc.org/ns/odm/v1.3")
public class RestOdmContainer extends ODM {

	@XmlElement(name = "RestData", namespace = "http://www.cdisc.org/ns/odm/v1.3")
	private RestData restData;

	public RestData getRestData() {
		return restData;
	}

	public void setRestData(RestData restData) {
		this.restData = restData;
	}

	public void collectOdmRoot() {
		Date creationDatetime = new Date();
		SimpleDateFormat localTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		TimeZone timeZone = TimeZone.getDefault();
		localTime.setTimeZone(timeZone);
		int offset = localTime.getTimeZone().getOffset(creationDatetime.getTime());
		String sign = "+";
		if (offset < 0) {
			offset = -offset;
			sign = "-";
		}
		int hours = offset / 3600000;
		int minutes = (offset - hours * 3600000) / 60000;
		DecimalFormat twoDigits = new DecimalFormat("00");
		setFileOID("REST-Data".concat(new SimpleDateFormat("yyyyMMddHHmmssZ").format(creationDatetime)));
		setDescription("REST Data");
		setCreationDateTime(XMLGregorianCalendarImpl.parse(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss".concat(sign)
				.concat(twoDigits.format(hours)).concat(":").concat(twoDigits.format(minutes)))
				.format(creationDatetime)));
		setODMVersion("1.3");
		setFileType(FileType.SNAPSHOT);
	}
}
