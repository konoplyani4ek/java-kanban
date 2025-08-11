package javakanban.manager;

public class ManagerSaveException extends Exception {
    public ManagerSaveException(String message, Throwable throwable) {
        super(message, throwable);
    }
}