package com.ecnu.trivia.web.utils;

/**
 * 信息常量
 *
 * @author irs-user
 * @version 2017年6月9日
 * @see ConstantsMsg
 */
public class ConstantsMsg {

    /**文件上传失败*/
    public static final String ERROR_UPLOAD_FAILED = "文件上传失败";
    public static final String ERROR_UPLOAD_BECAUSE_OLAP = "文件上传失败，数据保存到OLAP库出现异常";
    public static final String ERROR_UPLOAD_BECAUSE_DATA_LENGTH = "文件上传失败，数据长度不符合规范";

    /**任务相关*/
    public static final String TASK_NAME_ALREADY_EXISTS = "任务名称已存在";
    public static final String TASK_DONOT_EXISTS = "任务不存在";
    public static final String STATUS_NOT_RIGHT = "任务状态不允许该操作，请检查任务状态";
    public static final String FUNCTION_DISABLED_OR_DONOT_EXISTS = "任务已禁用或不存在！";

    /**设备相关*/
    public static final String DEVICE_DONOT_EXISTS = "设备不存在";
    public static final String DEVICE_ALREADY_INUSED = "设备被占用";

    /**未知*/
    public static final String UNKNOWN = "未知";

    /**logger*/
    public static final String DEVICE_PORT_ERROR = "设备端口被占用，请检查设备状态及端口设置！";
    public static final String TRACE_DATA_ERROR = "请求设备数据出错。";

    /**参数相关*/
    public static final String PARAM_FREQUENCY_ERROR = "任务频率参数不合法！";

    /**Job Error Msg*/
    public static final String READ_DATA_TIMEOUT = "请求设备数据超时！";
    public static final String READ_DATA_ERROR = "设备返回值出错！";

    /**数据上传相关*/
    public static  final String SYSTEM_NOT_VERIFIED = "本地系统未验证，无法上传，请检查license！";
    public static final String REMOTE_URI_ERROR = "服务器地址未配置，请检查服务器路径！";
    public static final String ROOM_STATE_ERROR = "房间状态出错！";
    public static final String ROOM_GAME_OVER = "房间：{} 游戏结束！";
    public static final String QUESTION_CHOOSE_ERROR = "为玩家：{} 随机选择题目类型：{}出错！";
}
