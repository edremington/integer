/*
 *  Copyright (c) 2014 Harvard University and the persons
 *  identified as authors of the code.  All rights reserved. 
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are
 *  met:
 * 
 * 	.    Redistributions of source code must retain the above copyright
 * 		 notice, this list of conditions and the following disclaimer.
 * 
 * 	.    Redistributions in binary form must reproduce the above copyright
 * 		 notice, this list of conditions and the following disclaimer in the
 * 		 documentation and/or other materials provided with the distribution.
 * 
 * 	.    Neither the name of Harvard University, nor the names of specific
 * 		 contributors, may be used to endorse or promote products derived from
 * 		 this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *      
 */
package edu.harvard.integer.common.exception;

// TODO: Auto-generated Javadoc
/**
 * The Enum for common error codes which are not target to a component.
 *
 * @author dchan
 */
public enum CommonErrorCodes implements ErrorCodeInterface {
	
	
	/** The IO error indicate IO error accessing a system resource such as file. */
	IOError("IOError", "IO Error"),
	
	/**  The Run time error is. */
	RunTimeError("RuntimeError", "Run time error"),
	
	/** The Parser error for parser MIB or scripts. */
	ParserError("ParserError", "Parser Error"),
	
	/** The Directory not valid. */
	DirectoryNotValid("DirectoryNotValid", "Directory is not valid");

	/** The error code. */
	private String errorCode = null;
	
	/** The message. */
	private String message = null;
	
	/**
	 * Instantiates a new common error codes.
	 *
	 * @param errorCode the error code
	 * @param message the message
	 */
	private CommonErrorCodes(String errorCode, String message) {
		this.message = message;
	}
	
	/* (non-Javadoc)
	 * @see edu.harvard.integer.common.exception.ErrorCodeInterface#getMessage()
	 */
	@Override
	public String getMessage() {
		return message;
	}

	/* (non-Javadoc)
	 * @see edu.harvard.integer.common.exception.ErrorCodeInterface#getErrorCode()
	 */
	@Override
	public String getErrorCode() {
		return errorCode;
	}

}
