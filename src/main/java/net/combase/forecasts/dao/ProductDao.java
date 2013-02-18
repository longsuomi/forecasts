/**
 * Copyright 2013 COMBASE AG
 */
package net.combase.forecasts.dao;

import net.combase.forecasts.domain.Product;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Till Freier
 * 
 */
public interface ProductDao extends JpaRepository<Product, Long>
{

}
