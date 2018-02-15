package client;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.net.Socket;

public class RpcImporter<S> {
    public S importer(final Class<?> serviceClass, final InetSocketAddress address){
        return (S) Proxy.newProxyInstance(serviceClass.getClassLoader(), new Class<?>[]{
                serviceClass.getInterfaces()[0]}, new InvocationHandler() {
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

                Socket socket = null;
                ObjectOutputStream outputStream = null;
                ObjectInputStream inputStream = null;

                try {
                    socket = new Socket();
                    socket.connect(address);
                    // 将远程服务调用所需要的接口类，方法名， 参数列表等编码参数发给服务提供者
                    outputStream = new ObjectOutputStream(socket.getOutputStream());
                    outputStream.writeUTF(serviceClass.getName());
                    outputStream.writeUTF(method.getName());
                    outputStream.writeObject(method.getParameterTypes());
                    outputStream.writeObject(args);

                    inputStream = new ObjectInputStream(socket.getInputStream());
                    return inputStream.readObject();
                }finally {
                    if (null != socket){
                        socket.close();
                    }

                    if (null != outputStream){
                        outputStream.close();
                    }

                    if (null != inputStream){
                        inputStream.close();
                    }
                }
            }
        });
    }
}
