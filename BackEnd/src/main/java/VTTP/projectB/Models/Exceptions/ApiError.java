package VTTP.projectB.Models.Exceptions;

public class ApiError {
    private int status;
    private String msg;
    
    public int getStatus() {
        return status;
    }
    public void setStatus(int status) {
        this.status = status;
    }
    public String getMsg() {
        return msg;
    }
    public void setMsg(String msg) {
        this.msg = msg;
    }

    public ApiError(int status, String msg){
        this.status = status;
        this.msg = msg;
    }

    

}
