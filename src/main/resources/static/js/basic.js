function saveForm(guestId, reservationId){

    $.ajax({
        method: "POST",
        url: "/hotelreservation/deleteContact/" + guestId + "/reservationId/" + reservationId,
        data: $('#reservation').serialize(),
        success: function(status){
            if(status) {
                //here you check the response from your controller and add your business logic
            	//$.redirect('/hotelreservation/realiseReservation/'+reservationId);
            	 window.location = '/hotelreservation/realiseReservation/'+reservationId;
            }
        }
    });
}