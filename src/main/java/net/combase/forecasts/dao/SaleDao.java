/**
 * Copyright 2013 COMBASE AG
 */
package net.combase.forecasts.dao;

import java.math.BigDecimal;
import java.util.Date;

import net.combase.forecasts.domain.Sale;

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
	
	@Query("select count(*) from Sale")
	long getTotalSaleNo();
	
	 //Count number of day in a temp range
	@Query("select count(distinct date(s.dateTime)) from Sale s where s.temp > :tempFrom and s.temp <= :tempTo")
	long getNoOfDayForTempRange(@Param("tempFrom") BigDecimal tempFrom,@Param("tempTo") BigDecimal tempTo);
	@Query("select count(distinct date(s.dateTime)) from Sale s where s.temp > :temp30")
	long getNoOfDayWhenTempOver30(@Param("temp30") BigDecimal temp30);
	
	//Count number of day of week
	@Query("select count(distinct date(s.dateTime)) from Sale s where dayofweek(s.dateTime) = :day")
	long getdayOfWeek(@Param("day") BigDecimal day);
	
	
	
	
}