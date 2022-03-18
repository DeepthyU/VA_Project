package preprocessing;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//readfile
//writefile
public class Utils {

    public static Timestamp format_date(String oldDateString, int format) {
        Pattern pattern = Pattern.compile("((\\d{2}|[0-9])[\\s\\.\\-\\/]\\d{2}[\\s\\.\\-\\/]\\d{4})|(\\d{4}[\\s\\.\\-\\/]\\d{2}[\\s\\.\\-\\/]\\d{2})|((\\d{2}|[0-9])\\s*((J|j)(anuary|une|uly|an|un|ul|AN|UN|UL)|(F|f)(ebruary|eb|an|EB)|(M|m)(arch|ar|AR|ay|AY)|(A|a)(pril|ugust|pr|ug|PR|UG)|(S|s)(eptember|ept|EPT)|(O|o)(ctober|ct|CT)|(N|n)(ovember|ov|OV)|(D|d)(ecember|ec|EC))\\s*\\d{4})");
        Matcher matcher = pattern.matcher(oldDateString);
        boolean matchFound = matcher.find();
        if (matchFound) {
            String old_format = "dd MMMM yyyy";

            try {
                SimpleDateFormat sdf = new SimpleDateFormat(old_format);
                Date d = sdf.parse(oldDateString);
                Timestamp ts = new Timestamp(d.getTime());
                //sdf.applyPattern(new_format);
                return ts; //sdf.format(d);
            } catch (ParseException e) {
                old_format = "yyyy/MM/dd";
                SimpleDateFormat sdf = new SimpleDateFormat(old_format);
                try {
                    Date d = sdf.parse(oldDateString);
                    Timestamp ts = new Timestamp(d.getTime());
                    return ts;
                } catch (ParseException parseException) {
                    old_format = "ddMMMM yyyy";
                    sdf = new SimpleDateFormat(old_format);
                    try {
                        Date d = sdf.parse(oldDateString);
                        Timestamp ts = new Timestamp(d.getTime());
                        return ts;
                    } catch (ParseException parseEx) {
                        parseException.printStackTrace();
                    }
                }
            }
        }
        return null;
    }

    public static String readFile(String path, Charset encoding) {
        try {
            byte[] encoded = Files.readAllBytes(Paths.get(path));
            return new String(encoded, encoding);
        } catch (IOException e) {
            return "";
        }
    }

}