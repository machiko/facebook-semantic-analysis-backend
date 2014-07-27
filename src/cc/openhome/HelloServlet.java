package cc.openhome;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import libsvm.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

public class HelloServlet extends HttpServlet {
//    libSVMdemo ld = new libSVMdemo(); 
    public static void main(String args[]) {
        String name = TestEcho.test("XD");
        System.out.println(name);      
    }
    
    @Override
    protected void doGet (HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        HashMap<String, Object> map = new HashMap<String, Object>();
        
        //get svm return
        libSVMdemo ld = new libSVMdemo(request.getRealPath("") + "/sparseData.s3db");
//        map.put("accuracy", ld.testReturn());
        
        //json object
//        JSONObject map_json = new JSONObject(map);
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        String name = request.getParameter("name");
//        String echo = TestEcho.test("XD");
//        TestEcho echo = new TestEcho();
        
        //讀檔
        FileReader te_fr = new FileReader(request.getRealPath("") + "/data/1111.txt");
        System.out.println(request.getRealPath("") + "/data/1111.txt");
        BufferedReader te_br = new BufferedReader(te_fr);
        JSONArray te_strarr = new JSONArray();
        ArrayList<String> te_lines = new ArrayList<String>();
        String te_line = null; 
        
        while ((te_line = te_br.readLine()) != null) {
            te_line = te_line.replaceAll("\\s+", "");
            te_lines.add(te_line);
        }
//        System.out.println(te_strarr[0]+"|"+te_strarr[1]+"|"+te_strarr[2]+"|"+te_strarr[3]+"|");
        te_strarr.addAll(te_lines);
        map.put("readline", te_strarr);
        //json ������
        JSONObject map_json = new JSONObject(map);
        te_fr.close();
        te_br.close();  
        
//        out.println(ld.testReturn());
//        out.println("hello" + name + "_" + echo.test("lala") + "\n");
//      
        out.write(map_json.toString());
//        SegChinese seg = new SegChinese();
        out.close();
    }
    
}
