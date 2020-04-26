package NettyServerCourseWork.util;

public enum ResponseStatuses {
    OK(200), FULL_LOBBY(300), INTERNAL_ERROR(400);

    private Integer code;

    ResponseStatuses(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
