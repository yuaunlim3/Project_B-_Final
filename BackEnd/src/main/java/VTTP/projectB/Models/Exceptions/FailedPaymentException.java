package VTTP.projectB.Models.Exceptions;

public class FailedPaymentException extends RuntimeException {
    public FailedPaymentException(){
        super();
    }

    public FailedPaymentException(String msg){
        super(msg);
    }

    public FailedPaymentException(String msg, Throwable throwable){
        super(msg, throwable);
    }
}
