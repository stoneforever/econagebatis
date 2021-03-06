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
package com.flowyun.cornerstone.db.mybatis.entity;

import com.flowyun.cornerstone.db.mybatis.annotations.KeySequence;
import com.flowyun.cornerstone.db.mybatis.enums.IdType;
import com.flowyun.cornerstone.db.mybatis.util.MybatisPreconditions;
import com.flowyun.cornerstone.db.mybatis.util.MybatisStringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/*
* tableInfo可能使用到的场景
* 1，继承了basemapper的mapper类默认方法中动态生成sql
* 2，mapper关联的sqlprovider类中使用，此时tableinfo可能没有，需要通过modelClass获取tableInfo
* 3，分页插件。分页插件解析到一个非mapper型的modelClass时，会尝试解析他，此时mapper类与model类会不一致
* */
public class TableInfo {

    /**
     * 表主键ID 类型
     */
    private IdType idType = IdType.NONE;

    /**
     * 表名称
     */
    private String tableName;

    /**
     * 表映射结果集
     */
    private String defaultSelectResultMap;

    /**
     * <p>
     * 主键是否有存在字段名与属性名关联
     * </p>
     */
    private boolean keyAutoMapping;

    private String keyAutoMappingColumn;

    /**
     * 表主键ID 属性名
     */
    private String keyProperty;

    /**
     * 表主键ID 字段名
     */
    private String keyColumn;

    /**
     * <p>
     * 表主键ID Sequence
     * </p>
     */
    private KeySequence keySequence;

    /**
     * 表字段信息列表
     */
    private List<TableFieldInfo> fieldList;
    /*
     * 外键字段
     * */
    private TableFieldInfo fkField;
    /*
    * 充当乐观锁的字段
    * */
    private TableFieldInfo versionField;
    /*
    * 树形关系，父节点列
    * */
    private TableFieldInfo treeParentLinkField;
    /*
     * 树形关系，排序列
     * */
    private TableFieldInfo treeSiblingOrderField;

    /* 在加载fieldList时,刷新propertyFieldMap */
    private Map<String,TableFieldInfo> propertyFieldMap;
    /*
    * select 语句默认使用的列名，方便某些场景下写sql语句
    * */
    private String selectColumns;

    /*
     * 关联的model类信息
     * */
    private Class<?> modelClass;

    public IdType getIdType() {
        return idType;
    }

    void setIdType(IdType idType) {
        this.idType = idType;
    }

    public String getTableName() {
        return tableName;
    }

    void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getDefaultSelectResultMap() {
        return defaultSelectResultMap;
    }

    void setDefaultSelectResultMap(String defaultSelectResultMap) {
        this.defaultSelectResultMap = defaultSelectResultMap;
    }

    public boolean isKeyAutoMapping() {
        return keyAutoMapping;
    }

    void setKeyAutoMapping(boolean keyAutoMapping) {
        this.keyAutoMapping = keyAutoMapping;
    }

    public String getKeyAutoMappingColumn() {
        return keyAutoMappingColumn;
    }

    void setKeyAutoMappingColumn(String keyAutoMappingColumn) {
        this.keyAutoMappingColumn = keyAutoMappingColumn;
    }

    public String getKeyProperty() {
        return keyProperty;
    }

    void setKeyProperty(String keyProperty) {
        this.keyProperty = keyProperty;
    }

    public String getKeyColumn() {
        return keyColumn;
    }

    void setKeyColumn(String keyColumn) {
        this.keyColumn = keyColumn;
    }

    public KeySequence getKeySequence() {
        return keySequence;
    }

    void setKeySequence(KeySequence keySequence) {
        this.keySequence = keySequence;
    }

    public List<TableFieldInfo> getFieldList() {
        return fieldList;
    }


    void setFieldList(List<TableFieldInfo> fieldList) {
        this.fieldList = Collections.unmodifiableList(fieldList);
        this.propertyFieldMap = fieldList
                .stream()
                .collect( Collectors.toMap(TableFieldInfo::getProperty,t->t) );
    }

    public String getAutoMappingColumnByProperty(String property){
        MybatisPreconditions.checkNotNull(property,"property is null!");
        TableFieldInfo tableFieldInfo = propertyFieldMap.get(property);
        if(tableFieldInfo!=null){
            return tableFieldInfo.getAutoMappingColumn();
        }else if(MybatisStringUtils.equals(property,keyProperty)){
            return keyAutoMappingColumn;
        }else{
            return null;
        }
    }

    public Class<?> getModelClass() {
        return modelClass;
    }

    void setModelClass(Class<?> modelClass) {
        this.modelClass = modelClass;
    }

    public String getSelectColumns() {
        return selectColumns;
    }

    void setSelectColumns(String selectColumns) {
        this.selectColumns = selectColumns;
    }

    public TableFieldInfo getVersionField() {
        return versionField;
    }

    void setVersionField(TableFieldInfo versionField) {
        this.versionField = versionField;
    }

    public TableFieldInfo getFkField() {
        return fkField;
    }

    void setFkField(TableFieldInfo fkField) {
        this.fkField = fkField;
    }

    public String getFkProperty() {
        if(fkField!=null){
            return fkField.getProperty();
        }
        return MybatisStringUtils.EMPTY;
    }

    public String getFkColumn(){
        if(fkField!=null){
            return fkField.getColumn();
        }
        return MybatisStringUtils.EMPTY;
    }

    public TableFieldInfo getTreeParentLinkField() {
        return treeParentLinkField;
    }

    void setTreeParentLinkField(TableFieldInfo treeParentLinkField) {
        this.treeParentLinkField = treeParentLinkField;
    }

    public String getTreeParentLinkProperty() {
        if(treeParentLinkField!=null){
            return treeParentLinkField.getProperty();
        }
        return MybatisStringUtils.EMPTY;
    }

    public String getTreeParentLinkColumn(){
        if(treeParentLinkField!=null){
            return treeParentLinkField.getColumn();
        }
        return MybatisStringUtils.EMPTY;
    }

    public TableFieldInfo getTreeSiblingOrderField() {
        return treeSiblingOrderField;
    }

    void setTreeSiblingOrderField(TableFieldInfo treeSiblingOrderField) {
        this.treeSiblingOrderField = treeSiblingOrderField;
    }

    public String getTreeSiblingOrderProperty() {
        if(treeSiblingOrderField!=null){
            return treeSiblingOrderField.getProperty();
        }
        return MybatisStringUtils.EMPTY;
    }

    public String getTreeSiblingOrderColumn(){
        if(treeSiblingOrderField!=null){
            return treeSiblingOrderField.getColumn();
        }
        return MybatisStringUtils.EMPTY;
    }
}
