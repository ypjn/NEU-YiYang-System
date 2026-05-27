package neu.YYZX.dao;

import java.util.List;

/**
 * 通用DAO接口，定义基本CRUD操作
 * @param <T> 实体类型
 */
public interface BaseDao<T> {

    /** 查询所有记录 */
    List<T> findAll();

    /** 根据ID查询单条记录 */
    T findById(String id);

    /** 插入新记录，返回是否成功 */
    boolean insert(T entity);

    /** 更新记录，返回是否成功 */
    boolean update(T entity);

    /** 删除记录，返回是否成功 */
    boolean delete(String id);

    /** 判断记录是否存在 */
    boolean exists(String id);

    /** 从JSON文件加载数据 */
    void load();

    /** 保存数据到JSON文件 */
    void save();
}
