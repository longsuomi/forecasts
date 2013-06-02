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
	//get a list of "date", ordering by the date with most sales to least.
	@Query("select DATE(s.dateTime) from Sale s where s.dateTime between :from and :to GROUP BY DATE(s.dateTime) order by count(s.dateTime) DESC")
	List<Date> getTopSalesDay(@Param("from") Date from,@Param("to") Date to);
	
	//get a list of "number of sales", for each day
	@Query("select count(s.dateTime) from Sale s where s.dateTime between :from and :to GROUP BY DATE(s.dateTime) order by count(s.dateTime) DESC")
	List<Long> getTopSalesNo(@Param("from") Date from,@Param("to") Date to);
	
	//count number of sales in total 
	@Query("select count(*) from Sale s where s.dateTime between :minDay and :maxDay and DATE(s.dateTime) not in :outliers")
	long getTotalSaleNo(@Param("minDay") Date minDay,@Param("maxDay") Date maxDay,@Param("outliers") List<Date> outliers);
	//count number of sales in total EACH PRODUCT
	@Query("select count(*) from Sale s where s.product =:product and s.dateTime between :minDay and :maxDay and DATE(s.dateTime) not in :outliers")
	long getTotalSaleNoProd(@Param("product") Product product,@Param("minDay") Date minDay,@Param("maxDay") Date maxDay,@Param("outliers") List<Date> outliers);
	//count number of days in total 
	@Query("select count(distinct dateOnly) from Sale s where s.dateTime between :minDay and :maxDay and DATE(s.dateTime) not in :outliers")
	long getNoOfDayInTotal(@Param("minDay") Date minDay,@Param("maxDay") Date maxDay,@Param("outliers") List<Date> outliers);
	//count number of days in total Each Product
	@Query("select count(distinct dateOnly) from Sale s where s.product =:product and s.dateTime between :minDay and :maxDay and DATE(s.dateTime) not in :outliers")
	long getNoOfDayProd(@Param("product") Product product,@Param("minDay") Date minDay,@Param("maxDay") Date maxDay,@Param("outliers") List<Date> outliers);
	
	
	//*** Checking the effect of ratio: Day of week
	// Checking the total sales for Mon/Tue/Wed....
	@Query("select count(*) from Sale s where s.dayOfWeek = :day and s.dateTime between :minDay and :maxDay and DATE(s.dateTime) not in :outliers")
	long getSaleEachDay(@Param("day") int day,@Param("minDay") Date minDay,@Param("maxDay") Date maxDay,@Param("outliers") List<Date> outliers);
	//Count number of Mon/Tue/Wed... in the whole database 
	@Query("select count(distinct s.dateOnly) from Sale s where s.dayOfWeek = :day and s.dateTime between :minDay and :maxDay and DATE(s.dateTime) not in :outliers")
	long getDayOfWeek(@Param("day") int day,@Param("minDay") Date minDay,@Param("maxDay") Date maxDay,@Param("outliers") List<Date> outliers);
	// Checking the total sales for Mon/Tue/Wed....EACH PRODUCT
	@Query("select count(*) from Sale s where s.dayOfWeek = :day and s.product =:product and s.dateTime between :minDay and :maxDay and DATE(s.dateTime) not in :outliers")
	long getSaleEachDayProd(@Param("day") int day,@Param("product") Product product,@Param("minDay") Date minDay,@Param("maxDay") Date maxDay,@Param("outliers") List<Date> outliers);
	//count number of sales of all products on 1 specific day
	@Query("select count(*) from Sale s where s.dateOnly= :day")
	long getSalesNoSpecificDay(@Param("day") Date day);
	//count number of sales of 1 specific product on 1 specific day
	@Query("select count(*) from Sale s where s.dateOnly= :day and s.product =:product")
	long getSales1DayProd(@Param("day") Date day,@Param("product") Product product);
	
	//*** Checking the effect of ratio: Month of year
	//count number of months in total (-----------maximum 12 months-----------)
	@Query("select min(s.dateTime) from Sale s")
	Date getMinDate();
	@Query("select max(s.dateTime) from Sale s")
	Date getMaxDate();
	// Checking the total sales for Jan/Fer/Mar....
	@Query("select count(*) from Sale s where s.monthOfYear = :month")
	long getSaleEachMonth(@Param("month") int month);
	
	
	
	//***forecast sales in money each day of week
	//get a list of products for 1 day of week (Mon, Tue, Wed...)
	@Query("select s.product from Sale s where s.dateTime between :minDay and :maxDay and DATE(s.dateTime) not in :outliers and s.dayOfWeek = :day ")
	List<Product> getProdListDay(@Param("minDay") Date minDay,@Param("maxDay") Date maxDay,@Param("outliers") List<Date> outliers,@Param("day") int day);
	
	//get a list of products which were sold in 1 specific day
		@Query("select s.product from Sale s where s.dateOnly= :day ")
		List<Product> getProdList1Day(@Param("day") Date day);
			
	//get a list of price variants 
	@Query("select s.price from Sale s where s.dateTime between :minDay and :maxDay and DATE(s.dateTime) not in :outliers and s.dayOfWeek = :day and s.product =:product")
	List<BigDecimal> getPriceList(@Param("minDay") Date minDay,@Param("maxDay") Date maxDay,@Param("outliers") List<Date> outliers,@Param("day") int day, @Param("product") Product product);
		
	//get a list of price variants for 1 specific day
		@Query("select s.price from Sale s where s.dateOnly = :day and s.product =:product")
		List<BigDecimal> getPriceList1Day(@Param("day") Date day, @Param("product") Product product);
				
	// Get the no of sales of 1 product with 1 specific price variant
	@Query("select count(*) from Sale s where s.product = :product and s.dateTime between :minDay and :maxDay and s.price = :price and DATE(s.dateTime) not in :outliers")
	long getNoSalePriceVariant(@Param("product") Product product,@Param("minDay") Date minDay,@Param("maxDay") Date maxDay,@Param("price") BigDecimal price,@Param("outliers") List<Date> outliers);	
	
	// Get the no of sales of 1 product with 1 specific price variant for 1 specific day
		@Query("select count(*) from Sale s where s.product = :product  and s.price = :price and s.dateOnly = :day")
		long getNoSalePriceVariant1Day(@Param("product") Product product,@Param("price") BigDecimal price,@Param("day") Date day);	
		
	
	
	//*** Checking the effect of ratio: Price of the product 

	//get a list of products between 2 time ranges
	@Query("select s.product from Sale s where s.dateTime between :minDay and :maxDay and DATE(s.dateTime) not in :outliers")
	List<Product> getProdList(@Param("minDay") Date minDay,@Param("maxDay") Date maxDay,@Param("outliers") List<Date> outliers);
			
	//Get the min price of 1 product 
	@Query("select MIN(price) from Sale s where s.product = :product and s.dateTime between :minDay and :maxDay and DATE(s.dateTime) not in :outliers")
	BigDecimal getMinPrice(@Param("product") Product product,@Param("minDay") Date minDay,@Param("maxDay") Date maxDay,@Param("outliers") List<Date> outliers);
		
	//Get the max price of 1 product 
	@Query("select MAX(price) from Sale s where s.product = :product and s.dateTime between :minDay and :maxDay and DATE(s.dateTime) not in :outliers")
	BigDecimal getMaxPrice(@Param("product") Product product,@Param("minDay") Date minDay,@Param("maxDay") Date maxDay,@Param("outliers") List<Date> outliers);
		
	//Get the price of 1 product on 1 specific day
	@Query("select price from Sale s where s.product = :product and s.dateOnly= :day")
	BigDecimal getPrice1Day(@Param("product") Product product,@Param("day") Date Day);
	
	// Get the no of sales of 1 product between 2 price limits
	@Query("select count(*) from Sale s where s.product = :product and s.dateTime between :minDay and :maxDay and s.price between :minPrice and :maxPrice and DATE(s.dateTime) not in :outliers")
	long getNoSalePriceRange(@Param("product") Product product,@Param("minDay") Date minDay,@Param("maxDay") Date maxDay,@Param("minPrice") BigDecimal minPrice,@Param("maxPrice") BigDecimal maxPrice,@Param("outliers") List<Date> outliers);
	
	// Get the no of days the product was sold with price between 2 price limits
	@Query("select count(distinct dateOnly) from Sale s where s.product = :product and s.dateTime between :minDay and :maxDay and s.price between :minPrice and :maxPrice and DATE(s.dateTime) not in :outliers")
	long getNoDayPriceRange(@Param("product") Product product,@Param("minDay") Date minDay,@Param("maxDay") Date maxDay,@Param("minPrice") BigDecimal minPrice,@Param("maxPrice") BigDecimal maxPrice,@Param("outliers") List<Date> outliers);
		
}