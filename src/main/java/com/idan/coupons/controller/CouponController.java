package com.idan.coupons.controller;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.idan.coupons.beans.CouponEntity;
import com.idan.coupons.dao.CouponDao;
import com.idan.coupons.enums.CouponType;
import com.idan.coupons.enums.ErrorType;
import com.idan.coupons.enums.InputErrorType;
import com.idan.coupons.exceptions.ApplicationException;
import com.idan.coupons.utils.DateUtils;
import com.idan.coupons.utils.ValidationUtils;

@Controller
public class CouponController {

	@Autowired
	private CouponDao couponDao;
	
	/**
	 * Creating a coupon in the DB.
	 * @param coupon - the coupon as a Coupon object to add to the DB.
	 * @throws ApplicationException
	 */
	public void createCoupon(CouponEntity coupon)throws ApplicationException{
		//We validate the creation of a new coupon, including null values.
		validateCreateCoupon(coupon);
		
		//If we didn't catch any exception, we call the 'createCoupon' method.
		this.couponDao.createCoupon(coupon);
	}
	
	/**
	 * Getting information of a coupon.
	 * @param couponId - Long parameter represent the ID of the requested coupon.
	 * @return Coupon Object correspond to the provided ID.
	 * @throws ApplicationException
	 */
	public CouponEntity getCouponByCouponId(Long couponId) throws ApplicationException{
		if(couponId==null) {
			throw new ApplicationException(ErrorType.BAD_INPUT, DateUtils.getCurrentDateAndTime()
					+"  Bad input inserted, null value.");
		}
		return this.couponDao.getCouponByCouponId(couponId);
	}
	
	/**
	 * Getting list of all coupons from DB.
	 * @return List collection of all the coupons in the coupon table.
	 * @throws ApplicationException
	 */
	public List<CouponEntity> getAllCoupons() throws ApplicationException{
		
		List<CouponEntity> coupons = couponDao.getAllCoupons();
		
		return coupons;
		
	}
	
	/**
	 * Removing coupon from coupon table.
	 * @param couponID - the couponID as a long to remove from the DB.
	 * @throws ApplicationException
	 */
	public void removeCouponByCouponID(Long couponID) throws ApplicationException {
		
		if(couponID==null) {
			throw new ApplicationException(ErrorType.BAD_INPUT, DateUtils.getCurrentDateAndTime()
					+"  Bad input inserted, null value.");
		}
		couponDao.removeCouponByCouponID(couponID);
		
	}
	
	/**
	 * Removing a coupon-customer relation after customer cancelled a purchase.
	 * @param couponID - Long parameter of the coupon ID.
	 * @param customerID - Long parameter of the customer ID.
	 * @throws ApplicationException
	 */
	@Transactional(propagation=Propagation.REQUIRED)
	public void removeBoughtCouponByCouponIDandCustomerID(Long couponID, Long customerID) throws ApplicationException {
		if(couponID ==null || customerID == null) {
			throw new ApplicationException(ErrorType.BAD_INPUT, DateUtils.getCurrentDateAndTime()
					+"  Bad input inserted, null value.");
		}
		CouponEntity coupon = couponDao.getCouponByCouponId(couponID);
		if(couponDao.removeBoughtCouponByCouponIDandCustomerID(coupon, customerID)) {
			coupon.increaseAmountByOne();
		}
	}
	
	/**
	 * Updating a coupon in the coupon table. All the fields will be updated according to the ID of the coupon object.
	 * @param coupon- the coupon as a Coupon object to be updated in the DB.
	 * @throws ApplicationException
	 */
	public void updateCoupon(CouponEntity coupon) throws ApplicationException {
		
		//We validate the creation of a new coupon
		validateUpdateCoupon(coupon);
		
		//If we didn't catch any exception, we call the 'createCoupon' method.
		this.couponDao.updateCoupon(coupon);
		
	}
	
	/**
	 * Getting a list of coupon from the DB of a certain type.
	 * @param type - type of coupon.
	 * @returnList collection of all the coupons in the coupon table of the requested type.
	 * @throws ApplicationException
	 */
	public List<CouponEntity> getCouponByType(CouponType couponType) throws ApplicationException{
		
		if (couponType == null) {
			throw new ApplicationException(ErrorType.BAD_INPUT, DateUtils.getCurrentDateAndTime()
					+ "   Bad input inserted, null value.");
		}
		
		List<CouponEntity> coupons = couponDao.getCouponByType(couponType);		
		
		return coupons;
		
	}
	
