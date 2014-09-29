package com.toraysoft.utils.encrypt;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5
{
  private static MessageDigest sMd5MessageDigest;
  private static StringBuilder sStringBuilder;

  static
  {
    try
    {
      sMd5MessageDigest = MessageDigest.getInstance("MD5");
    }
    catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
    {
    }
    sStringBuilder = new StringBuilder();
  }

  public static String md5(String s)
  {
    sMd5MessageDigest.reset();
    sMd5MessageDigest.update(s.getBytes());

    byte[] digest = sMd5MessageDigest.digest();

    sStringBuilder.setLength(0);
    for (int i = 0; i < digest.length; i++) {
      int b = digest[i] & 0xFF;
      if (b < 16) {
        sStringBuilder.append('0');
      }
      sStringBuilder.append(Integer.toHexString(b));
    }

    return sStringBuilder.toString();
  }
  
  public static String md5(byte[] bytes)
  {
    sMd5MessageDigest.reset();
    sMd5MessageDigest.update(bytes);

    byte[] digest = sMd5MessageDigest.digest();

    sStringBuilder.setLength(0);
    for (int i = 0; i < digest.length; i++) {
      int b = digest[i] & 0xFF;
      if (b < 16) {
        sStringBuilder.append('0');
      }
      sStringBuilder.append(Integer.toHexString(b));
    }

    return sStringBuilder.toString();
  }
}