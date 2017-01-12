package main.java.com.robertwilk.beanhelp;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class StructParser {
    private FileOutputStream fos;

    public void parseToFile(Object object) throws IOException, InvocationTargetException, IllegalAccessException {
        fos = new FileOutputStream("example" + object.getClass().getSimpleName());
        for (Method getter : getGetters(getMethods(object.getClass()))) {
            writeField(getField(getter.getName()), String.valueOf(getter.invoke(object)));
        }
        fos.close();
    }

    private void writeField(String key, String value) throws IOException {
        fos.write((key + " : " + value + "\n").getBytes());
    }

    private String getField(String methodName) {
        return String.valueOf(Character.toLowerCase(methodName.charAt(3))) +
                methodName.substring(4, methodName.length());
    }

    private Method[] getMethods(Class clazz) {
        return clazz.getMethods();
    }

    private List<Method> getGetters(Method[] methods) {
        return Arrays.stream(methods)
                .filter(method -> method.getName().startsWith("get") && !method.getName().endsWith("Class"))
                .collect(Collectors.toList());
    }

    public static void main(String[] args) throws IOException, IllegalAccessException, InvocationTargetException {
        StructParser sp = new StructParser();
        ObservableList<Object> list = FXCollections.observableArrayList();
        list.add(new EtcStruct());
        list.add(new OtherStruct());
        for (Object o : list) {
            sp.parseToFile(o);
        }
    }
}
