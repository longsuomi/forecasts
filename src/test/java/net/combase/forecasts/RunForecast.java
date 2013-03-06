/**
 * Copyright 2013 COMBASE AG
 */
package net.combase.forecasts;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import net.combase.forecasts.dao.ProductDao;
import net.combase.forecasts.dao.SaleDao;
import net.combase.forecasts.domain.Product;
import net.combase.forecasts.domain.Sale;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.transaction.annotation.Transactional;

//import joda LocalDate class


/**
 * @author Till Freier Long Nghiem
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

		for (int day = -10; day < 0; day++)
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
				saleA.setPrice(new BigDecimal(3), new BigDecimal(6));
				saleA.setTemp(temp);
				saleA = saleDao.save(saleA);
				//checking the name of the product
				System.out.println(saleA.product.getName());

				Sale saleB = new Sale();
				saleB.setDateTime(time);
				saleB.setProduct(products.get(1));
				saleB.setPrice(new BigDecimal(4),new BigDecimal(6));
				saleB.setTemp(temp);
				saleB = saleDao.save(saleB);
				System.out.println(saleB.product.getName());

				Sale saleC = new Sale();
				saleC.setDateTime(time);
				saleC.setProduct(products.get(2));
				saleC.setPrice(new BigDecimal(2),new BigDecimal(4));
				saleC.setTemp(temp);
				saleC = saleDao.save(saleC);
				System.out.println(saleC.product.getName());
			}


		}
	}

	@Test
	public void run()
	{
		System.out.println("calculate");

		// final DateTime from = new DateTime().minusDays(365);

		// final long coldSales = saleDao.getSalesForTemp(from.toDate(), new Date(),
		// new BigDecimal(0), new BigDecimal(18));
		// final long warmSales = saleDao.getSalesForTemp(from.toDate(), new Date(),
		// new BigDecimal(18), new BigDecimal(45));

		// System.out.println(coldSales + " sales when it was cold");
		// System.out.println(warmSales + " sales when it was warm");

		// total number of day
		final BigDecimal noOfDayTotal = new BigDecimal (saleDao.getNoOfDayInTotal());
		System.out.println("number of day in total: " + noOfDayTotal);
		
		//total number of sales
		final BigDecimal totalSaleNo = new BigDecimal(saleDao.getTotalSaleNo());
		System.out.println("number of sale in total: " + totalSaleNo);
		
		//number of sale in 1 day on average
		final BigDecimal averageSalePerDay = totalSaleNo.divide(noOfDayTotal, RoundingMode.HALF_UP);
		
				
		//*** Checking the effect of ratio: Temperature
		// Checking number of sales in each temp range
		final BigDecimal saleTemp15to20 = new BigDecimal(saleDao.getSalesFromTemp15To20(
			new BigDecimal(15), new BigDecimal(20)));
		final BigDecimal saleTemp20to25 = new BigDecimal(saleDao.getSalesFromTemp20To25(
			new BigDecimal(20), new BigDecimal(25)));
		final BigDecimal saleTemp25to30 = new BigDecimal(saleDao.getSalesFromTemp25To30(
			new BigDecimal(25), new BigDecimal(30)));
		final BigDecimal saleTempOver30 = new BigDecimal(
			saleDao.getSalesWhenTempOver30(new BigDecimal(30)));
		
		System.out.println(saleTemp15to20 + " sales when temp is from 15 to 20");
		
		 //Count number of day in a temp range
		final BigDecimal noOfDayTemp15to20 = new BigDecimal(saleDao.getNoOfDayForTempRange(
			new BigDecimal(15), new BigDecimal(20)));
		final BigDecimal noOfDayTemp20to25 = new BigDecimal(saleDao.getNoOfDayForTempRange(
			new BigDecimal(20), new BigDecimal(25)));
		final BigDecimal noOfDayTemp25to30 = new BigDecimal(saleDao.getNoOfDayForTempRange(
			new BigDecimal(25), new BigDecimal(30)));
		final BigDecimal noOfDayTempOver30 = new BigDecimal(
			saleDao.getNoOfDayWhenTempOver30(new BigDecimal(30)));
		
		System.out.println("No Of Day When Temp is from 15 to 20:   " + noOfDayTemp15to20);

		//Calculate the coefficient of each temp range
		final BigDecimal coeffTem15to20 = saleTemp15to20.divide(noOfDayTemp15to20,
			RoundingMode.HALF_UP).subtract(averageSalePerDay);
		final BigDecimal coeffTem20to25 = saleTemp20to25.divide(noOfDayTemp20to25,
			RoundingMode.HALF_UP).subtract(averageSalePerDay);
		final BigDecimal coeffTem25to30 = saleTemp25to30.divide(noOfDayTemp25to30,
			RoundingMode.HALF_UP).subtract(averageSalePerDay);
		final BigDecimal coeffTemOver30 = saleTempOver30.divide(noOfDayTempOver30,
			RoundingMode.HALF_UP).subtract(averageSalePerDay);

		System.out.println("The coefficient of the temp. range 15-20 is: " + coeffTem15to20);
		System.out.println("The coefficient of the temp. range 20-25 is: " + coeffTem20to25);
		System.out.println("The coefficient of the temp. range 25-30 is: " + coeffTem25to30);
		System.out.println("The coefficient of the temp. range over 30 is: " + coeffTemOver30);

		
		//*** Checking the effect of ratio: Day of week
		// Checking the total sales for Mon/Tue/Wed....
		final BigDecimal saleMon = new BigDecimal (saleDao.getSaleEachDay(1));
		final BigDecimal saleTue = new BigDecimal (saleDao.getSaleEachDay(2));
		final BigDecimal saleWed = new BigDecimal (saleDao.getSaleEachDay(3));
		final BigDecimal saleThu = new BigDecimal (saleDao.getSaleEachDay(4));
		final BigDecimal saleFri = new BigDecimal (saleDao.getSaleEachDay(5));
		final BigDecimal saleSat = new BigDecimal (saleDao.getSaleEachDay(6));
		final BigDecimal saleSun = new BigDecimal (saleDao.getSaleEachDay(7));
			System.out.println("Total sales on Monday is: " + saleMon);
			//System.out.println("Total sales on Tuesday is: " + saleTue);
		
		//Count number of "day of week" in the whole database
		final BigDecimal noOfMon = new BigDecimal (saleDao.getDayOfWeek(1));
		final BigDecimal noOfTue = new BigDecimal (saleDao.getDayOfWeek(2));
		final BigDecimal noOfWed = new BigDecimal (saleDao.getDayOfWeek(3));
		final BigDecimal noOfThu = new BigDecimal (saleDao.getDayOfWeek(4));
		final BigDecimal noOfFri = new BigDecimal (saleDao.getDayOfWeek(5));
		final BigDecimal noOfSat = new BigDecimal (saleDao.getDayOfWeek(6));
		final BigDecimal noOfSun = new BigDecimal (saleDao.getDayOfWeek(7));
			System.out.println("number of Monday is: " + noOfMon);
			//System.out.println("number of Tuesday is: " + noOfTue);
		
		final BigDecimal coeffMon = saleMon.divide(noOfMon,
				RoundingMode.HALF_UP).subtract(averageSalePerDay);
		final BigDecimal coeffTue = saleTue.divide(noOfTue,
				RoundingMode.HALF_UP).subtract(averageSalePerDay);
		final BigDecimal coeffWed = saleWed.divide(noOfWed,
				RoundingMode.HALF_UP).subtract(averageSalePerDay);
		final BigDecimal coeffThu = saleThu.divide(noOfThu,
				RoundingMode.HALF_UP).subtract(averageSalePerDay);
		final BigDecimal coeffFri = saleFri.divide(noOfFri,
				RoundingMode.HALF_UP).subtract(averageSalePerDay);
		final BigDecimal coeffSat = saleSat.divide(noOfSat,
				RoundingMode.HALF_UP).subtract(averageSalePerDay);
		final BigDecimal coeffSun = saleSun.divide(noOfSun,
				RoundingMode.HALF_UP).subtract(averageSalePerDay);
				
		System.out.println("The coefficient of Monday is: " + coeffMon);
		System.out.println("The coefficient of Tueday is: " + coeffTue);
		System.out.println("The coefficient of Wednesday is: " + coeffWed);
		System.out.println("The coefficient of Thursday is: " + coeffThu);
		System.out.println("The coefficient of Friday is: " + coeffFri);
		System.out.println("The coefficient of Saturday is: " + coeffSat);
		System.out.println("The coefficient of Sunday is: " + coeffSun);
		
		//*** Checking the effect of ratio: Price of the product 
		final BigDecimal avgPriceProdA = new BigDecimal(saleDao.getAvgPrice("HotDog")); 
		System.out.println("The average price of HotDog is: " + avgPriceProdA);
		
		//final BigDecimal totalSaleNoProdA = new BigDecimal(saleDao.getTotalSaleNoEachProd("HotDog"));
		//final BigDecimal avgSalePerPriceVar = totalSaleNoProdA.divide(
		//		new BigDecimal(saleDao.getNoPriceVar("HotDog")), RoundingMode.HALF_UP);
		//System.out.println("The average number of sales per price variant is: " + avgSalePerPriceVar); 
		
		
	}

	@BeforeTransaction
	public void setupData() throws Exception
	{
		executeSqlScript("classpath:clear.sql", false);

		generateProducts();
		generateSales();


	}
}
