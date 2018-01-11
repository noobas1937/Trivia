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
            ,height: 'full-200'
            ,page: true //开启分页
            ,url: '/trivia/question/retrive/'
            ,id: 'mainTable'
            ,method: 'get'
            ,page:true
            ,cols: [[ //表头
            {field: 'id', title: 'ID', width:50, sort: true, fixed: 'left'}
            ,{field: 'description', align: 'center', title: '问题', width:239}
            ,{field: 'chooseA', align: 'center', title: '选择A', width:170}
            ,{field: 'chooseB', align: 'center', title: '选择B', width:170}
            ,{field: 'chooseC', align: 'center', title: '选择C', width:170}
            ,{field: 'chooseD', align: 'center', title: '选择D', width:170}
            ,{field: 'answer', align: 'center', title: '答案', width: 70, templet: "#answerTpl"}
            ,{field: 'type', align: 'center', title: '类型', width: 120}
            ,{field: 'typeId', align: 'center', title: '类型ID', width: 70}
            ,{fixed: 'right', width:178, align:'center', toolbar: '#barDemo'}
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
                layer.confirm('真的要删除该问题么', function(index){
                    $.ajax({
                        type: "DELETE",
                        /*url: "http://192.168.1.111:8080/trivia/session/login/",*/
                        url: "/trivia/question/" + data.id +"/",
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
                title: '编辑问题',  
                shadeClose: false, //点击遮罩关闭层  
                area : ['60%' , '90%'],//弹出框大小  
                shift:1,//弹出框动画效果  
                content: 'question-edit.html'//发送一个请求，后台处理数据返回到一个html页面加载到layer弹出层中  
                ,btn: ['确认修改', '取消']
                ,yes: function(index, layero){
                    //得到子页面id对应的数据
                    var body = layer.getChildFrame('body', index);
                    var description = body.find("#questionDescription").val();
                    var typeId = body.find('#selectType').find('option:selected').attr('id');
                    var type =  body.find('#selectType').find('option:selected').text(); 
                    var chooseA = body.find("#chooseA").val();
                    var chooseB = body.find("#chooseB").val();
                    var chooseC = body.find("#chooseC").val();
                    var chooseD = body.find("#chooseD").val();
                    var answer = body.find('#selectAnwser').find('option:selected').attr('value');

                    if(description == "" ||
                        chooseA == "" ||
                        chooseB == "" ||
                        chooseC == "" ||
                        chooseD == ""){
                        layer.alert("请确保每项均已填写");
                    }
                    else{
                    $.ajax({
                        type: "POST",
                        /*url: "http://192.168.1.111:8080/trivia/session/login/",*/
                        url: "/trivia/question/modify/",
                        data: JSON.stringify({
                            id : data.id,
                            description : description,
                            typeId : typeId,
                            chooseA : chooseA,
                            chooseB : chooseB,
                            chooseC : chooseC,
                            chooseD : chooseD,
                            answer : answer
                        }),
                        contentType: "application/json; charset=utf-8",
                        dataType:"json",
                        success: function (data) {
                            if (data.resCode === "200") {
                                obj.update({
                                    description : description,
                                    typeId : typeId,
                                    type: type,
                                    chooseA : chooseA,
                                    chooseB : chooseB,
                                    chooseC : chooseC,
                                    chooseD : chooseD,
                                    answer : answer
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
                area : ['60%' , '90%'],//弹出框大小  
                shift:1,//弹出框动画效果  
                content: 'question-add.html'//发送一个请求，后台处理数据返回到一个html页面加载到layer弹出层中  
                ,btn: ['确认增加', '取消']
                ,yes: function(index, layero){
                    //得到子页面id对应的数据
                    var body = layer.getChildFrame('body', index);
                    var description = body.find("#questionDescription").val();
                    var typeId = body.find('#selectType').find('option:selected').attr('id');
                    var type =  body.find('#selectType').find('option:selected').text(); 
                    var chooseA = body.find("#chooseA").val();
                    var chooseB = body.find("#chooseB").val();
                    var chooseC = body.find("#chooseC").val();
                    var chooseD = body.find("#chooseD").val();
                    var answer = body.find('#selectAnwser').find('option:selected').attr('value');

                    if(description == "" ||
                        chooseA == "" ||
                        chooseB == "" ||
                        chooseC == "" ||
                        chooseD == ""){
                        layer.alert("请确保每项均已填写");
                    }
                    else{
                    $.ajax({
                        type: "POST",
                        /*url: "http://192.168.1.111:8080/trivia/session/login/",*/
                        url: "/trivia/question/",
                        data: JSON.stringify({
                            description : description,
                            typeId : typeId,
                            chooseA : chooseA,
                            chooseB : chooseB,
                            chooseC : chooseC,
                            chooseD : chooseD,
                            answer : answer
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
                    })}
                }
                ,btn2: function(index, layero){
                }
                ,cancel: function(){ 
                }
                });
});
