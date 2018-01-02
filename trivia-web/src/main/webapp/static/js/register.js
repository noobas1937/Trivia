
function register() {
    console.log($("#nickname").val());
    $.ajax({
            type: "POST",
            /*url: "http://192.168.1.111:8080/trivia/session/login/",*/
            url: "/trivia/session/register/",

            data: JSON.stringify({
                nickname: $("#nickname").val(),
                headpic: $("#headpic").val(),
                account: $("#account").val(),
                password: $("#password").val()
            }),

            contentType: "application/json; charset=utf-8",
            dataType:"json",
            success: function (data) {
                console.log(data);
                if (data.resCode  == "200") {
                    location.href = "register.html";

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
    $("#register-button").click(function(){
        register();
    });
});