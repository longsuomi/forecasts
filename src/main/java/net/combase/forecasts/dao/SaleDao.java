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
}
