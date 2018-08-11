package me.anmol.codementor.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import me.anmol.codementor.model.InvalidDataException;

@ControllerAdvice
public class RestErrorHandler extends ResponseEntityExceptionHandler {

	private static Logger logger = LoggerFactory.getLogger(RestErrorHandler.class);

	@ExceptionHandler(InvalidDataException.class)
	@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Bad Request")
	public void handleInvalidDataExceptions(Exception e) {
		logger.debug("Exception occured while processing controller.", e);
	}
	
	@ExceptionHandler(Exception.class)
	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Something went wront at server.")
	public void handleAllOtherExceptions(Exception e) {
		logger.debug("Exception occured while processing controller.", e);
	}

}
