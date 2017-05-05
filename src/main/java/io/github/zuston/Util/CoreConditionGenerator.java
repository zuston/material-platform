package io.github.zuston.Util;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.QueryOperators;

import java.util.ArrayList;

import static io.github.zuston.Util.AnalyExpression.indexArr;

/**
 * Created by zuston on 17/5/2.
 */
public class CoreConditionGenerator {
    public static BasicDBObject coreContionGenertor(String formula,int flag){
        ArrayList<String> formualList = AnalyExpression.simpleAnaly(formula);
        BasicDBObject base = new BasicDBObject();
        if (formualList.size()>1){
            BasicDBList list = new BasicDBList();
            for (String oneFormula:formualList){
                list.add(simpleConditionGenertor(oneFormula,flag));
            }
            base.put("$or",list);
        }else{
            base = simpleConditionGenertor(formualList.get(0),flag);
        }
        return base;
    }

    private static BasicDBObject simpleConditionGenertor(String expression,int flag) {
        ArrayList<String> race = new ArrayList<String>();
        BasicDBObject condition = new BasicDBObject();

        race.addAll(RaceMapper.race);
        /**
         * 和的列表，其中又分bandGAP 单元素 族系元素
         * TODO: 17/3/27 单元素可能会报错
         */
        ArrayList<String> andList = indexArr(expression,'&');
        /**
         * 否的列表，其中分为族系元素
         */
        ArrayList<String> notList = indexArr(expression,'~');
        System.out.println(andList);
        if (andList.size()<=0){
            return null;
        }

        ArrayList<String> andShackList = new ArrayList<String>();
        ArrayList<String> andWaitList = new ArrayList<String>();

        for (String temp:andList){
            if (race.indexOf(temp)>-1){
                andWaitList.add(temp);
                continue;
            }

            if (temp.indexOf("=")>=0||temp.indexOf(">")>=0||temp.indexOf("<")>=0){
                conditionFormulaComponent(temp,condition);
                continue;
            }

            andShackList.add(temp);

        }


        BasicDBObject conditionChildren = new BasicDBObject();
        if (andShackList.size()>0){
            conditionChildren.append("$all",andShackList);
        }

        /**
         * TODO: 17/3/27 多组in,修改为多组
         */
        if (andWaitList.size()>0){
            ArrayList<String> elements = new ArrayList<String>();
            for (String key:andWaitList){
                elements.addAll(RaceMapper.hm.get(key));
            }
            conditionChildren.append("$in",elements);
        }

        if (notList.size()>0){
            for (String key:notList){
                conditionChildren.append(QueryOperators.NIN,RaceMapper.hm.get(key));
            }
        }
        if (notList.size()<=0&&andWaitList.size()>0){
            ArrayList<String> sb = (ArrayList<String>) RaceMapper.race.clone();
            for (String key:andWaitList){
                sb.remove(key);
            }
            ArrayList<String> elements = new ArrayList<String>();
            for (String value:sb){
                elements.addAll(RaceMapper.hm.get(value));
            }
            for (String value:andShackList){
                if (elements.indexOf(value)>-1){
                    elements.remove(value);
                }
            }
            conditionChildren.append(QueryOperators.NIN,elements);
        }
        if (conditionChildren.size()>0){
            condition.put("pymatgen_poscar.structure.sites.label",conditionChildren);
        }
        if (flag==1){
            condition.put("is_computed",1);
        }
        System.out.println();
        System.out.println("筛选条件语句:");
        System.out.println(condition);
        System.out.println();
        return condition;
    }


    private static void conditionFormulaComponent(String temp,BasicDBObject condition){
        int equalFlag = temp.split("=").length;
        int gtFlag = temp.split(">").length;
        int ltFlag = temp.split("<").length;
        int egtFlag = temp.split(">=").length;
        int lgtFlag = temp.split("<=").length;

        if (equalFlag>1&&egtFlag<2&&lgtFlag<2){
            String kk = temp.split("=")[0];
            String vv = temp.split("=")[1];

            String key = kk.substring(1,kk.length());
            String value = vv.substring(0,vv.length()-1);

            if (value.indexOf("-")>=0){
                String left = value.split("-")[0];
                String right = value.split("-")[1];
                key = KeyMapper.mapper.get(key);
                if (KeyMapper.DoubleList.contains(key)){
                    condition.put(key,new BasicDBObject("$gt",Integer.valueOf(left)).append("$lt",Integer.valueOf(right)));
                    return;
                }else{
                    return;
                }
            }

            if (KeyMapper.DoubleList.contains(KeyMapper.mapper.get(key))){
                condition.put(KeyMapper.mapper.get(key),Integer.valueOf(value));
            }else{
                condition.put(KeyMapper.mapper.get(key),value);
            }
            return;
        }

        if (gtFlag>1&&egtFlag<=1){
            String kk = temp.split(">")[0];
            String vv = temp.split(">")[1];
            String key = kk.substring(1,kk.length());
            String value = vv.substring(0,vv.length()-1);

            key = KeyMapper.mapper.get(key);

            if (KeyMapper.DoubleList.contains(key)){
                condition.put(key,new BasicDBObject("$gt",Integer.valueOf(value)));
            }
            return;
        }
        if (ltFlag>1&&lgtFlag<=1){
            String kk = temp.split("<")[0];
            String vv = temp.split("<")[1];
            String key = kk.substring(1,kk.length());
            String value = vv.substring(0,vv.length()-1);

            key = KeyMapper.mapper.get(key);
            System.out.println(key);
            if (KeyMapper.DoubleList.contains(key)){
                condition.put(key,new BasicDBObject("$lt",Integer.valueOf(value)));
            }
            return;
        }


        if (egtFlag>1){
            String kk = temp.split(">=")[0];
            String vv = temp.split(">=")[1];
            String key = kk.substring(1,kk.length());
            String value = vv.substring(0,vv.length()-1);

            key = KeyMapper.mapper.get(key);
            if (KeyMapper.DoubleList.contains(key)){
                condition.put(key,new BasicDBObject("$gte",Integer.valueOf(value)));
            }
            return;
        }

        if (lgtFlag>1){
            String kk = temp.split("<=")[0];
            String vv = temp.split("<=")[1];
            String key = kk.substring(1,kk.length());
            String value = vv.substring(0,vv.length()-1);

            if (KeyMapper.DoubleList.contains(key)){
                condition.put(KeyMapper.mapper.get(key),new BasicDBObject("$lte",Integer.valueOf(value)));
            }
            return;
        }


    }
}
