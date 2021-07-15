package com.sunner.function.service.impl;

import com.sunner.function.dao.SudokuDao;
import com.sunner.function.pojo.Sudoku;
import com.sunner.function.service.SudokuService;
import com.sunner.structure.common.Result;
import com.sunner.structure.enums.SudokuEnum;
import com.sunner.structure.exception.SunnerRuntimeException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Service
@SuppressWarnings("all")
public class SudokuServiceImpl implements SudokuService {
    //git测试
    //git测试2

    public Map<String,String> sudokuMap;//将玩家输入的数独值放入一个Map中
    public Map<String,Set<String>> possibleMap;//得到数独空白处可能值
    public int change=0;   //用于判断数独是否有变化,作用于fillNumber
    public int oneChange=0;//用于判断数独是否有变化,作用于removeSinglePossible
    public int twoChange=0;//用于判断数独是否有变化,作用于removeDoublePossible
    public int threeChange=0;//用于判断数独是否有变化,作用于removeTriplePossible
    private static final Logger log=Logger.getLogger(SudokuServiceImpl.class);
    @Autowired
    private SudokuDao sudokuDao;

    /**
     * 根据数独编号找到数独原始值,(无编号)返回一个最新的数独原始值
     * @param sudoku
     * @return
     */
    @Override
    public Result readSudoku(Sudoku sudoku) {
        //数独编码为空表示读取一个随机数独;数独编码不为空,读取指定数独
        Sudoku readSudoku = sudokuDao.findBySudokuNo(sudoku);
        if(readSudoku==null){
            return Result.fail(SudokuEnum.SUDOKU_NO_INVALID);
        }
        Map returnMap=new HashMap();
        returnMap.put("sudokuNo",String.valueOf(readSudoku.getSudokuNo()));
        returnMap.put("sudoku",sudoku2ResponseParam(readSudoku));
        return Result.success(returnMap);
    }

    /**
     * 破解数独
     * @param sudoku
     * @return
     */
    @Override
    @Transactional
    public Result crackSudoku(Sudoku sudoku) throws Exception{
        long time1 = System.currentTimeMillis();
        sudokuMap = sudoku2SudokuMap(sudoku);
        if (!sudokuJudge()){
            return Result.fail(SudokuEnum.SUDOKU_PARAMS_INVALID);
        }
        possibleMap = sudokuMap2PossibleMap(sudokuMap);
        /**
         * 数独破解主程序
         */
        sudokuResolve();
        /**
         * 判断是否破解成功
         */
        if (CollectionUtils.isEmpty(possibleMap)){
            //数独破解成功
            long time2 = System.currentTimeMillis();
            Map returnMap=new HashMap();
            returnMap.put("crackTime",time2-time1);
            returnMap.put("sudoku", sudokuMap2ResponseParam(sudokuMap));
            //保存该数独,返回前端数独编号
            List<Sudoku> sudokus = sudokuDao.findBySudoku(sudoku);
            if (sudokus.size() > 0) {
                returnMap.put("sudokuNo",sudokus.get(0).getSudokuNo());
            }else {
                if(1!=sudokuDao.addSudoku(sudoku)){
                    throw new SunnerRuntimeException(SudokuEnum.SUDOKU_SAVE_FAIL);
                }
                returnMap.put("sudokuNo",sudoku.getSudokuNo());
            }
            return Result.success(returnMap);
        }
        return Result.fail(SudokuEnum.CRACK_FAIL);
    }





    //===================================================判断数独正确性============================================================

    /**
     * 判断数独的数据是否正确
     * @return
     */
    public boolean sudokuJudge(){
        if(horizontalJudge()&&verticalJudge()&&nineJudge()){
            return true;
        }else{
            return false;
        }
    }
    /**
     * 判断数独的水平数据是否正确
     * @param sudokuMap
     * @return
     */
    public boolean horizontalJudge(){
        for(int i=1;i<=9;i++){
            //将数独的水平数据分别存入list集合(可以存重复值),set集合(不可以存重复值)
            List<String> list=new ArrayList<String>();
            Set<String> set=new HashSet<String>();
            for(int j=1;j<=9;j++){
                String value= sudokuMap.get(""+(char)(96+i)+j);
                if(null==value){
                    continue;
                }
                list.add(value);
                set.add(value);
            }
            if(list.size()!=set.size()){
                return false;
            }
        }
        return true;
    }
    /**
     * 判断数独的垂直数据是否正确
     * @param sudokuMap
     * @return
     */
    public boolean verticalJudge(){
        for(int i=1;i<=9;i++){
            //将数独的垂直数据分别存入list集合(可以存重复值),set集合(不可以存重复值)
            List<String> list=new ArrayList<String>();
            Set<String> set=new HashSet<String>();
            for(int j=1;j<=9;j++){
                String value= sudokuMap.get(""+(char)(96+j)+i);
                if(null==value){
                    continue;
                }
                list.add(value);
                set.add(value);
            }
            if(list.size()!=set.size()){
                return false;
            }
        }
        return true;
    }
    /**
     * 判断数独的小九宫格数据是否正确
     * @param sudokuMap
     * @return
     */
    public boolean nineJudge(){
        for(int i=0;i<=6;i+=3){
            for(int j=0;j<=6;j+=3){
                //将数独的小九宫格数据分别存入list结合(可以存重复值),set集合(不可以存重复值)
                List<String> list=new ArrayList<String>();
                Set<String> set=new HashSet<String>();
                for (int m = 1; m <= 3; m++) {
                    for (int n = 1; n <= 3; n++) {
                        String value= sudokuMap.get(""+(char)(96+i+m)+(n+j));//数独中的元素
                        if(null==value){
                            continue;
                        }
                        list.add(value);
                        set.add(value);
                    }
                }
                if(list.size()!=set.size()){
                    return false;
                }
            }
        }
        return true;
    }

    //===================================================操作possibleSet============================================================

