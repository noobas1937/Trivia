//导包
var tiledMap;
var operationView;
var questionDialog;
var questionTypeDialog;
var gameResultDialog;
var msgDialog;
var Handler= Laya.Handler;
var Stage     = Laya.Stage;
var TiledMap  = Laya.TiledMap;
var Rectangle = Laya.Rectangle;
var HTMLDivElement    = Laya.HTMLDivElement;
var WebGL     = Laya.WebGL;
var Event = Laya.Event;
var Socket = Laya.Socket;
var Byte   = Laya.Byte;
var Tween   = Laya.Tween;

// 定义常量
var GAME_READY = 0;
var GAME_DICE_RESULT = 1;
var GAME_CHOOSE_TYPE = 5;
var GAME_ANSWERING_QUESTION = 2;
var GAME_ANSWER_QUESTION_RESULT = 3;
var GAME_OVER = 4;
var ROOM_WAITING = 0;
var ROOM_PLAYING = 1;
var PLAYER_WAITING = 0;
var PLAYER_READY = 1;
var PLAYER_GAMING_FREE = 2;
var PLAYER_GAMING_HOLD = 3;

//View Global Variable
var traceBits=[1,1,2,2,1,1,1,0,0,1,1,1,1,1,2,2,2,3,3,3,2,2,2,1,1,1,2,2,2,2,3,3,3,3,3,3,0,0,0,0,3,3,2,2,2,2,3,3,0,0,0,0,0,0,0,0,0,0];
var graph;
var unit_x=100;
var unit_y=80;
var roles;
var rolesText;

//Game Data Variables
var myUserId;
var players;
var questionTypes2ID;

