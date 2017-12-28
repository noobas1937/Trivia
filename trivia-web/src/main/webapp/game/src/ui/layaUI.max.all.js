var CLASS$=Laya.class;
var STATICATTR$=Laya.static;
var View=laya.ui.View;
var Dialog=laya.ui.Dialog;
var GameResultDialogUI=(function(_super){
		function GameResultDialogUI(){
			
		    this.resultTitle=null;
		    this.btnRConfirm=null;
		    this.resultText=null;

			GameResultDialogUI.__super.call(this);
		}

		CLASS$(GameResultDialogUI,'ui.GameResultDialogUI',_super);
		var __proto__=GameResultDialogUI.prototype;
		__proto__.createChildren=function(){
		    
			laya.ui.Component.prototype.createChildren.call(this);
			this.createView(GameResultDialogUI.uiView);
		}

		STATICATTR$(GameResultDialogUI,
		['uiView',function(){return this.uiView={"type":"Dialog","props":{"width":600,"height":400},"child":[{"type":"Image","props":{"y":4,"x":-1,"width":602,"skin":"comp/bg.png","height":394}},{"type":"Label","props":{"y":86,"x":58,"width":281,"var":"resultTitle","height":27,"fontSize":20,"font":"SimHei","color":"#ffffff","align":"left"}},{"type":"Button","props":{"y":325,"x":441,"width":98,"var":"btnRConfirm","skin":"comp/button.png","name":"btnRConfirm","labelSize":20,"labelFont":"SimHei","label":"确定","height":41}},{"type":"Label","props":{"y":45,"x":59,"text":"游戏结束","fontSize":25,"font":"SimHei","color":"#ffffff"}},{"type":"TextArea","props":{"y":151,"x":54,"width":340,"var":"resultText","name":"resultText","height":210,"fontSize":20,"editable":false}}]};}
		]);
		return GameResultDialogUI;
	})(Dialog);
var MsgDialogUI=(function(_super){
		function MsgDialogUI(){
			
		    this.btnMConfirm=null;
		    this.msgContent=null;

			MsgDialogUI.__super.call(this);
		}

		CLASS$(MsgDialogUI,'ui.MsgDialogUI',_super);
		var __proto__=MsgDialogUI.prototype;
		__proto__.createChildren=function(){
		    
			laya.ui.Component.prototype.createChildren.call(this);
			this.createView(MsgDialogUI.uiView);
		}

		STATICATTR$(MsgDialogUI,
		['uiView',function(){return this.uiView={"type":"Dialog","props":{"width":600,"name":"msgDialog","height":400},"child":[{"type":"Image","props":{"y":6,"x":4,"width":602,"skin":"comp/bg.png","sizeGrid":"12,0,0,0","height":394}},{"type":"Button","props":{"y":320,"x":445,"width":98,"var":"btnMConfirm","skin":"comp/button.png","name":"btnMConfirm","labelSize":20,"labelFont":"SimHei","label":"确定","height":41}},{"type":"Label","props":{"y":46,"x":62,"width":85,"text":"提示:","height":43,"fontSize":30,"font":"SimHei","color":"#ffffff"}},{"type":"Label","props":{"y":137,"x":81,"width":432,"var":"msgContent","name":"msgContent","height":166,"fontSize":20,"font":"SimHei"}}]};}
		]);
		return MsgDialogUI;
	})(Dialog);
var OperationPanelUI=(function(_super){
		function OperationPanelUI(){
			
		    this.playerText=null;
		    this.btnReady=null;
		    this.btnDice=null;
		    this.btnExit=null;

			OperationPanelUI.__super.call(this);
		}

		CLASS$(OperationPanelUI,'ui.OperationPanelUI',_super);
		var __proto__=OperationPanelUI.prototype;
		__proto__.createChildren=function(){
		    
			laya.ui.Component.prototype.createChildren.call(this);
			this.createView(OperationPanelUI.uiView);
		}

		STATICATTR$(OperationPanelUI,
		['uiView',function(){return this.uiView={"type":"View","props":{"x":0,"width":400,"height":700},"child":[{"type":"Image","props":{"y":55,"x":29,"width":342,"skin":"template/Warn/alert_dialog.png","height":234}},{"type":"TextArea","props":{"y":77,"x":56,"width":290,"var":"playerText","text":"TextArea","height":192,"fontSize":20,"editable":false}},{"type":"Button","props":{"y":600,"x":40,"width":100,"var":"btnReady","skin":"comp/button.png","name":"btnReady","labelSize":20,"labelFont":"SimHei","label":"准备","height":50}},{"type":"Button","props":{"y":600,"x":150,"width":100,"var":"btnDice","skin":"comp/button.png","name":"btnDice","labelSize":20,"labelFont":"SimHei","label":"掷骰子","height":50}},{"type":"Button","props":{"y":600,"x":260,"width":100,"var":"btnExit","skin":"comp/button.png","name":"btnExit","labelSize":20,"labelFont":"SimHei","label":"退出","height":50}}]};}
		]);
		return OperationPanelUI;
	})(View);
