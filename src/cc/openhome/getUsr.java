package cc.openhome;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;

@WebServlet("/getUsr")
public class getUsr extends HttpServlet {
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
//        seg.run();
        HashMap<String, Object> map = new HashMap<String, Object>();
        String gusr = new String(req.getParameter("gusr").getBytes("ISO-8859-1"), "UTF-8");
        System.out.println(gusr);
        map.put("readline", gusr);
        JSONObject map_json = new JSONObject(map);
        resp.setContentType("application/json;charset=UTF-8");
        resp.getWriter().write(map_json.toString());
//        new SegChinese();
    }
}
