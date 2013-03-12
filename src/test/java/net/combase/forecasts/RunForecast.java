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
			final BigDecimal priceProdA =  new BigDecimal(1.5).add((BigDecimal)(new BigDecimal(Math.random()).multiply(
				(new BigDecimal(2).subtract(new BigDecimal(1.5)))))).setScale(1,RoundingMode.HALF_UP);
			final BigDecimal priceProdB =  new BigDecimal(2).add((BigDecimal)(new BigDecimal(Math.random()).multiply(
					(new BigDecimal(2.6).subtract(new BigDecimal(2)))))).setScale(1,RoundingMode.HALF_UP);
			final BigDecimal priceProdC =  new BigDecimal(3).add((BigDecimal)(new BigDecimal(Math.random()).multiply(
					(new BigDecimal(4).subtract(new BigDecimal(3)))))).setScale(1,RoundingMode.HALF_UP);
			
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
		final BigDecimal averageSalePerDay = totalSaleNo.divide(noOfDayTotal,2, RoundingMode.HALF_UP);
		
				
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
		
		final BigDecimal coeffTem15to20;
		if (noOfDayTemp15to20.doubleValue()==BigDecimal.ZERO.doubleValue()){
			coeffTem15to20 = new BigDecimal(0);
		} else {
			coeffTem15to20 = saleTemp15to20.divide(noOfDayTemp15to20,2,
			RoundingMode.HALF_UP).subtract(averageSalePerDay);
		}
		
		final BigDecimal coeffTem20to25;
		
		if (noOfDayTemp20to25.doubleValue()==BigDecimal.ZERO.doubleValue()){
			coeffTem20to25 = new BigDecimal(0);
		} else {
			coeffTem20to25 = saleTemp20to25.divide(noOfDayTemp20to25,2,
			RoundingMode.HALF_UP).subtract(averageSalePerDay);
		}
				
		final BigDecimal coeffTem25to30;
		if (noOfDayTemp25to30.doubleValue()==BigDecimal.ZERO.doubleValue()){
			coeffTem25to30 = new BigDecimal(0);
		} else {
			coeffTem25to30 = saleTemp25to30.divide(noOfDayTemp25to30,2,
			RoundingMode.HALF_UP).subtract(averageSalePerDay);
		}
		
		final BigDecimal coeffTemOver30;
		if (noOfDayTempOver30.doubleValue()==BigDecimal.ZERO.doubleValue()){
			coeffTemOver30 = new BigDecimal(0);
		} else {
			coeffTemOver30 = saleTempOver30.divide(noOfDayTempOver30,2,
			RoundingMode.HALF_UP).subtract(averageSalePerDay);
		}
				
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
		
		final BigDecimal coeffMon = saleMon.divide(noOfMon,2,
				RoundingMode.HALF_UP).subtract(averageSalePerDay);
		final BigDecimal coeffTue = saleTue.divide(noOfTue,2,
				RoundingMode.HALF_UP).subtract(averageSalePerDay);
		final BigDecimal coeffWed = saleWed.divide(noOfWed,2,
				RoundingMode.HALF_UP).subtract(averageSalePerDay);
		final BigDecimal coeffThu = saleThu.divide(noOfThu,2,
				RoundingMode.HALF_UP).subtract(averageSalePerDay);
		final BigDecimal coeffFri = saleFri.divide(noOfFri,2,
				RoundingMode.HALF_UP).subtract(averageSalePerDay);
		final BigDecimal coeffSat = saleSat.divide(noOfSat,2,
				RoundingMode.HALF_UP).subtract(averageSalePerDay);
		final BigDecimal coeffSun = saleSun.divide(noOfSun,2,
				RoundingMode.HALF_UP).subtract(averageSalePerDay);
				
		System.out.println("The coefficient of Monday is: " + coeffMon);
		System.out.println("The coefficient of Tueday is: " + coeffTue);
		System.out.println("The coefficient of Wednesday is: " + coeffWed);
		System.out.println("The coefficient of Thursday is: " + coeffThu);
		System.out.println("The coefficient of Friday is: " + coeffFri);
		System.out.println("The coefficient of Saturday is: " + coeffSat);
		System.out.println("The coefficient of Sunday is: " + coeffSun);
		
		//*** Checking the effect of ratio: Price of the product 
		List<Product> products = productDao.findAll();
		//-------------for (Product product : products){}
		// calculating x mean
		final BigDecimal avgPriceProd0 = new BigDecimal(saleDao.getAvgPrice(
				products.get(0))).setScale(4,RoundingMode.HALF_UP); 
		final BigDecimal avgPriceProd1 = new BigDecimal(saleDao.getAvgPrice(
				products.get(1))).setScale(4,RoundingMode.HALF_UP); 
		final BigDecimal avgPriceProd2 = new BigDecimal(saleDao.getAvgPrice(
				products.get(2))).setScale(4,RoundingMode.HALF_UP); 
		System.out.println("The average price of "+ products.get(0).getName() + " is: "
				+ avgPriceProd0);
		
		final BigDecimal totalSaleNoProd0 = new BigDecimal(
				saleDao.getTotalSaleNoEachProd(products.get(0)));
		final BigDecimal totalSaleNoProd1 = new BigDecimal(
				saleDao.getTotalSaleNoEachProd(products.get(1)));
		final BigDecimal totalSaleNoProd2 = new BigDecimal(
				saleDao.getTotalSaleNoEachProd(products.get(2)));
		System.out.println("The total sale number of "+ products.get(0).getName() + " is: "
				+ totalSaleNoProd0);
			
		//calculating y mean
		final BigDecimal avgSalePerPriceVarProd0 = totalSaleNoProd0.divide(new BigDecimal(
				saleDao.getNoPriceVar(products.get(0))),4,RoundingMode.HALF_UP);
		final BigDecimal avgSalePerPriceVarProd1 = totalSaleNoProd0.divide(new BigDecimal(
				saleDao.getNoPriceVar(products.get(1))),4,RoundingMode.HALF_UP);
		final BigDecimal avgSalePerPriceVarProd2 = totalSaleNoProd0.divide(new BigDecimal(
				saleDao.getNoPriceVar(products.get(2))),4,RoundingMode.HALF_UP);
		
		BigDecimal prod0Counter1_5 = new BigDecimal(0);
		BigDecimal prod0Counter1_6 = new BigDecimal(0);
		BigDecimal prod0Counter1_7 = new BigDecimal(0);
		BigDecimal prod0Counter1_8 = new BigDecimal(0);
		BigDecimal prod0Counter1_9 = new BigDecimal(0);
		BigDecimal prod0Counter2_0 = new BigDecimal(0);
		
		
		
		List<Sale> sales = saleDao.findAll();
		for (Sale sale : sales){
			if (sale.getPrice().doubleValue() == new BigDecimal(1.5).doubleValue()
					&& sale.getProduct()==products.get(0)){
				prod0Counter1_5 = prod0Counter1_5.add(new BigDecimal(1));
			}
			if (sale.getPrice().doubleValue() == new BigDecimal(1.6).doubleValue()
					&& sale.getProduct()==products.get(0)){
				prod0Counter1_6 = prod0Counter1_6.add(new BigDecimal(1));
			}		
			if (sale.getPrice().doubleValue() == new BigDecimal(1.7).doubleValue()
					&& sale.getProduct()==products.get(0)){
				prod0Counter1_7 = prod0Counter1_7.add(new BigDecimal(1));
			}
			if (sale.getPrice().doubleValue() == new BigDecimal(1.8).doubleValue()
					&& sale.getProduct()==products.get(0)){
				prod0Counter1_8 = prod0Counter1_8.add(new BigDecimal(1));
			}
			if (sale.getPrice().doubleValue() == new BigDecimal(1.9).doubleValue()
					&& sale.getProduct()==products.get(0)){
				prod0Counter1_9 = prod0Counter1_9.add(new BigDecimal(1));
			}
			if (sale.getPrice().doubleValue() == new BigDecimal(2).doubleValue()
					&& sale.getProduct()==products.get(0)){
				prod0Counter2_0 = prod0Counter2_0.add(new BigDecimal(1));
			}
		}
		
		 BigDecimal noOfDayProd0Price1_5 = new BigDecimal(
				saleDao.getNoDayPerPriceVar(products.get(0),new BigDecimal(1.5)));
		 BigDecimal noOfDayProd0Price1_6 = new BigDecimal(
				saleDao.getNoDayPerPriceVar(products.get(0),new BigDecimal(1.6)));
		 BigDecimal noOfDayProd0Price1_7 = new BigDecimal(
				saleDao.getNoDayPerPriceVar(products.get(0),new BigDecimal(1.7)));
		 BigDecimal noOfDayProd0Price1_8 = new BigDecimal(
				saleDao.getNoDayPerPriceVar(products.get(0),new BigDecimal(1.8)));
		 BigDecimal noOfDayProd0Price1_9 = new BigDecimal(
				saleDao.getNoDayPerPriceVar(products.get(0),new BigDecimal(1.9)));
		 BigDecimal noOfDayProd0Price2_0 = new BigDecimal(
				saleDao.getNoDayPerPriceVar(products.get(0),new BigDecimal(2.0)));
				
		System.out.println(noOfDayProd0Price1_5);
		System.out.println(noOfDayProd0Price1_6);
		System.out.println(noOfDayProd0Price1_7);
		System.out.println(noOfDayProd0Price1_8);
		System.out.println(noOfDayProd0Price1_9);
		System.out.println(noOfDayProd0Price2_0);
		
		BigDecimal prod0Price1_5, prod0Price1_6, prod0Price1_7, prod0Price1_8, prod0Price1_9, prod0Price2_0;
		BigDecimal prod0PriceDiff1_5,prod0PriceDiff1_6,prod0PriceDiff1_7,prod0PriceDiff1_8,prod0PriceDiff1_9,prod0PriceDiff2_0;
		
		if (noOfDayProd0Price1_5.doubleValue()==BigDecimal.ZERO.doubleValue()){
			prod0Price1_5 = new BigDecimal(0);
			prod0PriceDiff1_5 = new BigDecimal(0);
		} else{ prod0Price1_5 = (new BigDecimal(1.5).subtract(avgPriceProd0)).multiply(
				(prod0Counter1_5.divide(noOfDayProd0Price1_5, 4, RoundingMode.HALF_UP)).subtract(avgSalePerPriceVarProd0));
			prod0PriceDiff1_5 = (new BigDecimal(1.5).subtract(avgPriceProd0)).pow(2);
		}
		
		if (noOfDayProd0Price1_6.doubleValue()==BigDecimal.ZERO.doubleValue()){
			prod0Price1_6 = new BigDecimal(0);
			prod0PriceDiff1_6 = new BigDecimal(0);
		} else{ prod0Price1_6 = (new BigDecimal(1.6).subtract(avgPriceProd0)).multiply(
				(prod0Counter1_6.divide(noOfDayProd0Price1_6, 4, RoundingMode.HALF_UP)).subtract(avgSalePerPriceVarProd0));
			prod0PriceDiff1_6 = (new BigDecimal(1.6).subtract(avgPriceProd0)).pow(2);
		}
		
		if (noOfDayProd0Price1_7.doubleValue()==BigDecimal.ZERO.doubleValue()){
			prod0Price1_7 = new BigDecimal(0);
			prod0PriceDiff1_7 = new BigDecimal(0);
		} else{prod0Price1_7 = (new BigDecimal(1.7).subtract(avgPriceProd0)).multiply(
				(prod0Counter1_7.divide(noOfDayProd0Price1_7, 4, RoundingMode.HALF_UP)).subtract(avgSalePerPriceVarProd0));
			prod0PriceDiff1_7 = (new BigDecimal(1.7).subtract(avgPriceProd0)).pow(2);
		}
		
		if (noOfDayProd0Price1_8.doubleValue()==BigDecimal.ZERO.doubleValue()){
			prod0Price1_8 = new BigDecimal(0);
			prod0PriceDiff1_8 = new BigDecimal(0);
		} else{prod0Price1_8 = (new BigDecimal(1.8).subtract(avgPriceProd0)).multiply(
				(prod0Counter1_8.divide(noOfDayProd0Price1_8, 4, RoundingMode.HALF_UP)).subtract(avgSalePerPriceVarProd0)) ;
			prod0PriceDiff1_8 = (new BigDecimal(1.8).subtract(avgPriceProd0)).pow(2);
		}
		
		if (noOfDayProd0Price1_9.doubleValue()==BigDecimal.ZERO.doubleValue()){
			prod0Price1_9 = new BigDecimal(0);
			prod0PriceDiff1_9 = new BigDecimal(0);
		} else{prod0Price1_9 = (new BigDecimal(1.9).subtract(avgPriceProd0)).multiply(
				(prod0Counter1_9.divide(noOfDayProd0Price1_9, 4, RoundingMode.HALF_UP)).subtract(avgSalePerPriceVarProd0))  ;
			prod0PriceDiff1_9 = (new BigDecimal(1.9).subtract(avgPriceProd0)).pow(2);
		}
		
		if (noOfDayProd0Price2_0.doubleValue()==BigDecimal.ZERO.doubleValue()){
			prod0Price2_0 = new BigDecimal(0);
			prod0PriceDiff2_0 = new BigDecimal(0);
		} else{prod0Price2_0 = (new BigDecimal(2.0).subtract(avgPriceProd0)).multiply(
				(prod0Counter2_0.divide(noOfDayProd0Price2_0, 4, RoundingMode.HALF_UP)).subtract(avgSalePerPriceVarProd0)) ;
			prod0PriceDiff2_0 = (new BigDecimal(2.0).subtract(avgPriceProd0)).pow(2);
		}
		
		final BigDecimal dividendAlpha = prod0Price1_5.add(prod0Price1_6).add(prod0Price1_7).add(prod0Price1_8).add(prod0Price1_9).add(prod0Price2_0);
		
		final BigDecimal divisorAlpha = prod0PriceDiff1_5.add(prod0PriceDiff1_6).add(prod0PriceDiff1_7).add(prod0PriceDiff1_8).add(prod0PriceDiff1_9).add(prod0PriceDiff2_0);
		
		final BigDecimal alpha = dividendAlpha.divide(divisorAlpha, 2, RoundingMode.HALF_UP);
		final BigDecimal beta = avgSalePerPriceVarProd0.subtract(alpha.multiply(avgPriceProd0)).setScale(2, RoundingMode.HALF_UP);
				
		System.out.println("The alpha value is: "+ alpha);
		System.out.println("The beta value is: " + beta);
				
		System.out.println("********** This is the report *********");
		System.out.println("---The forecasted sale of icecream (2 Dollars/piece) on next Monday, temp. 20C is:");
		System.out.println("--- "+ (new BigDecimal(2).multiply(alpha)).add(beta).add(coeffTem15to20).add(coeffMon).setScale(0, RoundingMode.HALF_UP) + " pieces" );		
		System.out.println("---The forecasted sale of icecream (1.8 Dollars/piece) on next Tuesday, temp. 22C is:");
		System.out.println("--- "+ (new BigDecimal(1.8).multiply(alpha)).add(beta).add(coeffTem20to25).add(coeffTue).setScale(0, RoundingMode.HALF_UP) + " pieces" );				
		System.out.println("---The forecasted sale of icecream (1.6 Dollars/piece) on next Wednesday, temp. 19C is:");
		System.out.println("--- "+ (new BigDecimal(1.6).multiply(alpha)).add(beta).add(coeffTem15to20).add(coeffWed).setScale(0, RoundingMode.HALF_UP) + " pieces" );				
		System.out.println("---The forecasted sale of icecream (1.8 Dollars/piece) on next Thursday, temp. 24C is:");
		System.out.println("--- "+ (new BigDecimal(1.8).multiply(alpha)).add(beta).add(coeffTem20to25).add(coeffThu).setScale(0, RoundingMode.HALF_UP) + " pieces" );				
		System.out.println("---The forecasted sale of icecream (1.5 Dollars/piece) on next Friday, temp. 26C is:");
		System.out.println("--- "+ (new BigDecimal(1.5).multiply(alpha)).add(beta).add(coeffTem25to30).add(coeffFri).setScale(0, RoundingMode.HALF_UP) + " pieces" );				
		System.out.println("---The forecasted sale of icecream (1.9 Dollars/piece) on next Saturday, temp. 23C is:");
		System.out.println("--- "+ (new BigDecimal(1.9).multiply(alpha)).add(beta).add(coeffTem20to25).add(coeffSat).setScale(0, RoundingMode.HALF_UP) + " pieces" );				
		System.out.println("---The forecasted sale of icecream (1.7 Dollars/piece) on next Sunday, temp. 25C is:");
		System.out.println("--- "+ (new BigDecimal(1.7).multiply(alpha)).add(beta).add(coeffTem20to25).add(coeffSun).setScale(0, RoundingMode.HALF_UP) + " pieces" );
	}

	@BeforeTransaction
	public void setupData() throws Exception
	{
		executeSqlScript("classpath:clear.sql", false);

		generateProducts();
		generateSales();


	}
}
