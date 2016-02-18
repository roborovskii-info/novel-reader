package info.bunny178.util;

import org.simpleframework.xml.transform.Transform;

import java.text.DateFormat;
import java.util.Date;

/**
 *
 * Simple-Frameworkの日付パース用クラス
 *
 * @author ISHIMARU Sohei on 2014/09/29.
 */
public class DateFormatTransformer implements Transform<Date> {

    private DateFormat dateFormat;

    public DateFormatTransformer(DateFormat dateFormat) {
        this.dateFormat = dateFormat;
    }

    @Override
    public Date read(String value) throws Exception {
        return dateFormat.parse(value);
    }

    @Override
    public String write(Date value) throws Exception {
        return dateFormat.format(value);
    }
}
