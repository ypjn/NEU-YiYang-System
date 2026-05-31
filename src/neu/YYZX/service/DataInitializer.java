package neu.YYZX.service;

import neu.YYZX.common.PersistentIdGenerator;
import neu.YYZX.dao.*;
import neu.YYZX.model.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 数据初始化器：加载所有DAO数据，并填充默认数据
 */
public class DataInitializer {
    private static DataInitializer instance;

    private final UserDao userDao = new UserDao();
    private final RoleDao roleDao = new RoleDao();
    private final MenuDao menuDao = new MenuDao();
    private final RoleMenuDao roleMenuDao = new RoleMenuDao();
    private final BuildingDao buildingDao = new BuildingDao();
    private final RoomDao roomDao = new RoomDao();
    private final BedDao bedDao = new BedDao();
    private final BedDetailDao bedDetailDao = new BedDetailDao();
    private final ElderlyDao elderlyDao = new ElderlyDao();
    private final DietPreferenceDao dietPreferenceDao = new DietPreferenceDao();
    private final NursingLevelDao nursingLevelDao = new NursingLevelDao();
    private final CareProjectDao careProjectDao = new CareProjectDao();
    private final NursingContentDao nursingContentDao = new NursingContentDao();
    private final CareRecordDao careRecordDao = new CareRecordDao();
    private final OutRegistrationDao outRegistrationDao = new OutRegistrationDao();
    private final CheckOutDao checkOutDao = new CheckOutDao();
    private final FoodDao foodDao = new FoodDao();
    private final DietCalendarDao dietCalendarDao = new DietCalendarDao();
    private final HealthRecordDao healthRecordDao = new HealthRecordDao();
    private final EmployeeDao employeeDao = new EmployeeDao();
    private final ServiceAssignmentDao serviceAssignmentDao = new ServiceAssignmentDao();
    private final OperationLogDao operationLogDao = new OperationLogDao();
    private final MessageDao messageDao = new MessageDao();
    private final CustomerCareProjectDao customerCareProjectDao = new CustomerCareProjectDao();

    private DataInitializer() {
    }

    public static DataInitializer getInstance() {
        if (instance == null) {
            instance = new DataInitializer();
        }
        return instance;
    }

    /** 初始化：加载ID生成器 → 加载所有DAO → 补齐默认数据 */
    public void init() {
        PersistentIdGenerator.getInstance().load();
        loadAllDaos();
        ensureDefaults();
    }

    /** 保存所有数据 */
    public void saveAll() {
        loadAllDaos(); // 实际上不需要重新加载，但saveAll需要save所有
        userDao.save();
        roleDao.save();
        menuDao.save();
        roleMenuDao.save();
        buildingDao.save();
        roomDao.save();
        bedDao.save();
        bedDetailDao.save();
        elderlyDao.save();
        dietPreferenceDao.save();
        nursingLevelDao.save();
        careProjectDao.save();
        nursingContentDao.save();
        careRecordDao.save();
        outRegistrationDao.save();
        checkOutDao.save();
        foodDao.save();
        dietCalendarDao.save();
        healthRecordDao.save();
        employeeDao.save();
        serviceAssignmentDao.save();
        operationLogDao.save();
        messageDao.save();
        customerCareProjectDao.save();
        PersistentIdGenerator.getInstance().save();
    }

    private void loadAllDaos() {
        userDao.load();
        roleDao.load();
        menuDao.load();
        roleMenuDao.load();
        buildingDao.load();
        roomDao.load();
        bedDao.load();
        bedDetailDao.load();
        elderlyDao.load();
        dietPreferenceDao.load();
        nursingLevelDao.load();
        careProjectDao.load();
        nursingContentDao.load();
        careRecordDao.load();
        outRegistrationDao.load();
        checkOutDao.load();
        foodDao.load();
        dietCalendarDao.load();
        healthRecordDao.load();
        employeeDao.load();
        serviceAssignmentDao.load();
        operationLogDao.load();
        messageDao.load();
        customerCareProjectDao.load();
    }

    /** 确保有默认数据 */
    private void ensureDefaults() {
        ensureDefaultUsers();
        ensureDefaultRoles();
        ensureDefaultNursingLevels();
        ensureDefaultCareProjects();
        ensureDefaultBuildings();
        ensureDefaultRoomsAndBeds();
        ensureDefaultFoods();
        ensureDefaultEmployees();
    }

    private void ensureDefaultUsers() {
        if (userDao.size() == 0) {
            String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            userDao.insert(new User(null, "admin", "admin", User.ROLE_ADMIN, "系统管理员", "13800000000", null, null, now));
            userDao.insert(new User(null, "admin1", "admin1", User.ROLE_ADMIN, "系统管理员2", "13800000002", null, null, now));
            userDao.insert(new User(null, "admin2", "admin2", User.ROLE_ADMIN, "系统管理员3", "13800000003", null, null, now));
            userDao.insert(new User(null, "nurse", "nurse", User.ROLE_NURSE, "护工张三", "13800000001", null, null, now));
        }
    }

