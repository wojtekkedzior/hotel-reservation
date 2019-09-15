package hotelreservation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class Utils {
	private static final String LOG_MESSAGE = "converted localDate: {} to: {}";
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	public Date asDate(LocalDate localDate) {
		Date date = Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
		log.info(LOG_MESSAGE, localDate, date);
		return date;
	}

	public Date asDate(LocalDateTime localDateTime) {
		Date date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
		log.info(LOG_MESSAGE, localDateTime, date);
		return date;
	}

	public LocalDate asLocalDate(Date date) {
		LocalDate localDate = Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
		log.info(LOG_MESSAGE, date, localDate);
		return localDate;
	}

	public LocalDateTime asLocalDateTime(Date date) {
		LocalDateTime localDateTime = Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
		log.info(LOG_MESSAGE, date, localDateTime);
		return localDateTime;
	}

	public <T> List<T> toList(final Iterable<T> iterable) {
		return StreamSupport.stream(iterable.spliterator(), false).collect(Collectors.toList());
	}
	
	public <T> boolean contains(List<T> list, T item, Comparator<? super T> comparator) {
	    return list.stream()
	            .anyMatch(listItem -> comparator.compare(listItem, item) == 0
	            );
	}
	
	public boolean isNullOrEmpty(String s) {
		return (s==null || s.trim().equals(""));
	}
	
}