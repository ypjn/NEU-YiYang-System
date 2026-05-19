package neu.YYZX.util;

import neu.YYZX.model.CareProject;
import neu.YYZX.model.CareRecord;
import neu.YYZX.model.Elderly;
import neu.YYZX.model.NursingLevel;
import neu.YYZX.service.DataManager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class FileUtil {
    private static final String DATA_DIR = "data";
    private static final String DATA_FILE = DATA_DIR + "/yiyang_data.txt";

    public static void load(DataManager dm) {
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            dm.initDefaultData();
            save(dm);
            return;
        }

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
                                    parts[1], parts[2], parts[3], parts[4]));
                        }
                        break;
                    case "PROJECT":
                        if (parts.length >= 8) {
                            dm.getProjects().add(new CareProject(
                                    parts[1], parts[2], parts[3], parts[4],
                                    Double.parseDouble(parts[5]), parts[6], parts[7]));
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
                                    parts[1], parts[2], parts[3], parts[4], parts[5], parts[6]));
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
                    default:
                        break;
                }
            }
        } catch (Exception e) {
            System.out.println("数据文件加载失败，已恢复为默认业务数据。");
            dm.initDefaultData();
            save(dm);
            return;
        }

        if (dm.getLevels().isEmpty() && dm.getProjects().isEmpty()) {
            dm.initDefaultData();
            save(dm);
        } else if (maxRecordSeq > dm.getRecordSeq()) {
            dm.setRecordSeq(maxRecordSeq);
        }
    }

    public static void save(DataManager dm) {
        File dir = new File(DATA_DIR);
        if (!dir.exists() && !dir.mkdirs()) {
            System.out.println("无法创建数据目录。");
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATA_FILE))) {
            writer.write("META|" + dm.getRecordSeq());
            writer.newLine();

            for (NursingLevel level : dm.getLevels()) {
                writer.write(String.join("|", "LEVEL",
                        level.getCode(),
                        level.getName(),
                        level.getDescription(),
                        level.getFrequency()));
                writer.newLine();
            }

            for (CareProject project : dm.getProjects()) {
                writer.write(String.join("|", "PROJECT",
                        project.getCode(),
                        project.getName(),
                        project.getCategory(),
                        project.getUnit(),
                        String.valueOf(project.getPrice()),
                        project.getCycle(),
                        project.getRemark()));
                writer.newLine();
            }

            for (Elderly elder : dm.getElders()) {
                writer.write(String.join("|", "ELDER",
                        elder.getId(),
                        elder.getName(),
                        String.valueOf(elder.getAge()),
                        elder.getGender(),
                        elder.getNursingLevelCode(),
                        elder.getRoomNo()));
                writer.newLine();
            }

            for (CareRecord record : dm.getRecords()) {
                writer.write(String.join("|", "RECORD",
                        record.getId(),
                        record.getElderlyId(),
                        record.getProjectCode(),
                        record.getExecuteTime(),
                        record.getNurseName(),
                        nullToEmpty(record.getRemark())));
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("数据保存失败：" + e.getMessage());
        }
    }

    private static String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    public static void persist() {
        save(DataManager.getInstance());
    }
}
