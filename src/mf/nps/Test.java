package mf.nps;

import java.util.*;

public class Test {

    public static void main(String[] args) throws Exception {
//        Calendar cale = null;
//        cale = Calendar.getInstance();
//        String sb=Util.getInterfaceThroughput("2019-05-12","2019-05-17");
//
//        System.out.println(sb);
//        Float f = new Float(0.33);
//        String sf = f.toString();
//        System.out.println(sf);
//        HashMap<Long, List<Long>> th = new HashMap<>();
//        th.put(2148498650L, [4307008320l, 4307003370l, 4307004893l, 4307008320l, 4307003370l, 4307004893l]);
//        List<Long> list = new ArrayList();
//        list.add(4307008320l);
//        list.add(4307008320l);
//        list.add(4307003370l);
//        list.add(4307003370l);
//        //HashSet<Long> ht = new HashSet(list);
//        HashSet<Long> ht = new HashSet<>();
//        ht.addAll(list);
//        System.out.print(ht.toString());
        Util.initMaps();

//        System.out.println(Util.initL1AllNodeNames(4301105449l));
//        System.out.println(Util.getAllNodeInGroup1(4295063622l));
        List<String> list =Util.getAvail(Util.getAllNodeInGroup1(4295063622l),"2019-05-10","2019-05-12");

    }
}
