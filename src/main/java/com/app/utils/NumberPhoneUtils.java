package com.app.utils;

import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.Locale;
import javax.swing.text.MaskFormatter;

/**
 *
 * @author inuHa
 */
public class NumberPhoneUtils {

    public static String formatPhoneNumber(String numberPhone) {
        numberPhone = numberPhone.replaceAll("[^0-9]", "");
        if (numberPhone.startsWith("84")) {
            numberPhone = "0" + numberPhone.substring(2);
        }
	return numberPhone;
    }

    public static DefaultFormatterFactory getDefaultFormat() {
        MaskFormatter phoneFormatter;
        try {
            phoneFormatter = new MaskFormatter("0## ### ####");
            phoneFormatter.setPlaceholderCharacter('_');
	    phoneFormatter.setValueContainsLiteralCharacters(false);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }

        phoneFormatter.setAllowsInvalid(false);
        phoneFormatter.setOverwriteMode(true);
        return new DefaultFormatterFactory(phoneFormatter);
    }

}
