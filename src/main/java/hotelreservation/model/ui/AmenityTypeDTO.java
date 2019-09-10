package hotelreservation.model.ui;

import hotelreservation.model.AmenityType;
import hotelreservation.model.ReservationCharge;
import hotelreservation.model.enums.PaymentType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Immutable;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Data
@Immutable
@RequiredArgsConstructor
@AllArgsConstructor
public final class AmenityTypeDTO {

	private Long id;

	private String name;

	private String description;

	public AmenityTypeDTO(String name, String description) {
		this.name = name;
		this.description = description;
	}

}
