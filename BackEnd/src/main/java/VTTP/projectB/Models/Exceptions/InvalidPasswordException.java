package VTTP.projectB.Models.Exceptions;

public class InvalidPasswordException extends RuntimeException{
    public InvalidPasswordException(){
        super();
    }

    public InvalidPasswordException(String msg){
        super(msg);
    }

    public InvalidPasswordException(String msg, Throwable throwable){
        super(msg,throwable);
    }
}