    private void ensureDefaultRoles() {
        if (roleDao.size() == 0) {
            roleDao.insert(new Role(null, "系统管理员", "拥有所有系统权限"));
            roleDao.insert(new Role(null, "护工", "日常护理操作权限"));
        }
    }

    private void ensureDefaultNursingLevels() {
        if (nursingLevelDao.size() == 0) {
            nursingLevelDao.insert(new NursingLevel("ZL", "自理型",
                    "身体健康，生活完全自理，无须特殊护理，只需提供生活协助和膳食。",
                    "每日巡查 1 次", "启用"));
            nursingLevelDao.insert(new NursingLevel("HL-1", "一级护理",
                    "轻度失能，日常生活需部分协助，或患有慢性病需定时监测。",
                    "每日巡查 2-3 次", "启用"));
            nursingLevelDao.insert(new NursingLevel("HL-2", "二级护理",
                    "中度失能，日常生活需较大帮助，或轻度认知障碍。",
                    "每 2 小时巡查 1 次", "启用"));
            nursingLevelDao.insert(new NursingLevel("HL-3", "三级护理",
                    "重度失能，卧床不起，完全依赖他人照顾，或严重认知障碍。",
                    "每 1 小时巡查 1 次，24小时看护", "启用"));
            nursingLevelDao.insert(new NursingLevel("YZ", "医疗专护",
                    "患有严重疾病，需保留胃管、尿管，或需定期换药、康复训练的术后老人。",
                    "医护人员定时执行医嘱", "启用"));
        }
    }

    private void ensureDefaultCareProjects() {
        if (careProjectDao.size() == 0) {
            careProjectDao.insert(new CareProject("LZ-001", "晨间护理", "生活照料", "次", 15, "每天", 1, "启用", "所有护理级"));
            careProjectDao.insert(new CareProject("LZ-002", "晚间护理", "生活照料", "次", 10, "每天", 1, "启用", "所有护理级"));
            careProjectDao.insert(new CareProject("LZ-003", "床上擦浴", "生活照料", "次", 20, "隔天/每周", 1, "启用", "HL-2, HL-3"));
            careProjectDao.insert(new CareProject("LZ-004", "协助进食/鼻饲", "生活照料", "次", 10, "按餐次", 1, "启用", "HL-2, HL-3"));
            careProjectDao.insert(new CareProject("LZ-005", "协助如厕/更换尿布", "生活照料", "次", 5, "按需", 1, "启用", "HL-2, HL-3"));
            careProjectDao.insert(new CareProject("LZ-006", "剪指甲/理发", "生活照料", "次", 15, "每周", 1, "启用", "HL-1, HL-2"));
            careProjectDao.insert(new CareProject("LZ-007", "翻身拍背 (防褥疮)", "生活照料", "次", 8, "每2小时", 1, "启用", "HL-3"));
            careProjectDao.insert(new CareProject("YL-001", "生命体征监测", "医疗护理", "次", 5, "每日1-2次", 1, "启用", "所有护理级"));
            careProjectDao.insert(new CareProject("YL-002", "药物管理与喂药", "医疗护理", "次", 10, "按医嘱", 1, "启用", "所有护理级"));
            careProjectDao.insert(new CareProject("YL-003", "伤口换药", "医疗护理", "次", 50, "隔天", 1, "启用", "需医生开具处方"));
            careProjectDao.insert(new CareProject("YL-004", "导尿管/胃管护理", "医疗护理", "次", 60, "每周", 1, "启用", "专业护理"));
            careProjectDao.insert(new CareProject("YL-005", "吸氧", "医疗护理", "小时", 5, "按需", 1, "启用", "医生开具处方, 按需"));
            careProjectDao.insert(new CareProject("KF-001", "肢体被动训练", "康复心理", "次", 30, "每天", 1, "启用", "防止肌肉萎缩"));
            careProjectDao.insert(new CareProject("KF-002", "认知训练(益智游戏)", "康复心理", "次", 20, "隔天", 1, "启用", "针对失智老人"));
            careProjectDao.insert(new CareProject("KF-003", "心理疏导", "康复心理", "次", 40, "每周", 1, "启用", "一对一谈心"));
        }
    }

    private void ensureDefaultBuildings() {
        if (buildingDao.size() == 0) {
            buildingDao.insert(new Building(null, "606", 6, "颐养中心主楼"));
        }
    }

