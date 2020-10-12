package cn.charlie166.spring.boot.web.exception;

/**
 * 自定义异常类
 */
public class CustomException extends RuntimeException{

    private CustomException(String msg) {
        super(msg);
    }

    private CustomException(String msg, Throwable t){
        super(msg, t);
    }

    public static CustomException t(String msg) {
        return new CustomException(msg);
    }

    public static CustomException t(String msg, Throwable t) {
        return new CustomException(msg, t);
    }
}
