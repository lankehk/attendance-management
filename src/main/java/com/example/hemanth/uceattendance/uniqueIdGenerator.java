package com.example.rashed.uceattendance;

import android.util.Log;

import org.bouncycastle.jcajce.provider.digest.SHA3;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Rashed on 16-02-2018.
 */

public class uniqueIdGenerator {
    public static String generate(String id, String salt){
        String uniqueId = null;
        try{
            SHA3.DigestSHA3 sha3 = new SHA3.Digest512();
            sha3.update(salt.getBytes("UTF-8"));
            byte[] bytes = sha3.digest(id.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for(int i=0;i<bytes.length;i++){
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100,16).substring(1));
            }
            uniqueId = sb.toString();
        }
        catch(UnsupportedEncodingException e){
            Log.v("Error ","Unsupported Encoding" + e.getMessage());
        }
        return uniqueId;
    }
}
