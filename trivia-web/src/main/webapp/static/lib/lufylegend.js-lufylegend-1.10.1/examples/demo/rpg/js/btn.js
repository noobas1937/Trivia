/**
 * Created by Administrator on 2017/12/10 0010.
 */


   $(function() {
       var btn = document.getElementById('stand');
       btn.onclick = function () {

       };
        var ok_btn=document.getElementById('ok');
        var chose_btn=document.getElementById('chose_button');
        var res=document.getElementById('res');
           chose_btn.onclick=function (event){
               document.getElementById('light').style.display='none';
              /* res.style.display='block';    //冒泡处理*/
               var id = event.target.id;


           }
          /*  alert(chose_btn.getElementsByTagName(button).value())
      */



   }
   )


