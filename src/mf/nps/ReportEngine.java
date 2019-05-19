package mf.nps;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;


public class ReportEngine extends HttpServlet {
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req,resp);
    }

    @Override
    public void init() throws ServletException {
//        Util.initMaps();
        super.init();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html;charset=utf-8");
        resp.setHeader("Access-Control-Allow-Origin", "*");

        String start=req.getParameter("start");
        String end=req.getParameter("end");

//        System.out.println(start);
//        System.out.println(end);
        String str= Util.getInterfaceThroughput(start,end);
        PrintWriter out = resp.getWriter();
//        System.out.println(str);
        out.write(str);
        out.flush();
        out.close();


    }

}