    /**
     * 对数独数据sudokuMap进行处理
     * @return
     */
    public boolean sudokuResolve() {
        if (sudokuJudge() && fillBlank()) {
            suppose();
            if(sudokuJudge()){
                return true;
            }else{
                return false;
            }
        } else {
            return false;
        }
    }
    /**
     * 往数独的空格填值
     */
    public boolean fillBlank() {
        //将所有可能值为1个,2个的都排除
        if (sudokuJudge() && removeSinglePossible() && removeDoublePossible()) {
            if (change != 0) {
                change=0;
                return fillBlank();//递归
            }
            return true;
        } else {
            return false;
        }
    }
    //==================================================操作域为1的possibleSet=============================================================
    /**
     * 将某一"域"中元素为1个的possibleSet确定,并将该值赋值给对应的数独空格值
     * 情况一:该possible只有1个元素
     * 情况二:取一个元素,某一"域"中只有一个possibleSet包含该元素
     * @param sudokuMap
     * @return
     */
    public boolean removeSinglePossible(){
        if (sudokuJudge() && removeSinglePossibleSituation1() && removeSinglePossibleSituation2()) {
            if (oneChange != 0) {
                oneChange = 0;
                return removeSinglePossible();//递归
            } else {
                return true;
            }
        } else {
            return false;
        }
    }
    /**
     * 该possible中只有1个元素
     * @param sudokuMap
     * @return
     */
    public boolean removeSinglePossibleSituation1(){
        int oneChangeFlag=0;
        for(int i=1;i<=9;i++){
            for(int j=1;j<=9;j++){
                String sudokuKey=""+(char)(96+i)+j;
                //找到所有可能值中,可能值为1个的possibleSet
                Set<String> possibleSet=possibleMap.get(sudokuKey);
                if(possibleSet!=null&&possibleSet.size()==1){
                    for (String sudokuValue : possibleSet) {
                        //往sudokuMap中填值
                        sudokuMap.put(sudokuKey,sudokuValue);
                        //只要对sudokuMap进行了改动,就要对possibleMap进行处理
                        removePossibleMap(sudokuKey,sudokuValue);
                        oneChangeFlag++;
                        oneChange++;
                        change++;
                    }
                }
            }
        }
        //判断数独数据是否有变动
        if(oneChangeFlag!=0){
            if(sudokuJudge()){
                return removeSinglePossibleSituation1();//递归
            }else{
                return false;
            }
        }
        return true;
    }
    /**
     * 取一个1-9随机数,某一"域"中只有一个possibleSet包含该随机数
     * @param sudokuMap
     * @return
     */
    public boolean removeSinglePossibleSituation2() {
        //对于removeSingleSituation2方法,只要sudokuMap的值有变动,oneChangeFlag和oneChange就都会自增
        int oneChangeFlag = 0;
        for (int k = 1; k <= 9; k++) {
            String randomValue = String.valueOf(k);
            //判断水平方向
            for (int i = 1; i <= 9; i++) {
                int horizontalNum = 0;
                String sudokuKey = null;
                for (int j = 1; j <= 9; j++) {
                    Set<String> possibleSet = possibleMap.get("" + (char) (96 + i) + j);
                    if (possibleSet != null) {
                        for (String s : possibleSet) {
                            if (s.equals(randomValue)) {
                                horizontalNum++;
                                sudokuKey = "" + (char) (96 + i) + j;
                            }
                        }
                    }
                }
                if (horizontalNum == 1) {
                    sudokuMap.put(sudokuKey, randomValue);
                    removePossibleMap(sudokuKey,randomValue);
                    oneChangeFlag++;
                    oneChange++;
                    change++;
                }

            }
            //判断垂直方向
            for (int i = 1; i <= 9; i++) {
                int verticalNum = 0;
                String sudokuKey = null;
                for (int j = 1; j <= 9; j++) {
                    Set<String> possibleSet = possibleMap.get("" + (char) (96 + j) + i);
                    if (possibleSet != null) {
                        for (String s : possibleSet) {
                            if (s.equals(randomValue)) {
                                verticalNum++;
                                sudokuKey = "" + (char) (96 + j) + i;
                            }
                        }
                    }
                }
                if (verticalNum == 1) {
                    sudokuMap.put(sudokuKey, randomValue);
                    removePossibleMap(sudokuKey,randomValue);
                    oneChangeFlag++;
                    oneChange++;
                    change++;
                }
            }
            //判断小九宫格
            for (int i = 0; i <= 6; i += 3) {
                for (int j = 0; j <= 6; j += 3) {
                    int nineNum = 0;
                    String sudokuKey = null;
                    for (int m = 1; m <= 3; m++) {
                        for (int n = 1; n <= 3; n++) {
                            Set<String> possibleSet = possibleMap.get("" + (char) (96 + m + i) + (n + j));
                            if (possibleSet != null) {
                                for (String s : possibleSet) {
                                    if (s.equals(randomValue)) {
                                        nineNum++;
                                        sudokuKey = "" + (char) (96 + m + i) + (n + j);
                                    }
                                }
                            }
                        }
                    }
                    if (nineNum == 1) {
                        sudokuMap.put(sudokuKey, randomValue);
                        removePossibleMap(sudokuKey, randomValue);
                        oneChangeFlag++;
                        oneChange++;
                        change++;
                    }
                }
            }
        }
        if (oneChangeFlag != 0) {
            if (sudokuJudge()) {
                return removeSinglePossibleSituation2();//递归
            } else {
                return false;
            }
        }
        return true;
    }
    //=================================================操作域为2的possibleSet==============================================================
    /**
     * 将某一"域"中元素为2个的possibleSet确定
     * 情况一:某2个possibleSet只有2个元素,并且这2个possibleSet相同,则将这2个元素赋值给这2个possibleSet,并删除该"域"其它possibleSet中的这2个元素
     * 情况二:取2个随机数,在某一域中只有2个possibleSet包含这2个随机数,则将这2个元素赋值给这2个possibleSet
     * @param sudokuParams
     * @return
     */
    public boolean removeDoublePossible(){
        if(sudokuJudge()&&removeDoublePossibleSituation1()&&removeDoublePossibleSituation2()){
            if(twoChange!=0){
                twoChange=0;
                return removeDoublePossible();//递归
            }else{
                return true;
            }
        }else{
            return false;
        }
    }
    /**
     * 某2个possibleSet有且只有2个元素(并且相同),则将这2个元素赋值给这2个possibleSet,并删除该"域"其它possibleSet中的这2个元素
     * @param sudokuParams
     * @return
     */
    public boolean removeDoublePossibleSituation1(){
        int twoChangeFlag=0;
        //判断水平方向
        for(int i=1;i<=9;i++){
            List<String> recordList=new ArrayList<>();
            //找到某一域中只有2个元素的possibleSet对应的sudokuKey
            for(int j=1;j<=9;j++){
                String sudokuKey="" + (char) (96 + i) + j;
                Set<String> possibleSet = possibleMap.get(sudokuKey);
                if (possibleSet!=null&&possibleSet.size()==2){
                    recordList.add(sudokuKey);
                }
            }
            if(recordList.size()>=2){
                for(int m=1;m<=recordList.size()-1;m++){
                    for(int n=0;n<m;n++){
                        if(possibleMap.get(recordList.get(m)).equals(possibleMap.get(recordList.get(n)))){
                            //存在,删除该"域"其它possibleSet中的这2个元素(这里的删除,采用"判断式"删除,因为要用于计数)
                            for (String s : possibleMap.get(recordList.get(m))) {
                                for(int j=1;j<=9;j++){
                                    String sudokuKey="" + (char) (96 + i) + j;
                                    if(sudokuKey.equals(recordList.get(m))||sudokuKey.equals(recordList.get(n))){
                                        continue;
                                    }else{
                                        if(possibleMap.get(sudokuKey)!=null&&possibleMap.get(sudokuKey).remove(s)){
                                            twoChangeFlag++;
                                            twoChange++;
                                            change++;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        //判断垂直方向
        for(int i=1;i<=9;i++){
            List<String> recordList=new ArrayList<>();
            for(int j=1;j<=9;j++){
                String st="" + (char) (96 + j) + i;
                Set<String> possibleSet = possibleMap.get(st);
                if (possibleSet!=null&&possibleSet.size()==2){
                    recordList.add(st);
                }
            }
            if(recordList.size()>=2){
                //判断某一"域"中存在2个possibleSet只有2个元素,并且这2个possibleSet相同
                for(int m=1;m<=recordList.size()-1;m++){
                    for(int n=0;n<m;n++){
                        if(possibleMap.get(recordList.get(m)).equals(possibleMap.get(recordList.get(n)))){
                            //存在,删除该"域"其它possibleSet中的这2个元素
                            for (String s : possibleMap.get(recordList.get(m))) {
                                for(int j=1;j<=9;j++){
                                    String st="" + (char) (96 + j) + i;
                                    if(st.equals(recordList.get(m))||st.equals(recordList.get(n))){
                                        continue;
                                    }else{
                                        if(possibleMap.get(st)!=null&&possibleMap.get(st).remove(s)){
                                            //对possibleMap进行过改变
                                            twoChangeFlag++;
                                            twoChange++;
                                            change++;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        //判断小九宫格
        for(int i=1;i<=7;i+=3){
            for(int j=1;j<=7;j+=3){
                List<String> recordList=new ArrayList<>();
                for(int m=i;m<=i+2;m++){
                    for(int n=j;n<=j+2;n++){
                        //找到某一域中只有2个元素的possibleSet
                        String sudokuKey="" + (char) (96 + m) + n;
                        Set<String> possibleSet = possibleMap.get(sudokuKey);
                        if (possibleSet!=null&&possibleSet.size()==2){
                            recordList.add(sudokuKey);
                        }
                    }
                }
                if(recordList.size()>=2){
                    //判断某一"域"中存在2个possibleSet只有2个元素,并且这2个possibleSet相同
                    for(int x=1;x<=recordList.size()-1;x++){
                        for(int y=0;y<x;y++){
                            if(possibleMap.get(recordList.get(x)).equals(possibleMap.get(recordList.get(y)))){
                                for (String s : possibleMap.get(recordList.get(x))) {
                                    for(int m=i;m<=i+2;m++){
                                        for(int n=j;n<=j+2;n++){
                                            //找到某一域中只有2个元素的possibleSet
                                            String sudokuKey="" + (char) (96 + m) + n;
                                            if(sudokuKey.equals(recordList.get(x))||sudokuKey.equals(recordList.get(y))){
                                                continue;
                                            }else{
                                                if(possibleMap.get(sudokuKey)!=null&&possibleMap.get(sudokuKey).remove(s)){
                                                    //对possibleMap进行过改变
                                                    twoChangeFlag++;
                                                    twoChange++;
                                                    change++;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (twoChangeFlag!=0){
            //调用removeSingle,去掉变动后的possibleMap中possibleSet为1个的情况
            removeSinglePossible();
            if(sudokuJudge()){
                return removeDoublePossibleSituation1();//递归
            }else {
                return false;
            }
        }else{
            return true;
        }
    }
    /**
     * 取2个随机数,在某一域中只有2个possibleSet包含这2个随机数,则将这2个元素赋值给这2个possibleSet
     * @param sudokuParams
     * @return
     */
    public boolean removeDoublePossibleSituation2(){
        int twoChangeFlag=0;
        for (int i = 2; i <= 9; i++) {
            for (int j = 1; j < i; j++) {
                //取2个随机数
                String randomNumber1 = String.valueOf(i);
                String randomNumber2 = String.valueOf(j);
                //判断水平方向
                for (int m = 1; m <= 9; m++) {
                    //用于记录并"否定"该域
                    int negative=0;
                    Set<String> horizontalSet = new HashSet<String>();
                    for (int n = 1; n <= 9; n++) {
                        Set<String> possibleSet = possibleMap.get("" + (char) (96 + m) + n);
                        if (null != possibleSet) {
                            int horizonFlag = 0;
                            for (String s : possibleSet) {
                                if (s.equals(randomNumber1) || s.equals(randomNumber2)) {
                                    horizonFlag++;
                                }
                            }
                            if(horizonFlag==1){
                                //证明该域中有一个possibleSet中只包含randomNumber1或randomNumber2中的一个,那么该域不符合要求
                                negative++;
                                break;//中断循环,提高代码效率
                            }
                            if (horizonFlag == 2 ) {
                                //证明水平方向的该possibleSet包含这2个随机数
                                horizontalSet.add("" + (char) (96 + m) + n);
                            }
                        }
                    }
                    //水平方向有且仅有2个possibleSet拥有这2个不相同的元素,则将这2个possibleSet中的"可能值"重置为这2个不相同的元素
                    if (horizontalSet.size() == 2 && negative == 0) {
                        for (String sudokuKey : horizontalSet) {
                            if (possibleMap.get(sudokuKey).size() != 2) {//为了保证possibleMap的改变是"有效改变"
                                Set<String> twoSizeSet = new HashSet<String>();
                                twoSizeSet.add(randomNumber1);
                                twoSizeSet.add(randomNumber2);
                                possibleMap.put(sudokuKey, twoSizeSet);
                                twoChangeFlag++;
                                twoChange++;
                                change++;
                            }
                        }
                    }
                }
                //判断垂直方向
                for (int m = 1; m <= 9; m++) {
                    //用于记录并"否定"该域
                    int negative=0;
                    Set<String> verticalSet = new HashSet<String>();
                    for (int n = 1; n <= 9; n++) {
                        Set<String> possibleSet = possibleMap.get("" + (char) (96 + n) + m);
                        if (null != possibleSet) {
                            int verticalFlag = 0;
                            for (String s : possibleSet) {
                                if (s.equals(randomNumber1) || s.equals(randomNumber2)) {
                                    verticalFlag++;
                                }
                            }
                            if(verticalFlag==1){
                                //证明该域中有一个possibleSet中只包含randomNumber1或randomNumber2中的一个,那么该域不符合要求
                                negative++;
                                break;//中断循环,提高代码效率
                            }
                            if (verticalFlag == 2) {
                                //证明垂直方向的该possibleSet包含这2个随机数
                                verticalSet.add("" + (char) (96 + n) + m);
                            }
                        }
                    }
                    //垂直方向有且仅有2个possibleSet拥有这2个不相同的元素,则将这2个possibleSet中的"可能值"重置为这2个不相同的元素
                    if (verticalSet.size() == 2 && negative == 0) {
                        for (String s : verticalSet) {
                            if (possibleMap.get(s).size() != 2) {//为了使flag++代表的是"有效自增"
                                Set<String> twoSizeSet = new HashSet<String>();
                                twoSizeSet.add(randomNumber1);
                                twoSizeSet.add(randomNumber2);
                                possibleMap.put(s, twoSizeSet);
                                twoChangeFlag++;
                                twoChange++;
                                change++;
                            }
                        }
                    }
                }
                //判断小九宫格
                for(int x=1;x<=7;x+=3){
                    for(int y=1;y<=7;y+=3){
                        //用于记录并"否定"该域
                        int negative=0;
                        Set<String> nineSet = new HashSet<String>();
                        for(int m=i;m<=i+2;m++){
                            for(int n=j;n<=j+2;n++){
                                Set<String> possibleSet = possibleMap.get("" + (char) (96 + m) + n);
                                if (null != possibleSet) {
                                    int nineFlag = 0;
                                    for (String s : possibleSet) {
                                        if (s.equals(randomNumber1) || s.equals(randomNumber2)) {
                                            nineFlag++;
                                        }
                                    }
                                    if(nineFlag==1){
                                        //证明该域中有一个possibleSet中只包含randomNumber1或randomNumber2中的一个,那么该域不符合要求
                                        negative++;
                                        break;//中断循环,提高代码效率
                                    }
                                    if (nineFlag == 2) {
                                        //证明小九宫格的该possibleSet包含这2个不相同的元素
                                        nineSet.add("" + (char) (96 + m) + n);
                                    }
                                }
                            }
                        }
                        //小九宫格有且仅有2个possibleSet拥有这2个不相同的元素,则将这2个possibleSet中的"可能值"重置为这2个不相同的元素
                        if (nineSet.size() == 2 && negative == 0) {
                            for (String sudokuKey : nineSet) {
                                if(possibleMap.get(sudokuKey).size()!=2){//为了使flag++代表的是"有效自增"
                                    Set<String> twoSizeSet=new HashSet<String>();
                                    twoSizeSet.add(randomNumber1);
                                    twoSizeSet.add(randomNumber2);
                                    possibleMap.put(sudokuKey, twoSizeSet);
                                    twoChangeFlag++;
                                    twoChange++;
                                    change++;
                                }
                            }
                        }
                    }
                }
            }
        }
        if (twoChangeFlag!=0){
            //调用removeSingle,去掉变动后的possibleMap中possibleSet为1个的情况
            removeSinglePossible();
            if(sudokuJudge()){
                return removeDoublePossibleSituation2();//递归
            }else {
                return false;
            }
        }else{
            return true;
        }
    }
    //===============================================操作possibleMap================================================================
    /**
     * 只要sudokuMap有变动,就要对possibleMap进行处理
     * @param sudokuKey
     * @param value
     */
    public void removePossibleMap(String sudokuKey, String value){
        //首先删除对应的possibleSet
        possibleMap.remove(sudokuKey);
        //然后删除其它possibleSet中的value
        String horizontalOrdinate= sudokuKey.substring(0,1);
        String ordinate= sudokuKey.substring(1);
        //删除水平方向其它possibleSet中的value
        for(int i=1;i<=9;i++){
            Set<String> possibleSet = possibleMap.get(horizontalOrdinate + i);
            if(possibleSet!=null){
                possibleSet.remove(value);
            }
        }
        //删除垂直方向其它possibleSet中的value
        for(int i=1;i<=9;i++){
            Set<String> possibleSet = possibleMap.get((char) (96 + i) + ordinate);
            if(possibleSet!=null){
                possibleSet.remove(value);
            }
        }
        //删除小九宫格其它possibleSet中的value
        if(horizontalOrdinate.equals("a")||horizontalOrdinate.equals("b")||horizontalOrdinate.equals("c")){
            if(Integer.valueOf(ordinate)>=1&&Integer.valueOf(ordinate)<=3){
                for(int i=1;i<=3;i++){
                    for(int j=1;j<=3;j++){
                        Set<String> possibleSet = possibleMap.get("" + (char) (96 + i) + j);
                        if(possibleSet!=null){
                            possibleSet.remove(value);
                        }
                    }
                }
            }else if(Integer.valueOf(ordinate)>=4&&Integer.valueOf(ordinate)<=6){
                for(int i=1;i<=3;i++){
                    for(int j=4;j<=6;j++){
                        Set<String> possibleSet = possibleMap.get("" + (char) (96 + i) + j);
                        if(possibleSet!=null){
                            possibleSet.remove(value);
                        }
                    }
                }
            }else{
                for(int i=1;i<=3;i++){
                    for(int j=7;j<=9;j++){
                        Set<String> possibleSet = possibleMap.get("" + (char) (96 + i) + j);
                        if(possibleSet!=null){
                            possibleSet.remove(value);
                        }
                    }
                }
            }
        }else if(horizontalOrdinate.equals("d")||horizontalOrdinate.equals("e")||horizontalOrdinate.equals("f")){
            if(Integer.valueOf(ordinate)>=1&&Integer.valueOf(ordinate)<=3){
                for(int i=4;i<=6;i++){
                    for(int j=1;j<=3;j++){
                        Set<String> possibleSet = possibleMap.get("" + (char) (96 + i) + j);
                        if(possibleSet!=null){
                            possibleSet.remove(value);
                        }
                    }
                }
            }else if(Integer.valueOf(ordinate)>=4&&Integer.valueOf(ordinate)<=6){
                for(int i=4;i<=6;i++){
                    for(int j=4;j<=6;j++){
                        Set<String> possibleSet = possibleMap.get("" + (char) (96 + i) + j);
                        if(possibleSet!=null){
                            possibleSet.remove(value);
                        }
                    }
                }
            }else{
                for(int i=4;i<=6;i++){
                    for(int j=7;j<=9;j++){
                        Set<String> possibleSet = possibleMap.get("" + (char) (96 + i) + j);
                        if(possibleSet!=null){
                            possibleSet.remove(value);
                        }
                    }
                }
            }
        }else {
            if(Integer.valueOf(ordinate)>=1&&Integer.valueOf(ordinate)<=3){
                for(int i=7;i<=9;i++){
                    for(int j=1;j<=3;j++){
                        Set<String> possibleSet = possibleMap.get("" + (char) (96 + i) + j);
                        if(possibleSet!=null){
                            possibleSet.remove(value);
                        }
                    }
                }
            }else if(Integer.valueOf(ordinate)>=4&&Integer.valueOf(ordinate)<=6){
                for(int i=7;i<=9;i++){
                    for(int j=4;j<=6;j++){
                        Set<String> possibleSet = possibleMap.get("" + (char) (96 + i) + j);
                        if(possibleSet!=null){
                            possibleSet.remove(value);
                        }
                    }
                }
            }else{
                for(int i=7;i<=9;i++){
                    for(int j=7;j<=9;j++){
                        Set<String> possibleSet = possibleMap.get("" + (char) (96 + i) + j);
                        if(possibleSet!=null){
                            possibleSet.remove(value);
                        }
                    }
                }
            }
        }
    }

    //======================================================假设=========================================================

    /**
     * 作出假设
     * @param sudokuMap
     * @return
     */
    public boolean suppose(){
        //作一个sudokuMap的克隆sudokuMapCopy
        Map<String, String> sudokuMapCopy=new HashMap<>();
        for(int m=1;m<=9;m++){
            for(int n=1;n<=9;n++){
                String sudokuKey=""+(char)(96+m)+n;
                sudokuMapCopy.put(sudokuKey, sudokuMap.get(sudokuKey));
            }
        }
        //作一个possibleMap的克隆possibleMapCopy
        Map<String,Set<String>> possibleMapCopy=new HashMap<>();
        for(int m=1;m<=9;m++){
            for(int n=1;n<=9;n++){
                String sudokuKey=""+(char)(96+m)+n;
                if(possibleMap.get(sudokuKey)!=null){
                    Set<String> possibleSetCopy=new HashSet<>();
                    for (String s : possibleMap.get(sudokuKey)) {
                        possibleSetCopy.add(s);
                    }
                    possibleMapCopy.put(sudokuKey,possibleSetCopy);
                }
            }
        }
        //首先判断数独有没有被解开
        if(CollectionUtils.isEmpty(possibleMap)){
            //数独已经被解开
            return true;
        }else{
            //数独还没有被解开
            //将possibleMap中所有长度为2的possibleSet的坐标存入twoLengthSupposeList中
            List<String> twoLengthSupposeList=new ArrayList<>();
            for(int i=1;i<=9;i++) {
                for (int j = 1; j <= 9; j++) {
                    String sudokuKey=""+(char)(96+i)+j;
                    Set<String> possibleSet = possibleMap.get(sudokuKey);
                    if (possibleSet!=null&&possibleSet.size()==2){
                        //supposePossibleMap.put(st,possibleSet);
                        twoLengthSupposeList.add(sudokuKey);
                    }
                }
            }
            if(twoLengthSupposeList.size()==0){
                //此时说明possibleMap中没有一个possibleSet的长度为2
                //就开始假设possibleMap中长度为3的possibleSet  (以后再优化)
                return true;
            }else if(twoLengthSupposeList.size()==1){
                //一层假设
                String sudokuKey=twoLengthSupposeList.get(0);
                for (String sudokuValue : possibleMap.get(sudokuKey)) {
                    sudokuMap.put(sudokuKey,sudokuValue);
                    removePossibleMap(sudokuKey,sudokuValue);
                    //对假设后的结果进行判断
                    if(sudokuJudge()&&fillBlank()&&CollectionUtils.isEmpty(possibleMap)){
                        //假设过程中没有出错,并且解开了数独
                        return true;
                    }else{
                        //假设过程中出错,或者是没有解开数独,还原数据(注意还原数据的方式)
                        //还原sudokuMap
                        for(int m=1;m<=9;m++){
                            for(int n=1;n<=9;n++){
                                String st=""+(char)(96+m)+n;
                                sudokuMap.put(st,sudokuMapCopy.get(st));
                            }
                        }
                        //还原possibleMap
                        for(int m=1;m<=9;m++){
                            for(int n=1;n<=9;n++){
                                String sudokuKey2=""+(char)(96+m)+n;
                                if(possibleMapCopy.get(sudokuKey2)!=null){
                                    Set<String> copyPossibleSet=new HashSet<>();
                                    for (String s : possibleMapCopy.get(sudokuKey2)) {
                                        copyPossibleSet.add(s);
                                    }
                                    possibleMap.put(sudokuKey2,copyPossibleSet);
                                }
                            }
                        }
                    }
                }
            }else if(twoLengthSupposeList.size()>=2){
                //2层假设
                for(int i=1;i<=twoLengthSupposeList.size()-1;i++){
                    for(int j=0;j<i;j++){
                        String sudokuKey1=twoLengthSupposeList.get(i);
                        String sudokuKey2=twoLengthSupposeList.get(j);
                        //注意下面这2步并不是多余的,而是必须要这样转
                        Set<String> possibleSet1=new HashSet<>();
                        Set<String> possibleSet2=new HashSet<>();
                        for (String s : possibleMap.get(sudokuKey1)) {
                            possibleSet1.add(s);
                        }
                        for (String s : possibleMap.get(sudokuKey2)) {
                            possibleSet2.add(s);
                        }
                        for (String sudokuValue1 : possibleSet1) {
                            for (String sudokuValue2 : possibleSet2) {
                                sudokuMap.put(sudokuKey1,sudokuValue1);
                                removePossibleMap(sudokuKey1,sudokuValue1);
                                sudokuMap.put(sudokuKey2,sudokuValue2);
                                removePossibleMap(sudokuKey2,sudokuValue2);
                                //对假设后的结果进行判断
                                if(sudokuJudge()&&fillBlank()&&CollectionUtils.isEmpty(possibleMap)){
                                    //假设过程中没有出错,并且解开了数独
                                    return true;
                                }else{
                                    //假设过程中出错,或者是没有解开数独,还原数据(注意还原数据的方式)
                                    //还原sudokuParams
                                    for(int m=1;m<=9;m++){
                                        for(int n=1;n<=9;n++){
                                            String st=""+(char)(96+m)+n;
                                            sudokuMap.put(st,sudokuMapCopy.get(st));
                                        }
                                    }
                                    //还原possibleMap
                                    for(int m=1;m<=9;m++){
                                        for(int n=1;n<=9;n++){
                                            String st=""+(char)(96+m)+n;
                                            if(possibleMapCopy.get(st)!=null){
                                                Set<String> copyPossibleSet=new HashSet<>();
                                                for (String s : possibleMapCopy.get(st)) {
                                                    copyPossibleSet.add(s);
                                                }
                                                possibleMap.put(st,copyPossibleSet);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }else if(twoLengthSupposeList.size()>=3){
                //3层假设
            }else if(twoLengthSupposeList.size()>=4){
                //4层假设
            }
        }
        return true;
    }

    //====================================================数据转换===========================================================

    /**
     * 将类中的数独值"填"到一个Map中
     * @param sudoku
     * @return
     */
    public Map<String,String> sudoku2SudokuMap(Sudoku sudoku){
        Map<String,String> sudokuMap=new HashMap<>();
        sudokuMap.put("a1",sudoku.getA1());
        sudokuMap.put("a2",sudoku.getA2());
        sudokuMap.put("a3",sudoku.getA3());
        sudokuMap.put("a4",sudoku.getA4());
        sudokuMap.put("a5",sudoku.getA5());
        sudokuMap.put("a6",sudoku.getA6());
        sudokuMap.put("a7",sudoku.getA7());
        sudokuMap.put("a8",sudoku.getA8());
        sudokuMap.put("a9",sudoku.getA9());
        sudokuMap.put("b1",sudoku.getB1());
        sudokuMap.put("b2",sudoku.getB2());
        sudokuMap.put("b3",sudoku.getB3());
        sudokuMap.put("b4",sudoku.getB4());
        sudokuMap.put("b5",sudoku.getB5());
        sudokuMap.put("b6",sudoku.getB6());
        sudokuMap.put("b7",sudoku.getB7());
        sudokuMap.put("b8",sudoku.getB8());
        sudokuMap.put("b9",sudoku.getB9());
        sudokuMap.put("c1",sudoku.getC1());
        sudokuMap.put("c2",sudoku.getC2());
        sudokuMap.put("c3",sudoku.getC3());
        sudokuMap.put("c4",sudoku.getC4());
        sudokuMap.put("c5",sudoku.getC5());
        sudokuMap.put("c6",sudoku.getC6());
        sudokuMap.put("c7",sudoku.getC7());
        sudokuMap.put("c8",sudoku.getC8());
        sudokuMap.put("c9",sudoku.getC9());
        sudokuMap.put("d1",sudoku.getD1());
        sudokuMap.put("d2",sudoku.getD2());
        sudokuMap.put("d3",sudoku.getD3());
        sudokuMap.put("d4",sudoku.getD4());
        sudokuMap.put("d5",sudoku.getD5());
        sudokuMap.put("d6",sudoku.getD6());
        sudokuMap.put("d7",sudoku.getD7());
        sudokuMap.put("d8",sudoku.getD8());
        sudokuMap.put("d9",sudoku.getD9());
        sudokuMap.put("e1",sudoku.getE1());
        sudokuMap.put("e2",sudoku.getE2());
        sudokuMap.put("e3",sudoku.getE3());
        sudokuMap.put("e4",sudoku.getE4());
        sudokuMap.put("e5",sudoku.getE5());
        sudokuMap.put("e6",sudoku.getE6());
        sudokuMap.put("e7",sudoku.getE7());
        sudokuMap.put("e8",sudoku.getE8());
        sudokuMap.put("e9",sudoku.getE9());
        sudokuMap.put("f1",sudoku.getF1());
        sudokuMap.put("f2",sudoku.getF2());
        sudokuMap.put("f3",sudoku.getF3());
        sudokuMap.put("f4",sudoku.getF4());
        sudokuMap.put("f5",sudoku.getF5());
        sudokuMap.put("f6",sudoku.getF6());
        sudokuMap.put("f7",sudoku.getF7());
        sudokuMap.put("f8",sudoku.getF8());
        sudokuMap.put("f9",sudoku.getF9());
        sudokuMap.put("g1",sudoku.getG1());
        sudokuMap.put("g2",sudoku.getG2());
        sudokuMap.put("g3",sudoku.getG3());
        sudokuMap.put("g4",sudoku.getG4());
        sudokuMap.put("g5",sudoku.getG5());
        sudokuMap.put("g6",sudoku.getG6());
        sudokuMap.put("g7",sudoku.getG7());
        sudokuMap.put("g8",sudoku.getG8());
        sudokuMap.put("g9",sudoku.getG9());
        sudokuMap.put("h1",sudoku.getH1());
        sudokuMap.put("h2",sudoku.getH2());
        sudokuMap.put("h3",sudoku.getH3());
        sudokuMap.put("h4",sudoku.getH4());
        sudokuMap.put("h5",sudoku.getH5());
        sudokuMap.put("h6",sudoku.getH6());
        sudokuMap.put("h7",sudoku.getH7());
        sudokuMap.put("h8",sudoku.getH8());
        sudokuMap.put("h9",sudoku.getH9());
        sudokuMap.put("i1",sudoku.getI1());
        sudokuMap.put("i2",sudoku.getI2());
        sudokuMap.put("i3",sudoku.getI3());
        sudokuMap.put("i4",sudoku.getI4());
        sudokuMap.put("i5",sudoku.getI5());
        sudokuMap.put("i6",sudoku.getI6());
        sudokuMap.put("i7",sudoku.getI7());
        sudokuMap.put("i8",sudoku.getI8());
        sudokuMap.put("i9",sudoku.getI9());
        return sudokuMap;
    }

    /**
     * 根据sudokuMap,得到其它空格的可能值possibleMap
     * @param sudokuMap
     * @return
     */
    public Map<String, Set<String>> sudokuMap2PossibleMap(Map<String, String> sudokuMap){
        Map<String,Set<String>> possibleMap=new HashMap<String,Set<String>>();
        for(int i=1;i<=9;i++) {
            for (int j = 1; j <= 9; j++) {
                if(null==sudokuMap.get(""+(char)(96+i)+j)) {
                    //创建一个包含1~9元素的set集合
                    Set<String> set = new HashSet<String>();
                    for (int k = 1; k <= 9; k++) {
                        set.add(String.valueOf(k));
                    }
                    //排除水平方向
                    for(int k=1;k<=9;k++){
                        String str=sudokuMap.get(""+(char)(96+i)+k);
                        if(null==str){
                            continue;
                        }else{
                            set.remove(str);
                        }
                    }
                    //排除垂直方向
                    for(int k=1;k<=9;k++){
                        String str=sudokuMap.get(""+(char)(96+k)+j);
                        if(null==str){
                            continue;
                        }else{
                            set.remove(String.valueOf(str));
                        }
                    }
                    //排除小九宫格
                    if(i>=1&&i<=3){
                        if(j>=1&&j<=3){
                            for(int m=1;m<=3;m++){
                                for(int n=1;n<=3;n++){
                                    String in=sudokuMap.get(""+(char)(96+m)+n);
                                    if(null==in){
                                        continue;
                                    }else{
                                        set.remove(String.valueOf(in));
                                    }
                                }
                            }
                        }else if(j>=4&&j<=6){
                            for(int m=1;m<=3;m++){
                                for(int n=4;n<=6;n++){
                                    String in=sudokuMap.get(""+(char)(96+m)+n);
                                    if(null==in){
                                        continue;
                                    }else{
                                        set.remove(String.valueOf(in));
                                    }
                                }
                            }
                        }else if(j>=7&&j<=9){
                            for(int m=1;m<=3;m++){
                                for(int n=7;n<=9;n++){
                                    String in=sudokuMap.get(""+(char)(96+m)+n);
                                    if(null==in){
                                        continue;
                                    }else{
                                        set.remove(String.valueOf(in));
                                    }
                                }
                            }
                        }
                    }else if(i>=4&&i<=6){
                        if(j>=1&&j<=3){
                            for(int m=4;m<=6;m++){
                                for(int n=1;n<=3;n++){
                                    String in=sudokuMap.get(""+(char)(96+m)+n);
                                    if(null==in){
                                        continue;
                                    }else{
                                        set.remove(String.valueOf(in));
                                    }
                                }
                            }
                        }else if(j>=4&&j<=6){
                            for(int m=4;m<=6;m++){
                                for(int n=4;n<=6;n++){
                                    String in=sudokuMap.get(""+(char)(96+m)+n);
                                    if(null==in){
                                        continue;
                                    }else{
                                        set.remove(String.valueOf(in));
                                    }
                                }
                            }
                        }else if(j>=7&&j<=9){
                            for(int m=4;m<=6;m++){
                                for(int n=7;n<=9;n++){
                                    String in=sudokuMap.get(""+(char)(96+m)+n);
                                    if(null==in){
                                        continue;
                                    }else{
                                        set.remove(String.valueOf(in));
                                    }
                                }
                            }
                        }
                    }else if(i>=7&&i<=9){
                        if(j>=1&&j<=3){
                            for(int m=7;m<=9;m++){
                                for(int n=1;n<=3;n++){
                                    String in=sudokuMap.get(""+(char)(96+m)+n);
                                    if(null==in){
                                        continue;
                                    }else{
                                        set.remove(String.valueOf(in));
                                    }
                                }
                            }
                        }else if(j>=4&&j<=6){
                            for(int m=7;m<=9;m++){
                                for(int n=4;n<=6;n++){
                                    String in=sudokuMap.get(""+(char)(96+m)+n);
                                    if(null==in){
                                        continue;
                                    }else{
                                        set.remove(String.valueOf(in));
                                    }
                                }
                            }
                        }else if(j>=7&&j<=9){
                            for(int m=7;m<=9;m++){
                                for(int n=7;n<=9;n++){
                                    String in=sudokuMap.get(""+(char)(96+m)+n);
                                    if(null==in){
                                        continue;
                                    }else{
                                        set.remove(String.valueOf(in));
                                    }
                                }
                            }
                        }
                    }
                    possibleMap.put(""+(char)(96+i)+j,set);
                }
            }
        }
        return possibleMap;
    }

    public HashMap sudokuMap2ResponseParam(Map<String,String> sudokuMap){
        HashMap<String, HashMap<String, String>> responseParam = new HashMap<>();
        HashMap<String, String> a = new HashMap<>();
        a.put("a1",sudokuMap.get("a1"));
        a.put("a2",sudokuMap.get("a2"));
        a.put("a3",sudokuMap.get("a3"));
        a.put("a4",sudokuMap.get("a4"));
        a.put("a5",sudokuMap.get("a5"));
        a.put("a6",sudokuMap.get("a6"));
        a.put("a7",sudokuMap.get("a7"));
        a.put("a8",sudokuMap.get("a8"));
        a.put("a9",sudokuMap.get("a9"));
        HashMap<String, String> b = new HashMap<>();
        b.put("b1",sudokuMap.get("b1"));
        b.put("b2",sudokuMap.get("b2"));
        b.put("b3",sudokuMap.get("b3"));
        b.put("b4",sudokuMap.get("b4"));
        b.put("b5",sudokuMap.get("b5"));
        b.put("b6",sudokuMap.get("b6"));
        b.put("b7",sudokuMap.get("b7"));
        b.put("b8",sudokuMap.get("b8"));
        b.put("b9",sudokuMap.get("b9"));
        HashMap<String, String> c = new HashMap<>();
        c.put("c1",sudokuMap.get("c1"));
        c.put("c2",sudokuMap.get("c2"));
        c.put("c3",sudokuMap.get("c3"));
        c.put("c4",sudokuMap.get("c4"));
        c.put("c5",sudokuMap.get("c5"));
        c.put("c6",sudokuMap.get("c6"));
        c.put("c7",sudokuMap.get("c7"));
        c.put("c8",sudokuMap.get("c8"));
        c.put("c9",sudokuMap.get("c9"));
        HashMap<String, String> d = new HashMap<>();
        d.put("d1",sudokuMap.get("d1"));
        d.put("d2",sudokuMap.get("d2"));
        d.put("d3",sudokuMap.get("d3"));
        d.put("d4",sudokuMap.get("d4"));
        d.put("d5",sudokuMap.get("d5"));
        d.put("d6",sudokuMap.get("d6"));
        d.put("d7",sudokuMap.get("d7"));
        d.put("d8",sudokuMap.get("d8"));
        d.put("d9",sudokuMap.get("d9"));
        HashMap<String, String> e = new HashMap<>();
        e.put("e1",sudokuMap.get("e1"));
        e.put("e2",sudokuMap.get("e2"));
        e.put("e3",sudokuMap.get("e3"));
        e.put("e4",sudokuMap.get("e4"));
        e.put("e5",sudokuMap.get("e5"));
        e.put("e6",sudokuMap.get("e6"));
        e.put("e7",sudokuMap.get("e7"));
        e.put("e8",sudokuMap.get("e8"));
        e.put("e9",sudokuMap.get("e9"));
        HashMap<String, String> f = new HashMap<>();
        f.put("f1",sudokuMap.get("f1"));
        f.put("f2",sudokuMap.get("f2"));
        f.put("f3",sudokuMap.get("f3"));
        f.put("f4",sudokuMap.get("f4"));
        f.put("f5",sudokuMap.get("f5"));
        f.put("f6",sudokuMap.get("f6"));
        f.put("f7",sudokuMap.get("f7"));
        f.put("f8",sudokuMap.get("f8"));
        f.put("f9",sudokuMap.get("f9"));
        HashMap<String, String> g = new HashMap<>();
        g.put("g1",sudokuMap.get("g1"));
        g.put("g2",sudokuMap.get("g2"));
        g.put("g3",sudokuMap.get("g3"));
        g.put("g4",sudokuMap.get("g4"));
        g.put("g5",sudokuMap.get("g5"));
        g.put("g6",sudokuMap.get("g6"));
        g.put("g7",sudokuMap.get("g7"));
        g.put("g8",sudokuMap.get("g8"));
        g.put("g9",sudokuMap.get("g9"));
        HashMap<String, String> h = new HashMap<>();
        h.put("h1",sudokuMap.get("h1"));
        h.put("h2",sudokuMap.get("h2"));
        h.put("h3",sudokuMap.get("h3"));
        h.put("h4",sudokuMap.get("h4"));
        h.put("h5",sudokuMap.get("h5"));
        h.put("h6",sudokuMap.get("h6"));
        h.put("h7",sudokuMap.get("h7"));
        h.put("h8",sudokuMap.get("h8"));
        h.put("h9",sudokuMap.get("h9"));
        HashMap<String, String> i = new HashMap<>();
        i.put("i1",sudokuMap.get("i1"));
        i.put("i2",sudokuMap.get("i2"));
        i.put("i3",sudokuMap.get("i3"));
        i.put("i4",sudokuMap.get("i4"));
        i.put("i5",sudokuMap.get("i5"));
        i.put("i6",sudokuMap.get("i6"));
        i.put("i7",sudokuMap.get("i7"));
        i.put("i8",sudokuMap.get("i8"));
        i.put("i9",sudokuMap.get("i9"));

        responseParam.put("a",a);
        responseParam.put("b",b);
        responseParam.put("c",c);
        responseParam.put("d",d);
        responseParam.put("e",e);
        responseParam.put("f",f);
        responseParam.put("g",g);
        responseParam.put("h",h);
        responseParam.put("i",i);

        return responseParam;
    }

    public HashMap sudoku2ResponseParam(Sudoku sudoku){
        HashMap responseParam = new HashMap<>();
        HashMap<String, String> a = new HashMap<>();
        a.put("a1",sudoku.getA1());
        a.put("a2",sudoku.getA2());
        a.put("a3",sudoku.getA3());
        a.put("a4",sudoku.getA4());
        a.put("a5",sudoku.getA5());
        a.put("a6",sudoku.getA6());
        a.put("a7",sudoku.getA7());
        a.put("a8",sudoku.getA8());
        a.put("a9",sudoku.getA9());
        HashMap<String, String> b = new HashMap<>();
        b.put("b1",sudoku.getB1());
        b.put("b2",sudoku.getB2());
        b.put("b3",sudoku.getB3());
        b.put("b4",sudoku.getB4());
        b.put("b5",sudoku.getB5());
        b.put("b6",sudoku.getB6());
        b.put("b7",sudoku.getB7());
        b.put("b8",sudoku.getB8());
        b.put("b9",sudoku.getB9());
        HashMap<String, String> c = new HashMap<>();
        c.put("c1",sudoku.getC1());
        c.put("c2",sudoku.getC2());
        c.put("c3",sudoku.getC3());
        c.put("c4",sudoku.getC4());
        c.put("c5",sudoku.getC5());
        c.put("c6",sudoku.getC6());
        c.put("c7",sudoku.getC7());
        c.put("c8",sudoku.getC8());
        c.put("c9",sudoku.getC9());
        HashMap<String, String> d = new HashMap<>();
        d.put("d1",sudoku.getD1());
        d.put("d2",sudoku.getD2());
        d.put("d3",sudoku.getD3());
        d.put("d4",sudoku.getD4());
        d.put("d5",sudoku.getD5());
        d.put("d6",sudoku.getD6());
        d.put("d7",sudoku.getD7());
        d.put("d8",sudoku.getD8());
        d.put("d9",sudoku.getD9());
        HashMap<String, String> e = new HashMap<>();
        e.put("e1",sudoku.getE1());
        e.put("e2",sudoku.getE2());
        e.put("e3",sudoku.getE3());
        e.put("e4",sudoku.getE4());
        e.put("e5",sudoku.getE5());
        e.put("e6",sudoku.getE6());
        e.put("e7",sudoku.getE7());
        e.put("e8",sudoku.getE8());
        e.put("e9",sudoku.getE9());
        HashMap<String, String> f = new HashMap<>();
        f.put("f1",sudoku.getF1());
        f.put("f2",sudoku.getF2());
        f.put("f3",sudoku.getF3());
        f.put("f4",sudoku.getF4());
        f.put("f5",sudoku.getF5());
        f.put("f6",sudoku.getF6());
        f.put("f7",sudoku.getF7());
        f.put("f8",sudoku.getF8());
        f.put("f9",sudoku.getF9());
        HashMap<String, String> g = new HashMap<>();
        g.put("g1",sudoku.getG1());
        g.put("g2",sudoku.getG2());
        g.put("g3",sudoku.getG3());
        g.put("g4",sudoku.getG4());
        g.put("g5",sudoku.getG5());
        g.put("g6",sudoku.getG6());
        g.put("g7",sudoku.getG7());
        g.put("g8",sudoku.getG8());
        g.put("g9",sudoku.getG9());
        HashMap<String, String> h = new HashMap<>();
        h.put("h1",sudoku.getH1());
        h.put("h2",sudoku.getH2());
        h.put("h3",sudoku.getH3());
        h.put("h4",sudoku.getH4());
        h.put("h5",sudoku.getH5());
        h.put("h6",sudoku.getH6());
        h.put("h7",sudoku.getH7());
        h.put("h8",sudoku.getH8());
        h.put("h9",sudoku.getH9());
        HashMap<String, String> i = new HashMap<>();
        i.put("i1",sudoku.getI1());
        i.put("i2",sudoku.getI2());
        i.put("i3",sudoku.getI3());
        i.put("i4",sudoku.getI4());
        i.put("i5",sudoku.getI5());
        i.put("i6",sudoku.getI6());
        i.put("i7",sudoku.getI7());
        i.put("i8",sudoku.getI8());
        i.put("i9",sudoku.getI9());

        responseParam.put("a",a);
        responseParam.put("b",b);
        responseParam.put("c",c);
        responseParam.put("d",d);
        responseParam.put("e",e);
        responseParam.put("f",f);
        responseParam.put("g",g);
        responseParam.put("h",h);
        responseParam.put("i",i);

        return responseParam;
    }
}
