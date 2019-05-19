package mf.nps;

import java.util.Calendar;

public class Test {

    public static void main(String[] args) throws Exception {
        Calendar cale = null;
        cale = Calendar.getInstance();
        String sb=Util.getInterfaceThroughput("2019-05-12","2019-05-17");

        System.out.println(sb);

    }
}
