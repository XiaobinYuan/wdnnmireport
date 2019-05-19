package mf.nps;

import com.sybase.jdbcx.EedInfo;
import org.omg.Messaging.SYNC_WITH_TRANSPORT;

import javax.naming.NamingException;
import java.io.UnsupportedEncodingException;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class Util {
    public static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static HashMap<Long , List<Long>> L1L2Mmap=new HashMap<>();
    public static HashMap<Long,Long> childParent=new HashMap<>();

    public static List<String> ParentChildgroup=new ArrayList<>();
    public static HashMap<Long ,List<Long>> L2L3Map=new HashMap<>();
    public static HashMap<Long ,List<Long>> L3Nodemap=new HashMap<>();
    public static HashMap<Long,String> groupNamemap=new HashMap<>();
    public static HashMap<Long,String> id_node_map=new HashMap<>();

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

    public static String getInterfaceThroughput(String start,String end){
        HashMap<String,Bo> objmap=new HashMap<>();
        String table="f_Hour_InterfaceMetrics";
        if((Integer.parseInt(end.split("-")[2])-Integer.parseInt(start.split("-")[2]))>10||(Integer.parseInt(end.split("-")[1])-Integer.parseInt(start.split("-")[1]))>0){
            table="f_Day_InterfaceMetrics";
        }
//        HashMap<String,Long> inmap=new HashMap<>();
//        HashMap<String,Long> outmap=new HashMap<>();
//        Calendar cale = null;
//        cale = Calendar.getInstance();
        String sql="SELECT [Node Name] ,[Interface Name],f.[Throughput In (bps) (avg)],f.[Throughput Out (bps) (avg)],f.[Day (of Month)],f.[Hour],f.[Day] FROM "+ table
                +" f where (f.[Interface Name]='xe-0/1/7' or  f.[Interface Name]='xe-0/2/7.0') and (f.[Node Name]='10.199.242.141' or  f.[Node Name]='10.199.242.137') and f.[Day]>='"+start+"' and f.[Day]<='"+end
                +"' and (f.[Hour (of Day)]='00:00:00' or f.[Hour (of Day)]='12:00:00' or f.[Hour (of Day)]='12:00:00' or " +
                "f.[Hour (of Day)]='06:00:00' or f.[Hour (of Day)]='18:00:00' or f.[Hour (of Day)]='23:00:00') order by Hour";
//        System.out.println(sql);
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

//    public static String initTree(){
//        initMaps();
//        StringBuilder sb=new StringBuilder();
//        sb.append("{\"data\": ");
//        sb.append("[");
//        for (Map.Entry<Long, List<Long>> entry : L1L2Mmap.entrySet()) {
//            //add l1
//                Long id=entry.getKey();
//                String l1name=groupNamemap.get(id);
//                System.out.println(l1name);
//                sb.append("{");
//                sb.append("\"id\": \""+id+"\",");
//                sb.append("\"text\":");
//                sb.append("\""+l1name+"\",");
//                sb.append("\"children\":[");
//                //add l2
//                List<Long> l2=entry.getValue();
//                for(int k=0;k<l2.size();k++){
////                for(Long l2id:l2){
//                    Long l2id=l2.get(k);
//                    String l2name=groupNamemap.get(l2id);
//                    sb.append("{");
//                    sb.append("\"id\":");
//                    sb.append("\""+l2id+"\",");
//                    sb.append("\"text\":");
//                    sb.append("\""+l2name+"\",");
//                    sb.append(" \"children\": ");
//                    //add l3
//                        sb.append("[");
//                        List<Long> l3=L2L3Map.get(l2id);
//                        if(l3==null){
//                            try {
//                                System.out.println(new String(l2name.getBytes(),"UTF-8")+":"+l2id);
//                            } catch (UnsupportedEncodingException e) {
//                                e.printStackTrace();
//                            }
//                            continue;
//                        }
//                        for(int i=0;i<l3.size();i++){
//                            Long l3id=l3.get(i);
////                        for(Long l3id:l3){
//                            sb.append("{");
//                            sb.append("\"id\":");
//                            sb.append("\""+l3id+"\",");
//                            sb.append("\"text\":");
//                            sb.append("\""+groupNamemap.get(l3id)+"\",");
//                            sb.append(" \"children\": ");
//
//                                //add node
//                                sb.append("[");
//                                sb.append("]");
//                            sb.append("}");
//                            if(i<l3.size()-1)
//                                sb.append(",");
//                        }
//                        sb.append("]");
//                    sb.append("}");
//                    if(k<l2.size()-1)
//                        sb.append(",");
//
//
//
//                }
//                sb.append("]");
//                sb.append("}");
//
//                break;
//
//        }
//        sb.append("]}");
//
//        return sb.toString();
//    }
    public static Connection getConnection() throws SQLException {
        String url = "jdbc:sybase:Tds:nps.wanda.cn:9303/PerfSPIDSN";
        String Userid = "DBA";//数据库用户名
        String Password = "HP_IQ";//密码
        Connection conn = null;

        try {
            conn = DriverManager.getConnection(url, Userid, Password);
        } catch (Exception e) {
            System.out.println(e.getCause());
            e.printStackTrace();
        }
        return conn;
    }


//    public static String getInterfaceThroughput(String interfaceAlias){
//        try {
//            Connection c = getConnection();
//            Statement stmt = c.createStatement();
//            String sql = "select s.cur_stat,s.prev_stat,s.last_change ,n.name  from nms_node_group_stat s ,nms_node_groups n where n.id=s.id and s.id>50000";
//            ResultSet rs = stmt.executeQuery(sql);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//
//
//        return "";
//    }
//    public static void initMaps(){
//        String gourpL1="select id from nms_node_groups where notes='L1'";
//        String gourpL2="select id from nms_node_groups where notes ='L2' ";
//        String gourpL3="select id from nms_node_groups where notes='L3'";
//        String nms_node_group_hierarchy_sql="select child,parent from nms_node_group_hierarchy";
//        String nodeL3="select grp,node from nms_nodegrp_assn where direct=true ";
//        try {
//
//            Connection conn=getPostgressConnection();
//            Statement stmt = conn.createStatement();
//            ResultSet rs = null;
//
//            rs=stmt.executeQuery(nodeL3);
//            while ( rs.next() ) {
//                List<Long> list=L3Nodemap.get(rs.getLong(1));
//                if(list==null)
//                    list=new ArrayList<Long>();
//                list.add(rs.getLong(2));
//                L3Nodemap.put(rs.getLong(1),list);
//            }
//
//            rs=stmt.executeQuery(gourpL3);
//            List<Long> l3group=new ArrayList<>();
//            while ( rs.next() ) {
//                l3group.add(rs.getLong(1));
//            }
//            rs=stmt.executeQuery(gourpL1);
//            List<Long> l1group=new ArrayList<>();
//            while ( rs.next() ) {
//                long parentid=rs.getLong(1);
//                l1group.add(parentid);
//
//            }
//            rs=stmt.executeQuery(gourpL2);
//            List<Long> l2group=new ArrayList<>();
//            while ( rs.next() ) {
//                l2group.add(rs.getLong(1));
//
//            }
//            rs=stmt.executeQuery("select id,name  from nms_node_groups");
//            while ( rs.next() ) {
//                String gname=rs.getString(2);
//                long grupid=rs.getLong(1);
//
//                if(!gname.contains("Island"))
//                    groupNamemap.put(grupid,gname);
//            }
//            rs= stmt.executeQuery(nms_node_group_hierarchy_sql);
//            while ( rs.next() ) {
//
//                long parent=rs.getLong(2);
//                long child=rs.getLong(1);
//                if(l1group.contains(parent)){
//                    List<Long> list=L1L2Mmap.get(parent);
//                    if(list==null)
//                        list=new ArrayList<Long>();
//                    list.add(child);
//                    L1L2Mmap.put(parent,list);
//
//                }
//                if(l2group.contains(parent)){
//                    List<Long> list=L2L3Map.get(parent);
//                    if(list==null)
//                        list=new ArrayList<Long>();
//                    list.add(child);
//                    L2L3Map.put(parent,list);
//                }
////                ParentChildgroup.add(parent+"@"+child);
//                childParent.put(child,parent);
//            }
//            rs= stmt.executeQuery("select id ,name from nms_node");
//            while ( rs.next() ) {
//
//                id_node_map.put(rs.getLong(1),rs.getString(2));
//            }
//            rs.close();
//            stmt.close();
//            conn.close();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }

}
