package com.idan.coupons.utils;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import com.idan.coupons.beans.CompanyEntity;
import com.idan.coupons.beans.CustomerEntity;
import com.idan.coupons.enums.UserType;

public class CookieUtil {

	/**
	 * Creates the cookies for company login.
	 * @param company - company objects of the logging in user
	 * @return - List of cookies with the company parameters.
	 */
	public static List<Cookie> loginCookies(CompanyEntity company) {

		List<Cookie> loginCookies = new ArrayList<Cookie>();

		loginCookies.add(new Cookie("userType", UserType.COMPANY.name()));
		loginCookies.add(new Cookie("userID", Long.toString(company.getCompanyId())));
		loginCookies.add(new Cookie("userName", company.getCompanyName()));
		loginCookies.add(new Cookie("userEmail", company.getCompanyEmail()));

		return loginCookies;
	}

	/**
	 * Creates the cookies for customer login.
	 * @param customer - customer objects of the logging in user
	 * @return - List of cookies with the customer parameters.
	 */
	public static List<Cookie> loginCookies(CustomerEntity customer) {

		List<Cookie> loginCookies = new ArrayList<Cookie>();

		loginCookies.add(new Cookie("userType", UserType.CUSTOMER.name()));
		loginCookies.add(new Cookie("userID", Long.toString(customer.getCustomerId())));
		loginCookies.add(new Cookie("userName", customer.getCustomerName()));
		loginCookies.add(new Cookie("userEmail", customer.getCustomerEmail()));

		return loginCookies;
	}
	
	/**
	 * Creates the cookies for admin login.
	 * @return - List of cookies with the customer parameters.
	 */
	public static List<Cookie> loginCookies() {

		List<Cookie> loginCookies = new ArrayList<Cookie>();

		loginCookies.add(new Cookie("userType", UserType.ADMIN.name()));
		loginCookies.add(new Cookie("userID", "0"));

		return loginCookies;
	}

	/**
	 * Adding the cookies to the browser of the client.
	 * @param response - response that is sent to the client.
	 * @param cookies - List of cookies with the user parameters.
	 * @return
	 */
	public static HttpServletResponse addCookies(HttpServletResponse response, List<Cookie> cookies) {
		for(Cookie c:cookies) {
			c.setPath("/");
			c.setMaxAge(30 *60);
			response.addCookie(c);
		}
		return response;
	}

	

}
