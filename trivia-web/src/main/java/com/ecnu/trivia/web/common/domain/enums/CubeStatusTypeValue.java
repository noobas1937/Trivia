/** created by liang.liang at 2016-3-21 17:50 */
package com.ecnu.trivia.web.common.domain.enums;

/**
 * cube状态枚举
 *
 * @author liang.liang
 */
public enum CubeStatusTypeValue {

    disable(0, "已停用"),
    normal(1, "正常状态");

    CubeStatusTypeValue(int status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    private int status;
    private String desc;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public static CubeStatusTypeValue valueOfStatus(int status) {
        for(CubeStatusTypeValue cubeStatusTypeValue : values()) {
            if(cubeStatusTypeValue.getStatus() == status) {
                return cubeStatusTypeValue;
            }
        }

        throw new IllegalArgumentException("不支持的参数类型:" + status);
    }
}
