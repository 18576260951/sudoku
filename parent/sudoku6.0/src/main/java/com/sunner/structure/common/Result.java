package com.sunner.structure.common;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author Sunner
 * @company www.eastsun.com
 * @date 2020/11/3
 * @time 14:39
 * @className Result
 * @classDescription 返回响应实体, 总会返回(响应码code, 响应信息msg, 响应实体data ( 可以为null))
 */
public class Result implements Serializable {
    private String code;
    private String msg;
    private Object data;

    public Result() {
    }

    public Result(String code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    /**
     * 响应中立内容(new一个Result类,然后给其属性赋值,最后返回)
     */
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public void setIResponseEnum(IResponseEnum iResponseEnum) {
        this.code = iResponseEnum.getCode();
        this.msg = iResponseEnum.getMsg();
    }

    /**
     * 响应成功内容(响应码和响应信息固定)
     */
    public static Result success() {
        return new Result(Constant.RESPONSE_SUCCESS_CODE, Constant.RESPONSE_SUCCESS_MSG, null);
    }

    public static Result success(Object data) {
        return new Result(Constant.RESPONSE_SUCCESS_CODE, Constant.RESPONSE_SUCCESS_MSG, data);
    }

    /**
     * 响应失败内容
     */
    public static Result fail() {
        return new Result(Constant.RESPONSE_FAIL_CODE, Constant.RESPONSE_FAIL_MSG, null);
    }

    //自定义输入"响应码,响应信息"
    public static Result fail(String msg) {
        return new Result(Constant.RESPONSE_FAIL_CODE, msg, null);
    }

    public static Result fail(String code, String msg) {
        return new Result(code, msg, null);
    }

    public static Result fail(String code, String msg, Object data) {
        return new Result(code, msg, data);
    }

    //从枚举类中取"响应码,响应信息",以IResponseEnum作为方法的形参,接收任何实现IResponseEnum接口的枚举类
    public static Result fail(IResponseEnum iResponseEnum) {
        return new Result(iResponseEnum.getCode(), iResponseEnum.getMsg(), null);
    }

    public static Result fail(IResponseEnum iResponseEnum, Object data) {
        return new Result(iResponseEnum.getCode(), iResponseEnum.getMsg(), data);
    }

    @Override
    public String toString() {
        return "Result{" +
                "code='" + code + '\'' +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Result result = (Result) o;
        return Objects.equals(code, result.code) &&
                Objects.equals(msg, result.msg) &&
                Objects.equals(data, result.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, msg, data);
    }
}
