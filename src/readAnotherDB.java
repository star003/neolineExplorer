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
	 * класс содержит функции работы с базой данных треков...
	 */
	static String pathDB 	= getPathDB()+"param.db";
	
	static String pathFolder= getPathDB();
	
	static String getPathDB() {
		/*
		 * вернет путь к программе и базам данных
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
		 * универсальная функция , возвращает результат запроса @sql
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
		 * выполнит запрос @sql
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
		 * подсчитаем растояние по @id трака
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
		 * выгрузим список траков в таблицу главной формы
		 */
		return getResult("select max(descr) as maxID ,count(id) as countID,date,time from points group by descr ;");
	} //ResultSet readData() throws ClassNotFoundException, SQLException
	
	static double distance(double lat1,double lon1,double lat2,double lon2) {
		/* Функция Расстояние(Широта1,Долгота1,Широта2,Долгота2)   
		 * Итак приступим.
		 * широта  = latitude  (lat)
		 * долгота = longitude (lon)
		 * Все зависит от Широты на которой находится ваш объект!
		 * Лучше всего взять среднюю Широту между двумя точками.
		 * Подставляете значение Широты в одну из следующих формул вместо х:
		 * - если Широта меньше 50 градусов: а = 111,33 - 0,0156х2 - 0,023х ;
		 * - если Широта больше 50 градусов: а = 135,35 - 0,00586х2 - 0,978х .
		 *
		 * И в эту формулу: b = 110,44 + 0,014х.
		 *
		 * Почти все 
		 * Посчитайте разницу между Широтами двух точек (в градусах! с десятичными долями)  - s 
		 * и
		 * между Долготами двух точек (тоже в градусах с десятичными долями) - d.
		 *
		 * Расстояние равно: ((bs)2 + (ad)2)1/2.
		 * (т.е. корень квадратный из суммы квадратов произведений bs и ad)
		  
		D =0;
	    A =0;
		B =0;
		A1=0;
		B1=0;
		S =0;  
		СрШирота = (Широта1+Широта2)/2;
		Если (СрШирота<50) тогда  
			A = 111.33-(0.0156*Степень(СрШирота))-(0.023*СрШирота);
		Иначе               
			A = 135.35-(0.00586*Степень(СрШирота))-(0.978*СрШирота);
		КонецЕсли;	
		B =110.44+(0.014*СрШирота);
		//B1=110.44+(0.014*Широта2);
		S = Широта1-Широта2;
		D = Долгота1-Долгота2; 
		рез =(Корень(Степень(B*S)+Степень(A*D)))*1000; 
		//Сообщить(Шаблон("A=[A] B=[B] S=[S] D=[D] Рез=[Рез]"));
		Возврат рез;
	 
	КонецФункции   
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
		/*  будем идти через анес
		 *  создадим bat файл и выполним его
		 *  почему то сетевые адреса запросов sqlite3 совсем не понимает
		 */
	} //static void loadFromLogger() throws ClassNotFoundException, SQLException
	
	static void tableOperation(JTable table,String sql) {
		/*  
		 * Выполняет запрос @sql и перерисовывает таблицу формы
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
		 * Выполняет запрос @sql и перерисовывает таблицу формы mainForm
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
	 * проверим ввод строки @s на наличие неперевариваемых символов
	 * если найдем - то заменим их...
	 */
		return s.replace(",",".").replace("-",".");
	} //static String delSymbol(String s)
	
	static void calculateDistance() throws ClassNotFoundException, SQLException, IOException {
		/*
		 * расчитает растояние пройденое за день
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
					//**тут скинем промежуточные показания в БД
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
		 * запустим выполнять командную строку cmd
		 */
		try {
			Runtime.getRuntime().exec(cmd);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}//static void runProc(String cmd)
	
	static String kt2(double lat,double lon) {
	/*
	 * Public Function КТ2(Широта, Долгота)
		Dim S, d, t, ЭталонШирота, ЭталонДолгота As Double
		t = Forms![Главная]![КоэфицентПоправки]
		Dim db As DAO.Database 'Объявляем базу данных
		Dim rs As DAO.Recordset 'Объявляем рекордсет
		Dim sSQL As String 'Переменная, где будет размещён SQL запрос
		Set db = CurrentDb
		Set rs = db.OpenRecordset("КонтрольныеТочки")
		With rs
      		Do While Not .EOF
      			If ((Abs(.Fields(2) - Широта) <= t) And Abs(.Fields(3) - Долгота) <= t) Then
        			КТ2 = IIf(.Fields(4) = True, "(X)", "") & .Fields(0)
        			GoTo l0
        		Else
        			КТ2 = "-"
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
	  *  тут будем хранить список трэков и расстояния
	  */
	String date;
	double dist;
} //class distance

