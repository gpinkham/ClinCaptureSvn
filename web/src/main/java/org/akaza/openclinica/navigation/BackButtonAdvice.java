package org.akaza.openclinica.navigation;

import javax.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;

public class BackButtonAdvice {
	public void simpleBeforeAdvice(JoinPoint joinPoint, HttpServletRequest request) {
		Navigation.addToNavigationStack(request);
	}
}
