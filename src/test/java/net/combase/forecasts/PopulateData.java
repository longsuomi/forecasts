/**
 * Copyright 2013 COMBASE AG
 */
package net.combase.forecasts;

import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.List;

import net.combase.forecasts.dao.ProductDao;
import net.combase.forecasts.dao.SaleDao;
import net.combase.forecasts.domain.Product;
import net.combase.forecasts.domain.Sale;
import net.combase.forecasts.util.CsvReader;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.transaction.annotation.Transactional;

//import joda LocalDate class


/**
 * @author Till Freier
 * 
 */
@ContextConfiguration("classpath:application-context.xml")
public class PopulateData extends AbstractTransactionalJUnit4SpringContextTests
{
	private static final DateTimeFormatter DATE_FORMAT = DateTimeFormat.forPattern("yyyy-MM-dd hh:mm:ss.SSSSS");

	@Autowired
	private ProductDao productDao;

	@Autowired
	private SaleDao saleDao;


	/**
	 * @throws IOException
	 * 
	 */
	@Transactional
	private void generateSales() throws IOException
	{
		final ClassPathResource res = new ClassPathResource("/data.csv");
		final CsvReader r = new CsvReader(new InputStreamReader(res.getInputStream()), ';');


		List<String> fields = null;
		int row = 0;
		while ((fields = r.readFields()) != null)
		{
			try
			{
				row++;
				System.out.println("read row " + row);
				final Long id = Long.valueOf(fields.get(0));
				final String name = fields.get(1);
				final DateTime dateTime = PopulateData.DATE_FORMAT.parseDateTime(fields.get(3));
				final BigDecimal price = new BigDecimal(fields.get(5));

				final Product prod = getProduct(id, name);

				final Sale s = new Sale();
				s.setDateTime(dateTime);
				s.setPrice(price);
				s.setProduct(prod);

				saleDao.save(s);
				System.out.println("sale stored");
			}
			catch (final Exception e)
			{
				System.out.println("skipped row");
			}
		}

		r.close();
	}

	/**
	 * 
	 */
	private Product getProduct(final Long id, final String name)
	{
		Product result = productDao.findOne(id);
		if (result != null)
			return result;

		result = new Product();
		result.setName(name);
		result.setId(id);

		return productDao.saveAndFlush(result);
	}

	@Test
	public void run()
	{

	}

	@BeforeTransaction
	public void setupData() throws Exception
	{
		executeSqlScript("classpath:clear.sql", false);

		generateSales();
	}
}
