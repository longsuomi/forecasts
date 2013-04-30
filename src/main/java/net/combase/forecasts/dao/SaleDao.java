/**
 * Copyright 2013 COMBASE AG
 */
package net.combase.forecasts.dao;

import java.math.BigDecimal;
import java.security.Timestamp;
import java.util.Date;
import java.util.List;

import net.combase.forecasts.domain.Product;
import net.combase.forecasts.domain.Sale;

import org.joda.time.DateTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * @author Till Freier
 * 
 */
public interface SaleDao extends JpaRepository<Sale, Long>
{
	@Query("select count(*) from Sale s where s.dateTime between :dateFrom and :dateTo and s.temp between :tempFrom and :tempTo")
	long getSalesForTemp(@Param("dateFrom") Date from, @Param("dateTo") Date to,
		@Param("tempFrom") BigDecimal tempFrom, @Param("tempTo") BigDecimal tempTo);

	// Checking number of sales in each temp range
	@Query("select count(*) from Sale s where s.temp >= :tempFrom and s.temp <= :tempTo")
	long getSalesFromTemp15To20(@Param("tempFrom") BigDecimal tempFrom,@Param("tempTo") BigDecimal tempTo);
	
	@Query("select count(*) from Sale s where s.temp > :tempFrom and s.temp <= :tempTo")
	long getSalesFromTemp20To25(@Param("tempFrom") BigDecimal tempFrom,@Param("tempTo") BigDecimal tempTo);
	
	@Query("select count(*) from Sale s where s.temp > :tempFrom and s.temp <= :tempTo")
	long getSalesFromTemp25To30(@Param("tempFrom") BigDecimal tempFrom,@Param("tempTo") BigDecimal tempTo);
	
	@Query("select count(*) from Sale s where s.temp > :temp30")
	long getSalesWhenTempOver30(@Param("temp30") BigDecimal temp30);
	
	
	//get a list of products between 2 time ranges
	@Query("select s.product from Sale s where s.dateTime between :minDay and :maxDay")
	List<Product> getSaleNoPeriod(@Param("minDay") Date minDay,@Param("maxDay") Date maxDay);
	//count number of sales in total 
	@Query("select count(*) from Sale s where s.dateTime between :minDay and :maxDay")
	long getTotalSaleNo(@Param("minDay") Date minDay,@Param("maxDay") Date maxDay);
	//count number of sales in total EACH PRODUCT
	@Query("select count(*) from Sale s where s.product =:product and s.dateTime between :minDay and :maxDay")
	long getTotalSaleNoProd(@Param("product") Product product,@Param("minDay") Date minDay,@Param("maxDay") Date maxDay);
	//count number of days in total 
	@Query("select count(distinct dateOnly) from Sale s where s.dateTime between :minDay and :maxDay")
	long getNoOfDayInTotal(@Param("minDay") Date minDay,@Param("maxDay") Date maxDay);
	//count number of days in total Each Product
	@Query("select count(distinct dateOnly) from Sale s where s.product =:product and s.dateTime between :minDay and :maxDay ")
	long getNoOfDayProd(@Param("product") Product product,@Param("minDay") Date minDay,@Param("maxDay") Date maxDay);
	
	
	
	//*** Checking the effect of ratio: Temperature
	//Count number of day in a temp range
	@Query("select count(distinct s.dateOnly) from Sale s where s.temp > :tempFrom and s.temp <= :tempTo")
	long getNoOfDayForTempRange(@Param("tempFrom") BigDecimal tempFrom,@Param("tempTo") BigDecimal tempTo);
	
