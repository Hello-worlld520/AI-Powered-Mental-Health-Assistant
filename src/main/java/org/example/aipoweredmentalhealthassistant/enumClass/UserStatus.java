package org.example.aipoweredmentalhealthassistant.enumClass;

import lombok.Getter;

@Getter
public enum UserStatus {
    DISABLED(0, "禁用"),
    NORMAL(1, "正常");

    private final Integer code;
    private final String displayName;

    UserStatus(Integer code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }//枚举类的构造方法

    public static UserStatus fromCode(Integer code) {
        for (UserStatus userStatus : values()) {
            if (userStatus.code.equals(code)) {
                return userStatus;
            }
        }
        return null;
    }//从状态码获取状态信息
}
