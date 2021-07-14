package com.sunner.structure.common;

/**
 * @author Sunner
 * @company www.eastsun.com
 * @date 2020/10/30
 * @time 0:18
 *
 * @className IResponseEnum
 * @classDescription 响应枚举类(接口)
 *
 */
public interface IResponseEnum {
    /**
     * 返回枚举项的 code
     * @return
     */
    String getCode();

    /**
     * 返回枚举项的 msg
     * @return
     */
    String getMsg();
}
