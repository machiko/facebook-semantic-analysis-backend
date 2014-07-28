package cc.openhome;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Writer;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import libsvm.*;

public class libSVMdemo {
	private static final int ArrayList = 0;
    svm_parameter _param;
	svm_problem _prob;
	String _model_file;
	
	String path;
	
	//建構子
	public libSVMdemo(String path) {
//	    this();
        
	    this.path = path;
        _param = new svm_parameter();
        
        _param.svm_type = svm_parameter.C_SVC;
        _param.kernel_type = svm_parameter.LINEAR;
        _param.degree = 3;
        _param.gamma = 0;       // 1/num_features
        _param.coef0 = 0;
        _param.nu = 0.5;
        _param.cache_size = 100;
        _param.C = 1;
        _param.eps = 1e-3;
        _param.p = 0.1;
        _param.shrinking = 1;
        _param.probability = 0;
        _param.nr_weight = 0;
        _param.weight_label = new int[0];
        _param.weight = new double[0];
        
        training();
        testing();
        testReturn();
	}
	
	public ArrayList<HashMap<String, Object>> selectSVM() throws ClassNotFoundException, SQLException {
	    Class.forName("org.sqlite.JDBC");
        Connection conn = DriverManager.getConnection("jdbc:sqlite:" + path);
        Statement stat = conn.createStatement();
        ResultSet rs = stat.executeQuery("SELECT * FROM data");
        
        ArrayList<HashMap<String, Object>> svm_list = new ArrayList<HashMap<String,Object>>();
        
        ResultSetMetaData metaData = rs.getMetaData();
        //宣告 meta list 儲存 meta key
        ArrayList<String> meta_list = new ArrayList<String>();
        for (int i = 2; i <= metaData.getColumnCount(); i++) {
            String name = metaData.getColumnName(i);
            meta_list.add(name);
          }
//        System.out.println(meta_list);
        
        
        while (rs.next()) {
            HashMap<String, Object> hash_data = new HashMap<String, Object>();
            for (String meta : meta_list) {
//                System.out.println(rs.getString(meta));
                hash_data.put(meta, rs.getString(meta));
            }
            svm_list.add(hash_data);
        }
        
//        System.out.println(svm_list);
        
        stat.close();
	    return svm_list; 
	}
	
	protected void loadData(boolean is_training){
		String limit;
		if(is_training){	//training
			System.out.print("Loading training data...");
			limit = " WHERE id <= 4700";
		}else{
			System.out.print("Loading testing data...");
			limit = " WHERE id > 4700";
		}
		
		int max_index = 0;
		_prob = new svm_problem();
		Vector<Double> vy = new Vector<Double>();
		Vector<svm_node[]> vx = new Vector<svm_node[]>();
		
		try{
			Class.forName("org.sqlite.JDBC");
			Connection conn = DriverManager.getConnection("jdbc:sqlite:" + path);
			Statement stat = conn.createStatement();
			ResultSet rs = stat.executeQuery("SELECT * FROM data"+limit);
	        
			
			while (rs.next()) {
				vy.addElement(rs.getDouble("label"));
				int rdk1 = rs.getInt("rdk1"), rdk2 = rs.getInt("rdk2");
//				System.out.println("rdk1 :" + rdk1);
				if(rdk1 == rdk2){	//兩個index相等只放一個
					svm_node[] x = new svm_node[1];
					x[0] = new svm_node();
					x[0].index = rdk1;
					x[0].value = 1;
					max_index = Math.max(max_index, rdk1);
					vx.addElement(x);
				}else{
					if(rdk2 < rdk1){	//如果第二個index比第一個小，交換
						rdk1 = rdk2;
						rdk2 = rs.getInt("rdk1");
					}
					
					svm_node[] x = new svm_node[2];
					x[0] = new svm_node();
					x[0].index = rdk1;
					x[0].value = 1;
					x[1] = new svm_node();
					x[1].index = rdk2;
					x[1].value = 1;
					max_index = Math.max(max_index, rdk2);
					vx.addElement(x);
				}
		    }
		    rs.close();
		    conn.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		if(max_index > 0) _param.gamma = 1.0/max_index;		// 1/num_features	

		_prob.l = vy.size();
		_prob.x = new svm_node[_prob.l][];
		for(int i=0;i<_prob.l;i++) _prob.x[i] = vx.elementAt(i);
		_prob.y = new double[_prob.l]; 
		for(int i=0;i<_prob.l;i++) _prob.y[i] = vy.elementAt(i);
		
		System.out.println("Done!!");
	}
	
	protected void training(){
		loadData(true);
		
		System.out.print("Training...");
		_model_file = "svm_model.txt";
			
		try{
			svm_model model = svm.svm_train(_prob, _param);
			System.out.println("Done!!");
			svm.svm_save_model(_model_file, model);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	protected void testing(){
		loadData(false);
		
		svm_model model;
		int correct = 0, total = 0;
		try {
			model = svm.svm_load_model(_model_file);
			
			for(int i=0;i<_prob.l;i++){
				double v;
				svm_node[] x = _prob.x[i];
				v = svm.svm_predict(model, x);
				total++;
				if(v == _prob.y[i]) correct++;
			}
			
			double accuracy = (double)correct/total*100;
			System.out.println("Accuracy = "+accuracy+"% ("+correct+"/"+total+")");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String testReturn() {
	    loadData(false);
        
        svm_model model;
        String accuracyStr = null;
        int correct = 0, total = 0;
        try {
            model = svm.svm_load_model(_model_file);
            
            for(int i=0;i<_prob.l;i++){
                double v;
                svm_node[] x = _prob.x[i];
                v = svm.svm_predict(model, x);
                total++;
                if(v == _prob.y[i]) correct++;
            }
            
            double accuracy = (double)correct/total*100;
            accuracyStr = "Accuracy = "+accuracy+"% ("+correct+"/"+total+")";
//            System.out.println("Accuracy = "+accuracy+"% ("+correct+"/"+total+")");
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return accuracyStr;
	}
	
	public libSVMdemo() {
		// default values
		_param = new svm_parameter();
		
		_param.svm_type = svm_parameter.C_SVC;
		_param.kernel_type = svm_parameter.LINEAR;
		_param.degree = 3;
		_param.gamma = 0;		// 1/num_features
		_param.coef0 = 0;
		_param.nu = 0.5;
		_param.cache_size = 100;
		_param.C = 1;
		_param.eps = 1e-3;
		_param.p = 0.1;
		_param.shrinking = 1;
		_param.probability = 0;
		_param.nr_weight = 0;
		_param.weight_label = new int[0];
		_param.weight = new double[0];
		
		training();
		testing();
	}
	
	public static void main(String[] args) {
		libSVMdemo ld = new libSVMdemo();
		
	}
}
