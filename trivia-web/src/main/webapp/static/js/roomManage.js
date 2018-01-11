var globalData;
var table;
$(function(){
    loadQuestionTable();
});

function loadQuestionTable(){
    layui.use('table', function(){
        table = layui.table;
        table.render({
            elem: '#table'
            ,height: 315
            ,page: true //开启分页
            ,url: '/trivia/room/list/'
            ,id: 'mainTable'
            ,method: 'get'
            ,page:true
            ,cols: [[ //表头
                {field: 'id', title: 'ID', width:50, sort: true, fixed: 'left'}
                ,{field: 'roomName', align: 'center', title: '房间名', width:239}
                ,{field: 'status', align: 'center', title: '状态', width:100}
            ]]
            ,request: {
                pageName: 'pno', //页码的参数名称，默认：page
                limitName: 'PAGE_SIZE' //每页数据量的参数名，默认：limit
            }
            ,response: {
                statusName: 'resCode',
                statusCode: '200',
                msgName: 'resMsg',
                countName: 'count',
                dataName: 'data'
            }
        });
        table.on('tool(table)', function(obj){
            var data = obj.data;
            globalData = data;
            if(obj.event === 'del'){
                layer.confirm('真的要删除该问题类型么', function(index){
                    $.ajax({
                        type: "DELETE",
                        /*url: "http://192.168.1.111:8080/trivia/session/login/",*/
                        url: "http://localhost:8081/trivia/question/type/" + data.id +"/",
                        contentType: "application/json; charset=utf-8",
                        dataType:"json",
                        success: function (data) {
                            console.log(data);
                            if (data.resCode === "200") {
                                obj.del(); //删除对应行（tr）的DOM结构
                                layer.alert("操作成功!");
                                layer.close(index);
                            }
                            else {
                                layer.alert("操作失败!");
                                layer.close(index);
                            }
                        }
                    })
                });
            } else if(obj.event === 'edit'){
                layer.open({
                    id: 'editFrame',
                    type: 2,//弹出框类型
                    title: '编辑问题类型',
                    shadeClose: false, //点击遮罩关闭层
                    area : ['60%' , '40%'],//弹出框大小
                    shift:1,//弹出框动画效果
                    content: 'question-type-edit.html'//发送一个请求，后台处理数据返回到一个html页面加载到layer弹出层中
                    ,btn: ['确认修改', '取消']
                    ,yes: function(index, layero){
                        //得到子页面id对应的数据
                        var body = layer.getChildFrame('body', index);
                        var description = body.find("#questionDescription").val();
                        console.log(data.id);
                        console.log(description);
                        if(description == ""){
                            layer.alert("请确保问题类型描述已填写");
                        }
                        else{
                            $.ajax({
                                type: "POST",
                                /*url: "http://192.168.1.111:8080/trivia/session/login/",*/
                                url: "http://localhost:8081/trivia/question/type/modify/?questionId="+ data.id +"&description=" + description,
                                contentType: "application/json; charset=utf-8",
                                dataType:"json",
                                success: function (data) {
                                    if (data.resCode === "200") {
                                        obj.update({
                                            name : description
                                        });
                                        layer.alert("操作成功!");
                                        layer.close(index);

                                    }
                                    else {
                                        layer.alert("操作失败!");
                                        layer.close(index);
                                    }
                                }
                            })}
                    }
                    ,btn2: function(index, layero){
                    }
                    ,cancel: function(){
                    }
                });
            }
        });
    });
}

$('#questionAddBtn').click(function(){
    layer.open({
        id: 'addFrame',
        type: 2,//弹出框类型
        title: '增加问题',
        shadeClose: false, //点击遮罩关闭层
        area : ['60%' , '40%'],//弹出框大小
        shift:1,//弹出框动画效果
        content: 'question-type-add.html'//发送一个请求，后台处理数据返回到一个html页面加载到layer弹出层中
        ,btn: ['确认增加', '取消']
        ,yes: function(index, layero){
            //得到子页面id对应的数据
            var body = layer.getChildFrame('body', index);
            var description = body.find("#questionDescription").val();

            if(description == ""){
                layer.alert("请确保每项均已填写");
            }
            else{
                $.ajax({
                    type: "POST",
                    /*url: "http://192.168.1.111:8080/trivia/session/login/",*/
                    url: "http://localhost:8081/trivia/question/type/?description=" + description,
                    contentType: "application/json; charset=utf-8",
                    dataType:"json",
                    success: function (data) {
                        if (data.resCode === "200") {
                            layer.alert("操作成功!");
                            layer.close(index);
                        }
                        else {
                            layer.alert("操作失败!");
                            layer.close(index);
                        }
                    }
                })}
        }
        ,btn2: function(index, layero){
        }
        ,cancel: function(){
        }
    });
});
