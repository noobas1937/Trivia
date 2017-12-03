package com.ecnu.trivia.web.utils.encoding;

import java.util.regex.Pattern;

/**
 * Created by Jack Chen on 15/8/2017.
 */
public class DeviceEncodeUtil
{
  private static Pattern NUMBER_PATTERN = Pattern.compile("[0-9]*");
  public static String formatMeid(String meid) {
    int dxml = meid.length();
    if (dxml != 14 && dxml != 16) {
      return meid;
    }
    String meidRes = "";
    if (dxml == 14) {
      meidRes =  meid + getmeid15(meid);
    }
    if (dxml == 16) {
      meidRes = meid.substring(2) + getmeid15(meid.substring(2));
    }
    return meidRes;
  }
  public static String formatImei(String imei) {
    boolean is_true = NUMBER_PATTERN.matcher(imei).matches();
    if(is_true == false) {
        return "1";
    }
    int dxml = imei.length();
    if (dxml != 14 && dxml != 16) {
      return imei;
    }
    String imeiRes = "";
    if (dxml == 14) {
      imeiRes =  imei + getimei15(imei);
    }
    if (dxml == 16) {
      imeiRes =  imei.substring(0,14) + getimei15(imei.substring(0,14));
    }
    return imeiRes;
  }
  private static String getmeid15(String meid) {
    if (meid.length() == 14) {
      String[] myStr = {"a", "b", "c", "d", "e", "f"};
      int sum = 0;
      for (int i = 0; i < meid.length(); i++) {
        String param = meid.substring(i, i + 1);
        for (int j = 0; j < myStr.length; j++) {
          if (param.equalsIgnoreCase(myStr[j])) {
            param = "1" + String.valueOf(j);
          }
        }
        if (i % 2 == 0) {
          sum = sum + Integer.parseInt(param);
        } else {
          sum = sum + 2 * Integer.parseInt(param) % 16;
          sum = sum + 2 * Integer.parseInt(param) / 16;
        }
      }
      if (sum % 16 == 0) {
        return "0";
      } else {
        int result = 16 - sum % 16;
        if (result > 9) {
          result += 65 - 10;
        }
        return result + "";
      }
    } else {
      return "";
    }
  }
  private static String getimei15(String imei){
    if (imei.length() == 14) {
      char[] imeiChar=imei.toCharArray();
      int resultInt=0;
      for (int i = 0; i < imeiChar.length; i++) {
        int a=Integer.parseInt(String.valueOf(imeiChar[i]));
        i++;
        final int temp=Integer.parseInt(String.valueOf(imeiChar[i]))*2;
        final int b=temp<10?temp:temp-9;
        resultInt+=a+b;
      }
      resultInt%=10;
      resultInt=resultInt==0?0:10-resultInt;
      return resultInt + "";
    }else{
      return "";
    }
  }

}
