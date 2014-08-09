package cc.openhome;

import java.io.BufferedReader;
import java.io.File;
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
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
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
        
        //寫檔案
        try {
            //傳入 WriteTxt
            WriteTxt txt = new WriteTxt();
            txt.input(request.getRealPath(""), ld.selectSVM());
//            System.out.println(ld.selectSVM());
        } catch (ClassNotFoundException | SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        //json object
//        JSONObject map_json = new JSONObject(map);
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        //接收參數值
        String seg = new String(request.getParameter("seg").getBytes("ISO-8859-1"), "UTF-8");
        System.out.println(seg);
        String user = new String(request.getParameter("user").getBytes("ISO-8859-1"), "UTF-8");
        System.out.println(user);
        String keycode = new String(request.getParameter("keycode").getBytes("ISO-8859-1"), "UTF-8");
        //0~9 的 keycode 為 48~57
        System.out.println("按鍵接收 : " + keycode);
        SegChinese sgc = new SegChinese();
        String segWd = sgc.segWords(seg, "  ");
        String[] splitArr = segWd.split("\\s+");        
        System.out.println("SplitWor: "+Arrays.toString(splitArr));
//        String echo = TestEcho.test("XD");
//        TestEcho echo = new TestEcho();
//判斷--------
		int accu_count=0;
		int p_count=0;
		int n_count=0;
		int teii=0;
		int tenun=2;  // how many test sentence
		int catchindex=0;  //test one catch
		int[] ifright = new int[100];
		String tetxt="";
		int wh=0;
//----------------  The Following is read emotion pattern and recognize  ----------------//	
		
  		FileReader epw_fr = new FileReader(request.getRealPath("") + "/data/epattern_word.txt");
  		BufferedReader epw_br = new BufferedReader(epw_fr);		
  		List<String> epw_lines = new ArrayList<String>();
  		String epw_line = null; 
  		String[] epw_strarr= new String[100000]; //str.length()
  		int epw_i=0;
  		while ((epw_line = epw_br.readLine()) != null) {
  			epw_line=epw_line.replaceAll("\\s+", "");
  			epw_lines.add(epw_line);            
  			epw_strarr[epw_i++]=epw_line;
  		}
//--  		System.out.println(epw_strarr[0]+"|"+epw_strarr[1]+"|"+epw_strarr[2]+"|"+epw_strarr[3]+"|");    
  	    
  	    List<String> epwlist = new ArrayList<String>();
  	    for(String epwss : epw_strarr) {
  	       if(epwss != null && epwss.length() > 0) {
  	    	 epwlist.add(epwss);
  	       }
  	    }
  	    epw_strarr = epwlist.toArray(new String[epwlist.size()]);
  	    
  		epw_fr.close();
  		epw_br.close(); 
  		
  		Scanner s = new Scanner(new File(request.getRealPath("") + "/data/epattern_vector.txt"));
			
        int[][] epvect = new int[100000][10];
        double[][] epvectw = new double[100000][10];
        int j=0;
        while(s.hasNext()) {
        	epvect[j][0]=s.nextInt(); 
        	epvect[j][1]=s.nextInt(); 
        	epvect[j][2]=s.nextInt(); 
        	epvect[j][3]=s.nextInt(); 
        	epvect[j][4]=s.nextInt(); 
        	epvect[j][5]=s.nextInt(); 
        	epvect[j][6]=s.nextInt(); 
        	epvect[j][7]=s.nextInt(); 
        	epvect[j][8]=s.nextInt(); 
        	epvect[j][9]=s.nextInt(); 

        	epvectw[j][0]=epvect[j][0];
        	epvectw[j][1]=epvect[j][1]/0.1232; 
        	epvectw[j][2]=epvect[j][2]/0.1910;
        	epvectw[j][3]=epvect[j][3]/0.1791;
        	epvectw[j][4]=epvect[j][4]/0.1273;
        	epvectw[j][5]=epvect[j][5]/0.1732;
        	epvectw[j][6]=epvect[j][6]/0.1304;
        	epvectw[j][7]=epvect[j][7]/0.0757;
        	epvectw[j][8]=epvect[j][8]/0.5120;
        	epvectw[j][9]=epvect[j][9]/0.4880;
        	j=j+1;        	
        }
          		
  		epw_fr.close();
  		epw_br.close(); 	
  		
//----------------  Comparison test sentence and emotion pattern (PMI)  ----------------//	
  		
  		int epvIdx = 0;  		int epvct = 0;
        int[] tevect = new int[10];
        double[] tevectw = new double[10];
        
  		for(;epvIdx < epw_strarr.length;) {
	  		if(Arrays.asList(splitArr).contains(epw_strarr[epvIdx++])){
	  			epvct=epvct+1;	
//--	  			System.out.println(epw_strarr[epvIdx-1]+Arrays.toString(epvect[epvIdx-1])+" epvIdx="+(epvIdx-1));
	  			tevect[0]=tevect[0]+epvect[epvIdx-1][0]; 
	  			tevect[1]=tevect[1]+epvect[epvIdx-1][1];
	  			tevect[2]=tevect[2]+epvect[epvIdx-1][2];
	  			tevect[3]=tevect[3]+epvect[epvIdx-1][3];
	  			tevect[4]=tevect[4]+epvect[epvIdx-1][4];
	  			tevect[5]=tevect[5]+epvect[epvIdx-1][5];
	  			tevect[6]=tevect[6]+epvect[epvIdx-1][6];
	  			tevect[7]=tevect[7]+epvect[epvIdx-1][7];
	  			tevect[8]=tevect[8]+epvect[epvIdx-1][8];
	  			tevect[9]=tevect[9]+epvect[epvIdx-1][9];

	  			tevectw[0]=tevectw[0]+epvectw[epvIdx-1][0]; 
	  			tevectw[1]=tevectw[1]+epvectw[epvIdx-1][1];
	  			tevectw[2]=tevectw[2]+epvectw[epvIdx-1][2];
	  			tevectw[3]=tevectw[3]+epvectw[epvIdx-1][3];
	  			tevectw[4]=tevectw[4]+epvectw[epvIdx-1][4];
	  			tevectw[5]=tevectw[5]+epvectw[epvIdx-1][5];
	  			tevectw[6]=tevectw[6]+epvectw[epvIdx-1][6];
	  			tevectw[7]=tevectw[7]+epvectw[epvIdx-1][7];
	  			tevectw[8]=tevectw[8]+epvectw[epvIdx-1][8];
	  			tevectw[9]=tevectw[9]+epvectw[epvIdx-1][9];	  			
	  			}
  		}  		
  		
//------------   Select emotion type of sentence   ---------------
  		double tevectwmax = tevectw[1];
  		int tevIdx = 1;
  		for (int i = 1; i < tevectw.length; i++) {
  		  if ( tevectw[i] > tevectwmax ) {
  			  tevectwmax = tevectw[i];
  			  tevIdx = i;
// 			System.out.println("Index= "+i+" Value="+tevectwmax);
  		   }
  		}
  		tevectw[0]=tevIdx;
  		catchindex=tevIdx;
//--  		System.out.println(Arrays.toString(te_strarr)+" num="+Arrays.toString(tevect));
  		System.out.println(wh+" Index= "+tevIdx+" "+Arrays.toString(splitArr)+" num="+Arrays.toString(tevectw));
  		
  		if( (tevIdx==1) || (tevIdx==2) || (tevIdx==3) || (tevIdx==4) ){  // N type
  			n_count=n_count+1;
  		}
  		if( (tevIdx==5) || (tevIdx==6) || (tevIdx==7) ){  // P type
  			p_count=p_count+1;
  		}
  		
	 // for whole sentence read as one time
	System.out.println("accu_count="+accu_count+" ,tenun="+tenun+" ,Precision="+(double)accu_count/tenun+" ,Posi="+(double)p_count/tenun+" ,Negi="+(double)n_count/tenun);
	for(int jk=0;jk<accu_count;jk++){
		System.out.println("#="+ifright[jk]+"   "+seg);
	}
		
		
//讀檔--------       
	
    	FileReader fd_fr = new FileReader(request.getRealPath("") + "/data/1111.txt");
    	BufferedReader fd_br = new BufferedReader(fd_fr);		
        List<String> fd_lines = new ArrayList<String>();
        String fd_line = null; 
        String[] fd_strarr= new String[1000]; //str.length()
        String[][] fdal_strarr= new String[8][1000]; //str.length()
        
        int fd_i=0; int ian=0; int idi=0; int ife=0; int isa=0; int iels=0;
        while ((fd_line = fd_br.readLine()) != null) {
        	fd_line=fd_line.replaceAll("\\s+", "");
        	fd_lines.add(fd_line);            
        	fd_strarr[fd_i++]=fd_line;
        	if(fd_i<10){
        		fdal_strarr[0][ian++]=fd_line;
        	}else if((fd_i>9)&&(fd_i<20)){
        		fdal_strarr[1][idi++]=fd_line;
        	}else if((fd_i>19)&&(fd_i<30)){
        		fdal_strarr[2][ife++]=fd_line;
        	}else if((fd_i>29)&&(fd_i<40)){
        		fdal_strarr[3][isa++]=fd_line;
        	}else{
        		fdal_strarr[4][iels++]=fd_line;
        	}
        }
//--        System.out.println(fd_strarr[0]+"|"+fd_strarr[1]+"|"+fd_strarr[2]+"|"+fd_strarr[3]+"|");    
//--        System.out.println(fdal_strarr[0][0]+"|"+fdal_strarr[0][1]+"|"+fdal_strarr[2][0]+"|"+fdal_strarr[2][1]+"|");

        Random rand = new Random();
        int  nfd0 = rand.nextInt(9) + 0;
        int  nfd1 = rand.nextInt(9) + 0;
        int  nfd2 = rand.nextInt(9) + 0;
        int  nfd3 = rand.nextInt(9) + 0;
        int pmi=rand.nextInt(3)+2;
        //50 is the maximum and the 1 is our minimum 
        
        int fd0=0; int fd1=0; int fd2=0; int fd3=0; int fd4=0; int fd5=0; int fd6=0; int fd7=0; 
        // need to record next call function
        String outfd = null;
        String[] emotionshow = {null,"生氣","討厭","擔心","難過","中性","開心","興奮"};
    	switch(catchindex){
    	case 1: outfd=fdal_strarr[0][nfd0]; emotionshow[catchindex] += pmi; emotionshow[catchindex] += " - "; emotionshow[catchindex] += outfd; System.out.println("catchindex= "+catchindex+" "+emotionshow[catchindex]+" feedback: "+outfd+" nfd0: "+nfd0+" length: "+fdal_strarr[0].length +" feedback: "+fdal_strarr[0][fd0++]);   break;
    	case 2: outfd=fdal_strarr[1][nfd1]; emotionshow[catchindex] += pmi; emotionshow[catchindex] += " - "; emotionshow[catchindex] += outfd; System.out.println("catchindex= "+catchindex+" "+emotionshow[catchindex]+" feedback: "+outfd+" nfd1: "+nfd1+" length: "+fdal_strarr[1].length +" feedback: "+fdal_strarr[1][fd1++]);   break;
    	case 3: outfd=fdal_strarr[2][nfd2]; emotionshow[catchindex] += pmi; emotionshow[catchindex] += " - "; emotionshow[catchindex] += outfd; System.out.println("catchindex= "+catchindex+" "+emotionshow[catchindex]+" feedback: "+outfd+" nfd2: "+nfd2+" length: "+fdal_strarr[2].length +" feedback: "+fdal_strarr[2][fd2++]);   break;
    	case 4: outfd=fdal_strarr[3][nfd3]; emotionshow[catchindex] += pmi; emotionshow[catchindex] += " - "; emotionshow[catchindex] += outfd; System.out.println("catchindex= "+catchindex+" "+emotionshow[catchindex]+" feedback: "+outfd+" nfd3: "+nfd3+" length: "+fdal_strarr[3].length +" feedback: "+fdal_strarr[3][fd3++]);   break;  
    	case 5: outfd=emotionshow[catchindex]; System.out.println("catchindex= "+catchindex+" "+emotionshow[catchindex]+" feedback: "+outfd);  ; break;
    	case 6: outfd=emotionshow[catchindex]; System.out.println("catchindex= "+catchindex+" "+emotionshow[catchindex]+" feedback: "+outfd);  ; break;
    	case 7: outfd=emotionshow[catchindex]; System.out.println("catchindex= "+catchindex+" "+emotionshow[catchindex]+" feedback: "+outfd);  ; break;
    	}
        
    	System.out.println("Address: "+request.getRealPath(""));
    	//System.out.println("catchindex= "+catchindex+" feedback: "+outfd);  
        JSONArray te_strarr = new JSONArray();
        te_strarr.add(emotionshow[catchindex]);
        outfd = null;
/*        ArrayList<String> te_lines = new ArrayList<String>();
      String te_line = null; */
        
/*        FileReader te_fr = new FileReader(request.getRealPath("") + "/data/1111.txt");
        System.out.println(request.getRealPath("") + "/data/1111.txt");
        BufferedReader te_br = new BufferedReader(te_fr); 
        JSONArray te_strarr = new JSONArray();
        ArrayList<String> te_lines = new ArrayList<String>();
        String te_line = null;  
        
        while ((te_line = te_br.readLine()) != null) {
            te_line = te_line.replaceAll("\\s+", "");
            te_lines.add(te_line);
        }*/
        
//        System.out.println(te_strarr[0]+"|"+te_strarr[1]+"|"+te_strarr[2]+"|"+te_strarr[3]+"|");
//        te_strarr.addAll(te_lines);
        map.put("readline", te_strarr);
        //json ������
        JSONObject map_json = new JSONObject(map);
//        te_fr.close();
//        te_br.close();  
        
//        out.println(ld.testReturn());
//        out.println("hello" + name + "_" + echo.test("lala") + "\n");
//      
        out.write(map_json.toString());
//        SegChinese seg = new SegChinese();
        out.close();
    }
    
}
