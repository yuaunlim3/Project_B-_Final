package VTTP.projectB.Models.Exceptions;

public class ExistedAccountException extends RuntimeException {
    public ExistedAccountException(){
        super();
    }

    public ExistedAccountException(String msg){
        super(msg);
    }

    public ExistedAccountException(String msg,Throwable t){
        super(msg,t);
    }
}
