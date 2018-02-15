package server;

public class RpcServiceImpl implements IRpcService {
    public String logMsg() {
        System.out.println("No Arguments log");
        return "No Argument";
    }

    public String logMsg(String msg) {
        System.out.println("With Arguments log");
        return "With Argument >>>" + msg;
    }
}
