package hotelreservation;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class UtilsTest {
	
	private Utils utils;

	@Before
	public void setUp() throws Exception {
		utils = new Utils();
	}

	@Test
	public void testAsDateLocalDate() {
		LocalDate localDate = LocalDate.of(2018, Month.JANUARY, 1);
		
		Calendar instance = Calendar.getInstance();
		instance.set(2018 , 0, 1, 0, 0, 0);
		instance.set(Calendar.MILLISECOND, 0);
		
		assertEquals(instance.getTime(), utils.asDate(localDate));
	}

	@Test
	public void testAsDateLocalDateTime() {
		LocalDateTime localDate = LocalDateTime.of(2018, Month.JANUARY, 1, 9, 30, 30);
		
		Calendar instance = Calendar.getInstance();
		instance.set(2018 , 0, 1, 9, 30, 30);
		instance.set(Calendar.MILLISECOND, 0);
		
		assertEquals(instance.getTime(), utils.asDate(localDate));
	}

	@Test
	public void testAsLocalDate() {
		LocalDate localDate = LocalDate.of(2018, Month.JANUARY, 1);
		
		Calendar instance = Calendar.getInstance();
		instance.set(2018 , 0, 1);
		
		assertEquals(localDate ,utils.asLocalDate(instance.getTime()));
	}

	@Test
	public void testAsLocalDateTime() {
		LocalDateTime localDate = LocalDateTime.of(2018, Month.JANUARY, 1, 9, 30, 30);
		
		Calendar instance = Calendar.getInstance();
		instance.set(2018 , 0, 1, 9, 30, 30);
		instance.set(Calendar.MILLISECOND, 0);
		
		assertEquals(localDate ,utils.asLocalDateTime(instance.getTime()));
	}

	@Test
	public void testToList() {
		Integer foo[] = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 0 };
		assertTrue(utils.toList( Arrays.asList(foo)) instanceof List);
	}
}