
function checklogin() {
   /* console.log("123");*/
    if ($("#username").val() == "") {
        alert("用户名不能为空！");
        $("#username").focus();
        return false;
    }
    if ($("#userpwd").val() == "") {
        alert("密码不能为空！");
        $("#userpwd").focus();
        return false;
    }
    $.ajax({
        type: "POST",
        /*url: "http://192.168.1.111:8080/trivia/session/login/",*/
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
                //location.href="../static/lib/lufylegend.js-lufylegend-1.10.1/examples/demo/rpg/1.html";
            }
            else {
                alert("请确认您输入的用户名或密码输入是否正确！");
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