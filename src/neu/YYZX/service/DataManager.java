package neu.YYZX.service;

import neu.YYZX.model.CareProject;
import neu.YYZX.model.CareRecord;
import neu.YYZX.model.Elderly;
import neu.YYZX.model.NursingLevel;

import java.util.ArrayList;
import java.util.List;

public class DataManager {
    private static final DataManager INSTANCE = new DataManager();

    private final ArrayList<NursingLevel> levels = new ArrayList<>();
    private final ArrayList<CareProject> projects = new ArrayList<>();
    private final ArrayList<Elderly> elders = new ArrayList<>();
    private final ArrayList<CareRecord> records = new ArrayList<>();
    private int recordSeq = 1;

    private DataManager() {
    }

    public static DataManager getInstance() {
        return INSTANCE;
    }

    public ArrayList<NursingLevel> getLevels() {
        return levels;
    }

    public ArrayList<CareProject> getProjects() {
        return projects;
    }

    public ArrayList<Elderly> getElders() {
        return elders;
    }

    public ArrayList<CareRecord> getRecords() {
        return records;
    }

    public int getRecordSeq() {
        return recordSeq;
    }

    public void setRecordSeq(int recordSeq) {
        this.recordSeq = recordSeq;
    }

    public void clearAll() {
        levels.clear();
        projects.clear();
        elders.clear();
        records.clear();
        recordSeq = 1;
    }

    public void initDefaultData() {
        clearAll();

        levels.add(new NursingLevel("ZL", "自理型",
                "身体健康，生活完全自理，无须特殊护理，只需提供生活协助和膳食。",
                "每日巡查 1 次"));
        levels.add(new NursingLevel("HL-1", "一级护理",
                "轻度失能，日常生活需部分协助（如洗澡、穿衣需提醒或辅助），或患有慢性病需定时监测。",
                "每日巡查 2-3 次"));
        levels.add(new NursingLevel("HL-2", "二级护理",
                "中度失能，日常生活需较大帮助（如需协助进食、如厕），或轻度认知障碍。",
                "每 2 小时巡查 1 次"));
        levels.add(new NursingLevel("HL-3", "三级护理",
                "重度失能，卧床不起，完全依赖他人照顾，或严重认知障碍（如晚期失智）。",
                "每 1 小时巡查 1 次，24小时看护"));
        levels.add(new NursingLevel("YZ", "医疗专护",
                "患有严重疾病，需保留胃管、尿管，或需定期换药、康复训练的术后老人。",
                "医护人员定时执行医嘱"));

        projects.add(new CareProject("LZ-001", "晨间护理", "生活照料", "次", 15, "每天", "所有护理级"));
        projects.add(new CareProject("LZ-002", "晚间护理", "生活照料", "次", 10, "每天", "所有护理级"));
        projects.add(new CareProject("LZ-003", "床上擦浴", "生活照料", "次", 20, "隔天/每周", "HL-2, HL-3"));
        projects.add(new CareProject("LZ-004", "协助进食/鼻饲", "生活照料", "次", 10, "按餐次", "HL-2, HL-3"));
        projects.add(new CareProject("LZ-005", "协助如厕/更换尿布", "生活照料", "次", 5, "按需", "HL-2, HL-3"));
        projects.add(new CareProject("LZ-006", "剪指甲/理发", "生活照料", "次", 15, "每周", "HL-1, HL-2"));
        projects.add(new CareProject("LZ-007", "翻身拍背 (防褥疮)", "生活照料", "次", 8, "每2小时", "HL-3"));

        projects.add(new CareProject("YL-001", "生命体征监测", "医疗护理", "次", 5, "每日1-2次", "含体温、血压、血糖"));
        projects.add(new CareProject("YL-002", "药物管理与喂药", "医疗护理", "次", 10, "按医嘱", "需记录服药时间"));
        projects.add(new CareProject("YL-003", "伤口换药", "医疗护理", "次", 50, "隔天", "需医生开具处方"));
        projects.add(new CareProject("YL-004", "导尿管/胃管护理", "医疗护理", "次", 60, "每周", "专业护理"));
        projects.add(new CareProject("YL-005", "吸氧", "医疗护理", "小时", 5, "按需", "医生开具处方, 按需"));

        projects.add(new CareProject("KF-001", "肢体被动训练", "康复心理", "次", 30, "每天", "防止肌肉萎缩"));
        projects.add(new CareProject("KF-002", "认知训练(益智游戏)", "康复心理", "次", 20, "隔天", "针对失智老人"));
        projects.add(new CareProject("KF-003", "心理疏导", "康复心理", "次", 40, "每周", "一对一谈心"));
    }

    public static int getLevelIndex(ArrayList<NursingLevel> list, String code) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getCode().equals(code)) {
                return i;
            }
        }
        return -1;
    }

    public static boolean containsLevel(String code) {
        return getLevelIndex(getInstance().levels, code) >= 0;
    }

    public static int getProjectIndex(ArrayList<CareProject> list, String code) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getCode().equals(code)) {
                return i;
            }
        }
        return -1;
    }

    public static boolean containsProject(String code) {
        return getProjectIndex(getInstance().projects, code) >= 0;
    }

    public static int getElderIndex(ArrayList<Elderly> list, String id) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId().equals(id)) {
                return i;
            }
        }
        return -1;
    }

    public static boolean containsElder(String id) {
        return getElderIndex(getInstance().elders, id) >= 0;
    }

    public NursingLevel findLevelByCode(String code) {
        int idx = getLevelIndex(levels, code);
        return idx >= 0 ? levels.get(idx) : null;
    }

    public CareProject findProjectByCode(String code) {
        int idx = getProjectIndex(projects, code);
        return idx >= 0 ? projects.get(idx) : null;
    }

    public Elderly findElderById(String id) {
        int idx = getElderIndex(elders, id);
        return idx >= 0 ? elders.get(idx) : null;
    }

    public List<CareProject> getApplicableProjects(String nursingLevelCode) {
        ArrayList<CareProject> result = new ArrayList<>();
        for (CareProject p : projects) {
            String remark = p.getRemark();
            if (remark != null && (remark.contains("所有护理级") || remark.contains(nursingLevelCode))) {
                result.add(p);
            }
        }
        return result;
    }

    public String nextRecordId() {
        String id = "R" + String.format("%04d", recordSeq);
        recordSeq++;
        return id;
    }
}
