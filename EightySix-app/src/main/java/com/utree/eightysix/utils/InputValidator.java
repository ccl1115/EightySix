package com.utree.eightysix.utils;

import android.text.TextUtils;
import com.utree.eightysix.U;
import java.util.regex.Pattern;

/**
 * Util to validate any EditText input
 */
public class InputValidator {

  private static final Pattern PWD_REGEX = Pattern.compile("\\w{6,16}");

  public static boolean phoneNumber(CharSequence phoneNumber) {
    return phoneNumber.length() == U.getConfigInt("account.phone.length")
        && TextUtils.isDigitsOnly(phoneNumber)
        && phoneNumber.charAt(0) == '1';

  }

  public static boolean pwd(CharSequence pwd) {
    return pwd.length() >= U.getConfigInt("account.pwd.length.min")
        && pwd.length() <= U.getConfigInt("account.pwd.length.max");
  }

  public static boolean pwdRegex(CharSequence pwd) {
    return PWD_REGEX.matcher(pwd).matches();
  }

  public static boolean post(CharSequence post) {
    return post.length() <= U.getConfigInt("post.length");
  }
}
