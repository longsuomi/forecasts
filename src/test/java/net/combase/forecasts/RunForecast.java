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
//import joda LocalDate class
import org.joda.time.LocalDate;
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

		for (int day = -20; day < 0; day++)
		{
			final DateMidnight date = new DateMidnight().plusDays(day);
			
			//creating a date without time zone
			final LocalDate DateOnly = new LocalDate().plusDays(day);
			int dayOfWeek = DateOnly.getDayOfWeek();
			
			
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
				// create DateOnly attribute for saleA
				saleA.setDate(DateOnly);
				System.out.println("----The date is " + DateOnly);
				// create Day of Week attribute
				saleA.SetDayofWeek(dayOfWeek);
				System.out.println("----day of week is " + dayOfWeek);
				
				saleA.setDateTime(time);
				saleA.setProduct(products.get(0));
				saleA.setPrice(new BigDecimal(2.56));
				saleA.setTemp(temp);
				saleA = saleDao.save(saleA);
				
				Sale saleB = new Sale();
				saleB.setDate(DateOnly);
				saleB.setDateTime(time);
				saleB.setProduct(products.get(1));
				saleB.setPrice(new BigDecimal(3.56));
				saleB.setTemp(temp);
				saleB = saleDao.save(saleB);
				
				Sale saleC = new Sale();
				saleC.setDate(DateOnly);
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

		//final DateTime from = new DateTime().minusDays(365);

		//final long coldSales = saleDao.getSalesForTemp(from.toDate(), new Date(),
		//	new BigDecimal(0), new BigDecimal(18));
		//final long warmSales = saleDao.getSalesForTemp(from.toDate(), new Date(),
		//	new BigDecimal(18), new BigDecimal(45));

		//System.out.println(coldSales + " sales when it was cold");
		//System.out.println(warmSales + " sales when it was warm");
		
		BigDecimal saleTemp15to20 = new BigDecimal (saleDao.getSalesFromTemp15To20(new BigDecimal(15), new BigDecimal(20)));
		BigDecimal saleTemp20to25 =new BigDecimal ( saleDao.getSalesFromTemp20To25(new BigDecimal(20), new BigDecimal(25)));
		BigDecimal saleTemp25to30 =new BigDecimal ( saleDao.getSalesFromTemp25To30(new BigDecimal(25), new BigDecimal(30)));
		BigDecimal saleTempOver30 =new BigDecimal ( saleDao.getSalesWhenTempOver30(new BigDecimal(30)));
		BigDecimal totalSaleNo	=new BigDecimal ( saleDao.getTotalSaleNo());
		 
		System.out.println(saleTemp15to20 + " sales when temp is from 15 to 20");
		System.out.println(saleTemp20to25 + " sales when temp is from 20 to 25");
		System.out.println(saleTemp25to30 + " sales when temp is from 25 to 30");
		System.out.println(saleTempOver30 + " sales when temp is over 30");
		
		 
		
		
		BigDecimal NoOfDayTemp15to20 = new BigDecimal (saleDao.getNoOfDayForTempRange(new BigDecimal (15), new BigDecimal (20)));
		BigDecimal NoOfDayTemp20to25 = new BigDecimal (saleDao.getNoOfDayForTempRange(new BigDecimal (20), new BigDecimal (25)));
		BigDecimal NoOfDayTemp25to30 = new BigDecimal (saleDao.getNoOfDayForTempRange(new BigDecimal (25), new BigDecimal (30)));
		BigDecimal NoOfDayTempOver30 = new BigDecimal (saleDao.getNoOfDayWhenTempOver30(new BigDecimal(30)));
		System.out.println("No Of Day When Temp Over 30:   " + NoOfDayTempOver30 );
		
		
		BigDecimal CoeffTem15to20 = saleTemp15to20.divide(NoOfDayTemp15to20) .subtract(totalSaleNo.divide(new BigDecimal(60))) ;
		BigDecimal CoeffTem20to25 = saleTemp20to25.divide(NoOfDayTemp20to25) .subtract(totalSaleNo.divide(new BigDecimal(60)));
		BigDecimal CoeffTem25to30 = saleTemp25to30.divide(NoOfDayTemp25to30) .subtract(totalSaleNo.divide(new BigDecimal(60)));
		BigDecimal CoeffTemOver30 = saleTempOver30.divide(NoOfDayTempOver30) .subtract(totalSaleNo.divide(new BigDecimal(60)));
		
		System.out.println("The coefficient of the temp. range 15-20 is: " + CoeffTem15to20);
		System.out.println("The coefficient of the temp. range 20-25 is: " + CoeffTem20to25);
		System.out.println("The coefficient of the temp. range 25-30 is: " + CoeffTem25to30);
		System.out.println("The coefficient of the temp. range over 30 is: " + CoeffTemOver30);
	
		final long NoOfMonday =saleDao.getdayOfWeek(new BigDecimal(1));
		System.out.println("number of Monday is: " + NoOfMonday);
	
	
	}

	@BeforeTransaction
	public void setupData() throws Exception
	{
		executeSqlScript("classpath:clear.sql", false);

		generateProducts();
		generateSales();


	}
}
