"use strict";

$(function(){
    var senderMailForm = $("form[name='senderMail']");
    senderMailForm.on("submit", function(event){
        var senderMail = senderMailForm.find("#senderEmail").val();

        var payload = {
            sender: senderMail
        }

        console.log(payload);
        $.ajax({type:"POST", url: "?type=Sender", data: JSON.stringify(payload), contentType: "application/json"}).done(function(data, status, jqxhr){
            console.log(data, status, jqxhr);
        });

        // event.preventDefault();
        // return false;
    });

    var addHandleForm = $("form[name='addHandle']");
    addHandleForm.on("submit", function(event){

        var payload = {
            handle: addHandleForm.find("#newHandleName").val(),
            to: addHandleForm.find("#newHandleTo").val(),
            cc: addHandleForm.find("#newHandleCc").val(),
            bcc: addHandleForm.find("#newHandleBcc").val()
        }

        console.log(payload);
        $.ajax({type:"POST", url: "?type=Handle", data: JSON.stringify(payload), contentType: "application/json"}).done(function(data, status, jqxhr){
            console.log(data, status, jqxhr);
        });

        // event.preventDefault();
        // return false;
    });
});

function deleteHandle(handleName){
    var payload = {
        handle: handleName,
        action: "DELETE"
    }

    console.log(payload);
    $.ajax({type:"POST", url: "?type=Handle", data: JSON.stringify(payload), contentType: "application/json"}).done(function(data, status, jqxhr){
        console.log(data, status, jqxhr);
        location.reload();
    });
}