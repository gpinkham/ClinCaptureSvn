package com.clinovo.controller;

/**
 * Redirection class that manage redirections to the previous urls.
 */
public abstract class Redirection {

	/**
	 * Method returns url for controller redirection.
	 * 
	 * @return String url
	 */
	public abstract String getUrl();
}
