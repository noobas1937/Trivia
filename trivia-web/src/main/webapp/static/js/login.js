
function checklogin() {
    if ($("#username").val() === "") {
        layer.msg("用户名不能为空！");
        $("#username").focus();
        return false;
    }
    if ($("#userpwd").val() == "") {
        layer.msg("密码不能为空！");
        $("#userpwd").focus();
        return false;
    }
    $.ajax({
        type: "POST",
        url: "/trivia/session/login/",

        data: JSON.stringify({
            account : $("#username").val(),
            password : $("#userpwd").val()
        }),
        contentType: "application/json; charset=utf-8",
        dataType:"json",
        success: function (data) {
            console.log(data);
            if (data.resCode === "200") {
                location.href="hall.html";
            }
            else {
                layer.msg(data.resMsg);
                $("#userpwd").val("");
                $("#userpwd").focus();
            }
        }

    })
}

$(function(){
    $("#login-button").click(function(){
        checklogin();
    });
});