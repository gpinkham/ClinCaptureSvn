/*******************************************************************************
 * CLINOVO RESERVES ALL RIGHTS TO THIS SOFTWARE, INCLUDING SOURCE AND DERIVED BINARY CODE. BY DOWNLOADING THIS SOFTWARE YOU AGREE TO THE FOLLOWING LICENSE:
 *
 * Subject to the terms and conditions of this Agreement including, Clinovo grants you a non-exclusive, non-transferable, non-sublicenseable limited license without license fees to reproduce and use internally the software complete and unmodified for the sole purpose of running Programs on one computer.
 * This license does not allow for the commercial use of this software except by IRS approved non-profit organizations; educational entities not working in joint effort with for profit business.
 * To use the license for other purposes, including for profit clinical trials, an additional paid license is required. Please contact our licensing department at http://www.clincapture.com/contact for pricing information.
 *
 * You may not modify, decompile, or reverse engineer the software.
 * Clinovo disclaims any express or implied warranty of fitness for use.
 * No right, title or interest in or to any trademark, service mark, logo or trade name of Clinovo or its licensors is granted under this Agreement.
 * THIS SOFTWARE IS PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND. CLINOVO FURTHER DISCLAIMS ALL WARRANTIES, EXPRESS AND IMPLIED, INCLUDING WITHOUT LIMITATION, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NONINFRINGEMENT.

 * LIMITATION OF LIABILITY. IN NO EVENT SHALL CLINOVO BE LIABLE FOR ANY INDIRECT, INCIDENTAL, SPECIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES, OR DAMAGES FOR LOSS OF PROFITS, REVENUE, DATA OR DATA USE, INCURRED BY YOU OR ANY THIRD PARTY, WHETHER IN AN ACTION IN CONTRACT OR TORT, EVEN IF ORACLE HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. CLINOVO'S ENTIRE LIABILITY FOR DAMAGES HEREUNDER SHALL IN NO EVENT EXCEED TWO HUNDRED DOLLARS (U.S. $200).
 *******************************************************************************/
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
import com.clinovo.rest.model.Server;
import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;

/**
 * RestOdmContainer.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "ODM", namespace = "http://www.cdisc.org/ns/odm/v1.3")
public class RestOdmContainer extends ODM {

	public static final int MILLISEC_IN_HOUR = 3600000;
	public static final int MILLISEC_IN_MINUTES = 60000;

	@XmlElement(name = "Server", namespace = "http://www.cdisc.org/ns/odm/v1.3")
	private Server server;

	public Server getServer() {
		return server;
	}

	public void setServer(Server server) {
		this.server = server;
	}

	@XmlElement(name = "RestData", namespace = "http://www.cdisc.org/ns/odm/v1.3")
	private RestData restData;

	public RestData getRestData() {
		return restData;
	}

	public void setRestData(RestData restData) {
		this.restData = restData;
	}

	/**
	 * Collects odm root data.
	 */
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
		int hours = offset / MILLISEC_IN_HOUR;
		int minutes = (offset - hours * MILLISEC_IN_HOUR) / MILLISEC_IN_MINUTES;
		DecimalFormat twoDigits = new DecimalFormat("00");
		setFileOID("REST-Data".concat(new SimpleDateFormat("yyyyMMddHHmmssZ").format(creationDatetime)));
		setDescription("REST Data");
		setCreationDateTime(XMLGregorianCalendarImpl
				.parse(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss".concat(sign).concat(twoDigits.format(hours))
						.concat(":").concat(twoDigits.format(minutes))).format(creationDatetime)));
		setODMVersion("1.3");
		setFileType(FileType.SNAPSHOT);
	}
}
