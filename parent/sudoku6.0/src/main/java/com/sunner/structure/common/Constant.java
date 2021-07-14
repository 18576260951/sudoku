package com.sunner.structure.common;

/**
 * @author Sunner
 * @company www.eastsun.com
 * @date 2020/11/3
 * @time 11:06
 *
 * @className Constant
 * @classDescription 常量类
 *
 */
public class Constant {
    /**
     * "常量类Constant"与"实现IResponseEnum接口的对应模块的枚举类"的区别:常量类Constant应具有《通用性》
     * IResponseEnum的另一个作用:作为方法的形参,接收任何实现IResponseEnum接口的枚举类
     */
    //响应码-成功
    public static final String RESPONSE_SUCCESS_CODE="200";
    public static final String RESPONSE_SUCCESS_MSG="请求处理成功";
    //响应码-失败
    public static final String RESPONSE_FAIL_CODE="999";
    public static final String RESPONSE_FAIL_MSG="请求处理失败";

    //时间格式
    public static final String YYYYMMDD="yyyy-MM-dd";
    public static final String YYYYMMDDHHMMSS="yyyy-MM-dd HH:mm:ss";

    //编/解码
    public static final String UTF8="UTF-8";
    public static final String SHA256="SHA-256";

    //0-否   1-是
    public static final String NO="0";
    public static final String YES="1";

    //0-无效  1-有效
    public static final String INEFFECTIVE="0";
    public static final String EFFECTIVE="1";


    public enum ConstantEnum{

        //应用类型判断
        H5("0","H5"),
        APP("1","APP");

        private String code;
        private String msg;

        //构造方法
        private ConstantEnum(String code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        //根据code返回msg信息
        public static String getMsgByCode(String code) {
            for (ConstantEnum returnEnum : ConstantEnum.values()) {
                if (returnEnum.getCode().equals(code)) {
                    return returnEnum.getMsg();
                }
            }
            //当传入的code与枚举类要求的code不符时,抛异常(此方法可以对传入的参数进行校验)
            throw new IllegalArgumentException("Invalid Enum code:" + code);
        }

        public String getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }
    }
}
