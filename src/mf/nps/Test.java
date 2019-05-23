package mf.nps;

//import java.util.*;
import java.util.*;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

public class Test {

    public static void main(String[] args) throws Exception {
//        JSONObject obj = new JSONObject();
//        obj.put("nodeName", "node1");
//        obj.put("cpu_avg", 22.35f);
//
//        JSONArray node_cpu = new JSONArray();
//        node_cpu.add(obj);
//
//        obj.put("nodeName", "node2");
//        obj.put("cpu_avg", 0.04f);
//
//        node_cpu.add(obj);
//
//        System.out.println(node_cpu.toJSONString());
//        JSONObject all = new JSONObject();

//        Long tl = Long.parseLong("4295063622");
//        System.out.println(tl);

        //System.out.print(obj);
//        Calendar cale = null;
//        cale = Calendar.getInstance();
//        String sb=Util.getInterfaceThroughput("2019-05-12","2019-05-17");
//
//        System.out.println(sb);
//        Float f = new Float(0.33);
//        String sf = f.toString();
//        System.out.println(sf);
//        HashMap<Integer, String> th = new HashMap<>();
//        th.put(new Integer(1), "a");
//        th.put(new Integer(2), "a");
//        th.put(new Integer(3), "b");
//        HashSet<String> hs = new HashSet();
//        if (hs.isEmpty()){
//            System.out.println(hs.size());
//        };
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
       //  CPU TEST
        Util.initMaps();
        Util.initL1AllNodeNames(4295063622L);
        String r = Util.level1GroupAllNodesCpuUtilization(4295063622L, "2019-05-15");
        System.out.println(r);

//        HashSet<String> ths = new HashSet<>();
//        ths.add("a");
//        ths.add("b");
//        ths.add("C");
//        for (String s : ths){
//            System.out.println(s);
//        }

//        System.out.println(Util.initL1AllNodeNames(4301105449l));
//        System.out.println(Util.getAllNodeInGroup1(4295063622l));
        //List<String> list =Util.getAvail(Util.getAllNodeInGroup1(4295063622l),"2019-05-10","2019-05-12");

    }
}
