package com.cgmoffice.core.utils;

import java.security.spec.AlgorithmParameterSpec;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.lang3.StringUtils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class AES256Utils {
	//키값 32바이트: AES256(24: AES192, 16: AES128)
//    public static String secretKey = "01234567890123450123456789012345";
//    public static byte[] ivBytes = "0123456789012345".getBytes();

    //AES256 암호화
    public String aesEncode(String str, String chiper) {
        try {
            byte[] textBytes = str.getBytes("UTF-8");

            String _chiper = StringUtils.rightPad(chiper, 32, 'Z').substring(0, 32);

            byte[] ivBytes = _chiper.substring(0, 16).getBytes("UTF-8");

            AlgorithmParameterSpec ivSpec = new IvParameterSpec(ivBytes);
            SecretKeySpec newKey = new SecretKeySpec(_chiper.getBytes("UTF-8"), "AES");
            Cipher cipher = null;
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, newKey, ivSpec);

            return Base64.getEncoder().encodeToString(cipher.doFinal(textBytes));
        } catch (Exception e) {
        	e.printStackTrace();
        }
        return str;
    }

    //AES256 복호화
    public String aesDecode(String str, String chiper) {
        try {
        	//IOS url-encode 대응
        	str = str.replace(" ", "+");

            String _chiper = StringUtils.rightPad(chiper, 32, 'Z').substring(0, 32);

            byte[] textBytes = Base64.getDecoder().decode(str);
            byte[] ivBytes = _chiper.substring(0, 16).getBytes("UTF-8");
            AlgorithmParameterSpec ivSpec = new IvParameterSpec(ivBytes);
            SecretKeySpec newKey = new SecretKeySpec(_chiper.getBytes("UTF-8"), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, newKey, ivSpec);
            return new String(cipher.doFinal(textBytes), "UTF-8");
        } catch (Exception e) {
        	e.printStackTrace();
        }
        return str;
    }

}
