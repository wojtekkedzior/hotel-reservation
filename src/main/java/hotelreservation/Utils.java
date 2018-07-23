package hotelreservation;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Utils {
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	public Date asDate(LocalDate localDate) {
		Date date = Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
		log.debug("converted localDate: " + localDate + " to: " + date);
		return date;
	}

	public Date asDate(LocalDateTime localDateTime) {
		Date date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
		log.debug("converted localDate: " + localDateTime + " to: " + date);
		return date;
	}

	public LocalDate asLocalDate(Date date) {
		LocalDate d = Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
		log.debug("converted localDate: " + d + " to: " + date);
		return d;
	}

	public LocalDateTime asLocalDateTime(Date date) {
		LocalDateTime d = Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
		log.debug("converted localDate: " + d + " to: " + date);
		return d;
	}

	public <T> List<T> toList(final Iterable<T> iterable) {
		return StreamSupport.stream(iterable.spliterator(), false).collect(Collectors.toList());
	}
}
