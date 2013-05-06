/**
 * Copyright 2013 COMBASE AG
 */
package net.combase.forecasts.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author Till Freier
 * 
 */
public class RangedValue
{
	private final BigDecimal max;
	private final BigDecimal min;

	private BigDecimal value;

	public RangedValue(final BigDecimal min, final BigDecimal max)
	{
		super();
		this.min = min;
		this.max = max;
	}

	/**
	 * @return the max
	 */
	public BigDecimal getMax()
	{
		return max;
	}

	/**
	 * @return the min
	 */
	public BigDecimal getMin()
	{
		return min;
	}

	/**
	 * @return the value
	 */
	public BigDecimal getValue()
	{
		return value;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(final BigDecimal value)
	{
		this.value = value;
	}
}