	@Query("select count(distinct s.dateOnly) from Sale s where s.temp > :temp30")
	long getNoOfDayWhenTempOver30(@Param("temp30") BigDecimal temp30);
	
	
	//*** Checking the effect of ratio: Day of week
	// Checking the total sales for Mon/Tue/Wed....
	@Query("select count(*) from Sale s where s.dayOfWeek = :day and s.dateTime between :minDay and :maxDay")
	long getSaleEachDay(@Param("day") int day,@Param("minDay") Date minDay,@Param("maxDay") Date maxDay);
	//Count number of Mon/Tue/Wed... in the whole database 
	@Query("select count(distinct s.dateOnly) from Sale s where s.dayOfWeek = :day and s.dateTime between :minDay and :maxDay")
	long getDayOfWeek(@Param("day") int day,@Param("minDay") Date minDay,@Param("maxDay") Date maxDay);
	// Checking the total sales for Mon/Tue/Wed....EACH PRODUCT
	@Query("select count(*) from Sale s where s.dayOfWeek = :day and s.product =:product and s.dateTime between :minDay and :maxDay")
	long getSaleEachDayProd(@Param("day") int day,@Param("product") Product product,@Param("minDay") Date minDay,@Param("maxDay") Date maxDay);
	//count number of sales of all products on 1 specific day
	@Query("select count(*) from Sale s where s.dateOnly= :day")
	long getSalesNoSpecificDay(@Param("day") Date day);
	//count number of sales of 1 specific product on 1 specific day
	@Query("select count(*) from Sale s where s.dateOnly= :day and s.product =:product")
	long getSales1DayProd(@Param("day") Date day,@Param("product") Product product);
	

	@Query("select DATE(s.dateTime) from Sale s where s.dateTime between :from and :to GROUP BY DATE(s.dateTime) order by count(s.dateTime) DESC")
	List<Date> getTopSalesDay(@Param("from") Date from,@Param("to") Date to);
	
	
	//*** Checking the effect of ratio: Month of year
	//count number of months in total (-----------maximum 12 months-----------)
	@Query("select min(s.dateTime) from Sale s")
	Date getMinDate();
	@Query("select max(s.dateTime) from Sale s")
	Date getMaxDate();
	// Checking the total sales for Jan/Fer/Mar....
	@Query("select count(*) from Sale s where s.monthOfYear = :month")
	long getSaleEachMonth(@Param("month") int month);
	
	
		
	
	//*** Checking the effect of ratio: Price of the product 
	//Get the average price of 1 product 
	@Query("select AVG(price) from Sale s where s.product = :product and s.dateTime between :minDay and :maxDay")
	double getAvgPrice(@Param("product") Product product,@Param("minDay") Date minDay,@Param("maxDay") Date maxDay);
	
	//Get the min price of 1 product 
	@Query("select MIN(price) from Sale s where s.product = :product and s.dateTime between :minDay and :maxDay")
	BigDecimal getMinPrice(@Param("product") Product product,@Param("minDay") Date minDay,@Param("maxDay") Date maxDay);
		
	//Get the max price of 1 product 
	@Query("select MAX(price) from Sale s where s.product = :product and s.dateTime between :minDay and :maxDay")
	BigDecimal getMaxPrice(@Param("product") Product product,@Param("minDay") Date minDay,@Param("maxDay") Date maxDay);
		
	// Get the no of sales of 1 product between 2 price limits
	@Query("select count(*) from Sale s where s.product = :product and s.dateTime between :minDay and :maxDay and s.price between :minPrice and :maxPrice")
	long getNoSalePriceRange(@Param("product") Product product,@Param("minDay") Date minDay,@Param("maxDay") Date maxDay,@Param("minPrice") BigDecimal minPrice,@Param("maxPrice") BigDecimal maxPrice);
	
	// Get number of days that have 1 specific product price
	@Query("select count(distinct s.dateOnly) from Sale s where s.product = :product and s.price between :min and :max")
	long getNoDayPerPriceVar(@Param("product") Product product, @Param("min") BigDecimal min, @Param("max") BigDecimal max);
	
	// Get the total number of sales for 1 specific product price
	@Query("select count(*) from Sale s where s.product = :product and s.price = :price")
	long getNoSalePerPriceVar(@Param("product") Product product, @Param("price") BigDecimal price);

}