package hotelreservation.model.enums;

public enum ReservationStatus {
	
	//Cancelled - means cancelled before it was put to in progress.  Abandoned - was upcoming, but guest didn't turn up 
	FULFILLED, IN_PROGRESS, UP_COMING, CANCELLED, ABANDONED;

}
