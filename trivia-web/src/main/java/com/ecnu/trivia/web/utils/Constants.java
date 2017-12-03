/**
 * 常量命名规范：
 * 	1、统一声明为 public static final {type} {UPPERCASE_NAME}
 * 	2、同一类别常量请声明在一起，并以统一的标志性名词起始
 * 	3、新增加类别的常量请打注释
 */
package com.ecnu.trivia.web.utils;

public class Constants {
	/**管理员用户*/
	public static final int ADMIN_USER_ROLE = 0;
	/**普通用户*/
	public static final int COMMON_USER_ROLE = 1;


	/**管理员用户*/
	public static final String SUPER_USER = "superUser";
	/**在线用户*/
	public static final String ONLINE_USER = "onlineUser";

    /**设备状态描述*/
    public static final int DEVICE_ENABLE = 0;
    public static final int DEVICE_DISABLE = 1;
    public static final int DEVICE_DELETED = -1;

	/**设备地址工作状态*/
    public static final int ADDRESS_ENABLE = 1;
    public static final int ADDRESS_DISABLE = 0;

    /**任务执行状态*/
    public static final int TASK_DELETE = -1;
    public static final int TASK_RUNNING = 0;
    public static final int TASK_PAUSE = 1;
    public static final int TASK_ERROR = 2;


	/**数据状态描述*/
	public static final int DATA_UPLOADED = 1;
	public static final int DATA_UNUPLOADED = 0;

    /**Job变量声明*/
    public static final String TASK_NAME = "jobName";
    public static final String JOBS_NAME = "jobs";
	public static final String CACHE_NAME = "cache_container";

    /**寄存器类型声明*/
    public static final int COIL_REGISTER = 0;
    public static final int HOLDING_REGISTER = 1;

    /**Corn任务执行时间间隔选项*/
	public static final String CORN_HALF_MINIUTE = "*/30 * * * * ?";
	public static final String CORN_ONE_MINIUTE = "0 */1 * * * ?";
	public static final String CORN_THREE_MINIUTE = "0 */3 * * * ?";
	public static final String CORN_FIVE_MINIUTE = "0 */5 * * * ?";
	public static final String CORN_TEN_MINIUTE = "0 */10 * * * ?";


	/**函数调用参数*/
	public static final int SYSTEM_CALL = 0;
	public static final int EXTERNAL_CALL = 1;


	/**系统角色,用于打印系统log日志*/
	public static final int ROLE_SYSTEM = 0;
	public static final int ROLE_DEVICE = 1;
	public static final int ROLE_DATA = 2;
	public static final int ROLE_TASK = 3;
	public static final int ROLE_FUNCTION = 4;

	/**系统运行状态*/
	public static final int SYSTEM_OFFLINE = 0;
	public static final int SYSTEM_ONLINE = 1;

	/**
	 * jwt
	 */
	public static final String JWT_ID = "jwt";
	public static final String JWT_SECRET = "7786df7fc3a34e26a61c034d5ec8245d";
	public static final int JWT_TTL = 60*60*1000;
	public static final int JWT_REFRESH_INTERVAL = 55*60*1000;
	public static final int JWT_REFRESH_TTL = 12*60*60*1000;
}
