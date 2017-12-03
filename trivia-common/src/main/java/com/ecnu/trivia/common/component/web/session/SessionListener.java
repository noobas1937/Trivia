/** Created by Jack Chen at 15-11-16 */
package com.ecnu.trivia.common.component.web.session;

import com.ecnu.trivia.common.component.listener.Listener;

/**
 * 会话监听器
 *
 * @author Jack Chen
 */
public interface SessionListener extends Listener {

    /**
     * 在会话删除时触发
     *
     * @param sessionId 当前会话id
     */
    void onDelete(String sessionId);
}