	/**
	 * Getting a list of coupon from the DB up to a certain price.
	 * @param price - Double parameter of the maximum wanted price for a coupon.
	 * @return List collection of all the coupons in the coupon table up to the requested price.
	 * @throws ApplicationException
	 */
	public List<CouponEntity> getCouponsUpToPrice(Double price) throws ApplicationException{
		if(price==null) {
			throw new ApplicationException(ErrorType.BAD_INPUT, DateUtils.getCurrentDateAndTime()
					+"  Bad input inserted, null value.");
		}
		if (!ValidationUtils.isValidPrice(price)) {
			throw new ApplicationException(ErrorType.INVALID_PARAMETER, DateUtils.getCurrentDateAndTime()
					+" Invalid price entered: " + price + ".");
		}
		
		List<CouponEntity> coupons = couponDao.getCouponsUpToPrice(price);
		
		return coupons;
		
	}
	
	/**
	 * Getting a list of coupon from the DB up to a certain expiration date.
	 * @param endDate - String parameter of the latest end date of the coupon.
	 * @return List collection of all the coupons in the coupon table up to the requested date.
	 * @throws ApplicationException
	 */
	public List<CouponEntity> getCouponsUpToEndDate(String endDate) throws ApplicationException{
		
		if(!ValidationUtils.isValidDateFormat(endDate)) {
			throw new ApplicationException(ErrorType.INVALID_PARAMETER, DateUtils.getCurrentDateAndTime()
					+" Invalid date format entered: " + endDate + ".");
		}
		
		List<CouponEntity> coupons = couponDao.getCouponsUpToEndDate(endDate);
		
		return coupons;
		
	}
	
	/**
	 * Getting a list of coupon from the DB from a certain company.
	 * @param companyID - Long parameter of the ID of the requested company.
	 * @return List collection of all the coupons in the coupon table issued by the requested company.
	 * @throws ApplicationException
	 */
	public List<CouponEntity> getCouponsByCompanyID(Long companyID) throws ApplicationException{
		if(companyID==null) {
			throw new ApplicationException(ErrorType.BAD_INPUT, DateUtils.getCurrentDateAndTime()
					+"  Bad input inserted, null value.");
		}
		
		List<CouponEntity> coupons = couponDao.getCouponsByCompanyID(companyID);
		
		return coupons;
		
	}
	
	/**
	 * Getting a list of coupon from the DB purchased from a certain customer.
	 * @param customerID - Long parameter of the ID of the requested customer.
	 * @return List collection of all the coupons in the coupon table bought by the requested customer.
	 * @throws ApplicationException
	 */
	public List<CouponEntity> getCouponsByCustomerID(Long customerID) throws ApplicationException{
		if(customerID==null) {
			throw new ApplicationException(ErrorType.BAD_INPUT, DateUtils.getCurrentDateAndTime()
					+"  Bad input inserted, null value.");
		}
		List<CouponEntity> coupons = couponDao.getCouponsByCustomerID(customerID);
		
		return coupons;
		
	}
	
	/**
	 * Getting a list of coupon of the newest coupons from the DB.
	 * @return List collection of the five newest coupons to display on the homepage.
	 * @throws ApplicationException
	 */
	public List<CouponEntity> getNewestCoupon() throws ApplicationException{
		List<CouponEntity> coupons = couponDao.getNewestCoupon();
		
		return coupons;
	}
	
	/**
	 * Adding a coupon-customer relation after customer bought a coupon.
	 * @param customerID - Long parameter of the customer ID.
	 * @param couponID - Long parameter of the coupon ID.
	 * @throws ApplicationException
	 */
	@Transactional(propagation=Propagation.REQUIRED)
	public void buyCoupon(Long customerID, Long couponID) throws ApplicationException {
		if(couponID==null) {
			throw new ApplicationException(ErrorType.BAD_INPUT, DateUtils.getCurrentDateAndTime()
					+"  Bad input inserted, null value of couponID.");
		}
		
		if(customerID==null) {
			throw new ApplicationException(ErrorType.BAD_INPUT, DateUtils.getCurrentDateAndTime()
					+"  Bad input inserted, null value of customerID.");
		}
		CouponEntity couponToBuy = this.couponDao.getCouponByCouponId(couponID);
		
		// Checking if there are remaining amount for coupon.
		if(couponToBuy.getCouponAmount() <= 0) {
			
			throw new ApplicationException(ErrorType.GENERAL_ERROR, DateUtils.getCurrentDateAndTime()
					+" Coupon out of stock.");
		}
		
		if(this.couponDao.isCouponAlreadyPurchasedByCustomerID(couponID,customerID)) {
			throw new ApplicationException(ErrorType.GENERAL_ERROR, DateUtils.getCurrentDateAndTime()
					+" Coupon already purchesed.");
		}
		this.couponDao.buyCoupon(customerID, couponToBuy);
		
		// After purchase the amount of the coupon is updated in the DB
		couponToBuy.reduceAmountByOne();
//		this.couponDao.updateCoupon(couponToBuy);
		
		
	}
		
