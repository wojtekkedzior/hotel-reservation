package hotelreservation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Calendar;

import org.junit.Before;
import org.junit.Test;

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
		fail("Not yet implemented");
	}

	@Test
	public void testAsLocalDateTime() {
		fail("Not yet implemented");
	}

	@Test
	public void testToList() {
		fail("Not yet implemented");
	}

	@Test
	public void testContains() {
		fail("Not yet implemented");
	}

}
