function LTileMap(data,img,width,height){
    var s = this;
    base(s,LSprite,[]);
    s.x = 0;
    s.y = 0;
    s.mapData = data;
    s.imgData = img;
    if(!width){
        var wbitmap = new LBitmapData(s.imgData);
        s.partWidth = wbitmap.image.width;
    }else{
        s.partWidth = width;
    }
    if(!height){
        var hbitmap = new LBitmapData(s.imgData);
        s.partHeight = hbitmap.image.height;
    }else{
        s.partHeight = height;
    }
    s.onshow();
}
LTileMap.prototype.onshow = function(){
    var s = this;
    var mapdata = s.mapData;
    var partWidth = s.partWidth;
    var partHeight = s.partHeight;

    var i,j;
    var index,indexY,indexX;
    var bitmapdata,bitmap;

    for(i=0;i<mapdata.length;i++){
        for(j=0;j<mapdata[0].length;j++){
            index = mapdata[i][j];
            indexY = Math.floor(index/mapdata.length);
            indexX = index - indexY*mapdata.length;

            bitmapdata = new LBitmapData(s.imgData,indexX*partWidth,indexY*partHeight,partWidth,partHeight);
            bitmap = new LBitmap(bitmapdata);
            bitmap.x = j*partWidth + s.x;
            bitmap.y = i*partHeight + s.y;

            s.addChild(bitmap);
            }
    }
}