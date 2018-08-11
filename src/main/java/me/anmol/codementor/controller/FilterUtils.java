package me.anmol.codementor.controller;

import javax.servlet.http.HttpServletRequest;

public class FilterUtils {
	
	public static Long getUserId(HttpServletRequest request) {
		return new Long(request.getAttribute("userId").toString());
	}
}
