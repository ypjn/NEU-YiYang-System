package neu.YYZX.dao;

import com.fasterxml.jackson.core.type.TypeReference;
import neu.YYZX.common.PersistentIdGenerator;
import neu.YYZX.util.JsonUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 基于JSON文件的DAO抽象基类
 * 提供通用的CRUD实现，子类只需指定文件名、ID前缀和实体类型键名
 * @param <T> 实体类型
 */
public abstract class BaseJsonDao<T> implements BaseDao<T> {

    protected List<T> list = new ArrayList<>();
    protected final String fileName;
    protected final String idPrefix;
    protected final String entityType;
    protected final PersistentIdGenerator idGenerator;

    /**
     * @param fileName JSON文件名，如 "users.json"
     * @param idPrefix ID前缀，如 "U" 表示用户
     * @param entityType 实体类型键名，用于ID生成器区分不同实体
     */
    public BaseJsonDao(String fileName, String idPrefix, String entityType) {
        this.fileName = fileName;
        this.idPrefix = idPrefix;
        this.entityType = entityType;
        this.idGenerator = PersistentIdGenerator.getInstance();
    }

    /** 获取实体的ID字段值 */
    protected abstract String getEntityId(T entity);

    /** 设置实体的ID字段值 */
    protected abstract void setEntityId(T entity, String id);

    /** 获取Jackson反序列化用的TypeReference */
    protected abstract TypeReference<List<T>> getTypeReference();

    @Override
    public List<T> findAll() {
        return list;
    }

    @Override
    public T findById(String id) {
        for (T entity : list) {
            if (id.equals(getEntityId(entity))) {
                return entity;
            }
        }
        return null;
    }

    @Override
    public boolean insert(T entity) {
        // 如果实体已有ID且不重复，直接添加
        String existingId = getEntityId(entity);
        if (existingId != null && !existingId.isEmpty()) {
            if (findById(existingId) != null) {
                return false;
            }
            list.add(entity);
            save();
            return true;
        }
        // 自动生成ID
        String newId = idGenerator.nextId(idPrefix, entityType);
        setEntityId(entity, newId);
        list.add(entity);
        save();
        return true;
    }

    @Override
    public boolean update(T entity) {
        String id = getEntityId(entity);
        for (int i = 0; i < list.size(); i++) {
            if (id.equals(getEntityId(list.get(i)))) {
                list.set(i, entity);
                save();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean delete(String id) {
        for (int i = 0; i < list.size(); i++) {
            if (id.equals(getEntityId(list.get(i)))) {
                list.remove(i);
                save();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean exists(String id) {
        return findById(id) != null;
    }

    @Override
    public void load() {
        List<T> loaded = JsonUtil.readList(fileName, getTypeReference());
        if (loaded != null && !loaded.isEmpty()) {
            list = loaded;
            // 同步ID生成器的序号
            int maxSeq = 0;
            for (T entity : list) {
                String id = getEntityId(entity);
                if (id != null && id.startsWith(idPrefix)) {
                    try {
                        int seq = Integer.parseInt(id.substring(idPrefix.length()));
                        if (seq > maxSeq) {
                            maxSeq = seq;
                        }
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
            if (maxSeq > idGenerator.currentSeq(entityType)) {
                idGenerator.setSeq(entityType, maxSeq);
            }
        } else {
            list = new ArrayList<>();
        }
    }

    @Override
    public void save() {
        JsonUtil.writeList(fileName, list);
    }

    /** 获取列表大小 */
    public int size() {
        return list.size();
    }

    /** 直接设置列表（用于数据初始化） */
    public void setList(List<T> list) {
        this.list = list;
    }

    /** 批量插入 */
    public void insertAll(List<T> entities) {
        for (T entity : entities) {
            String id = getEntityId(entity);
            if (id == null || id.isEmpty()) {
                setEntityId(entity, idGenerator.nextId(idPrefix, entityType));
            }
        }
        list.addAll(entities);
        save();
    }
}
