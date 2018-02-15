package test;

import client.RpcImporter;
import exporter.RpcExporter;
import server.IRpcService;
import server.RpcServiceImpl;

import java.net.InetSocketAddress;

public class RpcTest {
    public static void main(String[] args){
        new Thread(new Runnable() {
            public void run() {
                try {
                    RpcExporter.exporter("localhost", 8888);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();

        try {
            Thread.sleep(10000);
        }catch (InterruptedException e){
            e.printStackTrace();
        }

        // 创建客户端服务代理类
        RpcImporter<IRpcService> importer = new RpcImporter<IRpcService>();
        IRpcService iRpcService = importer.importer(RpcServiceImpl.class, new InetSocketAddress("localhost", 8888));
        System.out.println(iRpcService.logMsg());
        System.out.println(iRpcService.logMsg("ouguangqian"));
    }
}
