/**
 *    Copyright 2017-2018 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.econage.core.db.mybatis.mapper.sqlsource;

import com.econage.core.db.mybatis.adaptation.MybatisGlobalAssistant;
import com.econage.core.db.mybatis.entity.TableInfo;
import com.econage.core.db.mybatis.enums.FieldStrategy;
import com.econage.core.db.mybatis.util.MybatisMapUtils;
import com.econage.core.db.mybatis.util.MybatisStringUtils;
import com.econage.core.db.mybatis.adaptation.MybatisConfiguration;
import com.econage.core.db.mybatis.entity.TableFieldInfo;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import org.apache.ibatis.builder.SqlSourceBuilder;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.parsing.PropertyParser;

import java.util.Collection;
import java.util.Map;

public abstract class AbstractDefaultMethodSqlSource implements SqlSource {
    private static final Log logger = LogFactory.getLog(AbstractDefaultMethodSqlSource.class);

    private final MybatisConfiguration configuration;
    private final SqlSourceBuilder sqlSourceParser;
    protected final TableInfo tableInfo;

    public AbstractDefaultMethodSqlSource(
            MybatisConfiguration configuration,
            TableInfo tableInfo
    ) {
        this.configuration = configuration;
        this.sqlSourceParser = new SqlSourceBuilder(configuration);
        this.tableInfo = tableInfo;
    }

    @Override
    public final BoundSql getBoundSql(Object parameterObject) {
        SqlProviderBinding sqlProviderBinding = parseBinding(parameterObject);
        Map<String,Object> additionalParameter = sqlProviderBinding.getAdditionalParameter();

        Class<?> parameterType = parameterObject == null ? Object.class : parameterObject.getClass();

        if(logger.isDebugEnabled()){
            logger.debug("bindingSQL before parse:"+sqlProviderBinding.getSql());
        }

        SqlSource sqlSource = sqlSourceParser.parse(
                replacePlaceholder(sqlProviderBinding.getSql()),
                parameterType,
                additionalParameter
        );
        BoundSql boundSql = sqlSource.getBoundSql(parameterObject);

        if(MybatisMapUtils.isNotEmpty(additionalParameter)){
            for(Map.Entry<String,Object> entry : additionalParameter.entrySet() ){
                boundSql.setAdditionalParameter(entry.getKey(),entry.getValue());
            }
        }


        return boundSql;
    }

    protected abstract SqlProviderBinding parseBinding(Object parameterObject);

    public abstract String getMethodId();

    public abstract SqlCommandType getSqlCommandType();

    public MybatisConfiguration getConfiguration() {
        return configuration;
    }

    public MybatisGlobalAssistant getGlobalAssistant(){
        if(configuration!=null){
            return configuration.getGlobalAssistant();
        }
        return null;
    }

    protected SqlSource createStaticSqlSource(String sql,Class<?> parameterType){
        if(MybatisStringUtils.isEmpty(sql)){
            throw new IllegalArgumentException("sql is empty or null!");
        }
        return sqlSourceParser.parse(
                replacePlaceholder(sql),
                parameterType,
                Maps.<String, Object>newHashMap()
        );
    }

    private String replacePlaceholder(String sql) {
        return PropertyParser.parse(sql, configuration.getVariables());
    }

    //某个字段是否可以在修改、插入语句中使用
    protected boolean useFieldInModifySql(TableFieldInfo fieldInfo,Class<?> propertyType,Object propertyVal){
        boolean canUse = false;
        if(FieldStrategy.IGNORED==fieldInfo.getFieldStrategy()){
            canUse = true;
        }else if(FieldStrategy.NOT_NULL==fieldInfo.getFieldStrategy()){
            canUse = propertyVal!=null;
        }else if(FieldStrategy.NOT_EMPTY==fieldInfo.getFieldStrategy()){
            if(MybatisStringUtils.isCharSequence(propertyType)){
                canUse = !Strings.isNullOrEmpty((String)propertyVal);
            }else{
                canUse = propertyVal!=null;
            }
        }
        return canUse;
    }

    //尝试提取collection类型的参数
    protected Collection<?> fetchCollectionTypeParameter(Object parameter){
        if(parameter instanceof Collection){
            return (Collection<?>) parameter;
        }
        //DefaultSqlSession会把Collection类型的参数放入一个map中
        if(parameter instanceof Map){
            return (Collection<?>) ((Map) parameter).get("collection");
        }
        return null;
    }

}