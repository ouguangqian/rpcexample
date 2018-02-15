package exporter;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class RpcExporter {
    static Executor executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    public static void exporter(String hostName, int port) throws IOException{
        // 创建一个监听特定端口的socket 接收客户端连接请求
        ServerSocket serverSocket = new ServerSocket();
        // 绑定主机名， 端口号
        serverSocket.bind(new InetSocketAddress(hostName, port));

        try {
            while (true) {
            executor.execute(new ExporterTask(serverSocket.accept()));
            }
        }finally {
            serverSocket.close();
        }
    }

    private static class ExporterTask implements Runnable{
        Socket client = null;
        public ExporterTask(Socket client){
            this.client = client;
        }

        public void run() {
            // 定义输入流，输出流
            ObjectInputStream inputStream = null;
            ObjectOutputStream outputStream = null;

            try{
                // 获取输入流
                inputStream = new ObjectInputStream(client.getInputStream());
                // 获取调用的接口名
                String interfaceName = inputStream.readUTF();
                // 加载接口
                Class<?> service = Class.forName(interfaceName);
                // 获取调用的方法名
                String methodName = inputStream.readUTF();
                // 获取调用方法的参数类型
                Class<?>[] parameterTypes = (Class<?>[]) inputStream.readObject();
                // 获取方法参数
                Object[] parameters = (Object[]) inputStream.readObject();


                // 通过反射获取方法
                Method method = service.getMethod(methodName,parameterTypes);
                // 通过反射调用方法
                Object result = method.invoke(service.newInstance(), parameters);

                outputStream = new ObjectOutputStream(client.getOutputStream());
                outputStream.writeObject(result);

            }catch (Exception e){
                e.printStackTrace();
            }finally {
                if (null != outputStream) {
                    try {
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if ( null != inputStream){
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if (null != client){
                    try {
                        client.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }
}
