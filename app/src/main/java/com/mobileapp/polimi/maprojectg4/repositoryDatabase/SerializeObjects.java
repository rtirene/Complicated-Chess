package com.mobileapp.polimi.maprojectg4.repositoryDatabase;

import android.database.Cursor;

import com.mobileapp.polimi.maprojectg4.model.Board;
import com.mobileapp.polimi.maprojectg4.model.FrozenPieces;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.ResultSet;

/**
 * Created by Alessandro on 31/12/2016.
 */

public class SerializeObjects {

    /**
     * Converts an object in a byte vector. It allows to store objects into the database.
     * @param obj the Object we want to store
     * @return the byte vector containing obj
     * @throws Exception
     */
    public static byte[] convertToByteStream(Object obj) throws Exception {
        byte[] data = null;

        try {
            ByteArrayOutputStream byteOs = new ByteArrayOutputStream();
            ObjectOutputStream objectOs = new ObjectOutputStream(byteOs);

            objectOs.writeObject(obj);
            byteOs.flush();
            byteOs.close();
            objectOs.close();

            data = byteOs.toByteArray();


        } catch (IOException e) {
            e.printStackTrace();
        }

        return data;
    }

    /**
     * Converts a byte vector stored into a database as object. It allows to retrieve objects from a database.
     * @param cursor contains the values of a database record
     * @param objectString represents the specific object we want to extract
     * @return the Object we want to convert
     * @throws Exception
     */
    public static Object getObject(Cursor cursor, String objectString) throws Exception {

        Object obj = new Object();
        ByteArrayInputStream byteIs = null;
        ObjectInputStream objectIs = null;

        try {
            if (cursor.moveToFirst()) {
                do {
                    byteIs = new ByteArrayInputStream(cursor.getBlob(cursor.getColumnIndex(objectString)));
                } while (cursor.moveToNext());
            }
            objectIs = new ObjectInputStream(byteIs);
            obj = objectIs.readObject();
            objectIs.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return obj;
    }
}