	/**
	 * Deleting expired coupon from DB.
	 * @throws ApplicationException
	 */
	public void deleteExpiredCoupon() throws ApplicationException {
//		CouponDao couponDao = new CouponDao();
		// Getting today's date.
		GregorianCalendar today = new GregorianCalendar();

		String todayStr = DateUtils.dateToStrConverter(today);
		couponDao.removeCouponByEndDate(todayStr);
		
	}

	/**
	 * Validating creation of coupon. If invalid ApplicationException will be thrown.
	 * @param coupon - Coupon object to be validated.
	 * @throws ApplicationException
	 */
	private void validateCreateCoupon(CouponEntity coupon) throws ApplicationException{
		
		validateCoupon(coupon);
		
		//We check if the coupon's name is already exist in the DB
		if (this.couponDao.isCouponExistByTitle(coupon.getCouponTitle())) {
			throw new ApplicationException(ErrorType.NAME_IS_ALREADY_EXISTS, DateUtils.getCurrentDateAndTime()
					+" Create coupon has failed."
					+"\nThe user attempted to create a new coupon using a name that is already exists."
					+"\nCoupon Name = "+coupon.getCouponTitle());
		}
		
	}

	/**
	 * Validating update of coupon. If invalid ApplicationException will be thrown.
	 * @param coupon - Coupon object to be validated.
	 * @throws ApplicationException
	 */
	private void validateUpdateCoupon(CouponEntity coupon) throws ApplicationException {
		validateCoupon(coupon);
		
		//We check if the change to coupon's name is already exist in the DB
		if (this.couponDao.isCouponTitleExistForUpdate(coupon.getCouponId() ,coupon.getCouponTitle())) {
			throw new ApplicationException(ErrorType.NAME_IS_ALREADY_EXISTS, DateUtils.getCurrentDateAndTime()
					+" Update coupon has failed."
					+"\nThe user attempted to update coupon using a name that is already exists."
					+"\nCoupon Name = "+coupon.getCouponTitle());
		}
		
	}

	/**
	 * Validating parameters of a coupon. In case of invalid parameter, ApplicationException will be thrown.
	 * @param company - company object to validate.
	 * @throws ApplicationException
	 */
	private void validateCoupon(CouponEntity coupon) throws ApplicationException {
		
		if(coupon == null) {
			throw new ApplicationException(ErrorType.BAD_INPUT, DateUtils.getCurrentDateAndTime()
					+ "   Bad input inserted, null value.");
		}
		
		List<InputErrorType> errorTypes = new ArrayList<InputErrorType>();
		boolean isDateValide = true;
		
		if(!ValidationUtils.isValidNameFormat(coupon.getCouponTitle())) {
			
			errorTypes.add(InputErrorType.INVALID_NAME);
		}
		
		if(!ValidationUtils.isValidDateFormat(coupon.getCouponStartDate())) {
			
			errorTypes.add(InputErrorType.INVALID_START_DATE);
			isDateValide = false;
		}
		
		if(!ValidationUtils.isValidDateFormat(coupon.getCouponEndDate())) {
			
			errorTypes.add(InputErrorType.INVALID_END_DATE);
			isDateValide = false;
		}
		
		if(!ValidationUtils.isValidAmount(coupon.getCouponAmount())){
			errorTypes.add(InputErrorType.INVALID_AMOUNT);
		}
		
		if(!ValidationUtils.isValidMessageFormat(coupon.getCouponMessage())){
			errorTypes.add(InputErrorType.INVALID_MESSAGE);
		}
		
		if(!ValidationUtils.isValidPrice(coupon.getCouponPrice())) {
			errorTypes.add(InputErrorType.INVALID_PRICE);
		}
		
		if(isDateValide && ValidationUtils.isStartEndDateMiss(coupon.getCouponStartDate(), coupon.getCouponEndDate())) {
			errorTypes.add(InputErrorType.INVALID_END_BEFORE_START);
		}
		
		if(isDateValide && ValidationUtils.isStartDateAlreadyPassed(coupon.getCouponStartDate())) {
			errorTypes.add(InputErrorType.INVALID_START_ALREADY_PASSED);
		}
		
		if(!errorTypes.isEmpty()) {
			throw new ApplicationException(ErrorType.INVALID_PARAMETER, DateUtils.getCurrentDateAndTime()
					+" Create coupon has failed."
					+"\nOne or more of the fields are incorrect.", errorTypes);
		}
	}
		
}
