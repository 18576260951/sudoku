package com.sunner.structure.exception;

import com.sunner.structure.common.IResponseEnum;
import com.sunner.structure.common.ResponseEnum;

public class SunnerRuntimeException extends RuntimeException {
    private static final long serialVersionUID = -7618538867846754645L;

    private String errCode;
    private Object data;

    public SunnerRuntimeException(String errCode, String errMessage) {
        super(errMessage);
        this.errCode = errCode;
    }

    public SunnerRuntimeException(String errCode, String errMessage, Object data) {
        super(errMessage);
        this.errCode = errCode;
        this.data = data;
    }

    public SunnerRuntimeException(String errCode, String errMessage, Throwable ex) {
        super(errMessage,ex);
        this.errCode = errCode;
    }

    public SunnerRuntimeException(IResponseEnum constant) {
        super(constant.getMsg());
        this.errCode = constant.getCode();
    }

    public SunnerRuntimeException(IResponseEnum constant,Object data) {
        super(constant.getMsg());
        this.errCode = constant.getCode();
        this.data=data;
    }

    public SunnerRuntimeException(String message) {
        super(message);
        this.errCode = ResponseEnum.FAILED.getCode();
    }

    public String getErrCode() {
        return errCode;
    }

    public Object getData() {
        return data;
    }
}
