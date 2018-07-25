package hotelreservation.service;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class ReportService {
	private final Logger log = LoggerFactory.getLogger(this.getClass());

}
