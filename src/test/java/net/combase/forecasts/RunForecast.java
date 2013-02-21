/**
 * Copyright 2013 COMBASE AG
 */
package net.combase.forecasts;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;

import net.combase.forecasts.dao.ProductDao;
import net.combase.forecasts.dao.SaleDao;
import net.combase.forecasts.domain.Product;
import net.combase.forecasts.domain.Sale;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.transaction.annotation.Transactional;


/**
 * @author Till Freier
 * 			Long Nghiem
 * 
 */
@ContextConfiguration("classpath:application-context.xml")
public class RunForecast extends AbstractTransactionalJUnit4SpringContextTests
{
	private static final BigDecimal TEMP_MAX = new BigDecimal(34);
	private static final BigDecimal TEMP_MIN = new BigDecimal(15);

	@Autowired
	private ProductDao productDao;

	@Autowired
	private SaleDao saleDao;


	/**
	 * 
	 */
	@Transactional
	private void generateProducts()
	{
		Product prodA = new Product();
		prodA.setName("Icecream");
		Product prodB = new Product();
		prodA.setName("Softdrink");
		Product prodC = new Product();
		prodA.setName("HotDog");

		prodA = productDao.saveAndFlush(prodA);
		prodB = productDao.saveAndFlush(prodB);
		prodC = productDao.saveAndFlush(prodC);
	}

	/**
	 * 
	 */
	@Transactional
	private void generateSales()
	{
		// generate Sales
		final BigDecimal tempDif = RunForecast.TEMP_MAX.subtract(RunForecast.TEMP_MIN);
		final int avg = (int)(100 * Math.random()) + 100;
		final int maxOffset = (int)(40 * Math.random()) + 20;

		System.out.println("average: " + avg + "  max. offset: " + maxOffset);

		final List<Product> products = productDao.findAll();

		for (int day = -60; day < 0; day++)
		{
			final DateMidnight date = new DateMidnight().plusDays(day);
			final BigDecimal temp = RunForecast.TEMP_MIN.add(
				tempDif.multiply(new BigDecimal(Math.random()))).setScale(0, RoundingMode.HALF_UP);

			final int sales = avg + (int)(maxOffset * 2d * Math.random()) - maxOffset;

			System.out.println("generate " + sales + " sales for " + date + " with " + temp + "C");

			for (int sale = 0; sale < sales; sale++)
			{
				final DateTime time = date.toDateTime()
					.plusHours(8)
					.plusSeconds((int)(3600 * 8 * Math.random()));
				System.out.println("generate sale  " + time);

				Sale saleA = new Sale();
				saleA.setDateTime(time);
				saleA.setProduct(products.get(0));
				saleA.setPrice(new BigDecimal(2.56));
				saleA.setTemp(temp);
				saleA = saleDao.save(saleA);
				Sale saleB = new Sale();
				saleB.setDateTime(time);
				saleB.setProduct(products.get(1));
				saleB.setPrice(new BigDecimal(3.56));
				saleB.setTemp(temp);
				saleB = saleDao.save(saleB);
				Sale saleC = new Sale();
				saleC.setDateTime(time);
				saleC.setProduct(products.get(2));
				saleC.setPrice(new BigDecimal(2.18));
				saleC.setTemp(temp);
				saleC = saleDao.save(saleC);
			}


		}
	}

	@Test
	public void run()
	{
		System.out.println("calculate");

		final DateTime from = new DateTime().minusDays(365);

		final long coldSales = saleDao.getSalesForTemp(from.toDate(), new Date(),
			new BigDecimal(0), new BigDecimal(18));
		final long warmSales = saleDao.getSalesForTemp(from.toDate(), new Date(),
			new BigDecimal(18), new BigDecimal(45));

		System.out.println(coldSales + " sales when it was cold");
		System.out.println(warmSales + " sales when it was warm");
	}

	@BeforeTransaction
	public void setupData() throws Exception
	{
		executeSqlScript("classpath:clear.sql", false);

		generateProducts();
		generateSales();


	}
}
