package neu.YYZX.model;

/**
 * 系统菜单实体类
 */
public class SysMenu {
    private String menuId;
    private String menuName;
    private String parentId;
    private String url;
    private int sortOrder;
    private String icon;

    public SysMenu() {
    }

    public SysMenu(String menuId, String menuName, String parentId,
                   String url, int sortOrder, String icon) {
        this.menuId = menuId;
        this.menuName = menuName;
        this.parentId = parentId;
        this.url = url;
        this.sortOrder = sortOrder;
        this.icon = icon;
    }

    public String getMenuId() {
        return menuId;
    }

    public void setMenuId(String menuId) {
        this.menuId = menuId;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
