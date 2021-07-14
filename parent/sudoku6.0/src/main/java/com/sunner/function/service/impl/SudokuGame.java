//package com.sunner.function.service.impl;
//
//
//import com.sunner.structure.common.Result;
//import com.sunner.structure.enums.SudokuEnum;
//import org.apache.log4j.Logger;
//
//
//import javax.servlet.ServletException;
//import javax.servlet.annotation.WebServlet;
//import javax.servlet.http.HttpServlet;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.util.*;
//
///**
// * @author SunnerChen
// * @date 2019/12/15
// */
//@SuppressWarnings("all")
//@WebServlet(urlPatterns = "/sudoku")
//public class SudokuGame extends HttpServlet{
//
//    private static final Logger log=Logger.getLogger(SudokuGame.class);
//    public int change=0;   //用于判断数独是否有变化,作用于fillNumber
//    public int oneChange=0;//用于判断数独是否有变化,作用于removeSingle
//    public int twoChange=0;//用于判断数独是否有变化,作用于removeDouble
//    public static Map<String,Set<String>> possibleMap;  //possibleMap,作用于全局
//
//    @Override
//    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        Result result = new Result();
//        Map sodukuInfo=new HashMap();
//        Map<String, Integer> originalSudokuParams = setSudokuParams(req);
//        possibleMap=getPossibleMap(originalSudokuParams);
//        PlaySudokuGameAgo(originalSudokuParams);
//        Map<String, Integer> susudokuMap = PlaySudokuGame(originalSudokuParams);
//        if(susudokuMap==null){
//            result.setCode(SudokuEnum.PARAM_EMPTY.getCode());
//            result.setMsg(SudokuEnum.PARAM_EMPTY.getMsg());
//        }else{
//            for(int i=1;i<=9;i++){
//                List sudokuList=new ArrayList();
//                for(int j=1;j<=9;j++){
//                    String no=""+(char)(64+i)+j;
//                    Integer integer = susudokuMap.get(no);
//                    if(integer==null){
//                        //只要有一个数独值是null,就代表破解失败
//                        result.setCode(SudokuEnum.PARAM_EMPTY.getCode());
//                        result.setMsg(SudokuEnum.PARAM_EMPTY.getMsg());
//                        sudokuList.add("null");
//                    }else{
//                        sudokuList.add(String.valueOf(integer));
//                    }
//                }
//                sodukuInfo.put(""+(char)(64+i),sudokuList);
//            }
//            if(!(SudokuEnum.PARAM_EMPTY.getCode().equals(result.getCode())||null==susudokuMap)){
//                result.setCode(SudokuEnum.PARAM_EMPTY.getCode());
//                result.setMsg(SudokuEnum.PARAM_EMPTY.getMsg());
//            }
//        }
//    }
//
//    @Override
//    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        this.doGet(req,resp);
//    }
//
//    /**
//     * 往数独的空格中填值
//     * @return
//     */
//    public static Map<String,Integer> setSudokuParams(HttpServletRequest request){
//        Map<String,Integer> s=new HashMap<>();
//        for(int i=1;i<=9;i++){
//            for(int j=1;j<=9;j++){
//                String no=""+(char)(64+i)+j;
//                String parameter = request.getParameter(no);
//                if ("null".equals(parameter)||"".equals(parameter)||null==parameter){
//                    //将null塞给对应的数独位
//                    s.put(no,null);
//                }else{
//                    //将玩家填入的数据值塞给对应的数独位
//                    s.put(no,Integer.valueOf(parameter));
//                }
//            }
//        }
//        Integer X=null;
//        //空白数独
//        /*s.put("A1",X);s.put("A2",X);s.put("A3",X);s.put("A4",X);s.put("A5",X);s.put("A6",X);s.put("A7",X);s.put("A8",X);s.put("A9",X);
//        s.put("B1",X);s.put("B2",X);s.put("B3",X);s.put("B4",X);s.put("B5",X);s.put("B6",X);s.put("B7",X);s.put("B8",X);s.put("B9",X);
//        s.put("C1",X);s.put("C2",X);s.put("C3",X);s.put("C4",X);s.put("C5",X);s.put("C6",X);s.put("C7",X);s.put("C8",X);s.put("C9",X);
//        s.put("D1",X);s.put("D2",X);s.put("D3",X);s.put("D4",X);s.put("D5",X);s.put("D6",X);s.put("D7",X);s.put("D8",X);s.put("D9",X);
//        s.put("E1",X);s.put("E2",X);s.put("E3",X);s.put("E4",X);s.put("E5",X);s.put("E6",X);s.put("E7",X);s.put("E8",X);s.put("E9",X);
//        s.put("F1",X);s.put("F2",X);s.put("F3",X);s.put("F4",X);s.put("F5",X);s.put("F6",X);s.put("F7",X);s.put("F8",X);s.put("F9",X);
//        s.put("G1",X);s.put("G2",X);s.put("G3",X);s.put("G4",X);s.put("G5",X);s.put("G6",X);s.put("G7",X);s.put("G8",X);s.put("G9",X);
//        s.put("H1",X);s.put("H2",X);s.put("H3",X);s.put("H4",X);s.put("H5",X);s.put("H6",X);s.put("H7",X);s.put("H8",X);s.put("H9",X);
//        s.put("I1",X);s.put("I2",X);s.put("I3",X);s.put("I4",X);s.put("I5",X);s.put("I6",X);s.put("I7",X);s.put("I8",X);s.put("I9",X);*/
//        //卡片一
//        /*s.put("A1",X);s.put("A2",X);s.put("A3",X);s.put("A4",X);s.put("A5",3);s.put("A6",X);s.put("A7",X);s.put("A8",X);s.put("A9",8);
//        s.put("B1",6);s.put("B2",X);s.put("B3",4);s.put("B4",X);s.put("B5",2);s.put("B6",X);s.put("B7",X);s.put("B8",X);s.put("B9",X);
//        s.put("C1",1);s.put("C2",X);s.put("C3",3);s.put("C4",4);s.put("C5",5);s.put("C6",X);s.put("C7",X);s.put("C8",X);s.put("C9",X);
//        s.put("D1",7);s.put("D2",X);s.put("D3",X);s.put("D4",X);s.put("D5",X);s.put("D6",X);s.put("D7",X);s.put("D8",X);s.put("D9",9);
//        s.put("E1",X);s.put("E2",X);s.put("E3",9);s.put("E4",X);s.put("E5",X);s.put("E6",1);s.put("E7",X);s.put("E8",X);s.put("E9",X);
//        s.put("F1",4);s.put("F2",X);s.put("F3",X);s.put("F4",X);s.put("F5",6);s.put("F6",X);s.put("F7",8);s.put("F8",1);s.put("F9",X);
//        s.put("G1",X);s.put("G2",X);s.put("G3",X);s.put("G4",2);s.put("G5",X);s.put("G6",X);s.put("G7",6);s.put("G8",X);s.put("G9",X);
//        s.put("H1",X);s.put("H2",X);s.put("H3",X);s.put("H4",X);s.put("H5",X);s.put("H6",5);s.put("H7",4);s.put("H8",X);s.put("H9",2);
//        s.put("I1",X);s.put("I2",X);s.put("I3",X);s.put("I4",6);s.put("I5",7);s.put("I6",X);s.put("I7",3);s.put("I8",9);s.put("I9",X);
//        {"I4":6,"I5":7,"E3":9,"I7":3,"I8":9,"E6":1,"A5":3,"A9":8,"F1":4,"B1":6,"F5":6,"B3":4,"F7":8,"F8":1,"B5":2,"G4":2,"C1":1,"C3":3,"G7":6,"C4":4,"C5":5,"D1":7,"H6":5,"H7":4,"H9":2,"D9":9}*/
//        //卡片二
//        /*s.put("A1",X);s.put("A2",X);s.put("A3",X);s.put("A4",X);s.put("A5",7);s.put("A6",X);s.put("A7",X);s.put("A8",4);s.put("A9",5);
//        s.put("B1",4);s.put("B2",7);s.put("B3",X);s.put("B4",X);s.put("B5",X);s.put("B6",X);s.put("B7",8);s.put("B8",X);s.put("B9",X);
//        s.put("C1",8);s.put("C2",5);s.put("C3",1);s.put("C4",X);s.put("C5",X);s.put("C6",X);s.put("C7",X);s.put("C8",X);s.put("C9",X);
//        s.put("D1",X);s.put("D2",8);s.put("D3",5);s.put("D4",X);s.put("D5",X);s.put("D6",2);s.put("D7",X);s.put("D8",X);s.put("D9",X);
//        s.put("E1",X);s.put("E2",X);s.put("E3",X);s.put("E4",5);s.put("E5",9);s.put("E6",X);s.put("E7",X);s.put("E8",6);s.put("E9",X);
//        s.put("F1",2);s.put("F2",1);s.put("F3",X);s.put("F4",X);s.put("F5",X);s.put("F6",4);s.put("F7",X);s.put("F8",X);s.put("F9",X);
//        s.put("G1",5);s.put("G2",X);s.put("G3",X);s.put("G4",4);s.put("G5",X);s.put("G6",X);s.put("G7",X);s.put("G8",X);s.put("G9",3);
//        s.put("H1",X);s.put("H2",X);s.put("H3",2);s.put("H4",X);s.put("H5",X);s.put("H6",X);s.put("H7",X);s.put("H8",X);s.put("H9",7);
//        s.put("I1",X);s.put("I2",X);s.put("I3",X);s.put("I4",6);s.put("I5",X);s.put("I6",X);s.put("I7",X);s.put("I8",5);s.put("I9",4);
//        {"I4":6,"E4":5,"I8":5,"E5":9,"I9":4,"E8":6,"A5":7,"A8":4,"A9":5,"F1":2,"F2":1,"B1":4,"B2":7,"F6":4,"B7":8,"G1":5,"G4":4,"C1":8,"C2":5,"C3":1,"G9":3,"H3":2,"D2":8,"D3":5,"H9":7,"D6":2}*/
//        //卡片三(未破解)
//        /*s.put("A1",X);s.put("A2",X);s.put("A3",X);s.put("A4",X);s.put("A5",9);s.put("A6",X);s.put("A7",X);s.put("A8",X);s.put("A9",7);
//        s.put("B1",X);s.put("B2",X);s.put("B3",1);s.put("B4",X);s.put("B5",X);s.put("B6",X);s.put("B7",9);s.put("B8",X);s.put("B9",X);
//        s.put("C1",6);s.put("C2",X);s.put("C3",X);s.put("C4",8);s.put("C5",X);s.put("C6",1);s.put("C7",X);s.put("C8",X);s.put("C9",4);
//        s.put("D1",3);s.put("D2",X);s.put("D3",X);s.put("D4",X);s.put("D5",8);s.put("D6",X);s.put("D7",X);s.put("D8",X);s.put("D9",X);
//        s.put("E1",2);s.put("E2",X);s.put("E3",X);s.put("E4",X);s.put("E5",X);s.put("E6",X);s.put("E7",4);s.put("E8",X);s.put("E9",6);
//        s.put("F1",5);s.put("F2",4);s.put("F3",X);s.put("F4",X);s.put("F5",X);s.put("F6",X);s.put("F7",3);s.put("F8",X);s.put("F9",1);
//        s.put("G1",7);s.put("G2",6);s.put("G3",X);s.put("G4",3);s.put("G5",X);s.put("G6",9);s.put("G7",X);s.put("G8",X);s.put("G9",X);
//        s.put("H1",X);s.put("H2",X);s.put("H3",5);s.put("H4",4);s.put("H5",2);s.put("H6",X);s.put("H7",X);s.put("H8",X);s.put("H9",X);
//        s.put("I1",X);s.put("I2",2);s.put("I3",X);s.put("I4",6);s.put("I5",X);s.put("I6",X);s.put("I7",X);s.put("I8",X);s.put("I9",X);*/
//        return s;
//    }
//    /**
//     * 打印玩家输入的数独
//     *
//     * @param sudokuParams
//     */
//    public static void PlaySudokuGameAgo(Map<String, Integer> sudokuParams) {
//        System.out.println("玩家输入的数独:");
//        System.out.println("    1' 2' 3' 4' 5' 6' 7' 8' 9'   ");//打印数独的横坐标定位符
//        for (int i = 1; i <= 9; i++) {
//            String st = "" + (char) (64 + i);
//            System.out.print(st + "   ");//打印数独的纵坐标定位符
//            for (int j = 1; j <= 9; j++) {
//                if (null == sudokuParams.get(st + j)) {
//                    System.out.print("■  ");//如果获取到数独中的值是空的
//                } else {
//                    System.out.print(sudokuParams.get(st + j) + "  ");//打印数独中的每一排
//                }
//            }
//            System.out.println();
//        }
//        System.out.println("==============================");
//    }
//    /**
//     * 打印补全的数独
//     *
//     * @param sudokuParams
//     */
//    public static Map<String, Integer> PlaySudokuGame(Map<String, Integer> sudokuParams) {
//        SudokuGame sdk = new SudokuGame();
//        Map<String, Integer> sudokuResult = sdk.sudokuResolve(sudokuParams);
//        if (null == sudokuResult) {
//            System.out.println("您输入的数独原始值不正确哦!!!");
//        } else {
//            System.out.println("    1' 2' 3' 4' 5' 6' 7' 8' 9'   ");//打印数独的横坐标定位符
//            for (int i = 1; i <= 9; i++) {
//                String st = "" + (char) (64 + i);
//                //打印数独的纵坐标定位符
//                System.out.print(st + "   ");
//                for (int j = 1; j <= 9; j++) {
//                    if (null == sudokuResult.get(st + j)) {
//                        //如果获取到数独中的值是空的
//                        System.out.print("■  ");
//                    } else {
//                        System.out.print(sudokuResult.get(st + j) + "  ");//打印数独中的每一排
//                    }
//                }
//                System.out.println();
//            }
//            System.out.println("author by ★Sunner★");
//        }
//        return sudokuResult;
//    }
//    /**
//     * 对玩家输入的数独数据进行处理
//     * @param sudokuParams
//     * @return
//     */
//    public Map<String,Integer> sudokuResolve(Map<String,Integer> sudokuParams){
//        if(sudokuJudge(sudokuParams)){
//            if(fillNumber(sudokuParams)){
//                suppose(sudokuParams);
//                if(sudokuJudge(sudokuParams)){
//                    return sudokuParams;
//                }else{
//                    return null;
//                }
//            }else{
//                return null;
//            }
//        }else{
//            return null;
//        }
//    }
//    /**
//     * 往数独的空格填值
//     * @param sudokuParams
//     */
//    public boolean fillNumber(Map<String, Integer> sudokuParams) {
//        //将所有可能值为1个,2个的都排除
//        if (removeSingle(sudokuParams) && removeDouble(sudokuParams)) {
//            if(change!=0){
//                //证明数独的sudokuParams有变动
//                change=0;
//                return fillNumber(sudokuParams);//递归
//            }
//            return true;
//        } else {
//            //填值过程中,发现玩家输入的数据有错误
//            return false;
//        }
//    }
//    /**
//     * 将某一"域"中元素为1个的possibleSet确定,并将该值赋值给对应的数独空格值
//     * 情况一:该possible只有1个元素
//     * 情况二:取一个元素,某一"域"中只有一个possibleSet包含该元素
//     * @param sudokuParams
//     * @return
//     */
//    public boolean removeSingle(Map<String,Integer> sudokuParams){
//        if(removeSingleSituation1(sudokuParams)&&removeSingleSituation2(sudokuParams)){
//            //对于removeSingle方法,只要sudokuParams有变动,oneChange和change就会自增
//            if (oneChange!=0){
//                //证明数独的sudokuParams有变动
//                oneChange=0;
//                return removeSingle(sudokuParams);  //递归
//            }else{
//                //数独没有变动
//                return true;
//            }
//        }else{
//            //填值过程中,发现玩家输入的数据有错误
//            return false;
//        }
//    }
//    /**
//     * 该possible中只有1个元素
//     * @param sudokuParams
//     * @return
//     */
//    public boolean removeSingleSituation1(Map<String,Integer> sudokuParams){
//        //对于removeSingleSituation1方法,只要sudokuParams的值有变动,flag和oneChange就都会自增
//        int flag=0;
//        for(int i=1;i<=9;i++){
//            for(int j=1;j<=9;j++){
//                String st=""+(char)(64+i)+j;
//                //找到所有可能值中,可能值为1个的possibleSet
//                Set<String> possibleSet=possibleMap.get(st);
//                if(possibleSet!=null&&possibleSet.size()==1){
//                    for (String s : possibleSet) {
//                        //往sudokuParams中填值
//                        sudokuParams.put(st,Integer.valueOf(s));
//                        //只要对sudokuParams进行了改动,就要对possibleMap进行处理
//                        fillSudokuParamsSuccess(st,s);
//                        flag++;
//                        oneChange++;
//                        change++;
//                    }
//                }
//            }
//        }
//        //判断数独数据是否有变动
//        if(flag!=0){
//            //证明sudokuParams有变动,就要对数独进行正确性判断
//            if(sudokuJudge(sudokuParams)){
//                //没有问题,就开始递归
//                return removeSingleSituation1(sudokuParams);
//            }else{
//                //存在问题
//                return false;
//            }
//        }
//        return true;
//    }
//    /**
//     * 取一个随机数,某一"域"中只有一个possibleSet包含该元素
//     * @param sudokuParams
//     * @return
//     */
//    public boolean removeSingleSituation2(Map<String, Integer> sudokuParams) {
//        //对于removeSingleSituation2方法,只要sudokuParams的值有变动,flag和oneChange就都会自增
//        int flag = 0;
//        for (int k = 1; k <= 9; k++) {
//            String randomNum = String.valueOf(k);
//            //判断水平方向
//            for (int i = 1; i <= 9; i++) {
//                int horizontalNo = 0;
//                String horizontalIndex = null;
//                for (int j = 1; j <= 9; j++) {
//                    Set<String> possibleSet = possibleMap.get("" + (char) (64 + i) + j);
//                    if (possibleSet != null) {
//                        for (String s : possibleSet) {
//                            if (s.equals(randomNum)) {
//                                horizontalNo++;
//                                horizontalIndex = "" + (char) (64 + i) + j;
//                            }
//                        }
//                    }
//                }
//                if (horizontalNo == 1) {
//                    //往sudokuParams中填值
//                    sudokuParams.put(horizontalIndex, Integer.valueOf(randomNum));
//                    //只要对sudokuParams进行了改动,就要对possibleMap进行处理
//                    fillSudokuParamsSuccess(horizontalIndex,randomNum);
//                    flag++;
//                    oneChange++;
//                    change++;
//                }
//
//            }
//            //判断垂直方向
//            for (int i = 1; i <= 9; i++) {
//                int verticalNo = 0;
//                String verticalIndex = null;
//                for (int j = 1; j <= 9; j++) {
//                    Set<String> possibleSet = possibleMap.get("" + (char) (64 + j) + i);
//                    if (possibleSet != null) {
//                        for (String s : possibleSet) {
//                            if (s.equals(randomNum)) {
//                                verticalNo++;
//                                verticalIndex = "" + (char) (64 + j) + i;
//                            }
//                        }
//                    }
//                }
//                if (verticalNo == 1) {
//                    //往sudokuParams中填值
//                    sudokuParams.put(verticalIndex, Integer.valueOf(randomNum));
//                    //只要对sudokuParams进行了改动,就要对possibleMap进行处理
//                    fillSudokuParamsSuccess(verticalIndex,randomNum);
//                    flag++;
//                    oneChange++;
//                    change++;
//                }
//            }
//            //判断小九宫格
//            for (int i = 0; i <= 6; i += 3) {
//                for (int j = 0; j <= 6; j += 3) {
//                    int nineNo = 0;
//                    String nineIndex = null;
//                    for (int m = 1; m <= 3; m++) {
//                        for (int n = 1; n <= 3; n++) {
//                            Set<String> possibleSet = possibleMap.get("" + (char) (64 + m + i) + (n + j));
//                            if (possibleSet != null) {
//                                for (String s : possibleSet) {
//                                    if (s.equals(randomNum)) {
//                                        nineNo++;
//                                        nineIndex = "" + (char) (64 + m + i) + (n + j);
//                                    }
//                                }
//                            }
//                        }
//                    }
//                    if (nineNo == 1) {
//                        //往sudokuParams中填值
//                        sudokuParams.put(nineIndex, Integer.valueOf(randomNum));
//                        //只要对sudokuParams进行了改动,就要对possibleMap进行处理
//                        fillSudokuParamsSuccess(nineIndex, randomNum);
//                        flag++;
//                        oneChange++;
//                        change++;
//                    }
//                }
//            }
//        }
//        if (flag != 0) {
//            //证明sudokuParams有改动,就要进行正确性判断
//            if (sudokuJudge(sudokuParams)) {
//                //没有问题,就开始递归
//                return removeSingleSituation2(sudokuParams);
//            } else {
//                //存在问题
//                return false;
//            }
//        }
//        return true;
//    }
//    /**
//     * 将某一"域"中元素为2个的possibleSet确定
//     * 情况一:某2个possibleSet只有2个元素,并且这2个possibleSet相同,则将这2个元素赋值给这2个possibleSet,并删除该"域"其它possibleSet中的这2个元素
//     * 情况二:取2个随机数,在某一域中只有2个possibleSet包含这2个随机数,则将这2个元素赋值给这2个possibleSet
//     * @param sudokuParams
//     * @return
//     */
//    public boolean removeDouble(Map<String,Integer> sudokuParams){
//        //对于removeDouble方法,只要possibleMap有变动,twoChange和change就会自增
//        if(removeDoubleSituation1(sudokuParams)&&removeDoubleSituation2(sudokuParams)){
//            if(twoChange!=0){
//                //证明数独的sudokuParams有变动
//                twoChange=0;   //这里的twoChange代表的是sudokuParams改变
//                return removeDouble(sudokuParams);//递归
//            }else{
//                //数独没有变动
//                return true;
//            }
//        }else{
//            //填值过程中,发现玩家输入的数据有错误
//            return false;
//        }
//    }
//    /**
//     * 某2个possibleSet只有2个元素,并且这2个possibleSet相同,则将这2个元素赋值给这2个possibleSet,并删除该"域"其它possibleSet中的这2个元素(这里的删除,采用"判断式"删除,因为要用于计数)
//     * @param sudokuParams
//     * @return
//     */
//    public boolean removeDoubleSituation1(Map<String,Integer> sudokuParams){
//        //对于removeDoubleSituation1方法,只要possibleMap有变动,flag和twoChange就都会自增
//        int flag=0;
//        //判断水平方向
//        for(int i=1;i<=9;i++){
//            List<String> recordList=new ArrayList<>();
//            //找到某一域中只有2个元素的possibleSet
//            for(int j=1;j<=9;j++){
//                String st="" + (char) (64 + i) + j;
//                Set<String> possibleSet = possibleMap.get(st);
//                if (possibleSet!=null&&possibleSet.size()==2){
//                    recordList.add(st);
//                }
//            }
//            if(recordList.size()>=2){
//                //判断某一"域"中存在2个possibleSet只有2个元素,并且这2个possibleSet相同
//                for(int m=1;m<=recordList.size()-1;m++){
//                    for(int n=0;n<m;n++){
//                        if(possibleMap.get(recordList.get(m)).equals(possibleMap.get(recordList.get(n)))){
//                            //存在,删除该"域"其它possibleSet中的这2个元素(这里的删除,采用"判断式"删除,因为要用于计数)
//                            for (String s : possibleMap.get(recordList.get(m))) {
//                                for(int j=1;j<=9;j++){
//                                    String st="" + (char) (64 + i) + j;
//                                    if(st.equals(recordList.get(m))||st.equals(recordList.get(n))){
//                                        continue;
//                                    }else{
//                                        if(possibleMap.get(st)!=null&&possibleMap.get(st).remove(s)){
//                                            //对possibleMap进行过改变
//                                            flag++;
//                                            twoChange++;
//                                            change++;
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//        //判断垂直方向
//        for(int i=1;i<=9;i++){
//            List<String> recordList=new ArrayList<>();
//            //找到某一域中只有2个元素的possibleSet
//            for(int j=1;j<=9;j++){
//                String st="" + (char) (64 + j) + i;
//                Set<String> possibleSet = possibleMap.get(st);
//                if (possibleSet!=null&&possibleSet.size()==2){
//                    recordList.add(st);
//                }
//            }
//            if(recordList.size()>=2){
//                //判断某一"域"中存在2个possibleSet只有2个元素,并且这2个possibleSet相同
//                for(int m=1;m<=recordList.size()-1;m++){
//                    for(int n=0;n<m;n++){
//                        if(possibleMap.get(recordList.get(m)).equals(possibleMap.get(recordList.get(n)))){
//                            //存在,删除该"域"其它possibleSet中的这2个元素(这里的删除,采用"判断式"删除,因为要用于计数)
//                            for (String s : possibleMap.get(recordList.get(m))) {
//                                for(int j=1;j<=9;j++){
//                                    String st="" + (char) (64 + j) + i;
//                                    if(st.equals(recordList.get(m))||st.equals(recordList.get(n))){
//                                        continue;
//                                    }else{
//                                        if(possibleMap.get(st)!=null&&possibleMap.get(st).remove(s)){
//                                            //对possibleMap进行过改变
//                                            flag++;
//                                            twoChange++;
//                                            change++;
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//        //判断小九宫格
//        for(int i=1;i<=7;i+=3){
//            for(int j=1;j<=7;j+=3){
//                List<String> recordList=new ArrayList<>();
//                for(int m=i;m<=i+2;m++){
//                    for(int n=j;n<=j+2;n++){
//                        //找到某一域中只有2个元素的possibleSet
//                        String st="" + (char) (64 + m) + n;
//                        Set<String> possibleSet = possibleMap.get(st);
//                        if (possibleSet!=null&&possibleSet.size()==2){
//                            recordList.add(st);
//                        }
//                    }
//                }
//                if(recordList.size()>=2){
//                    //判断某一"域"中存在2个possibleSet只有2个元素,并且这2个possibleSet相同
//                    for(int x=1;x<=recordList.size()-1;x++){
//                        for(int y=0;y<x;y++){
//                            if(possibleMap.get(recordList.get(x)).equals(possibleMap.get(recordList.get(y)))){
//                                //存在,删除该"域"其它possibleSet中的这2个元素(这里的删除,采用"判断式"删除,因为要用于计数)
//                                for (String s : possibleMap.get(recordList.get(x))) {
//                                    for(int m=i;m<=i+2;m++){
//                                        for(int n=j;n<=j+2;n++){
//                                            //找到某一域中只有2个元素的possibleSet
//                                            String st="" + (char) (64 + m) + n;
//                                            if(st.equals(recordList.get(x))||st.equals(recordList.get(y))){
//                                                continue;
//                                            }else{
//                                                if(possibleMap.get(st)!=null&&possibleMap.get(st).remove(s)){
//                                                    //对possibleMap进行过改变
//                                                    flag++;
//                                                    twoChange++;
//                                                    change++;
//                                                }
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//        if (flag!=0){
//            //证明possibleMap有变动
//            //调用removeSingle,去掉变动后的possibleMap中possibleSet为1个的情况
//            removeSingle(sudokuParams);
//            if(sudokuJudge(sudokuParams)){
//                return removeDoubleSituation1(sudokuParams);//递归
//            }else {
//                return false;
//            }
//        }else{
//            return true;
//        }
//    }
//    /**
//     * 取2个随机数,在某一域中只有2个possibleSet包含这2个随机数,则将这2个元素赋值给这2个possibleSet
//     * @param sudokuParams
//     * @return
//     */
//    public boolean removeDoubleSituation2(Map<String,Integer> sudokuParams){
//        //对于removeDoubleSituation2方法,只要possibleMap有变动,flag和twoChange就都会自增
//        int flag=0;
//        for (int i = 2; i <= 9; i++) {
//            for (int j = 1; j < i; j++) {
//                //取2个随机数
//                String randomNumber1 = String.valueOf(i);
//                String randomNumber2 = String.valueOf(j);
//                //判断水平方向
//                for (int m = 1; m <= 9; m++) {
//                    //用于记录并"否定"该域
//                    int negative=0;
//                    Set<String> horizontalSet = new HashSet<String>();
//                    for (int n = 1; n <= 9; n++) {
//                        Set<String> possibleSet = possibleMap.get("" + (char) (64 + m) + n);
//                        if (null != possibleSet) {
//                            //只要possibleSet包含任一随机数,flag4Horizon就自增
//                            int flag4Horizon = 0;
//                            for (String s : possibleSet) {
//                                if (s.equals(randomNumber1) || s.equals(randomNumber2)) {
//                                    flag4Horizon++;
//                                }
//                            }
//                            if(flag4Horizon==1){
//                                //证明该域中有一个possibleSet中只包含randomNumber1或randomNumber2中的一个,那么该域不符合要求
//                                negative++;
//                                break;//中断循环,提高代码效率
//                            }
//                            if (flag4Horizon == 2 ) {
//                                //证明水平方向的该possibleSet包含这2个不相同的元素
//                                horizontalSet.add("" + (char) (64 + m) + n);
//                            }
//                        }
//                    }
//                    //水平方向有且仅有2个possibleSet拥有这2个不相同的元素,则将这2个possibleSet中的"可能值"重置为这2个不相同的元素
//                    if (horizontalSet.size() == 2 && negative == 0) {
//                        for (String s : horizontalSet) {
//                            if (possibleMap.get(s).size() != 2) {//为了保证possibleMap的改变是"有效改变"
//                                Set<String> twoSizeSet = new HashSet<String>();
//                                twoSizeSet.add(randomNumber1);
//                                twoSizeSet.add(randomNumber2);
//                                possibleMap.put(s, twoSizeSet);
//                                flag++;
//                                twoChange++;
//                                change++;
//                            }
//                        }
//                    }
//                }
//                //判断垂直方向
//                for (int m = 1; m <= 9; m++) {
//                    //用于记录并"否定"该域
//                    int negative=0;
//                    Set<String> verticalSet = new HashSet<String>();
//                    for (int n = 1; n <= 9; n++) {
//                        Set<String> possibleSet = possibleMap.get("" + (char) (64 + n) + m);
//                        if (null != possibleSet) {
//                            //只要possibleSet包含任一随机数,flag4Vertical就自增
//                            int flag4Vertical = 0;
//                            for (String s : possibleSet) {
//                                if (s.equals(randomNumber1) || s.equals(randomNumber2)) {
//                                    flag4Vertical++;
//                                }
//                            }
//                            if(flag4Vertical==1){
//                                //证明该域中有一个possibleSet中只包含randomNumber1或randomNumber2中的一个,那么该域不符合要求
//                                negative++;
//                                break;//中断循环,提高代码效率
//                            }
//                            if (flag4Vertical == 2) {
//                                //证明垂直方向的该possibleSet包含这2个不相同的元素
//                                verticalSet.add("" + (char) (64 + n) + m);
//                            }
//                        }
//                    }
//                    //垂直方向有且仅有2个possibleSet拥有这2个不相同的元素,则将这2个possibleSet中的"可能值"重置为这2个不相同的元素
//                    if (verticalSet.size() == 2 && negative == 0) {
//                        for (String s : verticalSet) {
//                            if (possibleMap.get(s).size() != 2) {//为了使flag++代表的是"有效自增"
//                                Set<String> twoSizeSet = new HashSet<String>();
//                                twoSizeSet.add(randomNumber1);
//                                twoSizeSet.add(randomNumber2);
//                                possibleMap.put(s, twoSizeSet);
//                                flag++;
//                                twoChange++;
//                                change++;
//                            }
//                        }
//                    }
//                }
//                //判断小九宫格
//                for(int x=1;x<=7;x+=3){
//                    for(int y=1;y<=7;y+=3){
//                        //用于记录并"否定"该域
//                        int negative=0;
//                        Set<String> nineSet = new HashSet<String>();
//                        for(int m=i;m<=i+2;m++){
//                            for(int n=j;n<=j+2;n++){
//                                Set<String> possibleSet = possibleMap.get("" + (char) (64 + m) + n);
//                                if (null != possibleSet) {
//                                    //只要possibleSet包含任一随机数,flag4Nine就自增
//                                    int flag4Nine = 0;
//                                    for (String s : possibleSet) {
//                                        if (s.equals(randomNumber1) || s.equals(randomNumber2)) {
//                                            flag4Nine++;
//                                        }
//                                    }
//                                    if(flag4Nine==1){
//                                        //证明该域中有一个possibleSet中只包含randomNumber1或randomNumber2中的一个,那么该域不符合要求
//                                        negative++;
//                                        break;//中断循环,提高代码效率
//                                    }
//                                    if (flag4Nine == 2) {
//                                        //证明小九宫格的该possibleSet包含这2个不相同的元素
//                                        nineSet.add("" + (char) (64 + m) + n);
//                                    }
//                                }
//                            }
//                        }
//                        //小九宫格有且仅有2个possibleSet拥有这2个不相同的元素,则将这2个possibleSet中的"可能值"重置为这2个不相同的元素
//                        if (nineSet.size() == 2 && negative == 0) {
//                            for (String s : nineSet) {
//                                if(possibleMap.get(s).size()!=2){//为了使flag++代表的是"有效自增"
//                                    Set<String> twoSizeSet=new HashSet<String>();
//                                    twoSizeSet.add(randomNumber1);
//                                    twoSizeSet.add(randomNumber2);
//                                    possibleMap.put(s, twoSizeSet);
//                                    flag++;
//                                    twoChange++;
//                                    change++;
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//        if (flag!=0){
//            //证明possibleMap有变动
//            //调用removeSingle,去掉变动后的possibleMap中possibleSet为1个的情况
//            removeSingle(sudokuParams);
//            if(sudokuJudge(sudokuParams)){
//                return removeDoubleSituation2(sudokuParams);//递归
//            }else {
//                return false;
//            }
//        }else{
//            return true;
//        }
//    }
//
//    /**
//     * 作出假设
//     * @param sudokuParams
//     * @return
//     */
//    public boolean suppose(Map<String,Integer> sudokuParams){
//        //作一个sudokuParams的克隆,用于还原sudokuParams
//        Map<String, Integer> copySudokuParams=new HashMap<>();
//        for(int m=1;m<=9;m++){
//            for(int n=1;n<=9;n++){
//                String st=""+(char)(64+m)+n;
//                copySudokuParams.put(st,sudokuParams.get(st));
//            }
//        }
//        //作一个possibleMap的克隆,用于还原possibleMap
//        Map<String,Set<String>> copyPossibleMap=new HashMap<>();
//        for(int m=1;m<=9;m++){
//            for(int n=1;n<=9;n++){
//                String st=""+(char)(64+m)+n;
//                if(possibleMap.get(st)!=null){
//                    Set<String> copyPossibleSet=new HashSet<>();
//                    for (String s : possibleMap.get(st)) {
//                        copyPossibleSet.add(s);
//                    }
//                    copyPossibleMap.put(st,copyPossibleSet);
//                }
//            }
//        }
//        //首先判断数独有没有被解开
//        Map<String,Set<String>> testMap=new HashMap<>();
//        if(possibleMap.equals(testMap)){
//            //数独已经被解开
//            return true;
//        }else{
//            //数独还没有被解开
//            //将possibleMap种所有长度为2的possibleSet的坐标存入supposeList中
//            List<String> supposeList=new ArrayList<>();
//            for(int i=1;i<=9;i++) {
//                for (int j = 1; j <= 9; j++) {
//                    String st=""+(char)(64+i)+j;
//                    Set<String> possibleSet = possibleMap.get(st);
//                    if (possibleSet!=null&&possibleSet.size()==2){
//                        //supposePossibleMap.put(st,possibleSet);
//                        supposeList.add(st);
//                    }
//                }
//            }
//            //任意取2个长度为2的possibleSet去假设,一共有4中情况
//            if(supposeList.size()==0){
//                //此时说明possibleMap中没有一个possibleSet的长度为2,这种情况不用假设了,直接跳过
//                return true;
//            }else if(supposeList.size()==1){
//                //此时说明possibleMap有一个possibleSet的长度为2
//                String coordinate=supposeList.get(0);
//                for (String s1 : possibleMap.get(coordinate)) {
//                    sudokuParams.put(coordinate,Integer.valueOf(s1));
//                    fillSudokuParamsSuccess(coordinate,s1);
//                    //对假设后的结果进行判断
//                    if(sudokuJudge(sudokuParams)&&fillNumber(sudokuParams)&&possibleMap.equals(testMap)){
//                        //假设过程中没有出错,并且解开了数独
//                        return true;
//                    }else{
//                        //假设过程中出错,或者是没有解开数独,还原数据(注意还原数据的方式)
//                        //还原sudokuParams
//                        for(int m=1;m<=9;m++){
//                            for(int n=1;n<=9;n++){
//                                String st=""+(char)(64+m)+n;
//                                sudokuParams.put(st,copySudokuParams.get(st));
//                            }
//                        }
//                        //还原possibleMap
//                        for(int m=1;m<=9;m++){
//                            for(int n=1;n<=9;n++){
//                                String st=""+(char)(64+m)+n;
//                                if(copyPossibleMap.get(st)!=null){
//                                    Set<String> copyPossibleSet=new HashSet<>();
//                                    for (String s : copyPossibleMap.get(st)) {
//                                        copyPossibleSet.add(s);
//                                    }
//                                    possibleMap.put(st,copyPossibleSet);
//                                }
//                            }
//                        }
//                    }
//                }
//            }else{
//                //此时说明possibleMap中至少有2个possibleSet的长度为2
//                for(int i=1;i<=supposeList.size()-1;i++){
//                    for(int j=0;j<i;j++){
//                        String coordinate1=supposeList.get(i);
//                        String coordinate2=supposeList.get(j);
//                        //注意下面这2步并不是多余的,而是必须要这样转
//                        Set<String> possibleSet1=new HashSet<>();
//                        Set<String> possibleSet2=new HashSet<>();
//                        for (String s : possibleMap.get(coordinate1)) {
//                            possibleSet1.add(s);
//                        }
//                        for (String s : possibleMap.get(coordinate2)) {
//                            possibleSet2.add(s);
//                        }
//                        for (String s1 : possibleSet1) {
//                            for (String s2 : possibleSet2) {
//                                sudokuParams.put(coordinate1,Integer.valueOf(s1));
//                                fillSudokuParamsSuccess(coordinate1,s1);
//                                sudokuParams.put(coordinate2,Integer.valueOf(s2));
//                                fillSudokuParamsSuccess(coordinate2,s2);
//                                //对假设后的结果进行判断
//                                if(sudokuJudge(sudokuParams)&&fillNumber(sudokuParams)&&possibleMap.equals(testMap)){
//                                    //假设过程中没有出错,并且解开了数独
//                                    return true;
//                                }else{
//                                    //假设过程中出错,或者是没有解开数独,还原数据(注意还原数据的方式)
//                                    //还原sudokuParams
//                                    for(int m=1;m<=9;m++){
//                                        for(int n=1;n<=9;n++){
//                                            String st=""+(char)(64+m)+n;
//                                            sudokuParams.put(st,copySudokuParams.get(st));
//                                        }
//                                    }
//                                    //还原possibleMap
//                                    for(int m=1;m<=9;m++){
//                                        for(int n=1;n<=9;n++){
//                                            String st=""+(char)(64+m)+n;
//                                            if(copyPossibleMap.get(st)!=null){
//                                                Set<String> copyPossibleSet=new HashSet<>();
//                                                for (String s : copyPossibleMap.get(st)) {
//                                                    copyPossibleSet.add(s);
//                                                }
//                                                possibleMap.put(st,copyPossibleSet);
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//        return true;
//    }
//
//    /**
//     * 判断数独的数据是否正确
//     * @return
//     */
//    public boolean sudokuJudge(Map<String,Integer> sudokuParams){
//        if(horizontalJudge(sudokuParams)&&verticalJudge(sudokuParams)&&nineJudge(sudokuParams)){
//            return true;
//        }else{
//            return false;
//        }
//    }
//    /**
//     * 判断数独的水平数据是否正确
//     * @param sudokuParams
//     * @return
//     */
//    public boolean horizontalJudge(Map<String,Integer> sudokuParams){
//        for(int i=1;i<=9;i++){
//            //将数独的水平数据分别存入list集合(可以存重复值),set集合(不可以存重复值)
//            List<Integer> list=new ArrayList<Integer>();
//            Set<Integer> set=new HashSet<Integer>();
//            for(int j=1;j<=9;j++){
//                Integer value=sudokuParams.get(""+(char)(64+i)+j);//数独中的元素
//                if(null==value){
//                    continue;
//                }
//                list.add(value);
//                set.add(value);
//            }
//            if(list.size()!=set.size()){
//                return false;
//            }
//        }
//        return true;
//    }
//    /**
//     * 判断数独的垂直数据是否正确
//     * @param sudokuParams
//     * @return
//     */
//    public boolean verticalJudge(Map<String,Integer> sudokuParams){
//        for(int i=1;i<=9;i++){
//            //将数独的垂直数据分别存入list集合(可以存重复值),set集合(不可以存重复值)
//            List<Integer> list=new ArrayList<Integer>();
//            Set<Integer> set=new HashSet<Integer>();
//            for(int j=1;j<=9;j++){
//                Integer value=sudokuParams.get(""+(char)(64+j)+i);//数独中的元素
//                if(null==value){
//                    continue;
//                }
//                list.add(value);
//                set.add(value);
//            }
//            if(list.size()!=set.size()){
//                return false;
//            }
//        }
//        return true;
//    }
//    /**
//     * 判断数独的小九宫格数据是否正确
//     * @param sudokuParams
//     * @return
//     */
//    public boolean nineJudge(Map<String,Integer> sudokuParams){
//        for(int i=0;i<=6;i+=3){
//            for(int j=0;j<=6;j+=3){
//                //将数独的小九宫格数据分别存入list结合(可以存重复值),set集合(不可以存重复值)
//                List<Integer> list=new ArrayList<Integer>();
//                Set<Integer> set=new HashSet<Integer>();
//                for (int m = 1; m <= 3; m++) {
//                    for (int n = 1; n <= 3; n++) {
//                        Integer value=sudokuParams.get(""+(char)(64+i+m)+(n+j));//数独中的元素
//                        if(null==value){
//                            continue;
//                        }
//                        list.add(value);
//                        set.add(value);
//                    }
//                }
//                if(list.size()!=set.size()){
//                    return false;
//                }
//            }
//        }
//        return true;
//    }
//
//    /**
//     * 根据现有值,往数独的空格处,填上所有的可能值
//     * @param sudokuParams
//     */
//    public Map<String,Set<String>> getPossibleMap(Map<String,Integer> sudokuParams){
//        Map<String,Set<String>> possibleMap=new HashMap<String,Set<String>>();
//        for(int i=1;i<=9;i++){
//            for(int j=1;j<=9;j++){
//                if(null==sudokuParams.get(""+(char)(64+i)+j)){
//                    //创建一个包含1~9元素的set集合
//                    Set<String> set=new HashSet<String>();
//                    for(int k=1;k<=9;k++){
//                        set.add(String.valueOf(k));
//                    }
//                    //判断水平方向
//                    for(int k=1;k<=9;k++){
//                        Integer in=sudokuParams.get(""+(char)(64+i)+k);
//                        if(null==in){
//                            continue;
//                        }else{
//                            set.remove(String.valueOf(in));
//                        }
//                    }
//                    //判断垂直方向
//                    for(int k=1;k<=9;k++){
//                        Integer in=sudokuParams.get(""+(char)(64+k)+j);
//                        if(null==in){
//                            continue;
//                        }else{
//                            set.remove(String.valueOf(in));
//                        }
//                    }
//                    //判断小九宫格
//                    if(i>=1&&i<=3){
//                        if(j>=1&&j<=3){
//                            for(int m=1;m<=3;m++){
//                                for(int n=1;n<=3;n++){
//                                    Integer in=sudokuParams.get(""+(char)(64+m)+n);
//                                    if(null==in){
//                                        continue;
//                                    }else{
//                                        set.remove(String.valueOf(in));
//                                    }
//                                }
//                            }
//                        }else if(j>=4&&j<=6){
//                            for(int m=1;m<=3;m++){
//                                for(int n=4;n<=6;n++){
//                                    Integer in=sudokuParams.get(""+(char)(64+m)+n);
//                                    if(null==in){
//                                        continue;
//                                    }else{
//                                        set.remove(String.valueOf(in));
//                                    }
//                                }
//                            }
//                        }else if(j>=7&&j<=9){
//                            for(int m=1;m<=3;m++){
//                                for(int n=7;n<=9;n++){
//                                    Integer in=sudokuParams.get(""+(char)(64+m)+n);
//                                    if(null==in){
//                                        continue;
//                                    }else{
//                                        set.remove(String.valueOf(in));
//                                    }
//                                }
//                            }
//                        }
//                    }else if(i>=4&&i<=6){
//                        if(j>=1&&j<=3){
//                            for(int m=4;m<=6;m++){
//                                for(int n=1;n<=3;n++){
//                                    Integer in=sudokuParams.get(""+(char)(64+m)+n);
//                                    if(null==in){
//                                        continue;
//                                    }else{
//                                        set.remove(String.valueOf(in));
//                                    }
//                                }
//                            }
//                        }else if(j>=4&&j<=6){
//                            for(int m=4;m<=6;m++){
//                                for(int n=4;n<=6;n++){
//                                    Integer in=sudokuParams.get(""+(char)(64+m)+n);
//                                    if(null==in){
//                                        continue;
//                                    }else{
//                                        set.remove(String.valueOf(in));
//                                    }
//                                }
//                            }
//                        }else if(j>=7&&j<=9){
//                            for(int m=4;m<=6;m++){
//                                for(int n=7;n<=9;n++){
//                                    Integer in=sudokuParams.get(""+(char)(64+m)+n);
//                                    if(null==in){
//                                        continue;
//                                    }else{
//                                        set.remove(String.valueOf(in));
//                                    }
//                                }
//                            }
//                        }
//                    }else if(i>=7&&i<=9){
//                        if(j>=1&&j<=3){
//                            for(int m=7;m<=9;m++){
//                                for(int n=1;n<=3;n++){
//                                    Integer in=sudokuParams.get(""+(char)(64+m)+n);
//                                    if(null==in){
//                                        continue;
//                                    }else{
//                                        set.remove(String.valueOf(in));
//                                    }
//                                }
//                            }
//                        }else if(j>=4&&j<=6){
//                            for(int m=7;m<=9;m++){
//                                for(int n=4;n<=6;n++){
//                                    Integer in=sudokuParams.get(""+(char)(64+m)+n);
//                                    if(null==in){
//                                        continue;
//                                    }else{
//                                        set.remove(String.valueOf(in));
//                                    }
//                                }
//                            }
//                        }else if(j>=7&&j<=9){
//                            for(int m=7;m<=9;m++){
//                                for(int n=7;n<=9;n++){
//                                    Integer in=sudokuParams.get(""+(char)(64+m)+n);
//                                    if(null==in){
//                                        continue;
//                                    }else{
//                                        set.remove(String.valueOf(in));
//                                    }
//                                }
//                            }
//                        }
//                    }
//                    possibleMap.put(""+(char)(64+i)+j,set);
//                }
//            }
//        }
//        return possibleMap;
//    }
//
//    /**
//     * 只要sudokuParams有变动,就要对possibleMap进行处理
//     * @param coordinate
//     * @param value
//     */
//    public void fillSudokuParamsSuccess(String coordinate,String value){
//        //首先删除对应的possibleSet
//        possibleMap.remove(coordinate);
//        //然后删除其它possibleSet中的value
//        String horizontalCoordinate=coordinate.substring(0,1);
//        String verticalCoordinate=coordinate.substring(1);
//        //删除水平方向其它possibleSet中的value
//        for(int i=1;i<=9;i++){
//            Set<String> possibleSet = possibleMap.get(horizontalCoordinate + i);
//            if(possibleSet!=null){
//                possibleSet.remove(value);
//            }
//        }
//        //删除垂直方向其它possibleSet中的value
//        for(int i=1;i<=9;i++){
//            Set<String> possibleSet = possibleMap.get((char) (64 + i) + verticalCoordinate);
//            if(possibleSet!=null){
//                possibleSet.remove(value);
//            }
//        }
//        //删除小九宫格其它possibleSet中的value
//        if(horizontalCoordinate.equals("A")||horizontalCoordinate.equals("B")||horizontalCoordinate.equals("C")){
//            if(Integer.valueOf(verticalCoordinate)>=1&&Integer.valueOf(verticalCoordinate)<=3){
//                for(int i=1;i<=3;i++){
//                    for(int j=1;j<=3;j++){
//                        Set<String> possibleSet = possibleMap.get("" + (char) (64 + i) + j);
//                        if(possibleSet!=null){
//                            possibleSet.remove(value);
//                        }
//                    }
//                }
//            }else if(Integer.valueOf(verticalCoordinate)>=4&&Integer.valueOf(verticalCoordinate)<=6){
//                for(int i=1;i<=3;i++){
//                    for(int j=4;j<=6;j++){
//                        Set<String> possibleSet = possibleMap.get("" + (char) (64 + i) + j);
//                        if(possibleSet!=null){
//                            possibleSet.remove(value);
//                        }
//                    }
//                }
//            }else{
//                for(int i=1;i<=3;i++){
//                    for(int j=7;j<=9;j++){
//                        Set<String> possibleSet = possibleMap.get("" + (char) (64 + i) + j);
//                        if(possibleSet!=null){
//                            possibleSet.remove(value);
//                        }
//                    }
//                }
//            }
//        }else if(horizontalCoordinate.equals("D")||horizontalCoordinate.equals("E")||horizontalCoordinate.equals("F")){
//            if(Integer.valueOf(verticalCoordinate)>=1&&Integer.valueOf(verticalCoordinate)<=3){
//                for(int i=4;i<=6;i++){
//                    for(int j=1;j<=3;j++){
//                        Set<String> possibleSet = possibleMap.get("" + (char) (64 + i) + j);
//                        if(possibleSet!=null){
//                            possibleSet.remove(value);
//                        }
//                    }
//                }
//            }else if(Integer.valueOf(verticalCoordinate)>=4&&Integer.valueOf(verticalCoordinate)<=6){
//                for(int i=4;i<=6;i++){
//                    for(int j=4;j<=6;j++){
//                        Set<String> possibleSet = possibleMap.get("" + (char) (64 + i) + j);
//                        if(possibleSet!=null){
//                            possibleSet.remove(value);
//                        }
//                    }
//                }
//            }else{
//                for(int i=4;i<=6;i++){
//                    for(int j=7;j<=9;j++){
//                        Set<String> possibleSet = possibleMap.get("" + (char) (64 + i) + j);
//                        if(possibleSet!=null){
//                            possibleSet.remove(value);
//                        }
//                    }
//                }
//            }
//        }else {
//            if(Integer.valueOf(verticalCoordinate)>=1&&Integer.valueOf(verticalCoordinate)<=3){
//                for(int i=7;i<=9;i++){
//                    for(int j=1;j<=3;j++){
//                        Set<String> possibleSet = possibleMap.get("" + (char) (64 + i) + j);
//                        if(possibleSet!=null){
//                            possibleSet.remove(value);
//                        }
//                    }
//                }
//            }else if(Integer.valueOf(verticalCoordinate)>=4&&Integer.valueOf(verticalCoordinate)<=6){
//                for(int i=7;i<=9;i++){
//                    for(int j=4;j<=6;j++){
//                        Set<String> possibleSet = possibleMap.get("" + (char) (64 + i) + j);
//                        if(possibleSet!=null){
//                            possibleSet.remove(value);
//                        }
//                    }
//                }
//            }else{
//                for(int i=7;i<=9;i++){
//                    for(int j=7;j<=9;j++){
//                        Set<String> possibleSet = possibleMap.get("" + (char) (64 + i) + j);
//                        if(possibleSet!=null){
//                            possibleSet.remove(value);
//                        }
//                    }
//                }
//            }
//        }
//    }
//}