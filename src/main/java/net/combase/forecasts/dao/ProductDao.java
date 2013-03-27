/**
 * Copyright 2013 COMBASE AG
 */
package net.combase.forecasts.dao;

import java.util.Date;
import java.util.List;

import net.combase.forecasts.domain.Product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * @author Till Freier
 * 
 */
public interface ProductDao extends JpaRepository<Product, Long>
{
	
}
