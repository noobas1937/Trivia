/**
 * @Title: JSONString.java
 * @Package com.iresearch.core.json
 * @Description: TODO
 * Copyright: Copyright (c) 2016
 * Company:艾瑞咨询
 * @author Iresearch-billzhuang
 * @date 2016年3月22日 上午10:19:58
 * @version V1.0.0
 */
package com.ecnu.trivia.web.utils.json;

/**
 * @ClassName: JSONString
 * @Description: TODO
 * @author Iresearch-billzhuang
 * @date 2016年3月22日 上午10:19:58
 *
 */
public interface JSONString {
    /**
     * The <code>toJSONString</code> method allows a class to produce its own JSON
     * serialization.
     *
     * @return A strictly syntactically correct JSON text.
     */
    public String toJSONString();
}
