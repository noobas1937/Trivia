package com.ecnu.trivia.common.component.domain;

/**
 * Created by zhenjie tang at 2015/11/18
 *
 * @author zhenjie tang
 */
public enum TableType {

     standard(1,"标准表"),
     olap(2,"数据分析表"),
     statistics(3,"系统统计表");

     private int type;

     private String desc;

     TableType(int type, String desc) {
          this.type = type;
          this.desc = desc;
     }

     public int getType() {
          return type;
     }

     public String getDesc() {
          return desc;
     }
}