    private void ensureDefaultRoomsAndBeds() {
        if (roomDao.size() == 0) {
            Building building = buildingDao.findAll().stream().findFirst().orElse(null);
            if (building == null) return;
            String buildingId = building.getBuildingId();
            for (int floor = 1; floor <= 6; floor++) {
                for (int r = 1; r <= 2; r++) {
                    String roomNo = floor + "0" + r;
                    Room room = new Room();
                    room.setRoomNo(roomNo);
                    room.setBuildingId(buildingId);
                    room.setFloor(floor);
                    room.setRoomType(floor <= 2 ? "双人间" : "三人间");
                    room.setCapacity(floor <= 2 ? 2 : 3);
                    room.setPrice(floor <= 2 ? 3000 : 2000);
                    room.setStatus("active");
                    roomDao.insert(room);
                    // 每个房间创建2个床位
                    for (int b = 1; b <= 2; b++) {
                        Bed bed = new Bed();
                        bed.setRoomId(room.getRoomId());
                        bed.setBedNo(roomNo + "-" + b);
                        bed.setStatus("available");
                        bedDao.insert(bed);
                    }
                }
            }
        }
    }

    private void ensureDefaultFoods() {
        if (foodDao.size() == 0) {
            foodDao.insert(new Food(null, "清蒸鲈鱼", "荤菜", "份", 25, "高蛋白低脂肪", "适合老人"));
            foodDao.insert(new Food(null, "小米南瓜粥", "主食", "碗", 8, "易消化", "适合早餐"));
            foodDao.insert(new Food(null, "番茄炒蛋", "素菜", "份", 12, "维生素丰富", ""));
            foodDao.insert(new Food(null, "排骨冬瓜汤", "汤品", "碗", 15, "补钙", ""));
            foodDao.insert(new Food(null, "清炒西兰花", "素菜", "份", 10, "富含纤维", ""));
            foodDao.insert(new Food(null, "鸡蛋羹", "荤菜", "份", 8, "易消化高蛋白", "适合咀嚼困难老人"));
            foodDao.insert(new Food(null, "馒头/花卷", "主食", "个", 2, "碳水化合物", ""));
            foodDao.insert(new Food(null, "苹果", "水果", "个", 5, "富含维C", ""));
            foodDao.insert(new Food(null, "酸奶", "零食", "杯", 6, "助消化", ""));
            foodDao.insert(new Food(null, "药膳鸡汤", "汤品", "碗", 20, "滋补", "需预约"));
        }
    }

    private void ensureDefaultEmployees() {
        if (employeeDao.size() == 0) {
            employeeDao.insert(new Employee(null, "李管家", "女", "健康管家", "13900000001", null, "2024-01-01", 6000, "在岗", ""));
            employeeDao.insert(new Employee(null, "王管家", "男", "健康管家", "13900000002", null, "2024-03-01", 6000, "在岗", ""));
            employeeDao.insert(new Employee(null, "张三", "男", "护工", "13900000003", null, "2024-02-01", 5000, "在岗", ""));
            employeeDao.insert(new Employee(null, "赵护工", "女", "护工", "13900000004", null, "2024-06-01", 5000, "在岗", ""));
            employeeDao.insert(new Employee(null, "钱护工", "男", "护工", "13900000005", null, "2025-01-01", 5000, "在岗", ""));
        }
    }

    // ===== Getter 方法 =====

    public UserDao getUserDao() { return userDao; }
    public RoleDao getRoleDao() { return roleDao; }
    public MenuDao getMenuDao() { return menuDao; }
    public RoleMenuDao getRoleMenuDao() { return roleMenuDao; }
    public BuildingDao getBuildingDao() { return buildingDao; }
    public RoomDao getRoomDao() { return roomDao; }
    public BedDao getBedDao() { return bedDao; }
    public BedDetailDao getBedDetailDao() { return bedDetailDao; }
    public ElderlyDao getElderlyDao() { return elderlyDao; }
    public DietPreferenceDao getDietPreferenceDao() { return dietPreferenceDao; }
    public NursingLevelDao getNursingLevelDao() { return nursingLevelDao; }
    public CareProjectDao getCareProjectDao() { return careProjectDao; }
    public NursingContentDao getNursingContentDao() { return nursingContentDao; }
    public CareRecordDao getCareRecordDao() { return careRecordDao; }
    public OutRegistrationDao getOutRegistrationDao() { return outRegistrationDao; }
    public CheckOutDao getCheckOutDao() { return checkOutDao; }
    public FoodDao getFoodDao() { return foodDao; }
    public DietCalendarDao getDietCalendarDao() { return dietCalendarDao; }
    public HealthRecordDao getHealthRecordDao() { return healthRecordDao; }
    public EmployeeDao getEmployeeDao() { return employeeDao; }
    public ServiceAssignmentDao getServiceAssignmentDao() { return serviceAssignmentDao; }
    public OperationLogDao getOperationLogDao() { return operationLogDao; }
    public MessageDao getMessageDao() { return messageDao; }
    public CustomerCareProjectDao getCustomerCareProjectDao() { return customerCareProjectDao; }
}
