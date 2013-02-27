/**
 * Copyright 2013 COMBASE AG
 */
package net.combase.forecasts.domain;


import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

/**
 * @author Till Freier
 * 
 */
@Entity
public class Sale implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5276342266895181387L;

	//
	private Date dateOnly;

	private Date dateTime;
	//
	private int dayOfWeek;

	private BigDecimal discout = BigDecimal.ZERO;

	@Id
	@GeneratedValue
	private Long id;

	private BigDecimal price;

	@ManyToOne
	private Product product;

	private BigDecimal temp;


	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(final Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Sale other = (Sale)obj;
		if (id == null)
		{
			if (other.id != null)
				return false;
		}
		else if (!id.equals(other.id))
			return false;
		return true;
	}

	// ////////
	/**
	 * @return the date
	 */
	public LocalDate getDate()
	{
		return new LocalDate(dateOnly);
	}

	/**
	 * @return the datetime
	 */
	public DateTime getDateTime()
	{
		return new DateTime(dateTime);
	}

	/**
	 * @return the dayOfWeek
	 */
	public int getDayOfWeek()
	{
		return dayOfWeek;
	}

	/**
	 * @return the discout
	 */
	public BigDecimal getDiscout()
	{
		return discout;
	}


	/**
	 * @return the id
	 */
	public Long getId()
	{
		return id;
	}

	/**
	 * @return the price
	 */
	public BigDecimal getPrice()
	{
		return price;
	}

	/**
	 * @return the product
	 */
	public Product getProduct()
	{
		return product;
	}

	/**
	 * @return the temp
	 */
	public BigDecimal getTemp()
	{
		return temp;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	/**
	 * @param date
	 *            the date to set
	 */
	public void setDateTime(final DateTime date)
	{
		dateTime = date.toDate();
		dateOnly = date.toLocalDate().toDate();
		dayOfWeek = date.getDayOfWeek();
	}

	/**
	 * @param dayOfWeek
	 *            the dayOfWeek to set
	 */
	public void setDayOfWeek(final int dayOfWeek)
	{
		this.dayOfWeek = dayOfWeek;
	}


	/**
	 * @param discout
	 *            the discout to set
	 */
	public void setDiscout(final BigDecimal discout)
	{
		this.discout = discout;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	protected void setId(final Long id)
	{
		this.id = id;
	}

	/**
	 * @param price
	 *            the price to set
	 */
	public void setPrice(final BigDecimal price)
	{
		this.price = price;
	}

	/**
	 * @param product
	 *            the product to set
	 */
	public void setProduct(final Product product)
	{
		this.product = product;
	}

	/**
	 * @param temp
	 *            the temp to set
	 */
	public void setTemp(final BigDecimal temp)
	{
		this.temp = temp;
	}


}
