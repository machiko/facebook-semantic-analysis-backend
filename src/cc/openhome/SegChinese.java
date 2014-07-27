package cc.openhome;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;

import com.chenlb.mmseg4j.ComplexSeg;
import com.chenlb.mmseg4j.Dictionary;
import com.chenlb.mmseg4j.MMSeg;
import com.chenlb.mmseg4j.Seg;
import com.chenlb.mmseg4j.Word;

@WebServlet("/SegChinese")
public class SegChinese extends HttpServlet {
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        //轉化編碼
        String reqSeg = new String(req.getParameter("text").getBytes("ISO-8859-1"), "UTF-8");
        
        //init seg
        SegChinese seg = new SegChinese();
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("seg_chinese", seg.run(reqSeg));
        JSONObject map_json = new JSONObject(map);
        resp.setContentType("application/json;charset=UTF-8");
        resp.getWriter().write(map_json.toString());
    }
    
    protected Dictionary dic;
    
    public SegChinese() {
        System.setProperty("mmseg.dic.path", "./src/SegChinese/data");  //這裡可以指定自訂詞庫
        dic = Dictionary.getInstance();
    }

    protected Seg getSeg() {
        return new ComplexSeg(dic);
    }
    
    public String segWords(String txt, String wordSpilt) throws IOException {
        Reader input = new StringReader(txt);
        StringBuilder sb = new StringBuilder();
        Seg seg = getSeg();
        MMSeg mmSeg = new MMSeg(input, seg);
        Word word = null;
        boolean first = true;
        while((word=mmSeg.next())!=null) {
            if(!first) {
                sb.append(wordSpilt);
            }
            String w = word.getString();
            sb.append(w);
            first = false;      
        }
        return sb.toString();
    }
    
    protected String run(String txt) throws IOException {
//        txt = "這行文字是要被中文斷詞處理的文章，可以從執行結果看斷詞是否成功 莊圓大師";
        String segWrods = segWords(txt, " | ");
        
//        System.out.println(segWords(txt, " | "));
        return segWords(txt, " | ");
    }

    public static void main(String[] args) throws IOException {     
//        new SegChinese().run();
    }
}

