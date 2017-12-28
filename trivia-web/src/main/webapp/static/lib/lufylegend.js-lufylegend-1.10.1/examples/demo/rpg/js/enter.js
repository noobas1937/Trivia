var zero=0;
function enter(roomId) {
    console.log("123");
    $.ajax({
            type: "GET",
            url: "/trivia/room/enter/",
            contentType: "application/json; charset=utf-8",
           data: {
            "roomId":roomId
           },
            success: function (data) {
                console.log(data);
                if (data=== "200") {
                    console.log("12345");
                    location.href = "index.aspx";
                    return true;
                }
                else {
                    alert("用户名已存在！");
                    /* $("#username").val("");
                     $("#userpwd").val("");
                     $("#userpwd").focus();*/
                    return false;
                }
            }
        }
    )
}
$(function(){
    $("#room1").click(function(){
        console.log("123");
        enter(1);
    });
    $("#room2").click(function(){
        console.log("123");
        enter(2);
    });
    $("#room3").click(function(){
        console.log("123");
        enter(3);
    });
    $("#room4").click(function(){
        console.log("123");
        enter(4);
    });

    $("#room5").click(function(){
        console.log("123");
        enter(5);
    });
    $("#room6").click(function(){
        console.log("123");
        enter(6);
    });

});
