package mf.nps;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;


public class CpuUtilizationServlet extends HttpServlet {
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
        String id_string = req.getParameter("level1GroupId");
        Long id_Long = Long.parseLong(id_string);
        System.out.print("printing out gouprid and start pass by jquery:");
        System.out.println(id_Long);
        System.out.println(start);
        if (Util.zdjkL1AllNodeNames.isEmpty()){
            Util.initL1AllNodeNames(id_Long);
        }
        String utilization = Util.level1GroupAllNodesCpuUtilization(id_Long, start);
        out.write(utilization);
        out.flush();
        out.close();

    }

}
