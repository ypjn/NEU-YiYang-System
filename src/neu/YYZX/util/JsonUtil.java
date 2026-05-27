package neu.YYZX.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * JSON文件读写工具类
 * 使用Jackson操作独立的JSON文件实现数据持久化
 */
public class JsonUtil {
    private static final String DATA_DIR = "data/json";
    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * 确保数据目录存在
     */
    private static void ensureDir() {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    /**
     * 从JSON文件读取对象列表
     * @param fileName 文件名（不含路径），如 "users.json"
     * @param typeRef 类型引用，如 new TypeReference<List<User>>(){}
     * @return 对象列表，文件不存在则返回空列表
     */
    public static <T> List<T> readList(String fileName, TypeReference<List<T>> typeRef) {
        ensureDir();
        File file = new File(DATA_DIR, fileName);
        if (!file.exists()) {
            return new ArrayList<>();
        }
        try {
            List<T> list = MAPPER.readValue(file, typeRef);
            return list != null ? list : new ArrayList<>();
        } catch (IOException e) {
            System.out.println("读取 " + fileName + " 失败: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * 将对象列表写入JSON文件
     * @param fileName 文件名（不含路径），如 "users.json"
     * @param list 要保存的对象列表
     */
    public static <T> void writeList(String fileName, List<T> list) {
        ensureDir();
        File file = new File(DATA_DIR, fileName);
        try {
            MAPPER.writerWithDefaultPrettyPrinter().writeValue(file, list);
        } catch (IOException e) {
            System.out.println("保存 " + fileName + " 失败: " + e.getMessage());
        }
    }

    /**
     * 从JSON文件读取单个对象
     * @param fileName 文件名（不含路径）
     * @param clazz 对象类型
     * @return 对象，文件不存在则返回null
     */
    public static <T> T readObject(String fileName, Class<T> clazz) {
        ensureDir();
        File file = new File(DATA_DIR, fileName);
        if (!file.exists()) {
            return null;
        }
        try {
            return MAPPER.readValue(file, clazz);
        } catch (IOException e) {
            System.out.println("读取 " + fileName + " 失败: " + e.getMessage());
            return null;
        }
    }

    /**
     * 将单个对象写入JSON文件
     * @param fileName 文件名（不含路径）
     * @param object 要保存的对象
     */
    public static <T> void writeObject(String fileName, T object) {
        ensureDir();
        File file = new File(DATA_DIR, fileName);
        try {
            MAPPER.writerWithDefaultPrettyPrinter().writeValue(file, object);
        } catch (IOException e) {
            System.out.println("保存 " + fileName + " 失败: " + e.getMessage());
        }
    }

    /**
     * 检查JSON文件是否存在
     */
    public static boolean fileExists(String fileName) {
        return new File(DATA_DIR, fileName).exists();
    }

    /**
     * 获取完整文件路径
     */
    public static String getFilePath(String fileName) {
        return DATA_DIR + "/" + fileName;
    }
}
