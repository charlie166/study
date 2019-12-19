package cn.charlie166.spring.boot.web.exception;

/**
 * 自定义异常类
 */
public class CustomException extends RuntimeException{

    public CustomException(String msg) {
        super(msg);
    }

    public CustomException(Throwable t) {
        super(t);
    }

    public CustomException(String msg, Throwable t){
        super(msg, t);
    }

    public static CustomException t(String msg) {
        return new CustomException(msg);
    }

}
