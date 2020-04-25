package NettyServerCourseWork.exception;

public class UserDisconnectedException extends Exception {
    public UserDisconnectedException() {
        super("User disconnected.");
    }
}
