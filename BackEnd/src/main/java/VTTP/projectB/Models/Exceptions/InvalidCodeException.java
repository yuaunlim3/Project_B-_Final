package VTTP.projectB.Models.Exceptions;

public class InvalidCodeException extends RuntimeException{
    public InvalidCodeException(){
        super();
    }
    
    public InvalidCodeException(String msg){
        super(msg);
    }

    public InvalidCodeException(String msg, Throwable t){
        super(msg, t);
    }
}
