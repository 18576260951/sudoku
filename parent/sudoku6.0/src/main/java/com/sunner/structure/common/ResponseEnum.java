package com.sunner.structure.common;

public enum ResponseEnum implements IResponseEnum {
    SUCCEED("200","请求处理成功"),
    EXCEPTION("998","程序产生异常"),
    FAILED("999","请求处理失败"),
    /************************各位开发人员注意:所有响应码在此条注释下书写*****************************/

    CONNECT_OVER_TIME("990","连接OTP超时!"),
    SMSCODE_NOT_RIGHT("991","验证码错误!"),
    SMSCODE_OVERDUE("992","验证码过期!"),
    VERIFY_SMS_FAILED("993","验证码校验出错,请重试!"),
    CELL_PHONE_LOCKED("895","手机号已被锁定,请30分钟后重试"),
    HTTP_GET_FAILED("216","发送http get请求失败"),
    HTTP_POST_FAILED("217","发送http post请求失败"),
    DECRYPT_BODY_FAIL("660","解密处理失败!"),
    ENCRYPT_BODY_FAIL("661","加密处理失败"),

    /************************各位开发人员注意:所有响应码不要超过此条注释线*****************************/

    REPETITIVE_OPERATION("990","请勿重复操作"),
    TOKEN_MISTAKE("899","token格式错误"),
    HYSTRIX_FALLBACK("991","内部调用服务繁忙,请稍后重试"),
    ADVICE_CATCH_EXCEPTION("992","请求异常,处理失败"),
    ILLEGALITY_FEIGN_REQUEST("993","非法feign内部请求"),
    WRONG_PARAM("994","请求参数错误,请联系管理员");


    //构造方法
    private String code;
    private String msg;

    private ResponseEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    //根据code返回msg信息
    public static String getMsgByCode(String code) {
        for (ResponseEnum returnEnum : ResponseEnum.values()) {
            if (returnEnum.getCode().equals(code)) {
                return returnEnum.getMsg();
            }
        }
        //当传入的code与枚举类要求的code不符时,抛异常(此方法可以对传入的参数进行校验)
        throw new IllegalArgumentException("Invalid Enum code:" + code);
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMsg() {
        return msg;
    }
}
