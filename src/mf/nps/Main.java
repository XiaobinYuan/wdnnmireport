package mf.nps;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Main {

    public static void main(String[] args) throws Exception {
        Connection conn=Util.getPostgressConnection();

        Set<String> nodes=new HashSet<>();

        Statement stmt = conn.createStatement();
        ResultSet rs = null;

        rs=stmt.executeQuery("select node_uuid from nms_incidents where lifecyclestate<113 and( name='InterfaceDown' or name='NodeDown')");
        while ( rs.next() ) {
            nodes.add(rs.getString(1));
        }

        Set<String> no_normal_nodes=new HashSet<>();
        Map<String,String> node_ip=new HashMap<>();

        rs=stmt.executeQuery("select nms_node.uuid ,nms_node.long_name,nms_node.name from nms_node_stat ,nms_node where nms_node_stat.cur_stat>1 and nms_node.id=nms_node_stat.id");
        while ( rs.next() ) {
            no_normal_nodes.add(rs.getString(1));
            node_ip.put(rs.getString(1),rs.getString(2));
        }
        System.out.println(node_ip.size());
        for(String uuid:nodes){
//            no_normal_nodes.remove(uuid);
            node_ip.remove(uuid);
        }
        System.out.println(node_ip.size());

        int i=1;
        for(String ip:node_ip.values()){
//            System.out.println("/opt/OV/support/nnmtwiddle.ovpl invoke com.hp.ov.nms.apa:service=NmsApa clearConclusionsAndStatusOnAllObjectsInNodeThenStatusPoll "+ip);
            System.out.println("echo \""+"------------------------------"+i+"\"");

            System.out.println("/opt/OV/support/nnmtwiddle.ovpl invoke com.hp.ov.nms.apa:service=NmsApa clearConclusionsAndStatusOnAllObjectsInNodeThenStatusPoll "+ip);
            i++;


        }


    }
}
