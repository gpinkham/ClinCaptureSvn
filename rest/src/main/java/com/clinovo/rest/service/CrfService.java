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

package com.clinovo.rest.service;

import java.util.List;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.clinovo.rest.model.Response;
import com.clinovo.rest.service.base.BaseCrfService;

/**
 * CrfService.
 */
@RestController("restCrfService")
public class CrfService extends BaseCrfService {

	/**
	 * Method imports new crf.
	 *
	 * @param jsonData
	 *            String
	 * @return CRFVersionBean
	 * @throws Exception
	 *             an Exception
	 */
	@RequestMapping(value = "/crf/json/importCrf", method = RequestMethod.POST)
	public CRFVersionBean importCrf(@RequestParam("jsonData") String jsonData) throws Exception {
		return importCrf(jsonData, false);
	}

	/**
	 * Method imports new crf version.
	 *
	 * @param jsonData
	 *            String
	 * @return CRFVersionBean
	 * @throws Exception
	 *             an Exception
	 */
	@RequestMapping(value = "/crf/json/importCrfVersion", method = RequestMethod.POST)
	public CRFVersionBean importCrfVersion(@RequestParam("jsonData") String jsonData) throws Exception {
		return importCrf(jsonData, true);
	}

	/**
	 * Method returns crf.
	 *
	 * @param crfId
	 *            int
	 * @return CRFBean
	 * @throws Exception
	 *             an Exception
	 */
	@RequestMapping(value = "/crf", method = RequestMethod.GET)
	public CRFBean crf(@RequestParam(value = "id") int crfId) throws Exception {
		return getCrfBean(crfId);
	}

	/**
	 * Method returns crf version.
	 *
	 * @param crfVersionId
	 *            int
	 * @return CRFVersionBean
	 * @throws Exception
	 *             an Exception
	 */
	@RequestMapping(value = "/crfVersion", method = RequestMethod.GET)
	public CRFVersionBean crfVersion(@RequestParam(value = "id") int crfVersionId) throws Exception {
		return getCrfVersionBean(crfVersionId, false);
	}

	/**
	 * Method returns all crfs.
	 *
	 * @return List of CRFBean
	 * @throws Exception
	 *             an Exception
	 */
	@RequestMapping(value = "/crfs", method = RequestMethod.GET)
	public List<CRFBean> crfs() throws Exception {
		return getAllCrfs();
	}

	/**
	 * Method returns all crf versions.
	 *
	 * @return List of CRFVersionBean
	 * @throws Exception
	 *             an Exception
	 */
	@RequestMapping(value = "/crfVersions", method = RequestMethod.GET)
	public List<CRFVersionBean> crfVersions() throws Exception {
		return getAllCrfVersions();
	}

	/**
	 * Method removes crf.
	 *
	 * @param crfId
	 *            int
	 * @return CRFBean
	 * @throws Exception
	 *             an Exception
	 */
	@RequestMapping(value = "/crf/remove", method = RequestMethod.POST)
	public CRFBean removeCrf(@RequestParam("id") int crfId) throws Exception {
		return removeCrfBean(crfId);
	}

	/**
	 * Method restores crf.
	 *
	 * @param crfId
	 *            int
	 * @return CRFBean
	 * @throws Exception
	 *             an Exception
	 */
	@RequestMapping(value = "/crf/restore", method = RequestMethod.POST)
	public CRFBean restoreCrf(@RequestParam("id") int crfId) throws Exception {
		return restoreCrfBean(crfId);
	}

	/**
	 * Method removes crf version.
	 *
	 * @param crfVersionId
	 *            int
	 * @return CRFVersionBean
	 * @throws Exception
	 *             an Exception
	 */
	@RequestMapping(value = "/crfVersion/remove", method = RequestMethod.POST)
	public CRFVersionBean removeCrfVersion(@RequestParam("id") int crfVersionId) throws Exception {
		return removeCrfVersionBean(crfVersionId);
	}

	/**
	 * Method restores crf version.
	 *
	 * @param crfVersionId
	 *            int
	 * @return CRFVersionBean
	 * @throws Exception
	 *             an Exception
	 */
	@RequestMapping(value = "/crfVersion/restore", method = RequestMethod.POST)
	public CRFVersionBean restoreCrfVersion(@RequestParam("id") int crfVersionId) throws Exception {
		return restoreCrfVersionBean(crfVersionId);
	}

	/**
	 * Method locks crf version.
	 *
	 * @param crfVersionId
	 *            int
	 * @return CRFVersionBean
	 * @throws Exception
	 *             an Exception
	 */
	@RequestMapping(value = "/crfVersion/lock", method = RequestMethod.POST)
	public CRFVersionBean lockCrfVersion(@RequestParam("id") int crfVersionId) throws Exception {
		return lockCrfVersionBean(crfVersionId);
	}

	/**
	 * Method unlocks crf version.
	 *
	 * @param crfVersionId
	 *            int
	 * @return CRFVersionBean
	 * @throws Exception
	 *             an Exception
	 */
	@RequestMapping(value = "/crfVersion/unlock", method = RequestMethod.POST)
	public CRFVersionBean unlockCrfVersion(@RequestParam("id") int crfVersionId) throws Exception {
		return unlockCrfVersionBean(crfVersionId);
	}

	/**
	 * Method deletes crf.
	 *
	 * @param crfId
	 *            int
	 * @param force
	 *            boolean
	 * @return Response
	 * @throws Exception
	 *             an Exception
	 */
	@RequestMapping(value = "/crf/delete", method = RequestMethod.POST)
	public Response deleteCrf(@RequestParam("id") int crfId,
			@RequestParam(value = "force", defaultValue = "false", required = false) boolean force) throws Exception {
		return deleteCrfBean(crfId, force);
	}

	/**
	 * Method deletes crf version.
	 *
	 * @param crfVersionId
	 *            int
	 * @param force
	 *            boolean
	 * @return Response
	 * @throws Exception
	 *             an Exception
	 */
	@RequestMapping(value = "/crfVersion/delete", method = RequestMethod.POST)
	public Response deleteCrfVersion(@RequestParam("id") int crfVersionId,
			@RequestParam(value = "force", defaultValue = "false", required = false) boolean force) throws Exception {
		return deleteCrfVersionBean(crfVersionId, force);
	}
}
