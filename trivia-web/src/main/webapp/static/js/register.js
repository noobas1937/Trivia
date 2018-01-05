
function register() {
    if ($("#nickname").val() === "") {
        layer.msg("昵称不能为空！");
        $("#nickname").focus();
        return false;
    }
    if (headimg === "") {
        layer.msg("头像不能为空！");
        return false;
    }
    if ($("#account").val() == "") {
        layer.msg("账号不能为空！");
        $("#account").focus();
        return false;
    }
    if ($("#password").val() == "") {
        layer.msg("密码不能为空！");
        $("#password").focus();
        return false;
    }
    $.ajax({
            type: "POST",
            url: "/trivia/session/register/",

            data: JSON.stringify({
                nickname: $("#nickname").val(),
                headpic: headimg,
                account: $("#account").val(),
                password: $("#password").val()
            }),

            contentType: "application/json; charset=utf-8",
            dataType:"json",
            success: function (data) {
                console.log(data);
                if (data.resCode  === "200") {
                    location.href = "hall.html";
                }
                else {
                    layer.msg(data.resMsg);
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