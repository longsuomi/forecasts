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
	private BigDecimal avg;

	public BigDecimal getAvg() {
		return avg;
	}

	public void setAvg(BigDecimal max, BigDecimal min) {
		this.avg = (max.subtract(min)).divide(new BigDecimal(2), 2, RoundingMode.HALF_UP);
	}

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
