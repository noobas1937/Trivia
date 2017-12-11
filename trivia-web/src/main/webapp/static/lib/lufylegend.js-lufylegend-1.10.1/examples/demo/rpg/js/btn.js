/**
 * Created by Administrator on 2017/12/10 0010.
 */


   $(function() {
       var btn = document.getElementById('stand');
       btn.onclick = function () {

       };




       var score=0;
        var ok_btn=document.getElementById('ok');
        var chose_btn=document.getElementById('chose_button');
        var res=document.getElementById('');
           chose_btn.onclick=function (event){
               document.getElementById('light').style.display='none';
                //冒泡处理
               var id = event.target.id;
               var show=document.getElementById('neirong');
               if(id.indexOf('A1')){
                   score++;
                   show.innerHTML="you win"+"now your score is"+score;

               }
               else
               {
                   show.innerHTML="you are closed into prisin"+num;
                   self.prisin=false;

               }

           }
          /*  alert(chose_btn.getElementsByTagName(button).value())
      */



   }
   )


