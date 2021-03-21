import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class CustomizeClassLoader extends ClassLoader {

    public static void main(String[] args) {
        try {
            Class helloClass = new CustomizeClassLoader().findClass("Hello");
            Method method = helloClass.getDeclaredMethod("hello");
            method.invoke(helloClass.newInstance());
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
    @Override
    protected Class<?> findClass(String name) {
        InputStream inputStream = CustomizeClassLoader.class.getResourceAsStream("/Hello.xlass");
        byte[] bytes = new byte[0];
        try {
            bytes = inputStream2byte(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (int i = 0, bytesLength = bytes.length; i < bytesLength; i++) {
            bytes[i] = (byte)(255 - bytes[i]);
        }
        return defineClass(name, bytes, 0, bytes.length);
    }
    private static byte[] inputStream2byte(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buff = new byte[100];
        int rc = 0;
        while ((rc = inputStream.read(buff, 0, 100)) > 0) {
            byteArrayOutputStream.write(buff, 0, rc);
        }
        return byteArrayOutputStream.toByteArray();
    }

}
