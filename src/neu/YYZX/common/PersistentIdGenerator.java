package neu.YYZX.common;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 持久化自增ID生成器（单例模式）
 * 为每个实体类型维护独立的ID序号，持久化到 data/last-id.json
 */
public class PersistentIdGenerator {
    private static final PersistentIdGenerator INSTANCE = new PersistentIdGenerator();

    private static final String DATA_DIR = "data";
    private static final String ID_FILE = DATA_DIR + "/last-id.json";
    private static final ObjectMapper MAPPER = new ObjectMapper();

    /** 实体类型 → 当前序号 */
    private final Map<String, Integer> idMap = new HashMap<>();

    private PersistentIdGenerator() {
    }

    public static PersistentIdGenerator getInstance() {
        return INSTANCE;
    }

    /**
     * 从文件加载ID序号
     */
    public void load() {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(ID_FILE);
        if (file.exists()) {
            try {
                Map<String, Integer> loaded = MAPPER.readValue(file,
                        new TypeReference<Map<String, Integer>>() {});
                if (loaded != null) {
                    idMap.putAll(loaded);
                }
            } catch (IOException e) {
                System.out.println("ID文件加载失败，使用默认序号: " + e.getMessage());
            }
        }
    }

    /**
     * 保存ID序号到文件
     */
    public void save() {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        try {
            MAPPER.writerWithDefaultPrettyPrinter().writeValue(new File(ID_FILE), idMap);
        } catch (IOException e) {
            System.out.println("ID文件保存失败: " + e.getMessage());
        }
    }

    /**
     * 生成下一个ID
     * @param prefix ID前缀，如 "U" 表示用户，"C" 表示客户
     * @param entityType 实体类型键名，用于区分不同实体的序号
     * @return 格式化后的ID，如 "U0001"
     */
    public String nextId(String prefix, String entityType) {
        int seq = idMap.getOrDefault(entityType, 0) + 1;
        idMap.put(entityType, seq);
        save();
        return prefix + String.format("%04d", seq);
    }

    /**
     * 获取当前序号（不递增）
     */
    public int currentSeq(String entityType) {
        return idMap.getOrDefault(entityType, 0);
    }

    /**
     * 设置序号（用于数据迁移）
     */
    public void setSeq(String entityType, int seq) {
        idMap.put(entityType, seq);
    }
}
