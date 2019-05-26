package mf.nps;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;


public class AllAvailServlet extends HttpServlet {
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

        if(result!=null){
            System.out.println(reportid+":get data from cache...");
            out.write(result);
            out.flush();
            out.close();

        }else {
            System.out.println(reportid+":get data from db...");
            List<String> list =Util.getAllAvail(start,end);
            StringBuilder sb=new StringBuilder();
            sb.append("[");
            for(String str:list){
                String[] strs=str.split("#%#%");
                sb.append("{\"id\":\""+strs[1]+"\",");
                sb.append("\"name\": "+Util.sishewuru(Double.parseDouble(strs[0])));
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
