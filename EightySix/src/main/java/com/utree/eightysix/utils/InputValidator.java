package com.utree.eightysix.utils;

import android.text.TextUtils;
import com.utree.eightysix.U;

/**
 * Util to validate any EditText input
 */
public class InputValidator {
    public static boolean phoneNumber(CharSequence phoneNumber) {
        return phoneNumber.length() == U.getConfigInt("account.phone.length")
                && TextUtils.isDigitsOnly(phoneNumber)
                && phoneNumber.charAt(0) == '1';

    }

    public static boolean pwd(CharSequence pwd) {
        return pwd.length() >= U.getConfigInt("account.pwd.length.min")
                && pwd.length() <= U.getConfigInt("account.pwd.length.max");
    }
}
