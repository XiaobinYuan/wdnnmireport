package mf.nps;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

public class ThroughputServlet extends HttpServlet {
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req,resp);
    }

    @Override
    public void init() throws ServletException {
        super.init();
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html;charset=utf-8");
        resp.setHeader("Access-Control-Allow-Origin", "*");

        int start=Integer.parseInt(req.getParameter("start"));
        int end=Integer.parseInt(req.getParameter("end"));

        System.out.println(new Date());
//        String str= Util.getInterfaceThroughput("10.199.242.137","xe-0/1/7.0",16,20);
//        PrintWriter out = resp.getWriter();
//        System.out.println(str);
//        out.write(str);
//        out.flush();
//        out.close();


    }
}