layui.use('layer', function() { //独立版的layer无需执行这一句
    var $ = layui.jquery, layer = layui.layer; //独立版的layer无需执行这一句
}
function OperationPanel()
{
	OperationPanel.super(this);   
}
function QuestionDialog()
{
	QuestionDialog.super(this);
}
function MsgDialog()
{
	MsgDialog.super(this);
}
function QuestionTypeDialog()
{
	QuestionTypeDialog.super(this);
}
function GameResultDialog()
{
	GameResultDialog.super(this);
}
(function()
{
    // 不支持WebGL时自动切换至Canvas
    Laya.init(1500, 950, WebGL);

    //调用DebugTool调试面板
    // Laya.DebugTool.init();
    Laya.stage.alignV = Stage.ALIGN_MIDDLE;
    Laya.stage.alignH = Stage.ALIGN_CENTER;
    Laya.stage.scaleMode = Stage.SCALE_SHOWALL;
    Laya.stage.bgColor = "#FFFFFF";

    createMap();
	
})();

function initData(){
    $.ajax({
        type: "GET",
        url: "/trivia/game/refresh/",
        contentType: "application/json; charset=utf-8",
        dataType:"json",
        success: function (body) {
            if (body.resCode !== "200") {
                layer.msg("拉取房间数据出错！");
            }   
        }
    });
}

function createDice(){
    operationView.btnDice.on(Event.CLICK, this, initDiceBtnListener);
}

function initDiceBtnListener(){
    setBtnVisibility(false,false,false);
    $.ajax({
        type: "GET",
        url: "/trivia/game/roll/dice/",
        contentType: "application/json; charset=utf-8",
        dataType:"json",
        success: function (body) {
            if (body.resCode !== "200") {
                layer.msg(body.resMsg);
            }
        }
    });
}

/**
 * 建立WebSocket连接
 */
function connect()
{
    socket = new Socket();
    socket.connectByUrl("ws://115.159.35.11:8080/trivia/websocket/");

    output = socket.output;

    socket.on(Event.OPEN, this, onSocketOpen);
    socket.on(Event.CLOSE, this, onSocketClose);
    socket.on(Event.MESSAGE, this, onMessageReveived);
    socket.on(Event.ERROR, this, onConnectError);
}


function onSocketOpen()
{
    console.log("Connected");
}

function onSocketClose()
{
    layer.msg("socket连接关闭，请检查登录状态");
}

function onMessageReveived(message)
{
    console.log("Message from server:");
    var json = JSON.parse(message); //由JSON字符串转换为JSON对象
    refreshUI(json);
    console.log(json);
    socket.input.clear();
}

function onConnectError(e)
{
    console.log("error");
    layer.msg("socket建立失败！");
}

function refreshUI(message){
    if(typeof message.nickName !== 'undefined'){
        //socket连接数据包，更新玩家ID
        myUserId = message.id;
        return;
    }
    refreshPlayerPanel(message);
    refreshPlayerPosition(message);
    refreshOpeartionpanel(message);
}

/**
 * 刷新操作按钮
 * @param {*} message 
 */
function refreshOpeartionpanel(message){
    var stage = message.game.stage;
    var curPlayer,curPlayerName;
    //获取当前玩家ID
    $.each(message.playerList,function(index,item){
        if(item.id === message.game.currentPlayerId) {
            curPlayer = item.userId;
            curPlayerName = item.nickName;
            return false;
        }
    });
    switch(stage){
        case GAME_READY:
            //新一轮游戏就绪 UI数据包
            if(curPlayer===myUserId){
                setBtnVisibility(false,false,true);
            }
        break;
        case GAME_DICE_RESULT:
            //掷骰子点数结果 UI数据包
            var dice = $("#dice");
            dice.attr("class","dice");//清除上次动画后的点数 
            dice.css("cursor","default"); 
            $(".wrap").append("<div id='dice_mask'></div>");//加遮罩 
            var num = message.game.diceNumber;
            dice.animate({left: '+2px'}, 100,function(){ 
                dice.addClass("dice_t"); 
            }).delay(200).animate({top:'-2px'},100,function(){ 
                dice.removeClass("dice_t").addClass("dice_s"); 
            }).delay(200).animate({opacity: 'show'},600,function(){ 
                dice.removeClass("dice_s").addClass("dice_e"); 
            }).delay(100).animate({left:'-2px',top:'2px'},100,function(){ 
                dice.removeClass("dice_e").addClass("dice_"+num); 
                $("#result").html("掷得点数是<span>"+num+"</span>"); 
                $("#dice_mask").remove();//移除遮罩 
            }); 
            if(curPlayer===myUserId){
            }else{
                
            }
        break;
        case GAME_CHOOSE_TYPE:
            setBtnVisibility(false,false,false);
            //选择问题类型
            $.ajax({
                type: "GET",
                url: "/trivia/game/question/type/",
                contentType: "application/json; charset=utf-8",
                dataType:"json",
                success: function (body) {
                    if (body.resCode === "200") {
                        var label = "";
                        questionTypes2ID= new Array();
                        $.each(body.data,function(index,item){
                            label+=item.name+",";
                            questionTypes2ID[index] = item.id;
                        });
                        label = label.substring(0,label.length-1);
                        questionTypeDialog.typeRadio.labels = label;
                        setTimeout("questionTypeDialog.show()",2000);
                    } else{
                        layer.msg(body.resMsg);
                    }  
                }
            });
            break;
        case GAME_ANSWERING_QUESTION:
            setBtnVisibility(false,false,false);
            //获取问题中（回答中） UI数据包
            $.ajax({
                type: "GET",
                url: "/trivia/game/question/",
                contentType: "application/json; charset=utf-8",
                dataType:"json",
                data: {
                    'id' : message.game.questionId,
                },
                success: function (body) {
                    if (body.resCode === "200") {
                        var label = "";
                        label+=body.data.chooseA+",";
                        label+=body.data.chooseB+",";
                        label+=body.data.chooseC+",";
                        label+=body.data.chooseD;
                        questionDialog.questionText.text = body.data.description;
                        questionDialog.questionText.wordWrap = true;
                        questionDialog.questionRadio.labels = label;
                        questionDialog.show();
                    } else{
                        layer.msg(body.resMsg);
                    }  
                }
            });
            if(curPlayer===myUserId){
            }else{
                
            }
        break;
        case GAME_ANSWER_QUESTION_RESULT:
            //回答问题结果 UI数据包
            if(curPlayer===myUserId){//我的轮次
                $.each(message.playerList,function(index,item){
                    if(item.userId === curPlayer){
                        if(item.status === PLAYER_GAMING_HOLD){
                            layer.msg("抱歉哦！您被关在监狱中了~ 只有下局掷得偶数才可以前进哦~");
                        }else{
                            layer.msg("恭喜您答对了，给你一个小金币~");
                        }
                    }
                });
            }else{//其他人的轮次
                $.each(message.playerList,function(index,item){
                    if(item.userId !== curPlayer){
                        if(item.status === PLAYER_GAMING_HOLD){
                            layer.msg("恭喜玩家："+curPlayerName+",答错题目被关入监狱~");
                        }else{
                            layer.msg("恭喜玩家："+curPlayerName+",答对题目获得一个小金币~");
                        }
                    }
                });
            }
        break;
        case GAME_OVER:
            //游戏结束 UI数据包
            setBtnVisibility(true,true,false);
            if(message.playerList[0].status === PLAYER_WAITING){
                 break; 
            }
            gameResultDialog.resultText.text = " \t排名\t \t\t \t昵称\t \t\t \t位置\t \t\t \t金币\n\n";
            $.each(message.playerList,function(index,item){
                gameResultDialog.resultText.text += " \t\t"+(index+1)+"\t";
                gameResultDialog.resultText.text += " \t\t\t\t"+item.nickName+"\t";
                gameResultDialog.resultText.text += " \t\t\t\t"+item.position+"\t";
                gameResultDialog.resultText.text += " \t\t\t\t"+item.balance+"\n\n";
            });
            gameResultDialog.show();
        break;
    }
}

/**
 * 刷新玩家位置
 * @param {*} obj 
 */
function refreshPlayerPosition(obj){
    if(obj.status === ROOM_PLAYING){
        $.each(obj.playerList,function(index,item){
            if(item.position === -1) item.position = 0;
            var position =graph[item.position];
            roles[index].visible = true;
            rolesText[index].visible = true;
            rolesText[index].innerHTML = item.nickName;
            Tween.to(roles[index],
            {
                x: position.x,
                y: position.y
            }, 1000);
            Tween.to(rolesText[index],
            {
                x: position.x-20,
                y: position.y+60
            }, 1000);
        });
    }else if(obj.status === ROOM_WAITING){
        $.each(obj.playerList,function(index,item){
            if(item.position === -1) item.position = 0;
            var position = graph[item.position];
            roles[index].x=position.x;
            roles[index].y=position.y; 
            rolesText[index].x=position.x;
            rolesText[index].y=position.y;
        });
    }
}

/**
 * 刷新玩家界面
 * @param {*数据包} message 
 */
function refreshPlayerPanel(obj){
    console.log(obj);
    if(obj.status === ROOM_PLAYING){
        operationView.playerText.text = " \t昵称\t \t\t \t位置\t \t\t \t金币\n\n";
        $.each(obj.playerList,function(index,item){
            operationView.playerText.text += " \t\t"+item.nickName+"\t";
            operationView.playerText.text += " \t\t\t\t"+item.position+"\t";
            operationView.playerText.text += " \t\t\t\t"+item.balance+"\n\n";
        });
    }else if(obj.status === ROOM_WAITING){
        operationView.playerText.text = " \t昵称\t \t\t \t位置\t \t\t \t金币\n\n";
        $.each(obj.playerList,function(index,item){
            operationView.playerText.text += " \t\t"+item.nickName+"\t";
            operationView.playerText.text += " \t\t\t\t"+ 0 +"\t";
            operationView.playerText.text += " \t\t\t\t"+ 0 +"\n\n";
        });
    } else {
        console.log("房间状态异常=====");
    }
}

/**
 * 设置按钮的显示状态
 * @param {*} btnReady 
 * @param {*} btnExit 
 * @param {*} btnDice 
 */
function setBtnVisibility(btnReady,btnExit,btnDice){
    operationView.btnReady.visible = btnReady;
    operationView.btnExit.visible = btnExit;
    operationView.btnDice.visible = btnDice;
}

/**
 * 创建地图
 */
function createMap()
{
    tiledMap = new TiledMap();
    tiledMap.createMap("../laya/assets/map/orthogonal-test-movelayer.json", new Rectangle(0, 0, 1100, 950),Handler.create(this,onMapLoaded));
}

/**
 * 地图加载完成回调
 */
function onMapLoaded(){
    this.tiledMap.scale = 1.0;
    //初始化地图走向
    graph = [];
    $.each(traceBits,function(index,item){
        if(index === 0) {
            graph.push({"x":50,"y":40});
            return true;
        }
        var position = graph[index-1];
        var t = traceBits[index-1];
        switch(t){
            case 0:
            graph.push({"x":position.x,"y":position.y-unit_y});
            break;
            case 1:
            graph.push({"x":position.x+unit_x,"y":position.y});
            break;
            case 2:
            graph.push({"x":position.x,"y":position.y+unit_y});
            break;
            case 3:
            graph.push({"x":position.x-unit_x,"y":position.y});
            break;
        }
    });
    //初始化角色列表
    roles = [tiledMap.getLayerObject("hero","role1"),tiledMap.getLayerObject("hero","role2"),
            tiledMap.getLayerObject("hero","role3"),tiledMap.getLayerObject("hero","role4")];
    rolesText = [new HTMLDivElement(),new HTMLDivElement(),new HTMLDivElement(),new HTMLDivElement()];
    $.each(roles,function(index,item){
        item.visible = false;
        rolesText[index].style.font = "Impact";
		rolesText[index].style.fontSize = 20;
		rolesText[index].style.color = "#000000";
        rolesText[index].innerHTML = "";
        rolesText[index].visible = false;
        Laya.stage.addChild(rolesText[index]);
    });
    //预加载资源文件后执行回调
    Laya.loader.load(["h5/res/atlas/comp.atlas","h5/res/atlas/template/ButtonTab.atlas","h5/res/atlas/template/Warn.atlas"], Handler.create(this, onDialogLoaded));
}

/**
 * 操作加载完成回调
 */
function onDialogLoaded(){
    Laya.class(OperationPanel, "OperationPanel", OperationPanelUI);
    Laya.class(QuestionDialog, "QuestionDialog", QuestionDialogUI);
    Laya.class(QuestionTypeDialog, "QuestionTypeDialog", QuestionTypeDialogUI);
    Laya.class(GameResultDialog, "GameResultDialog", GameResultDialogUI);
    Laya.class(MsgDialog, "MsgDialog", MsgDialogUI);
    operationView = new OperationPanel();
    questionDialog = new QuestionDialog();
    questionTypeDialog = new QuestionTypeDialog();
    gameResultDialog = new GameResultDialog();
    msgDialog = new MsgDialog();
    msgDialog.msgContent.wordWrap = true;
    operationView.x = 1100;
    operationView.y = 0;
    operationView.btnReady.on(Event.CLICK, this, btnReadyClicked);
    operationView.btnExit.on(Event.CLICK, this, btnExitClicked);
    Laya.stage.addChild(operationView);
    createDice();
    initDialogs();
    connect();
    initData();
}

function initDialogs(){
    questionTypeDialog.btnTConfirm.on(Event.CLICK, this, btnTConfirmClicked);
    questionDialog.btnQConfirm.on(Event.CLICK, this, btnQConfirmClicked);
    gameResultDialog.btnRConfirm.on(Event.CLICK, this, btnRConfirmClicked);
    msgDialog.btnMConfirm.on(Event.CLICK, this, btnMConfirmClicked);
}

function btnExitClicked(){
    $.ajax({
        type: "POST",
        url: "/trivia/room/exit/",
        contentType: "application/json; charset=utf-8",
        dataType:"json",
        success: function (body) {
            if (body.resCode !== "200") {
                layer.msg(body.resMsg);
            }else{
                layer.msg(body.resMsg);
                location.href = "../../pages/hall.html"
            }
        }
    });
}

function btnMConfirmClicked(){
    msgDialog.close();
}

function btnRConfirmClicked(){
    gameResultDialog.close();
}

function btnQConfirmClicked(){
    questionDialog.close();
    $.ajax({
        type: "POST",
        url: "/trivia/game/question/answer/",
        contentType: "application/json; charset=utf-8",
        dataType:"json",
        data: JSON.stringify(
            questionDialog.questionRadio.selectedIndex+1
        ),
        success: function (body) {
            if (body.resCode !== "200") {
                layer.msg(body.resMsg+"请重新尝试！");
                questionDialog.show();
            }
        }
    });
}

/**
 * 类型选择按钮点击事件监听
 */
function btnTConfirmClicked(){
    questionTypeDialog.close();
    $.ajax({
        type: "GET",
        url: "/trivia/game/question/choose/",
        contentType: "application/json; charset=utf-8",
        dataType:"json",
        data: {
            'type' : questionTypes2ID[questionTypeDialog.typeRadio.selectedIndex]
        },
        success: function (body) {
            if (body.resCode !== "200") {
                layer.msg(body.resMsg+"请更换选项尝试！");
                questionTypeDialog.show();
            }
        }
    });
}   

/**
 * 准备按钮点击事件 
 */
function btnReadyClicked(){
    setBtnVisibility(false,false,false);
    $.ajax({
        type: "GET",
        url: "/trivia/game/ready/1",
        contentType: "application/json; charset=utf-8",
        dataType:"json",
        success: function (body) {
            if (body.resCode === "200") {
                operationView.btnReady.label="取消准备";
                operationView.btnReady._events = null
                operationView.btnReady.on(Event.CLICK, this, btnCancelReadyClicked);
                setBtnVisibility(true,false,false);
            } else {
                layer.msg(body.resMsg);
                setBtnVisibility(true,false,false);
            }
        }
    });
}

/**
 * 取消准备监听
 */
function btnCancelReadyClicked(){
    setBtnVisibility(false,false,false);
    $.ajax({
        type: "GET",
        url: "/trivia/game/ready/0",
        contentType: "application/json; charset=utf-8",
        dataType:"json",
        success: function (body) {
            if (body.resCode === "200") {
                operationView.btnReady.label="准备";
                operationView.btnReady._events = null;
                operationView.btnReady.on(Event.CLICK, this, btnReadyClicked);
                setBtnVisibility(true,true,false);
            } else {
                layer.msg(body.resMsg);
                setBtnVisibility(true,false,false);
            }
        }
    });
}