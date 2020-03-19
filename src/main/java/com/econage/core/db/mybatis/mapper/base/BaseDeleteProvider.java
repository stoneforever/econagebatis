package com.econage.core.db.mybatis.mapper.base;

import com.econage.core.db.mybatis.entity.TableInfo;
import com.econage.core.db.mybatis.mapper.provider.MybatisProviderContext;
import com.econage.core.db.mybatis.util.MybatisCollectionUtils;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.builder.annotation.ProviderMethodResolver;

import java.io.Serializable;
import java.util.Collection;

import static com.econage.core.db.mybatis.mapper.MapperConst.*;

public class BaseDeleteProvider implements ProviderMethodResolver {

    public static String deleteById(
            MybatisProviderContext context
    ){
        TableInfo tableInfo = context.getTableInfo();
        return "delete from " + tableInfo.getTableName()+
                " where " + tableInfo.getKeyColumn()+"=#{" + ID_PARAM_NAME +"}";
    }

    public static String deleteByIds(
            MybatisProviderContext context,
            @Param(ID_COLLECTION_PARAM_NAME) Collection<? extends Serializable> idList
    ){
        if(MybatisCollectionUtils.isEmpty(idList)){
            throw new IllegalArgumentException("parameter is empty!");
        }
        return "delete from " +context.getTableName()+
                SqlProviderHelper.parseIdCollectionWherePart(context,idList);
    }

    public static String deleteByFk(
            MybatisProviderContext context
    ){
        TableInfo tableInfo = context.getTableInfo();
        return "delete from " + tableInfo.getTableName()+
               " where " + tableInfo.getFkColumn()+ "=#{" + FK_PARAM_NAME +"}";
    }

    /*
    * 由于mybatis解析Provider方法的问题,多写一个参数，规避解析不正确的问题
    * */
    public static String deleteByWhereLogic(
            MybatisProviderContext context,
            @Param(WHERE_LOGIC_PARAM_NAME) Object whereLogic,
            @Param("param1") Object obj
    ){
        return "delete from "+
                context.getTableName()+
                SqlProviderHelper.parseWhereLogic(context, whereLogic);
    }
}
