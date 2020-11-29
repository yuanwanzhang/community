package life.majiang.community.enums;

public enum NotificationEnum {
    RELY_QUESTION(1,"回复了问题"),
    RELY_COMMENT(2,"回复了评论");

    private int type;
    private String name;

    public int getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    NotificationEnum(int status, String name) {
        this.type = status;
        this.name = name;
    }

    public  static  String nameOfType(int type){
        for (NotificationEnum notificationEnum : NotificationEnum.values()) {
            if(notificationEnum.getType() == type){
                return notificationEnum.getName();
            }
        }
        return "";
    }
}
