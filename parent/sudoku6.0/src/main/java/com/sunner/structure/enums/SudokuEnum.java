package com.sunner.structure.enums;

import com.sunner.structure.common.IResponseEnum;
import com.sunner.structure.common.ResponseEnum;

public enum SudokuEnum implements IResponseEnum {
    PARAM_EMPTY("301","请求参数不能为空"),
    PARAM_ILLEGAL("302","请求参数非法"),
    SUDOKU_EXISTED("303","数独已经存在"),
    SUDOKU_NO_ILLEGAL("304","数独编号非法"),
    SUDOKU_NO_INVALID("305","数独编号无效"),
    SUDOKU_PARAMS_INVALID("306","提供的数独参数不能解出一个正确的数独"),
    SUDOKU_SAVE_FAIL("307","保存数独失败"),



    CRACK_FAIL("399","破解数独失败");

    private String code;
    private String msg;

    private SudokuEnum(String code,String msg){
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
