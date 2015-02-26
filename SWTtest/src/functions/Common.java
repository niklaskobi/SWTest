package functions;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Common {

	/**
	 * converts a double value into a string
	 * @param value
	 * @return
	 */
	public static String doubleToTime(Double v)
	{
		double value = v;
        Date m = new java.sql.Date((long) value);
        //DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        DateFormat df = new SimpleDateFormat("HH:mm:ss");
        return df.format(m);
	}
}
