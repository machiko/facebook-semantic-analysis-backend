package cc.openhome;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;

@WebServlet("/getSeg")
public class getSeg extends HttpServlet {
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
//        seg.run();
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("readline", "你好嗎");
        JSONObject map_json = new JSONObject(map);
        resp.setContentType("application/json;charset=UTF-8");
        resp.getWriter().write(map_json.toString());
//        new SegChinese();
    }
}
