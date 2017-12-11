/**
 * 常量命名规范：
 * 	1、统一声明为 public static final {type} {UPPERCASE_NAME}
 * 	2、同一类别常量请声明在一起，并以统一的标志性名词起始
 * 	3、新增加类别的常量请打注释
 */
package com.ecnu.trivia.web.utils;

public class Constants {


	/**管理员用户*/
	public static final String SUPER_USER = "superUser";
	/**在线用户*/
	public static final String ONLINE_USER = "onlineUser";

	/**房间状态定义*/
	public static final int ROOM_WAITING = 0;
	public static final int ROOM_PLAYING = 1;

	/**玩家状态定义*/
	public static final int PLAYER_WAITING = 0;
	public static final int PLAYER_READY = 1;

	/**账户状态定义*/
	public static final int ACCOUNT_NORMAL = 0;
	public static final int ACCOUNT_ADMIN = 1;
	public static final int ACCOUNT_DELETE = -1;

	/**
	 * jwt
	 */
	public static final String JWT_ID = "jwt";
	public static final String JWT_SECRET = "7786df7fc3a34e26a61c034d5ec8245d";
	public static final int JWT_TTL = 60*60*1000;
	public static final int JWT_REFRESH_INTERVAL = 55*60*1000;
	public static final int JWT_REFRESH_TTL = 12*60*60*1000;
}
