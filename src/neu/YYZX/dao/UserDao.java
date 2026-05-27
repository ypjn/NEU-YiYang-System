package neu.YYZX.dao;

import com.fasterxml.jackson.core.type.TypeReference;
import neu.YYZX.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户数据访问层
 */
public class UserDao extends BaseJsonDao<User> {

    private static final String FILE_NAME = "users.json";
    private static final String ID_PREFIX = "U";
    private static final String ENTITY_TYPE = "user";

    public UserDao() {
        super(FILE_NAME, ID_PREFIX, ENTITY_TYPE);
    }

    @Override
    protected String getEntityId(User entity) {
        return entity.getUserId();
    }

    @Override
    protected void setEntityId(User entity, String id) {
        entity.setUserId(id);
    }

    @Override
    protected TypeReference<List<User>> getTypeReference() {
        return new TypeReference<List<User>>() {};
    }

    /** 根据用户名查找用户 */
    public User findByUsername(String username) {
        for (User user : list) {
            if (user.getUsername().equalsIgnoreCase(username)) {
                return user;
            }
        }
        return null;
    }

    /** 判断用户名是否已存在 */
    public boolean containsUsername(String username) {
        return findByUsername(username) != null;
    }

    /** 统计指定角色的用户数量 */
    public int countByRole(String role) {
        int count = 0;
        for (User user : list) {
            if (role.equalsIgnoreCase(user.getRole())) {
                count++;
            }
        }
        return count;
    }

    /** 查询指定角色的所有用户 */
    public List<User> findByRole(String role) {
        List<User> result = new ArrayList<>();
        for (User user : list) {
            if (role.equalsIgnoreCase(user.getRole())) {
                result.add(user);
            }
        }
        return result;
    }
}
