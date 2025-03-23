package VTTP.projectB.Models.Exceptions;

public class InvalidUserException extends RuntimeException{
    public InvalidUserException(){
        super();
    }
    public InvalidUserException(String msg){
        super(msg);
    }
    public InvalidUserException(String msg, Throwable t){
        super(msg,t);
    }
}
