import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTable;

public class readAnotherDB {
	/*
	 * ����� �������� ������� ������ � ����� ������ ������...
	 */
	static String pathDB 	= getPathDB()+"param.db";
	
	static String pathFolder= getPathDB();
	
	static String getPathDB() {
		/*
		 * ������ ���� � ��������� � ����� ������
		 */
		try {
			List<fileData> a = loadParam.getData(loadParam.loadData("param.ini"));
			return loadParam.getValue(a,"programPath")+"//";
		} catch (Exception e2) {
			e2.printStackTrace();
			return "C://eclipse//neolineWork//";
		}
	} //static String getPathDB() 
	
	static ResultSet getResult(String sql) throws ClassNotFoundException, SQLException {
		/*
		 * ������������� ������� , ���������� ��������� ������� @sql
		 */
		Class.forName("org.sqlite.JDBC");
		Connection bd 			= DriverManager.getConnection("jdbc:sqlite:"+pathDB);
		ResultSet resultSet 	= null; 
		Statement st 			= bd.createStatement();
		st.setQueryTimeout(60);
		resultSet  				= st.executeQuery( sql );
		return resultSet;
	} //ResultSet getResult(String sql) throws ClassNotFoundException, SQLException
	
	static void getResult1(String sql) throws ClassNotFoundException, SQLException {
		/*
		 * �������� ������ @sql
		 */
		Class.forName("org.sqlite.JDBC");
		Connection bd 			= DriverManager.getConnection("jdbc:sqlite:"+pathDB);
		//ResultSet resultSet 	= null; 
		Statement st 			= bd.createStatement();
		st.setQueryTimeout(60);
		ResultSet resultSet  	= st.executeQuery(sql);
		resultSet.close();
	} //static getResult1(String sql) throws ClassNotFoundException, SQLException
	
	static double readDistance(String id) throws ClassNotFoundException, SQLException {
		/*
		 * ���������� ��������� �� @id �����
		 */
		ResultSet r =  getResult("SELECT lat,lon FROM points WHERE descr = '" + id + "';");
		int 	i		= 0;
		double  endLat 	= 0;
		double  endLon 	= 0;
		double  ds 		= 0;
		while (r.next()) {
			if (i>0){
				ds+=distance(Double.valueOf(r.getString("lat"))
							,Double.valueOf(r.getString("lon"))
							,endLat
							,endLon);
			}
			endLat=Double.valueOf(r.getString("lat"));
			endLon=Double.valueOf(r.getString("lon"));
			i++;
		}
		return ds;
	} //static double readDistance(String id) throws ClassNotFoundException, SQLException
	
	static ResultSet readData() throws ClassNotFoundException, SQLException {
		/*
		 * �������� ������ ������ � ������� ������� �����
		 */
		return getResult("select max(descr) as maxID ,count(id) as countID,date,time from points group by descr ;");
	} //ResultSet readData() throws ClassNotFoundException, SQLException
	
	static double distance(double lat1,double lon1,double lat2,double lon2) {
		/* ������� ����������(������1,�������1,������2,�������2)   
		 * ���� ���������.
		 * ������  = latitude  (lat)
		 * ������� = longitude (lon)
		 * ��� ������� �� ������ �� ������� ��������� ��� ������!
		 * ����� ����� ����� ������� ������ ����� ����� �������.
		 * ������������ �������� ������ � ���� �� ��������� ������ ������ �:
		 * - ���� ������ ������ 50 ��������: � = 111,33 - 0,0156�2 - 0,023� ;
		 * - ���� ������ ������ 50 ��������: � = 135,35 - 0,00586�2 - 0,978� .
		 *
		 * � � ��� �������: b = 110,44 + 0,014�.
		 *
		 * ����� ��� 
		 * ���������� ������� ����� �������� ���� ����� (� ��������! � ����������� ������)  - s 
		 * �
		 * ����� ��������� ���� ����� (���� � �������� � ����������� ������) - d.
		 *
		 * ���������� �����: ((bs)2 + (ad)2)1/2.
		 * (�.�. ������ ���������� �� ����� ��������� ������������ bs � ad)
		  
		D =0;
	    A =0;
		B =0;
		A1=0;
		B1=0;
		S =0;  
		�������� = (������1+������2)/2;
		���� (��������<50) �����  
			A = 111.33-(0.0156*�������(��������))-(0.023*��������);
		�����               
			A = 135.35-(0.00586*�������(��������))-(0.978*��������);
		���������;	
		B =110.44+(0.014*��������);
		//B1=110.44+(0.014*������2);
		S = ������1-������2;
		D = �������1-�������2; 
		��� =(������(�������(B*S)+�������(A*D)))*1000; 
		//��������(������("A=[A] B=[B] S=[S] D=[D] ���=[���]"));
		������� ���;
	 
	������������   
	*/
		double mLat = (lat1+lat2)/2;
		double A =0;
		if (mLat<50) {
			A = 111.33-(0.0156*Math.pow(mLat,2))-(0.023*mLat);
		}
		else {
			A = 135.35-(0.00586*Math.pow(mLat,2))-(0.978*mLat);	
		}
		double B =110.44+(0.014*mLat);
		double S = lat1-lat2;
		double D = lon1-lon2; 
		return Math.sqrt((Math.pow(B*S,2)+Math.pow(A*D,2)))*1000;
	} //static double distance(double lat1,double lon1,double lat2,double lon2)
	
	static void loadFromLogger() throws ClassNotFoundException, SQLException{
		/*  ����� ���� ����� ����
		 *  �������� bat ���� � �������� ���
		 *  ������ �� ������� ������ �������� sqlite3 ������ �� ��������
		 */
	} //static void loadFromLogger() throws ClassNotFoundException, SQLException
	
