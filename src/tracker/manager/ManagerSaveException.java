package tracker.manager;

public class ManagerSaveException extends RuntimeException {
    private static final String MSG_SAVE = "Error occurred while saving";
    private static final String MSG_LOAD = "Error occurred while load";

    public static ManagerSaveException saveException(Exception e) {
        return new ManagerSaveException(MSG_SAVE, e);
    }

    public static ManagerSaveException loadExceptionException(Exception e) {
        return new ManagerSaveException(MSG_LOAD, e);
    }

    private ManagerSaveException(String msg, final Throwable cause) {
        super(msg, cause);
    }
}
