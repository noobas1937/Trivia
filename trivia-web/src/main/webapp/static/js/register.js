


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
            success: function (data) {
                console.log(data);
                if (data == "200") {
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
    $("#register-button").click(function(){
        register();
    });
});