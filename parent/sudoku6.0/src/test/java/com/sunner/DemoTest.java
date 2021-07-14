package com.sunner;

import com.alibaba.fastjson.JSON;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DemoTest {

    private final static Logger log=Logger.getLogger(DemoTest.class);
    @Test
    public void abc(){


        Set<String> set1=new HashSet<>();
        Set<String> set2=new HashSet<>();
        set1.add("张三");
        set1.add("李四");
        set2.add("张三");
        set2.add("李四");

        System.out.println(CollectionUtils.isEmpty(set1));

        set1.remove("张三");
        set1.remove("李四");
        System.out.println(CollectionUtils.isEmpty(set1));
        set1=null;
        System.out.println(CollectionUtils.isEmpty(set1));


    }

    @Test
    public void exceptionTest(){
        try{
            int a=1/0;
        }catch(Exception e){
            log.error(e);
            System.out.println("=======================================================================================================");
            log.error("出错了",e);
        }
    }

    @Test
    public void Test(){
        log.info(JSON.toJSONString("ALSDFJKLASDF"));
    }
}
