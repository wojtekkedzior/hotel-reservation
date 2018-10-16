/*($(function() {
    $('#submit-new-period').click(saveForm);
});*/


function saveForm(){
    $.ajax({
        method: "POST",
        url: "/your/action/endpoint",
        data: $('#idYourForm').serialize(),
        success: function(status){
            if(status) {
                //here you check the response from your controller and add your business logic
            }
        }
    });
}