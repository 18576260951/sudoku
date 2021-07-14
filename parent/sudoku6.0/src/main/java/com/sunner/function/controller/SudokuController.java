package com.sunner.function.controller;

import com.sunner.function.service.SudokuService;
import com.sunner.structure.common.Constant;
import com.sunner.structure.common.Result;
import com.sunner.function.pojo.Sudoku;
import com.sunner.structure.enums.SudokuEnum;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.lang.reflect.Field;
import java.util.HashMap;

@Controller
@RequestMapping("/sudoku")
@SuppressWarnings("all")
public class SudokuController {

    private static final Logger log = Logger.getLogger(SudokuController.class);
    @Autowired
    private SudokuService sudokuService;


    /**
     * 返回"原始数独"(如果传入编号,返回对应的数独;如果不传编号,随机返回数独)
     * @param sudoku
     * @return
     */
    @ResponseBody
    @RequestMapping("/readSudoku")
    public Result readSudoku(@RequestBody Sudoku sudoku) throws Exception {
        Result paramVerifyResult = sudokuParamVerifyOnlySudokuNo(sudoku);
        if (!paramVerifyResult.getCode().equals(Constant.RESPONSE_SUCCESS_CODE)) {
            return paramVerifyResult;
        }
        return sudokuService.readSudoku(sudoku);
    }

    /**
     * 破解数独
     * @param sudoku
     * @return
     * @throws IllegalAccessException
     */
    @ResponseBody
    @RequestMapping("/crackSudoku")
    public Result crackSudoku(@RequestBody HashMap requestParam) throws Exception {
        Sudoku sudoku = requestParam2Sudoku(requestParam);
        Result paramVerifyResult = sudokuParamVerify(sudoku);
        if (!paramVerifyResult.getCode().equals(Constant.RESPONSE_SUCCESS_CODE)) {
            return paramVerifyResult;
        }
        return sudokuService.crackSudoku(sudoku);
    }

