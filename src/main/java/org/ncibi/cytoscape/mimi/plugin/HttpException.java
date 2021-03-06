/*************************************************************************
 * Copyright 2012 Regents of the University of Michigan 
 * 
 * NCIBI - The National Center for Integrative Biomedical Informatics (NCIBI)
 *         http://www.ncib.org.
 * 
 * This product includes software developed by other, see specific notes in the code. 
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms
 * of the GNU General Public License as published by the Free Software Foundation, 
 * either version 3 of the License, or (at your option) any later version, along with the following terms:
 * 1.	You may convey a work based on this program in accordance with section 5, 
 *      provided that you retain the above notices.
 * 2.	You may convey verbatim copies of this program code as you receive it, 
 *      in any medium, provided that you retain the above notices.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * 
 * See the GNU General Public License for more details, http://www.gnu.org/licenses/.
 * 
 * This work was supported in part by National Institutes of Health Grant #U54DA021519
 *
 ******************************************************************/
 
package org.ncibi.cytoscape.mimi.plugin;

import java.net.SocketException;

/**
 * HttpException
 *
 * Signals that a response code other than 200 was recieved.
 *
 * @author Alex Ade
 * @date   Sun Dec 17 19:16:06 EST 2006
 */
@SuppressWarnings("serial")
public class HttpException extends SocketException {

	/**
	 * Constructs a new HttpException with the specified detail message. A
	 * detail message is a String that gives a specific description of the
	 * error.	
	 */
	public HttpException(String msg) {
		super(msg);
	}

	/**
	 * Constructs a new HttpException with no detailed message.
	 */
	public HttpException() {}
}
