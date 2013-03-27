/**
 * Copyright 2013 COMBASE AG
 */
package net.combase.forecasts;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.combase.forecasts.dao.ProductDao;
import net.combase.forecasts.dao.SaleDao;
import net.combase.forecasts.domain.Product;
import net.combase.forecasts.domain.Sale;
import net.combase.forecasts.util.ProductValues;
import net.combase.forecasts.util.RangedValue;


import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.Months;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
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
		prodB.setName("Softdrink");
		Product prodC = new Product();
		prodC.setName("HotDog");

		prodA = productDao.saveAndFlush(prodA);
		prodB = productDao.saveAndFlush(prodB);
		prodC = productDao.saveAndFlush(prodC);
	}

	/**
	 * 
	 */
	/*
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
			final BigDecimal priceProdA = new BigDecimal(1.5).add(
				(new BigDecimal(Math.random()).multiply((new BigDecimal(2).subtract(new BigDecimal(
					1.5)))))).setScale(1, RoundingMode.HALF_UP);
			final BigDecimal priceProdB = new BigDecimal(2).add(
				(new BigDecimal(Math.random()).multiply((new BigDecimal(2.6).subtract(new BigDecimal(
					2))))))
				.setScale(1, RoundingMode.HALF_UP);
			final BigDecimal priceProdC = new BigDecimal(3).add(
				(new BigDecimal(Math.random()).multiply((new BigDecimal(4).subtract(new BigDecimal(
					3)))))).setScale(1, RoundingMode.HALF_UP);

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
				saleA.setPrice(priceProdA);
				saleA.setTemp(temp);
				saleA = saleDao.save(saleA);
				System.out.println("dateOnly is  " + saleA.dateOnly);
				Sale saleB = new Sale();
				saleB.setDateTime(time);
				saleB.setProduct(products.get(1));
				saleB.setPrice(priceProdB);
				saleB.setTemp(temp);
				saleB = saleDao.save(saleB);

				Sale saleC = new Sale();
				saleC.setDateTime(time);
				saleC.setProduct(products.get(2));
				saleC.setPrice(priceProdC);
				saleC.setTemp(temp);
				saleC = saleDao.save(saleC);

			}


		}
	}
	 */
	@Test
	public void run()
	{
		System.out.println("calculate");
		

		// final DateTime from = new DateTime().minusDays(365);

		// final long coldSales = saleDao.getSalesForTemp(from.toDate(), new Date(),
		// new BigDecimal(0), new BigDecimal(18));
		// final long warmSales = saleDao.getSalesForTemp(from.toDate(), new Date(),
		// new BigDecimal(18), new BigDecimal(45));
		
		 java.util.Calendar myCalendar = java.util.Calendar.getInstance();
		 myCalendar.set(2012, 0, 1, 0, 0, 1);
	        DateTime miniDay = new DateTime(myCalendar);
	        Date minDay = miniDay.toDate();
	     myCalendar.set(2012, 2, 31, 23, 59, 59);
	      	DateTime maxiDay = new DateTime(myCalendar);
	      	Date maxDay = maxiDay.toDate();
	      System.out.println("Starting time: " + minDay);
	      System.out.println("Ending time: " + maxDay);
	      
		// total number of day
		final BigDecimal noOfDayTotal = new BigDecimal(saleDao.getNoOfDayInTotal(minDay,maxDay));
		System.out.println("number of day in total: " + noOfDayTotal);

		// total number of sales
		final BigDecimal totalSaleNo = new BigDecimal(saleDao.getTotalSaleNo(minDay,maxDay));
		System.out.println("number of sale in total: " + totalSaleNo);

		// number of sale in 1 day on average
		final BigDecimal averageSalePerDay = totalSaleNo.divide(noOfDayTotal, 2,
			RoundingMode.HALF_UP);

		/*
		// *** Checking the effect of ratio: Temperature
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

		// Count number of day in a temp range
		final BigDecimal noOfDayTemp15to20 = new BigDecimal(saleDao.getNoOfDayForTempRange(
			new BigDecimal(15), new BigDecimal(20)));
		final BigDecimal noOfDayTemp20to25 = new BigDecimal(saleDao.getNoOfDayForTempRange(
			new BigDecimal(20), new BigDecimal(25)));
		final BigDecimal noOfDayTemp25to30 = new BigDecimal(saleDao.getNoOfDayForTempRange(
			new BigDecimal(25), new BigDecimal(30)));
		final BigDecimal noOfDayTempOver30 = new BigDecimal(
			saleDao.getNoOfDayWhenTempOver30(new BigDecimal(30)));

		System.out.println("No Of Day When Temp is from 15 to 20:   " + noOfDayTemp15to20);

		// Calculate the coefficient of each temp range

		final BigDecimal coeffTem15to20;
		if (noOfDayTemp15to20.doubleValue() == BigDecimal.ZERO.doubleValue())
		{
			coeffTem15to20 = new BigDecimal(0);
		}
		else
		{
			coeffTem15to20 = saleTemp15to20.divide(noOfDayTemp15to20, 2, RoundingMode.HALF_UP)
				.subtract(averageSalePerDay);
		}

		final BigDecimal coeffTem20to25;

		if (noOfDayTemp20to25.doubleValue() == BigDecimal.ZERO.doubleValue())
		{
			coeffTem20to25 = new BigDecimal(0);
		}
		else
		{
			coeffTem20to25 = saleTemp20to25.divide(noOfDayTemp20to25, 2, RoundingMode.HALF_UP)
				.subtract(averageSalePerDay);
		}

		final BigDecimal coeffTem25to30;
		if (noOfDayTemp25to30.doubleValue() == BigDecimal.ZERO.doubleValue())
		{
			coeffTem25to30 = new BigDecimal(0);
		}
		else
		{
			coeffTem25to30 = saleTemp25to30.divide(noOfDayTemp25to30, 2, RoundingMode.HALF_UP)
				.subtract(averageSalePerDay);
		}

		final BigDecimal coeffTemOver30;
		if (noOfDayTempOver30.doubleValue() == BigDecimal.ZERO.doubleValue())
		{
			coeffTemOver30 = new BigDecimal(0);
		}
		else
		{
			coeffTemOver30 = saleTempOver30.divide(noOfDayTempOver30, 2, RoundingMode.HALF_UP)
				.subtract(averageSalePerDay);
		}

		System.out.println("The coefficient of the temp. range 15-20 is: " + coeffTem15to20);
		System.out.println("The coefficient of the temp. range 20-25 is: " + coeffTem20to25);
		System.out.println("The coefficient of the temp. range 25-30 is: " + coeffTem25to30);
		System.out.println("The coefficient of the temp. range over 30 is: " + coeffTemOver30);
		 */

		/*
		// *** Checking the effect of ratio: Day of week (FOR ALL PRODUCTS)
		// Checking the total sales for Mon/Tue/Wed....
		final BigDecimal saleMon = new BigDecimal(saleDao.getSaleEachDay(1));
		final BigDecimal saleTue = new BigDecimal(saleDao.getSaleEachDay(2));
		final BigDecimal saleWed = new BigDecimal(saleDao.getSaleEachDay(3));
		final BigDecimal saleThu = new BigDecimal(saleDao.getSaleEachDay(4));
		final BigDecimal saleFri = new BigDecimal(saleDao.getSaleEachDay(5));
		final BigDecimal saleSat = new BigDecimal(saleDao.getSaleEachDay(6));
		final BigDecimal saleSun = new BigDecimal(saleDao.getSaleEachDay(7));
			
		// Count number of "day of week" in the whole database
		final BigDecimal noOfMon = new BigDecimal(saleDao.getDayOfWeek(1));
		final BigDecimal noOfTue = new BigDecimal(saleDao.getDayOfWeek(2));
		final BigDecimal noOfWed = new BigDecimal(saleDao.getDayOfWeek(3));
		final BigDecimal noOfThu = new BigDecimal(saleDao.getDayOfWeek(4));
		final BigDecimal noOfFri = new BigDecimal(saleDao.getDayOfWeek(5));
		final BigDecimal noOfSat = new BigDecimal(saleDao.getDayOfWeek(6));
		final BigDecimal noOfSun = new BigDecimal(saleDao.getDayOfWeek(7));
		
		
		final BigDecimal coeffMon = saleMon.divide(noOfMon, 2, RoundingMode.HALF_UP).subtract(
			averageSalePerDay);
		final BigDecimal coeffTue = saleTue.divide(noOfTue, 2, RoundingMode.HALF_UP).subtract(
			averageSalePerDay);
		final BigDecimal coeffWed = saleWed.divide(noOfWed, 2, RoundingMode.HALF_UP).subtract(
			averageSalePerDay);
		final BigDecimal coeffThu = saleThu.divide(noOfThu, 2, RoundingMode.HALF_UP).subtract(
			averageSalePerDay);
		final BigDecimal coeffFri = saleFri.divide(noOfFri, 2, RoundingMode.HALF_UP).subtract(
			averageSalePerDay);
		final BigDecimal coeffSat = saleSat.divide(noOfSat, 2, RoundingMode.HALF_UP).subtract(
			averageSalePerDay);
		final BigDecimal coeffSun = saleSun.divide(noOfSun, 2, RoundingMode.HALF_UP).subtract(
			averageSalePerDay);
		
		System.out.println("The coefficient of Monday is: " + coeffMon);
		System.out.println("The coefficient of Tueday is: " + coeffTue);
		System.out.println("The coefficient of Wednesday is: " + coeffWed);
		System.out.println("The coefficient of Thursday is: " + coeffThu);
		System.out.println("The coefficient of Friday is: " + coeffFri);
		System.out.println("The coefficient of Saturday is: " + coeffSat);
		System.out.println("The coefficient of Sunday is: " + coeffSun);
		*/
		
		
		final Map<Product, ProductValues> valueMap = new HashMap<Product, ProductValues>();
		
		
		/*
		for (final Product product : productDao.findAll()){
				
			final ProductValues values = new ProductValues(product);
			
			final BigDecimal testing = new BigDecimal(saleDao.getTotalSaleNoProd(product, minDay, maxDay));
			System.out.println("testing " + testing);
			System.out.println("min Price is: "+ saleDao.getMinPrice(product,minDay,maxDay));
			System.out.println(new BigDecimal(saleDao.getAvgPrice(product,minDay,maxDay)));
			values.getValues().add(new RangedValue(saleDao.getMinPrice(product,minDay,maxDay), new BigDecimal(saleDao.getAvgPrice(product,minDay,maxDay))));
			values.getValues().add(new RangedValue(new BigDecimal(saleDao.getAvgPrice(product,minDay,maxDay)), saleDao.getMaxPrice(product, minDay, maxDay)));

			valueMap.put(product, values);
		}
		*/
		
		final List<Product> products =productDao.findAll();		
		for (Product product : products){
			//***ratio: day of week, for EACH PRODUCT
			// total number of day EACH PRODUCT
			final BigDecimal noOfDayTotalProd = new BigDecimal(saleDao.getNoOfDayProd(product,minDay,maxDay));
			System.out.println("no of days \""+ product.getName()+"\" is sold: " +noOfDayTotalProd);
			if (noOfDayTotalProd.compareTo(BigDecimal.ZERO)!=0){
							
				// total number of sales EACH PRODUCT
				final BigDecimal totalSaleNoProd = new BigDecimal(saleDao.getTotalSaleNoProd(product,minDay,maxDay));
				System.out.println("number of sales of this product in this period: "+totalSaleNoProd);
				// number of sale in 1 day on average EACH PRODUCT
				final BigDecimal averageSalePerDayProd = totalSaleNoProd.divide(noOfDayTotalProd, 2,
				RoundingMode.HALF_UP);
				System.out.println("Number of sales per day, on average: "+averageSalePerDayProd);							
								
				// Checking the total sales for Mon/Tue/Wed....		
				final BigDecimal saleMonProd = new BigDecimal(saleDao.getSaleEachDayProd(1, product,minDay,maxDay));
				final BigDecimal saleTueProd = new BigDecimal(saleDao.getSaleEachDayProd(2, product,minDay,maxDay));
				final BigDecimal saleWedProd = new BigDecimal(saleDao.getSaleEachDayProd(3, product,minDay,maxDay));
				final BigDecimal saleThuProd = new BigDecimal(saleDao.getSaleEachDayProd(4, product,minDay,maxDay));
				final BigDecimal saleFriProd = new BigDecimal(saleDao.getSaleEachDayProd(5, product,minDay,maxDay));
				final BigDecimal saleSatProd = new BigDecimal(saleDao.getSaleEachDayProd(6, product,minDay,maxDay));
				final BigDecimal saleSunProd = new BigDecimal(saleDao.getSaleEachDayProd(7, product,minDay,maxDay));
				
				System.out.println("no of sales on Monday: "+saleMonProd);
				System.out.println("no of sales on Tueday: " +saleTueProd);
				System.out.println("no of sales on Wednesday: "+saleWedProd);
				System.out.println("no of sales on Thursday: "+saleThuProd);
				System.out.println("no of sales on Friday: "+saleFriProd);
				System.out.println("no of sales on Saturday: "+saleSatProd);
				System.out.println("no of sales on Sunday: "+saleSunProd);
						
				// Count number of "day of week" in the whole database
				final BigDecimal noOfMonProd = new BigDecimal(saleDao.getDayOfWeekProd(1,minDay,maxDay));
				final BigDecimal noOfTueProd = new BigDecimal(saleDao.getDayOfWeekProd(2,minDay,maxDay));
				final BigDecimal noOfWedProd = new BigDecimal(saleDao.getDayOfWeekProd(3,minDay,maxDay));
				final BigDecimal noOfThuProd = new BigDecimal(saleDao.getDayOfWeekProd(4,minDay,maxDay));
				final BigDecimal noOfFriProd = new BigDecimal(saleDao.getDayOfWeekProd(5,minDay,maxDay));
				final BigDecimal noOfSatProd = new BigDecimal(saleDao.getDayOfWeekProd(6,minDay,maxDay));
				final BigDecimal noOfSunProd = new BigDecimal(saleDao.getDayOfWeekProd(7,minDay,maxDay));
				System.out.println("no of Monday in this period: "+noOfMonProd);
				System.out.println("no of Tuesday in this period: "+noOfTueProd);
				System.out.println("no of Wednesday in this period: "+noOfWedProd);
				System.out.println("no of Thursday in this period: "+noOfThuProd);
				System.out.println("no of Friday in this period: "+noOfFriProd);
				System.out.println("no of Saturday in this period: "+noOfSatProd);
				System.out.println("no of Sunday in this period: "+noOfSunProd);
						
				BigDecimal coeffMonProd,coeffTueProd,coeffWedProd,coeffThuProd,coeffFriProd,coeffSatProd,coeffSunProd;
				if (noOfMonProd.compareTo(BigDecimal.ZERO) == 1){
					coeffMonProd = saleMonProd.divide(noOfMonProd, 2, RoundingMode.HALF_UP).subtract(
							averageSalePerDayProd);
				} else {
					coeffMonProd = BigDecimal.ZERO;
				}
				if (noOfTueProd.compareTo(BigDecimal.ZERO) == 1){
					coeffTueProd = saleTueProd.divide(noOfTueProd, 2, RoundingMode.HALF_UP).subtract(
							averageSalePerDayProd);
				} else {
					coeffTueProd = BigDecimal.ZERO;
				}
				if (noOfWedProd.compareTo(BigDecimal.ZERO) == 1){
					coeffWedProd = saleWedProd.divide(noOfWedProd, 2, RoundingMode.HALF_UP).subtract(
							averageSalePerDayProd);
				} else {
					coeffWedProd = BigDecimal.ZERO;
				}
				if (noOfThuProd.compareTo(BigDecimal.ZERO) == 1){
					coeffThuProd = saleThuProd.divide(noOfThuProd, 2, RoundingMode.HALF_UP).subtract(
							averageSalePerDayProd);
				} else {
					coeffThuProd = BigDecimal.ZERO;
				}
				if (noOfFriProd.compareTo(BigDecimal.ZERO) == 1){
					coeffFriProd = saleFriProd.divide(noOfFriProd, 2, RoundingMode.HALF_UP).subtract(
							averageSalePerDayProd);
				} else {
					coeffFriProd = BigDecimal.ZERO;
				}
				if (noOfSatProd.compareTo(BigDecimal.ZERO) == 1){
					coeffSatProd = saleSatProd.divide(noOfSatProd, 2, RoundingMode.HALF_UP).subtract(
							averageSalePerDayProd);
				} else {
					coeffSatProd = BigDecimal.ZERO;
				}
				if (noOfSunProd.compareTo(BigDecimal.ZERO) == 1){
					coeffSunProd = saleSunProd.divide(noOfSunProd, 2, RoundingMode.HALF_UP).subtract(
							averageSalePerDayProd);
				} else {
					coeffSunProd = BigDecimal.ZERO;
				}
				
				System.out.println("The coefficient of Monday of "+ product.getName()+ " is: " + coeffMonProd);
				System.out.println("The coefficient of Tueday of "+ product.getName()+ " is: " + coeffTueProd);
				System.out.println("The coefficient of Wednesday of "+ product.getName()+ " is: " + coeffWedProd);
				System.out.println("The coefficient of Thursday of "+ product.getName()+ " is: " + coeffThuProd);
				System.out.println("The coefficient of Friday of "+ product.getName()+ " is: " + coeffFriProd);
				System.out.println("The coefficient of Saturday of "+ product.getName()+ " is: " + coeffSatProd);
				System.out.println("The coefficient of Sunday of "+ product.getName()+ " is: " + coeffSunProd);
			
				//***ratio: price
				// calculating x mean
				final BigDecimal avgPriceProd =new BigDecimal(saleDao.getAvgPrice(product,minDay,maxDay)).setScale(
					4, RoundingMode.HALF_UP);
				
				System.out.println("The average price of \"" + product.getName() + "\" is: " +
					avgPriceProd);
				
				// calculating y mean
				final BigDecimal avgSalePerPriceVarProd = totalSaleNoProd.divide(
					new BigDecimal(saleDao.getNoPriceVar(product,minDay,maxDay)), 4, RoundingMode.HALF_UP);
			System.out.println("number of price variants: "+ saleDao.getNoPriceVar(product,minDay,maxDay));
			System.out.println("average sale per price variant "+ avgSalePerPriceVarProd);
			
			final ProductValues values = new ProductValues(product);
			values.getValues().add(new RangedValue(saleDao.getMinPrice(product,minDay,maxDay), new BigDecimal(saleDao.getAvgPrice(product,minDay,maxDay))));
			values.getValues().add(new RangedValue(new BigDecimal(saleDao.getAvgPrice(product,minDay,maxDay)), saleDao.getMaxPrice(product, minDay, maxDay)));

			valueMap.put(product, values);			
			
					
			} else {
				System.out.println("---\""+ product.getName()+"\" was not sold in this period.");
				continue;
			}
		}
						
		/*
		// *** Checking the effect of ratio: Price of the product
		
		// -------------for (Product product : products){}
		// calculating x mean
		final BigDecimal avgPriceProd0 = new BigDecimal(saleDao.getAvgPrice(products.get(0))).setScale(
			4, RoundingMode.HALF_UP);
		
		System.out.println("The average price of " + products.get(0).getName() + " is: " +
			avgPriceProd0);

		final BigDecimal totalSaleNoProd0 = new BigDecimal(
			saleDao.getTotalSaleNoEachProd(products.get(0)));
		
		System.out.println("The total sale number of " + products.get(0).getName() + " is: " +
			totalSaleNoProd0);

		// calculating y mean
		final BigDecimal avgSalePerPriceVarProd0 = totalSaleNoProd0.divide(
			new BigDecimal(saleDao.getNoPriceVar(products.get(0))), 4, RoundingMode.HALF_UP);
		
		

		final Map<Product, ProductValues> valueMap = new HashMap<Product, ProductValues>();

		for (final Product product : productDao.findAll())
		{
			final ProductValues values = new ProductValues(product);
			
			
			values.getValues().add(new RangedValue(saleDao.getMinPrice(product), new BigDecimal(saleDao.getAvgPrice(product))));
			values.getValues().add(new RangedValue(new BigDecimal(saleDao.getAvgPrice(product)), new BigDecimal(2)));

			valueMap.put(product, values);
		}


		// calculating alpha
		final List<Sale> sales = saleDao.findAll();
		for (final Sale sale : sales)
		{
			final ProductValues values = valueMap.get(sale.getProduct());

			for (final RangedValue range : values.getValues())
			{
				if (sale.getPrice().compareTo(range.getMin()) >= 0 &&
					sale.getPrice().compareTo(range.getMax()) <= 0)
				{
					BigDecimal value = range.getValue();
					if (value == null)
						value = BigDecimal.ONE;
					else
						value = value.add(BigDecimal.ONE);

					range.setValue(value);
				}
			}
		}

		BigDecimal dividendAlpha = BigDecimal.ZERO;
		BigDecimal divisorAlpha = BigDecimal.ZERO;

		for (final ProductValues values : valueMap.values())
		{
			for (final RangedValue range : values.getValues())
			{
				final BigDecimal noOfDays = new BigDecimal(saleDao.getNoDayPerPriceVar(
					values.getProduct(), range.getMin(), range.getMax()));

				BigDecimal prod0Price1_5;
				BigDecimal prod0PriceDiff1_5;
				if (noOfDays.doubleValue() == BigDecimal.ZERO.doubleValue())
				{
					prod0Price1_5 = new BigDecimal(0);
					prod0PriceDiff1_5 = new BigDecimal(0);
				}
				else
				{
					prod0Price1_5 = (new BigDecimal(1.5).subtract(avgPriceProd0)).multiply((range.getValue().divide(
						noOfDays, 4, RoundingMode.HALF_UP)).subtract(avgSalePerPriceVarProd0));
					prod0PriceDiff1_5 = (new BigDecimal(1.5).subtract(avgPriceProd0)).pow(2);
					System.err.println("+++On average, on 1 day the number of sold " +
						values.getProduct().getName() +
						" (price " +
						range.getMin() +
						"-" +
						range.getMax() +
						") is: " +
						new BigDecimal(saleDao.getNoSalePerPriceVar(products.get(0),
							new BigDecimal(1.5))).divide(noOfDays, 0, RoundingMode.HALF_UP));
				}

				dividendAlpha = dividendAlpha.add(prod0Price1_5);
				divisorAlpha = divisorAlpha.add(prod0PriceDiff1_5);
			}
		}


		final BigDecimal alpha = dividendAlpha.divide(divisorAlpha, 2, RoundingMode.HALF_UP);

		// calculating beta
		final BigDecimal beta = avgSalePerPriceVarProd0.subtract(alpha.multiply(avgPriceProd0))
			.setScale(2, RoundingMode.HALF_UP);

		System.out.println("The alpha value is: " + alpha);
		System.out.println("The beta value is: " + beta);
		*/

		/*
		// *** Checking the effect of ratio: Month of year (FOR ALL PRODUCTS)
		//
		// total number of month
		final DateTime minDate = new DateTime(saleDao.getMinDate());
		final DateTime maxDate = new DateTime(saleDao.getMaxDate());
		System.out.println("****minDate"+ minDate);
		
		Period p = new Period(minDate, maxDate);
	 	int noOfMonthTotal = p.normalizedStandard(PeriodType.months()).getMonths();
		
		
		System.out.println("number of months in total: " + noOfMonthTotal);

		// total number of sales: totalSaleNo
		
		// number of sale in 1 month on average
		final BigDecimal averageSalePerMonth = totalSaleNo.divide(new BigDecimal(noOfMonthTotal), 2,
					RoundingMode.HALF_UP);
		//Count number of sales for each month	
		final BigDecimal saleJan = new BigDecimal(saleDao.getSaleEachMonth(1));
		final BigDecimal saleFer = new BigDecimal(saleDao.getSaleEachMonth(2));
		final BigDecimal saleMar = new BigDecimal(saleDao.getSaleEachMonth(3));
		final BigDecimal saleApr = new BigDecimal(saleDao.getSaleEachMonth(4));
		final BigDecimal saleMay = new BigDecimal(saleDao.getSaleEachMonth(5));
		final BigDecimal saleJun = new BigDecimal(saleDao.getSaleEachMonth(6));
			
		// Count number of Jan/Feb/Mar in the whole database
		final BigDecimal noOfJan = new BigDecimal(saleDao.getMonthOfYear(1));
		final BigDecimal noOfFer = new BigDecimal(saleDao.getMonthOfYear(2));
		final BigDecimal noOfMar = new BigDecimal(saleDao.getMonthOfYear(3));
		final BigDecimal noOfApr = new BigDecimal(saleDao.getMonthOfYear(4));
		final BigDecimal noOfMay = new BigDecimal(saleDao.getMonthOfYear(5));
		final BigDecimal noOfJun = new BigDecimal(saleDao.getMonthOfYear(6));
		
		//calculate coefficients of each month (FOR ALL PRODUCTS)
		final BigDecimal coeffJan = BigDecimal.ZERO;
		final BigDecimal coeffFeb = BigDecimal.ZERO;
		final BigDecimal coeffMar = saleJan.divide(noOfMar, 2, RoundingMode.HALF_UP).subtract(
				averageSalePerMonth);
		*/
		
		/*
		System.out.println("********** This is the report *********");
		System.out.println("---The forecasted sale of icecream (2 Dollars/piece) on next Monday, temp is:");
		System.out.println("--- " +
			(new BigDecimal(2).multiply(alpha)).add(beta)
				
				.add(coeffMon)
				.setScale(0, RoundingMode.HALF_UP) + " pieces");
		System.out.println("---The forecasted sale of icecream (1.8 Dollars/piece) on next Tuesday is:");
		System.out.println("--- " +
			(new BigDecimal(1.8).multiply(alpha)).add(beta)
				
				.add(coeffTue)
				.setScale(0, RoundingMode.HALF_UP) + " pieces");
		System.out.println("---The forecasted sale of icecream (1.6 Dollars/piece) on next Wednesday is:");
		System.out.println("--- " +
			(new BigDecimal(1.6).multiply(alpha)).add(beta)
				
				.add(coeffWed)
				.setScale(0, RoundingMode.HALF_UP) + " pieces");
		System.out.println("---The forecasted sale of icecream (1.8 Dollars/piece) on next Thursday is:");
		System.out.println("--- " +
			(new BigDecimal(1.8).multiply(alpha)).add(beta)
				
				.add(coeffThu)
				.setScale(0, RoundingMode.HALF_UP) + " pieces");
		System.out.println("---The forecasted sale of icecream (1.5 Dollars/piece) on next Friday is:");
		System.out.println("--- " +
			(new BigDecimal(1.5).multiply(alpha)).add(beta)
				
				.add(coeffFri)
				.setScale(0, RoundingMode.HALF_UP) + " pieces");
		System.out.println("---The forecasted sale of icecream (1.9 Dollars/piece) on next Saturday is:");
		System.out.println("--- " +
			(new BigDecimal(1.9).multiply(alpha)).add(beta)
				
				.add(coeffSat)
				.setScale(0, RoundingMode.HALF_UP) + " pieces");
		System.out.println("---The forecasted sale of icecream (1.7 Dollars/piece) on next Sunday is:");
		System.out.println("--- " +
			(new BigDecimal(1.7).multiply(alpha)).add(beta)
				
				.add(coeffSun)
				.setScale(0, RoundingMode.HALF_UP) + " pieces");
		 */

		
	}

	@BeforeTransaction
	public void setupData() throws Exception
	{
		// use PopulateData.java instead
		/*-
		executeSqlScript("classpath:clear.sql", false);

		generateProducts();
		generateSales();
		 */

	}
}
