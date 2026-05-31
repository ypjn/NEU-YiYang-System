package neu.YYZX.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import neu.YYZX.model.CareProject;
import neu.YYZX.model.CareRecord;
import neu.YYZX.model.Elderly;
import neu.YYZX.model.NursingLevel;
import neu.YYZX.model.SystemData;
import neu.YYZX.model.User;
import neu.YYZX.service.DataManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class FileUtil {
    private static final String DATA_DIR = "data";
    private static final String JSON_FILE = DATA_DIR + "/yiyang_data.json";
    private static final String LEGACY_FILE = DATA_DIR + "/yiyang_data.txt";

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);

    public static void load(DataManager dm) {
        File jsonFile = new File(JSON_FILE);
        File legacyFile = new File(LEGACY_FILE);

        if (jsonFile.exists()) {
            loadFromJson(dm, jsonFile);
            return;
        }

        if (legacyFile.exists()) {
            loadFromLegacyText(dm, legacyFile);
            dm.ensureDefaults();
            save(dm);
            System.out.println("已将旧版文本数据迁移为 JSON 格式：" + JSON_FILE);
            return;
        }

        dm.initDefaultData();
        dm.initDefaultUsers();
        save(dm);
    }

    private static void loadFromJson(DataManager dm, File file) {
        try {
            SystemData data = MAPPER.readValue(file, SystemData.class);
            dm.applySystemData(data);
            dm.ensureDefaults();
            if (dm.getLevels().isEmpty() || dm.getUsers().isEmpty()) {
                save(dm);
            }
        } catch (IOException e) {
            System.out.println("JSON 数据加载失败，已恢复为默认数据：" + e.getMessage());
            dm.initDefaultData();
            dm.initDefaultUsers();
            save(dm);
        }
    }

    private static void loadFromLegacyText(DataManager dm, File file) {
        dm.clearAll();
        int maxRecordSeq = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }
                String[] parts = line.split("\\|", -1);
                if (parts.length < 2) {
                    continue;
                }
                String type = parts[0];
                switch (type) {
                    case "LEVEL":
                        if (parts.length >= 5) {
                            dm.getLevels().add(new NursingLevel(
                                    parts[1], parts[2], parts[3], parts[4], "启用"));
                        }
                        break;
                    case "PROJECT":
                        if (parts.length >= 8) {
                            dm.getProjects().add(new CareProject(
                                    parts[1], parts[2], parts[3], parts[4],
                                    Double.parseDouble(parts[5]), parts[6], 1, "启用", parts[7]));
                        }
                        break;
                    case "ELDER":
                        if (parts.length >= 7) {
                            dm.getElders().add(new Elderly(
                                    parts[1], parts[2], Integer.parseInt(parts[3]),
                                    parts[4], parts[5], parts[6]));
                        }
                        break;
                    case "RECORD":
                        if (parts.length >= 7) {
                            dm.getRecords().add(new CareRecord(
                                    parts[1], parts[2], parts[3], parts[4], 1, parts[5], parts[6]));
                            try {
                                int num = Integer.parseInt(parts[1].substring(1));
                                if (num >= maxRecordSeq) {
                                    maxRecordSeq = num + 1;
                                }
                            } catch (Exception ignored) {
                            }
                        }
                        break;
                    case "META":
                        if (parts.length >= 2) {
                            dm.setRecordSeq(Integer.parseInt(parts[1]));
                        }
                        break;
                    case "USER":
                        if (parts.length >= 4) {
                            dm.getUsers().add(new User(parts[1], parts[2], parts[3]));
                        }
                        break;
                    default:
                        break;
                }
            }
        } catch (Exception e) {
            System.out.println("旧版文本数据加载失败：" + e.getMessage());
            dm.initDefaultData();
            dm.initDefaultUsers();
            return;
        }

        if (maxRecordSeq > dm.getRecordSeq()) {
            dm.setRecordSeq(maxRecordSeq);
        }
    }

    public static void save(DataManager dm) {
        File dir = new File(DATA_DIR);
        if (!dir.exists() && !dir.mkdirs()) {
            System.out.println("无法创建数据目录。");
            return;
        }

        try {
            MAPPER.writeValue(new File(JSON_FILE), dm.toSystemData());
        } catch (IOException e) {
            System.out.println("JSON 数据保存失败：" + e.getMessage());
        }
    }

    public static void persist() {
        save(DataManager.getInstance());
    }
}
