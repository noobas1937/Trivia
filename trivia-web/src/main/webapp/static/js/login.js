
function IbtnEnter_onclick() {
    checklogin();
    return false;
}
function checklogin() {
    console.log("123");
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
        url: "http://localhost:8080/trivia/session/login/",
        data: JSON.stringify({
            account : $("#username").val(),
            password : $("#userpwd").val()
        }),
        contentType: "application/json; charset=utf-8",
        success: function (data) {
            console.log(data);
            if (data == "200") {
                location.href = "index.aspx";
                return true;
            }
            else {
                alert("请确认您输入的用户名或密码输入是否正确！");
                $("#username").val("");
                $("#userpwd").val("");
                $("#userpwd").focus();
                return false;
            }
        }

    })
}