package org.jeecg.modules.system.event;

import org.springframework.context.ApplicationEvent;

/**
 * 用户注册事件
 * @Description: 用户注册完成后发布的事件
 * @Author: jeecg-boot
 * @Date: 2025-01-20
 * @Version: V1.0
 */
public class UserRegisteredEvent extends ApplicationEvent {
    
    private final String userId;
    private final String username;
    
    public UserRegisteredEvent(Object source, String userId, String username) {
        super(source);
        this.userId = userId;
        this.username = username;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public String getUsername() {
        return username;
    }
}