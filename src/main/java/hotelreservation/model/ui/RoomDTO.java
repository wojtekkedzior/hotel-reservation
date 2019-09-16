package hotelreservation.model.ui;

import hotelreservation.model.Amenity;
import hotelreservation.model.RoomType;
import hotelreservation.model.Status;
import hotelreservation.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Immutable;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Immutable
@AllArgsConstructor
public final class RoomDTO {

	@NotNull
	private int roomNumber;

	@NotNull
	private Status status;

	private String name;
	private String description;

	private List<Amenity> roomAmenities;

	@NotNull
	private RoomType roomType;

	@NotNull
	private LocalDateTime createdOn;

	@NotNull
	private User createdBy;
}
