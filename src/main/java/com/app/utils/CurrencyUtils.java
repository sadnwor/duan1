package com.app.utils;

import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.Locale;

/**
 *
 * @author inuHa
 */
public class CurrencyUtils {

    public static String parseString(double price) {
        return parseString(Math.round(price));
    }
    
    public static String parseString(long price) {
        return "đ" + (new DecimalFormat("#,###")).format(price).replace(",", ".");
    }
    
    public static String parseTextField(double price) {
        return new DecimalFormat("#,###").format(price).replace(",", ".");
    }
    
    public static String parseNumber(int price) {
        return new DecimalFormat("#,###").format(price).replace(",", ".");
    }

    public static String parseNumber(double price) {
        return new DecimalFormat("#,###").format(price).replace(",", ".");
    }
        
    public static long parseNumber(String price) {
	if (price == null || price.isEmpty()) {
	    return 0;
	}
        return Long.parseLong(price.replace(".", "").replace("đ", "").replace("-", ""));
    }

    public static DefaultFormatterFactory getDefaultFormat() {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
        symbols.setGroupingSeparator('.');
        symbols.setDecimalSeparator(',');

        DecimalFormat decimalFormat = new DecimalFormat("#,###", symbols);

        NumberFormatter formatter = new NumberFormatter() {
	    
            @Override
            public Object stringToValue(String text) throws ParseException {
                if (text.isEmpty()) {
                    return null;
                }
		if (text.contains("-")) {
                    text = text.replace("-", "");
                }
                return super.stringToValue(text);
            }
        };

        formatter.setAllowsInvalid(false);
        formatter.setOverwriteMode(true);
        formatter.setFormat(decimalFormat);
        return new DefaultFormatterFactory(formatter);
    }

    public static String startPad(String original, int length, char padChar) {
        if (original.length() >= length) {
            return original;
        }
        
        StringBuilder sb = new StringBuilder();
        for (int i = original.length(); i < length; i++) {
            sb.append(padChar);
        }
        
        sb.append(original);
        return sb.toString();
    }
    
}
