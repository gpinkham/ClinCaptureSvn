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

package org.akaza.openclinica.bean.service;

/**
 * ProcessingFunction, by Tom Hickerson, 09/2010 Interface of the pre- and post-processing functions determined by the
 * new extract data interface Currently only implementing one post-processing function per XSL type Future
 * implementations will be with multiple pre- and post-functions
 * 
 * @author thickerson
 * 
 */
public interface ProcessingInterface {
	ProcessingResultType run();

}
