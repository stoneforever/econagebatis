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
package com.flowyun.cornerstone.db.mybatis.mapper;

import com.flowyun.cornerstone.db.mybatis.entity.BasicEntity;
import com.flowyun.cornerstone.db.mybatis.mapper.providerimpl.DeleteProviderImpl;
import com.flowyun.cornerstone.db.mybatis.mapper.providerimpl.InsertProviderImpl;
import com.flowyun.cornerstone.db.mybatis.mapper.providerimpl.SelectProviderImpl;
import com.flowyun.cornerstone.db.mybatis.mapper.providerimpl.UpdateProviderImpl;
import com.flowyun.cornerstone.db.mybatis.pagination.Pagination;
import org.apache.ibatis.annotations.*;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import static com.flowyun.cornerstone.db.mybatis.mapper.MapperConst.*;

/**
 * Mapper 继承该接口后，无需编写 mapper.xml 文件，即可获得CRUD功能
 * 除了insert方法，需要额外处理主键插入问题，其他方法由静态provider类解析sql语句
 */
public interface BaseMapper<T extends BasicEntity> extends BasicMapper {

    /**
     * 插入一条记录
     *
     * @param entity 实体对象
     * @return int
     */
    @InsertProvider(InsertProviderImpl.class)
    Integer insert(@Param(ENTITY_PARAM_NAME) T entity);

    /**
     * <p>
     * 插入一条记录
     * </p>
     *
     * @param entity 实体对象
     * @return int
     */
    @InsertProvider(InsertProviderImpl.class)
    Integer insertAllColumn(@Param(ENTITY_PARAM_NAME) T entity);

    /**
     * <p>
     * 根据 ID 删除
     * </p>
     *
     * @param id 主键ID
     * @return int
     */
    @DeleteProvider(DeleteProviderImpl.class)
    Integer deleteById(@Param(ID_PARAM_NAME) Serializable id);

    /**
     * <p>
     * 删除（根据ID 批量删除）
     * </p>
     *
     * @param idList 主键ID列表
     * @return int
     */
    @DeleteProvider(DeleteProviderImpl.class)
    Integer deleteByIds(@Param(ID_COLLECTION_PARAM_NAME) Collection<? extends Serializable> idList);

    /**
     * <p>
     * 删除（根据外键 批量删除）
     * </p>
     *
     * @param fk 外键ID
     * @return int
     */
    @DeleteProvider(DeleteProviderImpl.class)
    Integer deleteByFk(@Param(FK_PARAM_NAME) Serializable fk);

    /**
     * <p>
     * 删除（根据 where 条件批量删除）
     * </p>
     *
     * @param whereLogic where逻辑
     * @return int
     */
    @DeleteProvider(DeleteProviderImpl.class)
    Integer deleteByWhereLogic(@Param(WHERE_LOGIC_PARAM_NAME) Object whereLogic);

    /**
     * <p>
     * 根据 ID 修改更新非空的列
     * </p>
     *
     * @param entity 实体对象
     * @return int
     */
    @UpdateProvider(UpdateProviderImpl.class)
    Integer updateById(@Param(ENTITY_PARAM_NAME) T entity);

    /**
     * <p>
     * 根据 ID 修改更新全部列
     * </p>
     *
     * @param entity 实体对象
     * @return int
     */
    @UpdateProvider(UpdateProviderImpl.class)
    Integer updateAllColumnById(@Param(ENTITY_PARAM_NAME) T entity);

    /*
    * 根据id更新特定列
    * */
    @UpdateProvider(UpdateProviderImpl.class)
    Integer updatePartialColumnById(
            @Param(ENTITY_PARAM_NAME) T entity,
            @Param(PROPERTY_NAME_ARRAY_PARAM_NAME) Collection<String> propertyNameArray
    );

    /**
     * <p>
     * 根据 where 逻辑修改更新非空的列
     * 批量更新需要注意乐观锁字段的处理，此时谓语部分只依据whereLogic参数，没有加上乐观锁信息
     * 更新后，所有更新过的行会使用一个新的版本号
     * </p>
     *
     * @param entity set语句部分对应的值
     * @param whereLogic where逻辑
     * @return int
     */
    @UpdateProvider(UpdateProviderImpl.class)
    Integer updateBatchByWhereLogic(
            @Param(ENTITY_PARAM_NAME) T entity,
            @Param(WHERE_LOGIC_PARAM_NAME) Object whereLogic
    );

    /**
     * <p>
     * 根据 where逻辑 修改更新全部列
     * 批量更新需要注意乐观锁字段的处理，此时谓语部分只依据whereLogic参数，没有加上乐观锁信息
     * 更新后，所有更新过的行会使用一个新的版本号
     * </p>
     *
     *
     * @param entity set语句部分对应的值
     * @param whereLogic where逻辑
     * @return int
     */
    @UpdateProvider(UpdateProviderImpl.class)
    Integer updateBatchAllColumnByWhereLogic(
            @Param(ENTITY_PARAM_NAME) T entity,
            @Param(WHERE_LOGIC_PARAM_NAME) Object whereLogic
    );

    /*
     * <p>
     * 根据where条件更新特定列
     * 批量更新需要注意乐观锁字段的处理，此时谓语部分只依据whereLogic参数，没有加上乐观锁信息
     * 更新后，所有更新过的行会使用一个新的版本号
     * </p>
     *
     * @param entity set语句部分对应的值
     * @param propertyNameArray 限定使用的entity的属性
     * @param whereLogic where逻辑
     * @return int
     * */
    @UpdateProvider(UpdateProviderImpl.class)
    Integer updateBatchPartialColumnByWhereLogic(
            @Param(ENTITY_PARAM_NAME) T entity,
            @Param(PROPERTY_NAME_ARRAY_PARAM_NAME) Collection<String> propertyNameArray,
            @Param(WHERE_LOGIC_PARAM_NAME) Object whereLogic
    );

    /**
     * <p>
     * 根据 ID 查询
     * </p>
     *
     * @param id 主键ID
     * @return T
     */
    @SelectProvider(SelectProviderImpl.class)
    T selectById(@Param(ID_PARAM_NAME) Serializable id);

    /**
     * <p>
     * 查询（根据ID 批量查询）
     * </p>
     *
     * @param idList 主键ID列表
     * @return List<T>
     */
    @SelectProvider(SelectProviderImpl.class)
    List<T> selectListByIds(@Param(ID_COLLECTION_PARAM_NAME) Collection<? extends Serializable> idList);

    /*
    * 按照主键分页显示，会自动侦测主键信息
    * */
    @SelectProvider(SelectProviderImpl.class)
    List<T> selectListByPage(Pagination pagination);

    /*
    * 获取总数
    * */
    @SelectProvider(SelectProviderImpl.class)
    Integer selectCountAll();

    /*
    *  按照外键信息，显示
    * */
    @SelectProvider(SelectProviderImpl.class)
    List<T> selectListByFk(@Param(ID_COLLECTION_PARAM_NAME) Collection<? extends Serializable> fkCollection,Pagination page);

    /*
    * 通用查询方法，可以做分页查询
    * */
    @SelectProvider(SelectProviderImpl.class)
    List<T> selectListByWhereLogic(@Param(WHERE_LOGIC_PARAM_NAME) Object whereLogic, Pagination page);

    /*
    * 通用计数方法
    * */
    @SelectProvider(SelectProviderImpl.class)
    Integer selectCountByWhereLogic(@Param(WHERE_LOGIC_PARAM_NAME) Object whereLogic);

}
