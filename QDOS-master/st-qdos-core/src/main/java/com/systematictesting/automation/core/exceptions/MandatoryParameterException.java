/*
 * Copyright (c) Jan 11, 2017 Systematic Testing Ltd. (www.systematictesting.com) to Present..
 * All rights reserved. 
 */
package com.systematictesting.automation.core.exceptions;

import java.util.logging.Level;
import java.util.logging.Logger;

public class MandatoryParameterException extends RuntimeException{

	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(MandatoryParameterException.class.getName());

	public MandatoryParameterException(String message){
		log.log(Level.SEVERE,message);
	}
}
