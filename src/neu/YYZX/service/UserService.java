package neu.YYZX.service;

import neu.YYZX.dao.UserDao;
import neu.YYZX.model.User;

import java.util.List;

/**
 * 用户业务服务
 */
public class UserService {
    private final UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    /** 用户登录认证 */
    public User authenticate(String username, String password, String role) {
        User user = userDao.findByUsername(username);
        if (user == null) {
            return null;
        }
        if (!user.getPassword().equals(password)) {
            return null;
        }
        if (!user.getRole().equalsIgnoreCase(role)) {
            return null;
        }
        return user;
    }

    /** 查询所有用户 */
    public List<User> findAll() {
        return userDao.findAll();
    }

    /** 添加用户 */
    public boolean addUser(User user) {
        if (userDao.containsUsername(user.getUsername())) {
            return false;
        }
        return userDao.insert(user);
    }

    /** 修改密码 */
    public boolean changePassword(String username, String newPassword) {
        User user = userDao.findByUsername(username);
        if (user == null) {
            return false;
        }
        user.setPassword(newPassword);
        return userDao.update(user);
    }

    /** 删除用户（管理员至少保留1个） */
    public String deleteUser(String username) {
        User user = userDao.findByUsername(username);
        if (user == null) {
            return "用户不存在";
        }
        if (User.ROLE_ADMIN.equalsIgnoreCase(user.getRole())
                && userDao.countByRole(User.ROLE_ADMIN) <= 1) {
            return "至少保留一名管理员，无法删除";
        }
        userDao.delete(user.getUserId());
        return null;
    }

    /** 判断用户名是否存在 */
    public boolean containsUsername(String username) {
        return userDao.containsUsername(username);
    }

    /** 统计管理员数量 */
    public int countAdmins() {
        return userDao.countByRole(User.ROLE_ADMIN);
    }
}