var QuestionDialogUI=(function(_super){
		function QuestionDialogUI(){
			
		    this.questionRadio=null;
		    this.questionText=null;
		    this.btnQConfirm=null;

			QuestionDialogUI.__super.call(this);
		}

		CLASS$(QuestionDialogUI,'ui.QuestionDialogUI',_super);
		var __proto__=QuestionDialogUI.prototype;
		__proto__.createChildren=function(){
		    
			laya.ui.Component.prototype.createChildren.call(this);
			this.createView(QuestionDialogUI.uiView);
		}

		STATICATTR$(QuestionDialogUI,
		['uiView',function(){return this.uiView={"type":"Dialog","props":{"width":600,"height":400},"child":[{"type":"Image","props":{"y":4,"x":-1,"width":602,"skin":"comp/bg.png","height":394}},{"type":"RadioGroup","props":{"y":166,"x":73,"width":320,"var":"questionRadio","space":20,"skin":"comp/radiogroup.png","selectedIndex":0,"labels":"选项A,label2,label2,label2","labelSize":20,"labelPadding":"0,0,0,10","labelFont":"SimHei","height":150,"direction":"vertical"}},{"type":"Label","props":{"y":77,"x":57,"width":498,"var":"questionText","text":"校园中的毛爷爷雕塑指向哪里？","height":40,"fontSize":20,"font":"SimHei","color":"#ffffff","align":"left"}},{"type":"Button","props":{"y":325,"x":441,"width":98,"var":"btnQConfirm","skin":"comp/button.png","name":"btnQConfirm","labelSize":20,"labelFont":"SimHei","label":"确定","height":41}},{"type":"Label","props":{"y":39,"x":58,"text":"问题:","fontSize":25,"font":"SimHei","color":"#ffffff"}}]};}
		]);
		return QuestionDialogUI;
	})(Dialog);
var QuestionTypeDialogUI=(function(_super){
		function QuestionTypeDialogUI(){
			
		    this.typeRadio=null;
		    this.btnTConfirm=null;

			QuestionTypeDialogUI.__super.call(this);
		}

		CLASS$(QuestionTypeDialogUI,'ui.QuestionTypeDialogUI',_super);
		var __proto__=QuestionTypeDialogUI.prototype;
		__proto__.createChildren=function(){
		    
			laya.ui.Component.prototype.createChildren.call(this);
			this.createView(QuestionTypeDialogUI.uiView);
		}

		STATICATTR$(QuestionTypeDialogUI,
		['uiView',function(){return this.uiView={"type":"Dialog","props":{"width":600,"height":400},"child":[{"type":"Image","props":{"y":4,"x":-1,"width":602,"skin":"comp/bg.png","height":394}},{"type":"RadioGroup","props":{"y":166,"x":73,"width":320,"var":"typeRadio","space":20,"skin":"comp/radiogroup.png","selectedIndex":0,"labels":"选项A,label2,label2,label2","labelSize":20,"labelPadding":"0,0,0,10","labelFont":"SimHei","height":150,"direction":"vertical"}},{"type":"Label","props":{"y":86,"x":58,"width":281,"text":"请选择你想要回答的问题类型：","height":27,"fontSize":20,"font":"SimHei","color":"#ffffff","align":"left"}},{"type":"Button","props":{"y":325,"x":441,"width":98,"var":"btnTConfirm","skin":"comp/button.png","name":"btnTConfirm","labelSize":20,"labelFont":"SimHei","label":"确定","height":41}},{"type":"Label","props":{"y":45,"x":59,"text":"问题类型:","fontSize":25,"font":"SimHei","color":"#ffffff"}}]};}
		]);
		return QuestionTypeDialogUI;
	})(Dialog);