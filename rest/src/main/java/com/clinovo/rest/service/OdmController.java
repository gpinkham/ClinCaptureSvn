package com.clinovo.rest.service;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * OdmController.
 */
@Controller
@RequestMapping("/odm")
public class OdmController {

	@RequestMapping
	@ResponseBody
	public String main() throws IOException {
		return IOUtils.toString(new FileSystemResourceLoader().getResource(
				"classpath:properties/ClinCapture_Rest_ODM1-3-0.xsd").getInputStream());
	}
}