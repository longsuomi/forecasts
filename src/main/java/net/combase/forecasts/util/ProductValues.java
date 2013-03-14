/**
 * Copyright 2013 COMBASE AG
 */
package net.combase.forecasts.util;

import java.util.ArrayList;
import java.util.List;

import net.combase.forecasts.domain.Product;

/**
 * @author Till Freier
 * 
 */
public class ProductValues
{
	private final Product product;

	private final List<RangedValue> values = new ArrayList<RangedValue>();


	public ProductValues(final Product product)
	{
		super();
		this.product = product;
	}

	/**
	 * @return the product
	 */
	public Product getProduct()
	{
		return product;
	}

	/**
	 * @return the values
	 */
	public List<RangedValue> getValues()
	{
		return values;
	}

}
