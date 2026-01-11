package co.com.techtest.sqs.listener.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class CleanBodyUtil {

    public static String cleanBody(String body) {
        return body.replace("\t", "").replace("\n", "").replace("\\", "");
    }
}
