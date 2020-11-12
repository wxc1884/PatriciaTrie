package com.wxcbk.nlp.util;

import java.io.*;

/**
 * @author :owen
 * @date :2020/11/12 9:53
 * @Description :
 */
public class DeepCopyUtil {

    public static <T> T deepClone(T obj) throws ClassNotFoundException, IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(obj);
        // 将流序列化成对象
        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bis);
        return (T) ois.readObject();
    }
}
