$(function(){
    loadUserTable();
});

function loadUserTable(){
    layui.use('table', function(){
        var table = layui.table;
        table.render({
            elem: '#table'
            ,height: 'full-200'
            ,page: true //开启分页
            ,url: '/trivia/user/'
            ,method: 'get'
            ,cols: [[ //表头
                {field: 'id', title: 'ID', width: 80, align: 'center', sort: 'true'},
                {field: 'account', title: '用户名', width: 150, align: 'center'},
                {field: 'nickName', title: '昵称', width: 150, align: 'center'},
                {field: 'headPic', title: '头像', width: 120, align: 'center', templet: '#picTpl'},
                {field: 'userType', title: '用户类型', width: 100, align: 'center',templet:'#userTypeTpl'},
                {field: 'status', title: '状态', width: 100, align: 'center',templet:'#statusTpl'},
                {field: 'balance', title: '财富', width: 100, align: 'center'},
                {field: 'lastLogin',title: '上次登陆时间',width: 180,align: 'center',templet: '#timeBeginTpl',sort: 'true'},
                {fixed: 'right',title:'操作', width: 165, align:'center', toolbar: '#barDemo'}
            ]]
            ,request: {
                pageName:"pno", //页码的参数名称，默认：page
                limitName: "PAGE_SIZE" //每页数据量的参数名，默认：limit
            }
            ,response: {
                statusName: 'resCode',
                statusCode: '200',
                msgName: 'resMsg',
                countName: 'count',
                dataName: 'data'
            },
            loading: true,
            size: 'lg'
        });

        //监听工具条
        table.on('tool(table)', function(obj){ //注：tool是工具条事件名，test是table原始容器的属性 lay-filter="对应的值"
            var data = obj.data, //获得当前行数据
                layEvent = obj.event; //获得 lay-event 对应的值
            if(layEvent === 'detail'){
                layer.msg('查看操作');
            } else if(layEvent === 'del'){
                layer.confirm('确定删除该行吗？', function(index){
                    //向服务端发送删除指令
                    $.ajax({
                        url: "/trivia/user/"+data.id+"/",
                        type: "DELETE",
                        dataType: "json",
                        success: function(data){
                            if (data.resCode === "200") {
                                obj.del(); //删除对应行（tr）的DOM结构
                                layer.close(index);
                                layer.msg('删除成功！');
                            }else{
                                layer.msg("删除失败！");
                            }
                        },
                    });
                });
            } else if(layEvent === 'edit'){
                layer.open({
                    id: 'editFrame',
                    type: 2,//弹出框类型
                    title: '编辑用户',
                    shadeClose: false, //点击遮罩关闭层
                    area : ['40%' , '42%'],//弹出框大小
                    shift:1,//弹出框动画效果
                    content: 'userEdit.html'//发送一个请求，后台处理数据返回到一个html页面加载到layer弹出层中
                    ,btn: ['确认修改', '取消']
                    ,yes: function(index, layero){
                        //console.log(JSON.stringify(data));
                        //layer.msg(layer.getChildFrame('body', index).find("#account").val());
                        //向服务端发送编辑指令
                        $.ajax({
                            url: "/trivia/user/modify/",
                            type: "POST",
                            dataType: "json",
                            contentType: "application/json; charset=utf-8",
                            data:JSON.stringify({
                                "id": data.id,
                                "password":data.password,
                                "status":layer.getChildFrame('body', index).find("#status").val(),
                                "balance":layer.getChildFrame('body', index).find("#balance").val(),
                                "headPic":data.headPic,
                                "userType":layer.getChildFrame('body', index).find("#userType").val(),
                                "nickName":layer.getChildFrame('body', index).find("#nickName").val()
                            }),
                            success: function(data){
                                if (data.resCode === "200") {
                                    layer.msg('编辑成功！');
                                }else{
                                    layer.msg("编辑失败！");
                                }
                            },
                        });
                        layer.close(index);
                    }
                    ,btn2: function(index, layero){
                    }
                    ,cancel: function(){
                    }
                });

                // layer.alert('\n' +
                //     '        <div class="layui-form-item">\n'+
                //     '        <label class="layui-form-label">用户名</label>\n' +
                //     '        <div class="layui-input-block">\n' +
                //     '            <input id="account" class="layui-input" type="text" placeholder='+data.account +' autocomplete="off" lay-verify="title">\n' +
                //     '        </div>\n' +
                //     '        </div>\n' +
                //     '        <div class="layui-form-item">\n'+
                //     '        <label class="layui-form-label">昵称</label>\n' +
                //     '        <div class="layui-input-block">\n' +
                //     '            <input id="nickName" class="layui-input" type="text" placeholder='+data.nickName +' autocomplete="off" lay-verify="title">\n' +
                //     '        </div>\n' +
                //     '        </div>\n' +
                //     '        <div class="layui-form-item">\n'+
                //     '        <label class="layui-form-label">类型</label>\n' +
                //     '        <div class="layui-input-block">\n' +
                //     '            <input id="userType" class="layui-input" type="text" placeholder='+data.userType +' autocomplete="off" lay-verify="title">\n' +
                //     '        </div>\n' +
                //     '        </div>\n' +
                //     '        <div class="layui-form-item">\n'+
                //     '        <label class="layui-form-label">状态</label>\n' +
                //     '        <div class="layui-input-block">\n' +
                //     '            <input id="status" class="layui-input" type="text" placeholder='+data.status +' autocomplete="off" lay-verify="title">\n' +
                //     '        </div>\n' +
                //     '        </div>\n' +
                //     '        <div class="layui-form-item">\n'+
                //     '        <label class="layui-form-label">财富</label>\n' +
                //     '        <div class="layui-input-block">\n' +
                //     '            <input id="balance" class="layui-input" type="text" placeholder='+data.balance +' autocomplete="off" lay-verify="title">\n' +
                //     '        </div>\n'+
                //     '        </div>\n'+JSON.stringify(data),{
                //     area: 'auto',
                //     skin: 'layui-layer-molv' //样式类名
                // },function(){
                //     layer.msg(""+$("#account").val()+"");
                //     //向服务端发送编辑指令
                //     // $.ajax({
                //     //     url: "http://localhost/trivia/user/modify/",
                //     //     type: "POST",
                //     //     dataType: "json",
                //     //     data:{
                //     //
                //     //     },
                //     //     success: function(data){
                //     //         if (data.resCode === "200") {
                //     //             layer.msg('编辑成功！');
                //     //         }else{
                //     //             layer.msg("编辑失败！");
                //     //         }
                //     //     },
                //     // });
                // })
            }
        });

    });


}

$('#userAddBtn').click(function(){
    layer.open({
        id: 'addFrame',
        type: 2,//弹出框类型
        title: '增加用户',
        shadeClose: false, //点击遮罩关闭层
        area : ['40%' , '35%'],//弹出框大小
        shift:1,//弹出框动画效果
        content: 'userAdd.html'//发送一个请求，后台处理数据返回到一个html页面加载到layer弹出层中
        ,btn: ['确认增加', '取消']
        ,yes: function(index, layero){
            //得到子页面id对应的数据
            var body = layer.getChildFrame('body', index);
            var account = body.find("#account").val();
            var nickName = body.find("#nickName").val();
            var password = body.find("#password").val();
            $.ajax({
                type: "POST",
                url: "/trivia/user/",
                data: JSON.stringify({
                    account : account,
                    nickName : nickName,
                    password : password
                }),
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
                        table.reload('mainTable',{});
                    }
                }
            })
        }
        ,btn2: function(index, layero){
        }
        ,cancel: function(){
        }
    });
});