    /**
     * 校验请求参数
     * @param sudoku
     * @return
     * @throws IllegalAccessException
     */
    public Result sudokuParamVerify(Sudoku sudoku) throws IllegalAccessException {
        boolean sudokuParamLegal=true;
        Field[] fields = Sudoku.class.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            String fieldName = field.getName();
            String stringField = String.valueOf(field.get(sudoku));
            //参数校验
            if ("null".equals(stringField)) {
                continue;
            }
            if("sudokuNo".equals(fieldName)){
                if(stringField.matches("\\d+")){
                    continue;
                }
            }
            if(!"sudokuNo".equals(fieldName)){
                if (stringField.matches("\\d")) {
                    continue;
                }
                if(stringField.matches("\\s*")){
                    field.set(sudoku,null);
                    continue;
                }
            }
            sudokuParamLegal=false;
        }
        if (sudokuParamLegal==true){
            return Result.success();
        }else{
            return Result.fail(SudokuEnum.PARAM_ILLEGAL);
        }
    }

    /**
     * 校验数独编号
     * @param sudoku
     * @return
     * @throws IllegalAccessException
     */
    public Result sudokuParamVerifyOnlySudokuNo(Sudoku sudoku) throws IllegalAccessException, NoSuchFieldException {
        Field field = Sudoku.class.getDeclaredField("sudokuNo");
        field.setAccessible(true);
        String stringField = String.valueOf(field.get(sudoku));
        //参数校验
        if (!"null".equals(stringField)) {
            if(!stringField.matches("\\d+")){
                return Result.fail(SudokuEnum.SUDOKU_NO_ILLEGAL);
            }
        }
        return Result.success();
    }

    public Sudoku requestParam2Sudoku(HashMap requestParam){
        Sudoku sudoku = new Sudoku();
        HashMap<String,String> a = (HashMap<String, String>) requestParam.get("a");
        HashMap<String,String> b = (HashMap<String, String>) requestParam.get("b");
        HashMap<String,String> c = (HashMap<String, String>) requestParam.get("c");
        HashMap<String,String> d = (HashMap<String, String>) requestParam.get("d");
        HashMap<String,String> e = (HashMap<String, String>) requestParam.get("e");
        HashMap<String,String> f = (HashMap<String, String>) requestParam.get("f");
        HashMap<String,String> g = (HashMap<String, String>) requestParam.get("g");
        HashMap<String,String> h = (HashMap<String, String>) requestParam.get("h");
        HashMap<String,String> i = (HashMap<String, String>) requestParam.get("i");
        if(a!=null){
            sudoku.setA1(a.get("a1"));
            sudoku.setA2(a.get("a2"));
            sudoku.setA3(a.get("a3"));
            sudoku.setA4(a.get("a4"));
            sudoku.setA5(a.get("a5"));
            sudoku.setA6(a.get("a6"));
            sudoku.setA7(a.get("a7"));
            sudoku.setA8(a.get("a8"));
            sudoku.setA9(a.get("a9"));
        }
        if(b!=null){
            sudoku.setB1(b.get("b1"));
            sudoku.setB2(b.get("b2"));
            sudoku.setB3(b.get("b3"));
            sudoku.setB4(b.get("b4"));
            sudoku.setB5(b.get("b5"));
            sudoku.setB6(b.get("b6"));
            sudoku.setB7(b.get("b7"));
            sudoku.setB8(b.get("b8"));
            sudoku.setB9(b.get("b9"));
        }
        if(c!=null){
            sudoku.setC1(c.get("c1"));
            sudoku.setC2(c.get("c2"));
            sudoku.setC3(c.get("c3"));
            sudoku.setC4(c.get("c4"));
            sudoku.setC5(c.get("c5"));
            sudoku.setC6(c.get("c6"));
            sudoku.setC7(c.get("c7"));
            sudoku.setC8(c.get("c8"));
            sudoku.setC9(c.get("c9"));
        }
        if(d!=null){
            sudoku.setD1(d.get("d1"));
            sudoku.setD2(d.get("d2"));
            sudoku.setD3(d.get("d3"));
            sudoku.setD4(d.get("d4"));
            sudoku.setD5(d.get("d5"));
            sudoku.setD6(d.get("d6"));
            sudoku.setD7(d.get("d7"));
            sudoku.setD8(d.get("d8"));
            sudoku.setD9(d.get("d9"));
        }
        if(e!=null){
            sudoku.setE1(e.get("e1"));
            sudoku.setE2(e.get("e2"));
            sudoku.setE3(e.get("e3"));
            sudoku.setE4(e.get("e4"));
            sudoku.setE5(e.get("e5"));
            sudoku.setE6(e.get("e6"));
            sudoku.setE7(e.get("e7"));
            sudoku.setE8(e.get("e8"));
            sudoku.setE9(e.get("e9"));
        }
        if(f!=null){
            sudoku.setF1(f.get("f1"));
            sudoku.setF2(f.get("f2"));
            sudoku.setF3(f.get("f3"));
            sudoku.setF4(f.get("f4"));
            sudoku.setF5(f.get("f5"));
            sudoku.setF6(f.get("f6"));
            sudoku.setF7(f.get("f7"));
            sudoku.setF8(f.get("f8"));
            sudoku.setF9(f.get("f9"));
        }
        if(g!=null){
            sudoku.setG1(g.get("g1"));
            sudoku.setG2(g.get("g2"));
            sudoku.setG3(g.get("g3"));
            sudoku.setG4(g.get("g4"));
            sudoku.setG5(g.get("g5"));
            sudoku.setG6(g.get("g6"));
            sudoku.setG7(g.get("g7"));
            sudoku.setG8(g.get("g8"));
            sudoku.setG9(g.get("g9"));
        }
        if(h!=null){
            sudoku.setH1(h.get("h1"));
            sudoku.setH2(h.get("h2"));
            sudoku.setH3(h.get("h3"));
            sudoku.setH4(h.get("h4"));
            sudoku.setH5(h.get("h5"));
            sudoku.setH6(h.get("h6"));
            sudoku.setH7(h.get("h7"));
            sudoku.setH8(h.get("h8"));
            sudoku.setH9(h.get("h9"));
        }
        if(i!=null){
            sudoku.setI1(i.get("i1"));
            sudoku.setI2(i.get("i2"));
            sudoku.setI3(i.get("i3"));
            sudoku.setI4(i.get("i4"));
            sudoku.setI5(i.get("i5"));
            sudoku.setI6(i.get("i6"));
            sudoku.setI7(i.get("i7"));
            sudoku.setI8(i.get("i8"));
            sudoku.setI9(i.get("i9"));
        }
        return sudoku;
    }
}
