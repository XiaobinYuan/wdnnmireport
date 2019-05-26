package mf.nps;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;


public class CPUMemServlet extends HttpServlet {
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req,resp);
    }

    @Override
    public void init() throws ServletException {
        super.init();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html;charset=utf-8");
        resp.setHeader("Access-Control-Allow-Origin", "*");
        PrintWriter out = resp.getWriter();

        String start=req.getParameter("start");
        String end=req.getParameter("end");
        String reportid=req.getParameter("reportid");
        String result=Util.cache.get(reportid+"@"+start+end);
        if(Util.cache.size()>50)
            Util.cache.clear();
        if(result!=null){
            System.out.println(reportid+":get data from cache...");
            out.write(result);
            out.flush();
            out.close();

        }else {
            System.out.println(reportid+":get data from db...");
            Map<String,String> list =Util.getCpuMem(Util.getAllNodeInGroup1(4295063622l),start,end);
            StringBuilder sb=new StringBuilder();
            sb.append("[");
            for(Map.Entry<String,String> entry:list.entrySet()){
                String name=entry.getKey();
                String value=entry.getValue();
                String cpu=value.split("#%#%")[0];
                String mem=value.split("#%#%")[1];

                sb.append("{\"host\":\""+name+"\",");
                sb.append("\"cpu\": "+Util.sishewuru(Double.parseDouble(cpu)*100)+",");
                sb.append("\"mem\": "+Util.sishewuru(Double.parseDouble(mem)*100));

                sb.append("},");

            }
            sb.append("]");
            Util.cache.put(reportid+"@"+start+end,sb.toString().replaceAll("},]","}]"));
            out.write(sb.toString().replaceAll("},]","}]"));
            out.flush();
            out.close();
        }


    }

}