	static void tableOperation(JTable table,String sql) {
		/*  
		 * ��������� ������ @sql � �������������� ������� �����
		 */
		try {
			if (sql != "")  readAnotherDB.getResult1(sql);
			table.setModel(editPoints.buildTableModel(readAnotherDB.getResult("SELECT * FROM points;"),false));
			table.revalidate();
			table.repaint();
		} catch (ClassNotFoundException | SQLException e1) {
			try {
				table.setModel(editPoints.buildTableModel(readAnotherDB.getResult("SELECT * FROM points;"),false));
			} catch (ClassNotFoundException | SQLException e2) {
				e2.printStackTrace();
			}
			table.revalidate();
			table.repaint();
		}
	}
	
	static void mainFormTableOperation(JTable table,String sql) {
		/*  
		 * ��������� ������ @sql � �������������� ������� ����� mainForm
		 */
		try {
			if (sql != "")  readAnotherDB.getResult1(sql);
			table.setModel(mainForm.buildTableModel(readAnotherDB.readData(),false));
			table.revalidate();
			table.repaint();
		} catch (ClassNotFoundException | SQLException e1) {
			try {
				table.setModel(mainForm.buildTableModel(readAnotherDB.readData(),false));
			} catch (ClassNotFoundException | SQLException e2) {
				e2.printStackTrace();
			}
			table.revalidate();
			table.repaint();
		}
	} //static void mainFormTableOperation(JTable table,String sql)
	
	static String delSymbol(String s) {
	/*
	 * �������� ���� ������ @s �� ������� ���������������� ��������
	 * ���� ������ - �� ������� ��...
	 */
		return s.replace(",",".").replace("-",".");
	} //static String delSymbol(String s)
	
	static void calculateDistance() throws ClassNotFoundException, SQLException, IOException {
		/*
		 * ��������� ��������� ��������� �� ����
		 */
		ResultSet r =  getResult("SELECT lat,lon,date FROM points;");
		int 	i		 = 0;
		double  endLat 	 = 0;
		double  endLon 	 = 0;
		double  ds 		 = 0;
		String startData = "";
		List<distance> v = new ArrayList<distance>();
		while (r.next()) {
			if (i>0){
				ds+=distance(Double.valueOf(r.getString("lat"))
							,Double.valueOf(r.getString("lon"))
							,endLat
							,endLon);
				if (startData.equals(String.valueOf(r.getString("date")))!=true) {
					//**��� ������ ������������� ��������� � ��
					distance v1 = new distance();
					v1.date = startData;
					v1.dist = ds;
					v.add(v1);
					startData	= String.valueOf(r.getString("date"));
					ds 			= 0;
				}
			}
			else {
				startData=String.valueOf(r.getString("date"));
			}
			endLat=Double.valueOf(r.getString("lat"));
			endLon=Double.valueOf(r.getString("lon"));
			i++;
		}
		distance v1 = new distance();
		v1.date = startData;
		v1.dist = ds;
		v.add(v1);
		
		r.close();
		Writer writer = null;
		writer =new BufferedWriter(new OutputStreamWriter(
	    		new FileOutputStream(pathFolder+"report.txt"), "utf-8"));
		String s = "";
		double it = 0;
		for (int j=0;j<v.size();j++){
			s =v.get(j).date+"	"+String.format("%.2f",v.get(j).dist)+"\r\n";
			it+=v.get(j).dist;
			writer.write(s);
		}//for
		writer.write("----------------------------\r\n");
		writer.write("Total:          "+String.format("%.2f",it)+"\r\n");
		writer.write("\r\n");
		writer.write("\r\n");
		it = 0;
		s =v.get(0).date.split("-")[1];
		for (int j=0;j<v.size();j++){
			if (s.equals(v.get(j).date.split("-")[1])!=true){
				//System.out.println(v.get(j).date.split("-")[1]);
				writer.write(s+"		"+String.format("%.2f",it)+"\r\n");
				s  = v.get(j).date.split("-")[1];
				it = 0;
			}
			it+=v.get(j).dist;
		}//for
		writer.write(s+"		"+String.format("%.2f",it)+"\r\n");
		writer.close();
		runProc("notepad.exe "+pathFolder+"report.txt");
	} //static void calculateDistance()
	
	static void runProc(String cmd) {
		/*
		 * �������� ��������� ��������� ������ cmd
		 */
		try {
			Runtime.getRuntime().exec(cmd);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}//static void runProc(String cmd)
	
	static String kt2(double lat,double lon) {
	/*
	 * Public Function ��2(������, �������)
		Dim S, d, t, ������������, ������������� As Double
		t = Forms![�������]![�����������������]
		Dim db As DAO.Database '��������� ���� ������
		Dim rs As DAO.Recordset '��������� ���������
		Dim sSQL As String '����������, ��� ����� �������� SQL ������
		Set db = CurrentDb
		Set rs = db.OpenRecordset("����������������")
		With rs
      		Do While Not .EOF
      			If ((Abs(.Fields(2) - ������) <= t) And Abs(.Fields(3) - �������) <= t) Then
        			��2 = IIf(.Fields(4) = True, "(X)", "") & .Fields(0)
        			GoTo l0
        		Else
        			��2 = "-"
        		End If
        		'Debug.Print , .Fields(0), .Fields(1), .Fields(2), .Fields(3)
        		.MoveNext
      		Loop
		End With
		l0:
		db.Close
		Set rs = Nothing
		Set db = Nothing
		End Function
	 */
		return "";
	} //String kt2(double lat,double lon)
} //public class readAnotherDB

 class distance {
	 /*
	  *  ��� ����� ������� ������ ������ � ����������
	  */
	String date;
	double dist;
} //class distance

