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
package com.flowyun.cornerstone.db.mybatis.mapper.provider;

import com.flowyun.cornerstone.db.mybatis.adaptation.MybatisConfiguration;
import com.flowyun.cornerstone.db.mybatis.entity.TableInfo;
import com.flowyun.cornerstone.db.mybatis.util.MybatisSqlUtils;
import com.flowyun.cornerstone.db.mybatis.util.MybatisStringUtils;
import com.flowyun.cornerstone.db.mybatis.wherelogic.MybatisWhereLogicHelper;
import org.apache.ibatis.reflection.MetaObject;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
* 进入到provider类的时候，会克隆MybatisProviderContext
* 以方便provider类中的方法使用
* */
public final class MybatisProviderContext implements Cloneable {

  /*todo */
  private final MybatisConfiguration configuration;
  private final Class<?> mapperType;
  private final Method mapperMethod;
  private final String databaseId;
  /*todo */
  private final TableInfo tableInfo;
  /*todo */
  private Map<String,Object> additionalParam;
  /*todo */
  private String runtimeTableName;

  MybatisProviderContext(
          MybatisConfiguration configuration,
          Class<?> mapperType,
          Method mapperMethod,
          String databaseId,
          TableInfo tableInfo) {
    this.configuration = configuration;
    this.mapperType = mapperType;
    this.mapperMethod = mapperMethod;
    this.databaseId = databaseId;
    this.tableInfo = tableInfo;
  }

  /**
   * Get a mapper interface type that specified provider.
   *
   * @return A mapper interface type that specified provider
   */
  public Class<?> getMapperType() {
    return mapperType;
  }

  /**
   * Get a mapper method that specified provider.
   *
   * @return A mapper method that specified provider
   */
  public Method getMapperMethod() {
    return mapperMethod;
  }

  /**
   * Get a database id that provided from {@link org.apache.ibatis.mapping.DatabaseIdProvider}.
   *
   * @return A database id
   * @since 3.5.1
   */
  public String getDatabaseId() {
    return databaseId;
  }

  public void setAdditionalParam(String key,Object object){
    if(additionalParam==null){
      additionalParam = new HashMap<>();
    }
    additionalParam.put(key,object);
  }

  public Map<String, Object> getAdditionalParam() {
    if(additionalParam==null){
      additionalParam = new HashMap<>();
    }
    return additionalParam;
  }

  public String getSelectColumns(){
    if(tableInfo!=null){
      return tableInfo.getSelectColumns();
    }else{
      return MybatisStringUtils.EMPTY;
    }
  }

  public String getTableName(){
    if(tableInfo!=null){
      return tableInfo.getTableName();
    }else{
      return MybatisStringUtils.EMPTY;
    }
  }

  void setRuntimeTableName(String runtimeTableName) {
    this.runtimeTableName = runtimeTableName;
  }

  public String getRuntimeTableName(){
    if(MybatisStringUtils.isNotEmpty(runtimeTableName)){
      return runtimeTableName;
    }
    return getTableName();
  }

  public TableInfo getTableInfo(){
    return tableInfo;
  }

  /*
  * 如果需要手动处理谓语查询部分，又需要自动解析谓语逻辑的功能，可通过方法获取
  * */
  public List<String> parseWhereLogic(Object whereLogic){

    return MybatisWhereLogicHelper.parseWhereLogic(
            configuration.getGlobalAssistant(),
            whereLogic,
            getAdditionalParam()
    );

  }

  public String formatCollection2ParameterMappings(
          String itemName,
          Collection<?> typeParams
  ){
    return MybatisSqlUtils.formatCollection2ParameterMappings(
            MybatisStringUtils.EMPTY,MybatisStringUtils.EMPTY,itemName,
            typeParams,
            getAdditionalParam()
    );
  }

  /*
  * 新建一个mybatis的bean操作工具类
  * */
  public MetaObject newMetaObject(Object obj){
    return configuration.newMetaObject(obj);
  }

  @Override
  public MybatisProviderContext clone(){
    try {
      return (MybatisProviderContext) super.clone();
    } catch (CloneNotSupportedException e) {
      return null;
    }
  }

}
