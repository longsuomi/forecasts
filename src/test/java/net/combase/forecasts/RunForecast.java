/**
 * Copyright 2013 COMBASE AG
 */
package net.combase.forecasts;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
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


	@Autowired
	private ProductDao productDao;

	@Autowired
	private SaleDao saleDao;

	
	@Test
	public void run()
	{
			
		 java.util.Calendar myCalendar = java.util.Calendar.getInstance();
		 myCalendar.set(2011, 0, 1, 0, 0, 1);
	        DateTime miniDay = new DateTime(myCalendar);
	        Date minDay = miniDay.toDate();
	     myCalendar.set(2011, 2, 31, 23, 59, 59);
	      	DateTime maxiDay = new DateTime(myCalendar);
	      	Date maxDay = maxiDay.toDate();
	      System.out.println("Starting time: " + minDay);
	      System.out.println("Ending time: " + maxDay);
	      
	    final List<Date> topSalesDay = saleDao.getTopSalesDay(minDay, maxDay);
		final List<Long> topSalesNo = saleDao.getTopSalesNo(minDay, maxDay);
		
		//Q3 splits highest 25% of data set
		int index_q3 = topSalesNo.size() / 4;
		int q3 = topSalesNo.get(index_q3-1).intValue();
		//Q1 splits lowest 25% of data set
		int index_q1 = index_q3 * 3;
		int q1 = topSalesNo.get(index_q1-1).intValue();
		
		int interquartile = q3 - q1;
		
		double lowerFence = q1 - 1.5 * interquartile;
		double upperFence = q3 + 1.5 * interquartile;
		System.out.println("A day is an outlier if its sales is lower than: "+ lowerFence);
		System.out.println("A day is an outlier if its sales is higher than: : "+ upperFence);  
		
			
		List<Date> outliers = new ArrayList<Date>();
		for (int i=0; i<topSalesNo.size(); i++){
			Long value = topSalesNo.get(i);
			if(value < lowerFence || value > upperFence){
				outliers.add(topSalesDay.get(i));
				System.out.println("an outlier date:" +topSalesDay.get(i).toString()
									+ ", with sales: " + value);
				
			}
		}
				
		
		// total number of day in examination period
		final BigDecimal noOfDayTotal = new BigDecimal(saleDao.getNoOfDayInTotal(minDay,maxDay,outliers));
		System.out.println("number of days in examination period: " + noOfDayTotal);

		// total number of sales
		final BigDecimal totalSaleNo = new BigDecimal(saleDao.getTotalSaleNo(minDay,maxDay,outliers));
		System.out.println("number of sales of all products in examination period: " + totalSaleNo);

		// number of sale in 1 day on average
		final BigDecimal averageSalePerDay = totalSaleNo.divide(noOfDayTotal, 0,
			RoundingMode.HALF_UP);
		System.out.println("number of sales per day, in average: " + averageSalePerDay);
		
		
		// *** Checking the effect of ratio: Day of week (FOR ALL PRODUCTS)
		System.out.println("------ Checking the effect of \"Day of week\" on sales ");
		
		List<BigDecimal> saleWeekday = new ArrayList<BigDecimal>(); // total number of sales on Mon/Tue/Wed...
		List<BigDecimal> noWeekday = new ArrayList<BigDecimal>(); // total number of Mon/Tue/Wed...
		List<BigDecimal> coeffWeekday = new ArrayList<BigDecimal>(); // coefficient of each weekday 
		for (int i=1;i<8;i++){
			saleWeekday.add(new BigDecimal(saleDao.getSaleEachDay(i, minDay, maxDay,outliers)));
			noWeekday.add(new BigDecimal(saleDao.getDayOfWeek(i, minDay, maxDay,outliers)));
			coeffWeekday.add(saleWeekday.get(i-1).divide(noWeekday.get(i-1), 0, RoundingMode.HALF_UP).subtract(
					averageSalePerDay));
		}
		
		printCoeffDay(coeffWeekday);
		
		
		// *** Checking the effect of ratio: Month of year (FOR ALL PRODUCTS)
		System.out.println("------ Checking the effect of \"Month of year\" on sales ");
		// total number of month		
		final DateTime miniDate = new DateTime(saleDao.getMinDate());		
		myCalendar.set(2013, 1, 28, 23, 59, 59);
		final DateTime maxiDate = new DateTime(myCalendar);
		Period p = new Period(miniDate, maxiDate);
	 	int noOfMonthTotal = p.getMonths()+ p.getYears() * 12 +1; //p.getMonths() only returns 1 (expected 2)
	 		System.out.println("number of months in total: " + noOfMonthTotal);		 
		
	 	final Date minDate = miniDate.toDate();	
	 	final Date maxDate = maxiDate.toDate();	
	 	
	 	// total number of sales in 26 months
	 	final BigDecimal sale26Months = new BigDecimal(saleDao.getTotalSaleNo(minDate,maxDate, outliers));
	 		System.out.println("number of sales in 26 months: "+ sale26Months);
		// number of sales in 1 month on average
		final BigDecimal averageSalePerMonth = sale26Months.divide(new BigDecimal(noOfMonthTotal), 0, RoundingMode.HALF_UP);
			System.out.println("The average no of sales per month is: "+ averageSalePerMonth);
		
		List<BigDecimal> salesEachMonth = new ArrayList<BigDecimal>(); //number of sales in Jan/Feb/Mar
		List<BigDecimal> noEachMonth = new ArrayList<BigDecimal>(); // number of Jan/Feb/Mar in the database
		List<BigDecimal> coeffMonth = new ArrayList<BigDecimal>(); // coefficient of Jan/Feb/Mar
		for (int i =0; i<=11; i++){
			salesEachMonth.add(new BigDecimal(saleDao.getSaleEachMonth(i)));
			if (i <= 2){
				noEachMonth.add(new BigDecimal(p.getYears()).add(BigDecimal.ONE)); // 1 extra Jan and 1 extra Feb (26 months) 
				} else {
				noEachMonth.add(new BigDecimal(p.getYears()));
			}
			coeffMonth.add(salesEachMonth.get(i).divide(noEachMonth.get(i), 0, RoundingMode.HALF_UP).subtract(
					averageSalePerMonth));
		}
					
		
System.out.println("*****************");
System.out.println("Making the forecast sales of all products for April 2012, using ratio \"Month of year\" and \"Day of week\"");
		
		myCalendar.set(2012, 3, 1); //value "3" represents April 
		int daysInApr = myCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		final BigDecimal coeffEachDayInApril = coeffMonth.get(3).divide(new BigDecimal(daysInApr),0, RoundingMode.HALF_UP);
		
		for (int i=1; i<=daysInApr; i++){
			myCalendar.set(2012, 3, i);	        
			DateTime april_day = new DateTime(myCalendar);
	        Date aprilDay = april_day.toLocalDate().toDate();
	       
	        for (int j=1;j<=7;j++){
	        	if (april_day.getDayOfWeek()==j){
		        	System.out.println("-FORECAST sales of \"" 
		        			+ aprilDay.toString().substring(0, 10) + "\": " 
		        			+ averageSalePerDay.add(coeffWeekday.get(j-1)).add(coeffEachDayInApril).setScale(0, RoundingMode.HALF_UP));
		        			System.out.println("ACTUAL sales: "
		        			+ saleDao.getSalesNoSpecificDay(aprilDay));
	        	}
	        }
		}
	
		
		
		//forecast sales of each product
		//final Map<Product, ProductValues> valueMap = new HashMap<Product, ProductValues>();
		final HashSet<Product> products = new HashSet<Product>(saleDao.getProdList(minDay, maxDay, outliers));
		for (Product product : products){
			//***ratio: day of week, for EACH PRODUCT
			// total number of day EACH PRODUCT
			final BigDecimal noOfDayTotalProd = new BigDecimal(saleDao.getNoOfDayProd(product,minDay,maxDay,outliers));
			System.out.println("---checking product \""+ product.getName()+"\", no of days this product was sold in: " +noOfDayTotalProd);

			// total number of sales EACH PRODUCT
			final BigDecimal totalSaleNoProd = new BigDecimal(saleDao.getTotalSaleNoProd(product,minDay,maxDay, outliers));

			// number of sale in 1 day on average EACH PRODUCT
			final BigDecimal averageSalePerDayProd = totalSaleNoProd.divide(noOfDayTotalProd, 2,
					RoundingMode.HALF_UP);

			//if avg no of sales is less than 5 per day, we cannot make a forecast
			if (averageSalePerDayProd.compareTo(new BigDecimal(5))>=0){					

				System.out.println("number of sales of this product in this period: "+totalSaleNoProd);
				System.out.println("Number of sales per day, on average: "+averageSalePerDayProd);	

				List <BigDecimal> saleWeekdayProd = new ArrayList<BigDecimal>(); // total sales of this product on Mon/Tue/Wed....		
				List <BigDecimal> coeffDayProd = new ArrayList<BigDecimal>();
				for (int i=1;i<=7;i++){
					saleWeekdayProd.add(new BigDecimal(saleDao.getSaleEachDayProd(i, product,minDay,maxDay, outliers)));
					coeffDayProd.add(saleWeekdayProd.get(i-1).divide(noWeekday.get(i-1), 0, RoundingMode.HALF_UP).subtract(
							averageSalePerDayProd));
				}
				printCoeffDayProd(product, coeffDayProd);

				//***ratio: price
				// calculating x mean
				BigDecimal priceDiff = saleDao.getMaxPrice(product, minDay, maxDay, outliers).subtract(saleDao.getMinPrice(product,minDay,maxDay, outliers));
				// if there is only 1 price all the time, we only use ratio day of week to calculate
				if (priceDiff.compareTo(BigDecimal.ZERO) == 0){
					System.out.println("*****************");
					System.out.println("Making the forecast sales of \""+product.getName()+ "\" for the first week of April 2012, using ratio \"Day of week\"");

					//checking 7 first days of April to return forecast sales as well as actual sales
					for (int i=1; i<=7; i++){
						myCalendar.set(2012, 3, i);	        
						DateTime april_day = new DateTime(myCalendar);
						Date aprilDay = april_day.toLocalDate().toDate();

						for (int j=1;j<=7;j++){
							if (april_day.getDayOfWeek()==j){
								System.out.println("-" + aprilDay.toString().substring(0, 10)+ ", \""+ product.getName() +"\", FORECAST sales: "  
										+ averageSalePerDayProd.add(coeffDayProd.get(j-1)).setScale(0, RoundingMode.HALF_UP));
								System.out.println("The ACTUAL sales: "
										+ saleDao.getSales1DayProd(aprilDay, product));
							}	
						}
					}
					// if there are 2 or more price variants, we will forecast sales based on day of week and price	
				} else {
					//x mean
					final BigDecimal avgPrice = saleDao.getMinPrice(product,minDay,maxDay, outliers).add(priceDiff.divide(new BigDecimal(2), 2, RoundingMode.HALF_UP));
					System.out.println("The average price of \"" + product.getName() + "\" is: " + avgPrice);

					// y mean
					final BigDecimal avgSalePerPriceRange = totalSaleNoProd.divide(
							new BigDecimal(2), 4, RoundingMode.HALF_UP);				
					System.out.println("average sale per price range "+ avgSalePerPriceRange);				

					BigDecimal dividendAlpha = BigDecimal.ZERO;
					BigDecimal divisorAlpha = BigDecimal.ZERO;	

					final ProductValues values = new ProductValues(product);
					values.getValues().add(new RangedValue(saleDao.getMinPrice(product,minDay,maxDay, outliers),avgPrice));
					values.getValues().add(new RangedValue(avgPrice, saleDao.getMaxPrice(product, minDay, maxDay, outliers)));

					for (final RangedValue range : values.getValues()){

						BigDecimal salePriceRange = new BigDecimal(saleDao.getNoSalePriceRange(product, minDay, maxDay,range.getMin(),range.getMax(),outliers));
						System.out.println("---No of sales of \""+ product.getName() + "\" with price between "
								+range.getMin()+ " and "+range.getMax() + " is: " + salePriceRange);

						BigDecimal rangePriceTag = (range.getMax().add(range.getMin())).divide(new BigDecimal(2), 2, RoundingMode.HALF_UP);
						//System.out.println(rangePriceTag);

						BigDecimal dividendFactor = (salePriceRange.subtract(avgSalePerPriceRange)).multiply(rangePriceTag.subtract(avgPrice));						
						dividendAlpha = dividendAlpha.add(dividendFactor);
						//System.out.println("dividendAlpha is: "+dividendAlpha);

						BigDecimal divisorFactor = (rangePriceTag.subtract(avgPrice)).pow(2);
						divisorAlpha = divisorAlpha.add(divisorFactor);
						//System.out.println("divisorAlpha is: "+divisorAlpha);
					}
					final BigDecimal alpha = dividendAlpha.divide(divisorAlpha, 2, RoundingMode.HALF_UP);	
					final BigDecimal beta = avgSalePerPriceRange.subtract(alpha.multiply(avgPrice)).setScale(2, RoundingMode.HALF_UP);

					System.out.println("*****************");
					System.out.println("Making the forecast sales of \""+product.getName()+ "\" for the first week of April 2012, using ratio \"Price of product\" and \"Day of week\"");

					//checking 7 first days of April to return forecast sales as well as actual sales
					for (int i=1; i<=7; i++){
						myCalendar.set(2012, 3, i);	        
						DateTime april_day = new DateTime(myCalendar);
						Date aprilDay = april_day.toLocalDate().toDate();
						
						BigDecimal price1Day;
						try {
						price1Day = saleDao.getPrice1Day(product,aprilDay);
						}
						catch (Exception e){
							price1Day = avgPrice;
						}
						for (int j=1;j<=7;j++){
							if (april_day.getDayOfWeek()==j){
								
								if (price1Day.compareTo(avgPrice)<=0){
									System.out.println("-" + aprilDay.toString().substring(0, 10)+ ", \""+ product.getName() +"\", FORECAST sales: "  
											+ (avgPrice.add(saleDao.getMinPrice(product,minDay,maxDay, outliers))).divide(new BigDecimal(2))
											.multiply(alpha).add(beta).add(coeffDayProd.get(j-1)).setScale(0, RoundingMode.HALF_UP));
									System.out.println("The ACTUAL sales: "
											+ saleDao.getSales1DayProd(aprilDay, product));
								} else {
									System.out.println("-" + aprilDay.toString().substring(0, 10)+ ", \""+ product.getName() +"\", FORECAST sales: "  
											+ (avgPrice.add(saleDao.getMaxPrice(product,minDay,maxDay, outliers))).divide(new BigDecimal(2))
											.multiply(alpha).add(beta).add(coeffDayProd.get(j-1)).setScale(0, RoundingMode.HALF_UP));
									System.out.println("The ACTUAL sales: "
											+ saleDao.getSales1DayProd(aprilDay, product));
								}
								
								
								
								
							}					        
						} 
					}
				}						

			} else {
				System.out.println("---We cannot make a forecast for: \""+ product.getName()+"\"");
				continue;

			}
		}

		System.out.println("Sales forecast has been calculated successfully!");		
	}

	private void printCoeffDayProd(Product product,
			List<BigDecimal> coeffDayProd) {
		System.out.println("The coefficient of Monday of "+ product.getName()+ " is: " + coeffDayProd.get(0));
		System.out.println("The coefficient of Tueday of "+ product.getName()+ " is: " + coeffDayProd.get(1));
		System.out.println("The coefficient of Wednesday of "+ product.getName()+ " is: " + coeffDayProd.get(2));
		System.out.println("The coefficient of Thursday of "+ product.getName()+ " is: " + coeffDayProd.get(3));
		System.out.println("The coefficient of Friday of "+ product.getName()+ " is: " + coeffDayProd.get(4));
		System.out.println("The coefficient of Saturday of "+ product.getName()+ " is: " + coeffDayProd.get(5));
		System.out.println("The coefficient of Sunday of "+ product.getName()+ " is: " + coeffDayProd.get(6));
	}

	private void printCoeffDay(List<BigDecimal> coeffWeekday) {
		System.out.println("The coefficient of Monday is: " + coeffWeekday.get(0));
		System.out.println("The coefficient of Tueday is: " + coeffWeekday.get(1));
		System.out.println("The coefficient of Wednesday is: " + coeffWeekday.get(2));
		System.out.println("The coefficient of Thursday is: " + coeffWeekday.get(3));
		System.out.println("The coefficient of Friday is: " + coeffWeekday.get(4));
		System.out.println("The coefficient of Saturday is: " + coeffWeekday.get(5));
		System.out.println("The coefficient of Sunday is: " + coeffWeekday.get(6));
	}

	
}
