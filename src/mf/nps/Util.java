package mf.nps;

import com.sun.org.apache.bcel.internal.generic.L2I;
import com.sybase.jdbcx.EedInfo;
import org.omg.Messaging.SYNC_WITH_TRANSPORT;

import javax.naming.NamingException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

public class Util {
    public static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static HashMap<String,String> cache=new HashMap<>();
    public static HashMap<Long , List<Long>> L1L2Mmap=new HashMap<>();
    public static HashMap<Long,Long> childParent=new HashMap<>();

    public static List<String> ParentChildgroup=new ArrayList<>();
    public static HashMap<Long ,List<Long>> L2L3Map=new HashMap<>();
    public static HashMap<Long ,List<Long>> L3Nodemap=new HashMap<>();
    public static HashMap<Long,String> groupNamemap=new HashMap<>();
    public static HashMap<Long,String> id_node_map=new HashMap<>();
    public static HashMap<Long,String> id_nodeIP_map=new HashMap<>();

    public static HashSet<String> zdjkL1AllNodeNames = new HashSet<>();

    static {
//        try {
//            ctx=new InitialContext();
//            envContext=(Context)ctx.lookup("java:/comp/env");
//        }catch (Exception e) {
//            e.printStackTrace();
//        }
        try {
            Class.forName("com.sybase.jdbc4.jdbc.SybDriver");
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    public static Connection getPostgressConnection() throws SQLException {
        Connection result=null;
        result = DriverManager
                .getConnection("jdbc:postgresql://nnm.wanda.cn:5432/nnm?useUnicode=true&amp;characterEncoding=gbk&amp;allowEncodingChanges=true",
                        "postgres", "nnmP0stgr3S");
        return result;

    }

//    public static String initJsTree(){
//        StringBuilder sb=new StringBuilder();
//        initMaps();
////        sb.append("'core' : {'data' : ");
//        sb.append("[");
//        sb.append("{'id' : '11111','parent' : '#','icon' : 'jstree-folder','text':'设备列表',\"state\" :{\"opened\" : true }},");
//        //add items
//
//        for (Map.Entry<Long, String> entry : groupNamemap.entrySet()) {
//            String name=entry.getValue();
//            Long id=entry.getKey();
//            sb.append("{");
//            sb.append("'id': '"+id+"',");
//            Long parentid=childParent.get(id);
//            if(parentid!=null)
//                sb.append("'parent' : '"+parentid+"',");
//            else
//                sb.append("'parent' : '11111',");
//            sb.append("'text':'"+name+"'");
//            sb.append("},");
//
//        }
//        for (Map.Entry<Long, List<Long>> entry : L3Nodemap.entrySet()) {
//            Long pid=entry.getKey();
//            List<Long> list=entry.getValue();
//            for(Long id:list){
//                String nodename=id_node_map.get(id);
//                if(nodename!=null&&groupNamemap.keySet().contains(pid)){
//                    sb.append("{");
//                    sb.append("'id': '"+id+"',");
//                    sb.append("'parent' : '"+pid+"',");
//                    sb.append("'text':'"+nodename+"'");
//                    sb.append("},");
//                }
//            }
//
//        }
//
//
//            sb.append("]");
//        return sb.toString();
//    }


    public static double sishewuru(double d){
        BigDecimal b=new BigDecimal(Double.toString(d));
        return  b.setScale(2, RoundingMode.HALF_UP).doubleValue();

    }
    public static String getbroadband(String start,String end){
        String table="f_Hour_InterfaceMetrics";
        HashMap<String,Bo> objmap=new HashMap<>();

        if((Integer.parseInt(end.split("-")[2])-Integer.parseInt(start.split("-")[2]))>10||(Integer.parseInt(end.split("-")[1])-Integer.parseInt(start.split("-")[1]))>0){
            table="f_Day_InterfaceMetrics";
        }
        String sql="SELECT [Node Name],[Utilization In (avg)],[Utilization Out (avg)],[Interface Name],f.[Throughput In (bps) (avg)],f.[Hour],f.[Day] FROM  " +table+
                " f where (f.[Interface Name]='Ethernet 2' or  f.[Interface Name]='Ethernet 1' or f.[Interface Name]='GigabitEthernet0/0' or f.[Interface Name]='Ten-GigabitEthernet1/0/25')  \n" +
                " and (f.[Node Name]='10.199.243.202' or f.[Node Name]='202.108.222.129'  or f.[Node Name]='10.0.39.45') and f.[Day]>='"+start+"' and f.[Day]<='"+end
                +"' and (f.[Hour (of Day)]='00:00:00' or f.[Hour (of Day)]='12:00:00' or f.[Hour (of Day)]='12:00:00' or " +
                "f.[Hour (of Day)]='06:00:00' or f.[Hour (of Day)]='18:00:00' or f.[Hour (of Day)]='23:00:00') order by Hour";

        try{
            Connection conn=getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = null;

            rs=stmt.executeQuery(sql);
            while ( rs.next() ) {

                String time=rs.getString("Hour").replaceAll(":00.000000","").substring(5);
                if(table.equals("f_Day_InterfaceMetrics"))
                    time=time.replaceAll(" 00:00","").replaceAll(" 12:00","");
                Double in=rs.getDouble("Utilization In (avg)");
                Double out=rs.getDouble("Utilization Out (avg)");
                String obj=rs.getString("Interface Name")+"@"+rs.getString("Node Name");
                Bo bo=objmap.get(obj);
                if(bo==null){
                    bo=new Bo();
                }
                bo.getInutil().put(time,in);
                bo.setName(obj);
                bo.getOututil().put(time,out);
                objmap.put(obj,bo);

            }


        }catch (Exception e){
            e.printStackTrace();
        }

        StringBuilder sb=new StringBuilder();
        for (Map.Entry<String,Bo> entry : objmap.entrySet()) {
            Bo bo=entry.getValue();
            HashMap<String ,Double> inmap=bo.getInutil();
            HashMap<String,Double> outmap=bo.getOututil();

            sb.append("{");
            sb.append("\"data\":[");
            sb.append("{");
            sb.append("\"name\":\""+bo.getName()+"\"");
            sb.append("},");


            sb.append("{");
            sb.append("\"time\":\"");
            for(String time:inmap.keySet()){
                sb.append("'"+time+"',");
            }
            sb.append("\"},");
            sb.append("{");
            sb.append("\"thin\":\"");
            for(Double in:inmap.values()){
                sb.append(sishewuru(in*100)+",");
            }
            sb.append("\"},");
            sb.append("{\"thout\":\"");
            for(Double out:outmap.values()){
                sb.append(sishewuru(out*100)+",");
            }
            sb.append("\"}");
            sb.append("],");
            sb.append("}");
            sb.append("#");
        }

        String str=sb.toString().replaceAll(",\"}","\"}").replaceAll("],}","]}");
        return str.substring(0,str.length()-1).replaceAll("Ethernet 1","Ethernet1(电信1G)")
                .replaceAll("Ethernet 2","Ethernet1(联通1G)")
                .replaceAll("GigabitEthernet0/0","GigabitEthernet0/0(联通100M)")
                .replaceAll("Ten-GigabitEthernet1/0/25","Ten-GigabitEthernet1/0/25(电信400M)");

    }
    public static String getInterfaceThroughput(String start,String end){
        HashMap<String,Bo> objmap=new HashMap<>();
        String table="f_Hour_InterfaceMetrics";
        if((Integer.parseInt(end.split("-")[2])-Integer.parseInt(start.split("-")[2]))>10||(Integer.parseInt(end.split("-")[1])-Integer.parseInt(start.split("-")[1]))>0){
            table="f_Day_InterfaceMetrics";
        }
        String sql="SELECT [Node Name] ,[Interface Name],f.[Throughput In (bps) (avg)],f.[Throughput Out (bps) (avg)],f.[Day (of Month)],f.[Hour],f.[Day] FROM "+ table
                +" f where (f.[Interface Name]='xe-0/1/7' or  f.[Interface Name]='xe-0/2/7.0') and (f.[Node Name]='10.199.242.141' or  f.[Node Name]='10.199.242.137') and f.[Day]>='"+start+"' and f.[Day]<='"+end
                +"' and (f.[Hour (of Day)]='00:00:00' or f.[Hour (of Day)]='12:00:00' or f.[Hour (of Day)]='12:00:00' or " +
                "f.[Hour (of Day)]='06:00:00' or f.[Hour (of Day)]='18:00:00' or f.[Hour (of Day)]='23:00:00') order by Hour";
        try{
            Connection conn=getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = null;

            rs=stmt.executeQuery(sql);
            while ( rs.next() ) {

                String time=rs.getString("Hour").replaceAll(":00.000000","").substring(5);
                if(table.equals("f_Day_InterfaceMetrics"))
                    time=time.replaceAll(" 00:00","").replaceAll(" 12:00","");
                Long in=rs.getLong("Throughput In (bps) (avg)");
                Long out=rs.getLong("Throughput Out (bps) (avg)");
                String obj=rs.getString("Interface Name")+"@"+rs.getString("Node Name");
                Bo bo=objmap.get(obj);
                if(bo==null){
                    bo=new Bo();
                }
                bo.getInmap().put(time,in);
                bo.setName(obj);
                bo.getOut().put(time,out);
                objmap.put(obj,bo);

            }


        }catch (Exception e){
            e.printStackTrace();
        }

        StringBuilder sb=new StringBuilder();
        for (Map.Entry<String,Bo> entry : objmap.entrySet()) {
            Bo bo=entry.getValue();
            HashMap<String ,Long> inmap=bo.getInmap();
            HashMap<String,Long> outmap=bo.getOut();
            sb.append("{");
            sb.append("\"data\":[");
            sb.append("{");
            sb.append("\"name\":\""+bo.getName()+"\"");
            sb.append("},");


            sb.append("{");
            sb.append("\"time\":\"");
            for(String time:inmap.keySet()){
                sb.append("'"+time+"',");
            }
            sb.append("\"},");
            sb.append("{");
            sb.append("\"thin\":\"");
            for(Long in:inmap.values()){
                sb.append(in+",");
            }
            sb.append("\"},");
            sb.append("{\"thout\":\"");
            for(Long out:outmap.values()){
                sb.append(out+",");
            }
            sb.append("\"}");
            sb.append("],");
            sb.append("}");
            sb.append("#");
        }

        String str=sb.toString().replaceAll(",\"}","\"}").replaceAll("],}","]}");
        return str.substring(0,str.length()-1);
    }

    public static String initTree(){
        initMaps();
        StringBuilder sb=new StringBuilder();
        sb.append("{\"data\": ");
        sb.append("[");
        for (Map.Entry<Long, List<Long>> entry : L1L2Mmap.entrySet()) {
            //add l1
                Long id=entry.getKey();
                String l1name=groupNamemap.get(id);
                sb.append("{");
                sb.append("\"id\": \""+id+"\",");
                sb.append("\"text\":");
                sb.append("\""+l1name+"\",");
                sb.append("\"children\":[");
                //add l2
                List<Long> l2=entry.getValue();
                for(int k=0;k<l2.size();k++){
//                for(Long l2id:l2){
                    Long l2id=l2.get(k);
                    String l2name=groupNamemap.get(l2id);
                    sb.append("{");
                    sb.append("\"id\":");
                    sb.append("\""+l2id+"\",");
                    sb.append("\"text\":");
                    sb.append("\""+l2name+"\",");
                    sb.append(" \"children\": ");
                    //add l3
                        sb.append("[");
                        List<Long> l3=L2L3Map.get(l2id);
                        if(l3==null){
                            try {
                                System.out.println(new String(l2name.getBytes(),"UTF-8")+":"+l2id);
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                            continue;
                        }
                        for(int i=0;i<l3.size();i++){
                            Long l3id=l3.get(i);
//                        for(Long l3id:l3){
                            sb.append("{");
                            sb.append("\"id\":");
                            sb.append("\""+l3id+"\",");
                            sb.append("\"text\":");
                            sb.append("\""+groupNamemap.get(l3id)+"\",");
                            sb.append(" \"children\": ");

                                //add node
                                sb.append("[");
                                sb.append("]");
                            sb.append("}");
                            if(i<l3.size()-1)
                                sb.append(",");
                        }
                        sb.append("]");
                    sb.append("}");
                    if(k<l2.size()-1)
                        sb.append(",");



                }
                sb.append("]");
                sb.append("}");

                break;

        }
        sb.append("]}");

        return sb.toString();
    }
    public static Connection getConnection() throws SQLException {
        String url = "jdbc:sybase:Tds:nps.wanda.cn:9303/PerfSPIDSN";
        String Userid = "DBA";//数据库用户名
        String Password = "HP_IQ";//密码
        Connection conn = null;

        try {
            conn = DriverManager.getConnection(url, Userid, Password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }

    public static HashMap<Long,String > getAllNodeInGroup1(Long groupid){
        HashMap<Long,String > map=new HashMap<>();
        List<Long> group2id=L1L2Mmap.get(groupid);
        for(Long id:group2id){
            List<Long> L3group=L2L3Map.get(id);
            if(L3group!=null){
               for(Long l3id:L3group){
                   List<Long> nodes=L3Nodemap.get(l3id);
                   if(nodes!=null){
                       for(Long nodeid:nodes){
                           map.put(nodeid,id_node_map.get(nodeid));
                       }
                   }

               }
            }
        }


        return map;



    }
    public static List<String> getZDJKIterfaceAvail(HashMap<Long,String > map,String Start,String end){
        List<String> list=new ArrayList<>();
        StringBuilder sql=new StringBuilder();
        sql.append("select f.[Node Short Name],f.[Node Name],avg([Availability (avg)]) FROM [DBA].f_Hour_InterfaceMetrics f where ");
        int i=0;
        Iterator<Long> iter=map.keySet().iterator();
        while ((iter.hasNext())){
            Long nodeid=iter.next();
            if(i==0){
                sql.append("(f.[Node ID]="+nodeid);
            }

            else {
                sql.append(" or f.[Node ID]="+nodeid);
            }
            i++;
        }
        sql.append(") group by f.[Node Name],f.[Node Short Name] order by avg([Availability (avg)]) ASC");
        try {
            Connection connection = getConnection();
            Statement stmt = connection.createStatement();
            ResultSet rs =stmt.executeQuery(sql.toString());
            while ( rs.next()){
                list.add(rs.getDouble(3)+"#%#%"+rs.getString(1)+"("+rs.getString(2)+")");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    public static Map<String,String> getCpuMem(HashMap<Long,String > map,String Start,String end){
        Map<String,String> data=new HashMap<>();
        StringBuilder sql=new StringBuilder();
        StringBuilder memsql=new StringBuilder();
        sql.append("SELECT avg(f.[CPU 1min Utilization (avg)]),f.[Node Short Name],f.[Node Name] FROM [DBA].f_Hour_ComponentMetrics " +
                "f where f.[Component Type]='CPU' and " );

        memsql.append("SELECT avg(f.[Memory Utilization (avg)]),f.[Node Short Name],f.[Node Name] FROM [DBA]." +
                "f_Hour_ComponentMetrics f where f.[Component Type]='MEMORY' and " );
        int i=0;
        Iterator<Long> iter=map.keySet().iterator();
        while ((iter.hasNext())){
            Long nodeid=iter.next();
            if(i==0){
                sql.append("(f.[Node ID]="+nodeid);
                memsql.append("(f.[Node ID]="+nodeid);
            }
            else {
                sql.append(" or f.[Node ID]="+nodeid);
                memsql.append(" or f.[Node ID]="+nodeid);

            }
            i++;
        }
        sql.append(")");
        sql.append(" and f.[Day]>='"+Start+"' and f.[Day]<'"+end+"' group by f.[Node Short Name],f.[Node ID],f.[Node Name] order by avg(f.[CPU 1min Utilization (avg)])");

        memsql.append(")");
        memsql.append(" and f.[Day]>='"+Start+"' and f.[Day]<'"+end+"' group by f.[Node Short Name],f.[Node ID],f.[Node Name] order by avg(f.[Memory Utilization (avg)])");
        try {
            Connection connection = getConnection();
            Statement stmt = connection.createStatement();
            ResultSet rs =stmt.executeQuery(sql.toString());
            while ( rs.next()){
                data.put(rs.getString(2)+"("+rs.getString(3)+")",(rs.getDouble(1)+"#%#%"));
            }

             rs =stmt.executeQuery(memsql.toString());
            while ( rs.next()){
                String value=data.get(rs.getString(2)+"("+rs.getString(3)+")");
                if(value!=null){
                    value=value+rs.getDouble(1);
                }else {
                    value="0#%#%"+rs.getDouble(1);
                }
                data.put(rs.getString(2)+"("+rs.getString(3)+")",value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return  data;
    }
    public static List<String> getAllAvail(String Start,String end){
        List<String> list=new ArrayList<>();

        StringBuilder sql=new StringBuilder();
        sql.append("SELECT avg(f.[Node Availability (avg)]),f.[Node Short Name],f.[Node Name] FROM [DBA].f_Hour_ComponentMetrics f where  ");
        sql.append(" f.[Day]>='"+Start+"' and f.[Day]<'"+end+"' group by f.[Node Short Name],f.[Node ID],f.[Node Name] order by avg(f.[Node Availability (avg)])");

        try {
            Connection connection = getConnection();
            Statement stmt = connection.createStatement();
            ResultSet rs =stmt.executeQuery(sql.toString());
            while ( rs.next()){
                list.add(rs.getDouble(1)+"#%#%"+rs.getString(2)+"("+rs.getString(3)+")");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        return list;
    }
    public static List<String> getAvail(HashMap<Long,String > map,String Start,String end){
        List<String> list=new ArrayList<>();
        StringBuilder sql=new StringBuilder();
        sql.append("SELECT avg(f.[Node Availability (avg)]),f.[Node Short Name],f.[Node Name] FROM [DBA].f_Hour_ComponentMetrics f where  ");
        int i=0;
        Iterator<Long> iter=map.keySet().iterator();
        while ((iter.hasNext())){
            Long nodeid=iter.next();
            if(i==0){
                sql.append("(f.[Node ID]="+nodeid+" and f.[Component ID]="+nodeid+")");
            }

            else {
                sql.append(" or (f.[Node ID]="+nodeid+" and f.[Component ID]="+nodeid+")");
            }
            i++;
        }

        sql.append(" and f.[Day]>='"+Start+"' and f.[Day]<'"+end+"' group by f.[Node Short Name],f.[Node ID],f.[Node Name] order by avg(f.[Node Availability (avg)])");

        try {
            Connection connection = getConnection();
            Statement stmt = connection.createStatement();
            ResultSet rs =stmt.executeQuery(sql.toString());
            while ( rs.next()){
                list.add(rs.getDouble(1)+"#%#%"+rs.getString(2)+"("+rs.getString(3)+")");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;

    }
    public static void initL1AllNodeNames(Long L1GroupId){
        HashMap<Long, String> nodeid_name = getAllNodeInGroup1(L1GroupId);
        HashSet<String> nodeNames = new HashSet(nodeid_name.values());
        if (L1GroupId == 4295063622L){
            Util.zdjkL1AllNodeNames = nodeNames;

        }

    }
    public static JSONObject getCpuAvg(String nodeName, String start, Connection connection){
        String sql = "select  avg(f.[CPU 1min Utilization (avg)]) from [DBA].fv1_Day_ComponentMetrics f where f.[Component Type]='CPU' and f.[Node Short Name]="+"'"+nodeName+"'"+" and f.[CPU 1min Utilization (avg)]!=null and f.Day>='"+start+"'";

        try{
            JSONObject cpuDataJSONObj = new JSONObject();
            Statement stmt = connection.createStatement();
            ResultSet rs =stmt.executeQuery(sql);
            while ( rs.next()){ //1 column in rs
                Float cpuUtilization = rs.getFloat(1);
                cpuDataJSONObj.put("nodeName", nodeName);
                cpuDataJSONObj.put("cpuAvg", cpuUtilization);
            }
            return cpuDataJSONObj;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
    public static String level1GroupAllNodesCpuUtilization(Long L1, String start){
        //return "[{\"nodeName\":\"BJ-ZB-CS-IRF\",\"cpuAvg\":0.028054412},{\"nodeName\":\"BJ_ZB_F17_HJ\",\"cpuAvg\":0.020868752},{\"nodeName\":\"BJ-ZB-F11-POE\",\"cpuAvg\":0.0},{\"nodeName\":\"BJ_ZB_F16_4\",\"cpuAvg\":0.0}]";
        JSONArray allNodesCpuAvgJSON = new JSONArray();
//        System.out.println("Start to getting cpu avg for all nodes under group id " + L1.toString() + "and start : " + start);
        try{
            Connection connection = getConnection();
            if (L1 == 4295063622l){
//                System.out.println("--------IN----------");
                for (String nodeName:zdjkL1AllNodeNames){
                    JSONObject nodeCpuAvg = getCpuAvg(nodeName, start, connection);
                    allNodesCpuAvgJSON.add(nodeCpuAvg);
                }
            }
            connection.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return allNodesCpuAvgJSON.toJSONString();
    }
    public static void initMaps(){
        String gourpL1="select id from nms_node_groups where notes='L1'";
        String gourpL2="select id from nms_node_groups where notes ='L2' ";
        String gourpL3="select id from nms_node_groups where notes='L3'";
        String nms_node_group_hierarchy_sql="select child,parent from nms_node_group_hierarchy";
        String nodeL3="select grp,node from nms_nodegrp_assn where direct=true ";
        try {

            Connection conn=getPostgressConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = null;

            rs=stmt.executeQuery(nodeL3);
            while ( rs.next() ) {
                List<Long> list=L3Nodemap.get(rs.getLong(1));
                if(list==null)
                    list=new ArrayList<Long>();
                list.add(rs.getLong(2));
                L3Nodemap.put(rs.getLong(1),list);
            }

            rs=stmt.executeQuery(gourpL3);
            List<Long> l3group=new ArrayList<>();
            while ( rs.next() ) {
                l3group.add(rs.getLong(1));
            }
            rs=stmt.executeQuery(gourpL1);
            List<Long> l1group=new ArrayList<>();
            while ( rs.next() ) {
                long parentid=rs.getLong(1);
                l1group.add(parentid);

            }
            rs=stmt.executeQuery(gourpL2);
            List<Long> l2group=new ArrayList<>();
            while ( rs.next() ) {
                l2group.add(rs.getLong(1));

            }
            rs=stmt.executeQuery("select id,name  from nms_node_groups");
            while ( rs.next() ) {
                String gname=rs.getString(2);
                long grupid=rs.getLong(1);

                if(!gname.contains("Island"))
                    groupNamemap.put(grupid,gname);
            }
            rs= stmt.executeQuery(nms_node_group_hierarchy_sql);
            while ( rs.next() ) {

                long parent=rs.getLong(2);
                long child=rs.getLong(1);
                if(l1group.contains(parent)){
                    List<Long> list=L1L2Mmap.get(parent);
                    if(list==null)
                        list=new ArrayList<Long>();
                    list.add(child);
                    L1L2Mmap.put(parent,list);

                }
                if(l2group.contains(parent)){
                    List<Long> list=L2L3Map.get(parent);
                    if(list==null)
                        list=new ArrayList<Long>();
                    list.add(child);
                    L2L3Map.put(parent,list);
                }
//                ParentChildgroup.add(parent+"@"+child);
                childParent.put(child,parent);
            }
            rs= stmt.executeQuery("select id ,name,long_name from nms_node");
            while ( rs.next() ) {

                id_node_map.put(rs.getLong(1),rs.getString(2));
                id_nodeIP_map.put(rs.getLong(1),rs.getString(3));
            }
            rs.close();
            stmt.close();
            conn.close();



        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    static {
        String gourpL1="select id from nms_node_groups where notes='L1'";
        String gourpL2="select id from nms_node_groups where notes ='L2' ";
        String gourpL3="select id from nms_node_groups where notes='L3'";
        String nms_node_group_hierarchy_sql="select child,parent from nms_node_group_hierarchy";
        String nodeL3="select grp,node from nms_nodegrp_assn where direct=true ";
        try {

            Connection conn=getPostgressConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = null;

            rs=stmt.executeQuery(nodeL3);
            while ( rs.next() ) {
                List<Long> list=L3Nodemap.get(rs.getLong(1));
                if(list==null)
                    list=new ArrayList<Long>();
                list.add(rs.getLong(2));
                L3Nodemap.put(rs.getLong(1),list);
            }

            rs=stmt.executeQuery(gourpL3);
            List<Long> l3group=new ArrayList<>();
            while ( rs.next() ) {
                l3group.add(rs.getLong(1));
            }
            rs=stmt.executeQuery(gourpL1);
            List<Long> l1group=new ArrayList<>();
            while ( rs.next() ) {
                long parentid=rs.getLong(1);
                l1group.add(parentid);

            }
            rs=stmt.executeQuery(gourpL2);
            List<Long> l2group=new ArrayList<>();
            while ( rs.next() ) {
                l2group.add(rs.getLong(1));

            }
            rs=stmt.executeQuery("select id,name  from nms_node_groups");
            while ( rs.next() ) {
                String gname=rs.getString(2);
                long grupid=rs.getLong(1);

                if(!gname.contains("Island"))
                    groupNamemap.put(grupid,gname);
            }
            rs= stmt.executeQuery(nms_node_group_hierarchy_sql);
            while ( rs.next() ) {

                long parent=rs.getLong(2);
                long child=rs.getLong(1);
                if(l1group.contains(parent)){
                    List<Long> list=L1L2Mmap.get(parent);
                    if(list==null)
                        list=new ArrayList<Long>();
                    list.add(child);
                    L1L2Mmap.put(parent,list);

                }
                if(l2group.contains(parent)){
                    List<Long> list=L2L3Map.get(parent);
                    if(list==null)
                        list=new ArrayList<Long>();
                    list.add(child);
                    L2L3Map.put(parent,list);
                }
//                ParentChildgroup.add(parent+"@"+child);
                childParent.put(child,parent);
            }
            rs= stmt.executeQuery("select id ,name,long_name from nms_node");
            while ( rs.next() ) {

                id_node_map.put(rs.getLong(1),rs.getString(2));
                id_nodeIP_map.put(rs.getLong(1),rs.getString(3));
            }
            rs.close();
            stmt.close();
            conn.close();



        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
