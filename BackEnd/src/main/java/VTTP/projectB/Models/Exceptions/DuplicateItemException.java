package VTTP.projectB.Models.Exceptions;

public class DuplicateItemException extends RuntimeException {
    public DuplicateItemException(){
        super();
    }

    public DuplicateItemException(String msg){
        super(msg);
    }

    public DuplicateItemException(String msg,Throwable t){
        super(msg,t);
    }
}
