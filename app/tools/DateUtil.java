package tools;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class DateUtil {

    public static Long getTimeNow() {

        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        return calendar.getTime().getTime();
    }

    public static String getDate(Long time) {

        final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss z");

        // Give it to me in GMT time.
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        return sdf.format(time);

    }
}
