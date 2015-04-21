import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class createGPX {
	/*
	 * выгружает данные в GPX файл
	 */
	final static String pathFolder =getPathDB() ;
	
	static String getPathDB() {
		/*
		 * вернет путь к программе и базам данных
		 */
		try {
			List<fileData> a = loadParam.getData(loadParam.loadData("param.ini"));
			return loadParam.getValue(a,"gpxPath")+"//";
		} catch (Exception e2) {
			e2.printStackTrace();
			return "E://sqllite//geo_project//gpx//";
		}
	} //static String getPathDB() 
	static String getFilename(String id) throws ClassNotFoundException, SQLException {
		ResultSet rx = readAnotherDB.getResult("SELECT MAX(id) AS maxID,COUNT(id) AS countID,date,time "
												+" FROM points WHERE id = '"+id+"' GROUP BY date,id ;");
		String rez = "";
		while (rx.next()) {
			rez="output_"+rx.getString("maxID")
				+"_"+rx.getString("date").replace(".","-")
				+"_"+rx.getString("time").replace(":","-")+".gpx";
		}
		return rez;
	} //static String getFilename(String id) throws ClassNotFoundException, SQLException
	
	public static void saveGPX(String id) throws ClassNotFoundException, SQLException {
		//**пишем GPX файл из базы данных
		//**@id номер трака в базе
		//**@path адрес куда пишем
		Writer writer = null;
		ResultSet rx = readAnotherDB.getResult("SELECT * FROM points WHERE id = '"+id+"';");
		try {
			writer =new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(pathFolder+getFilename(id)), "utf-8"));
			String s = 
					"<?xml version='1.0' encoding='UTF-8'?>"		
					+"<gpx xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' version='1.0'>";
	    
			writer.write(s, 0 , s.length());
			s ="<trk>";
			writer.write(s, 0 , s.length());
			s = "   <name>"+id+"</name>";
			writer.write(s, 0 , s.length());
			s ="       <trkseg>";
			writer.write(s, 0 , s.length());
			while (rx.next()) {
				s ="         <trkpt lat='"+rx.getString("lat")+"' lon='"+rx.getString("lon")+"'>";
				writer.write(s, 0 , s.length());
				s ="         <time>"+rx.getString("allTime")+"</time>";
				writer.write(s, 0 , s.length());
				s ="         <speed>"+rx.getString("speed")+"</speed>";
				writer.write(s, 0 , s.length());
				s ="         <ele>0.0</ele>";
				writer.write(s, 0 , s.length());
				s ="        </trkpt>";
				writer.write(s, 0 , s.length());
			}
			s ="       </trkseg>";
			writer.write(s, 0 , s.length());
			s ="</trk>";
			writer.write(s, 0 , s.length());
			s ="</gpx>";
			writer.write(s, 0 , s.length());
     
		} catch (IOException ex) {
	  // report
		} finally {
			try {writer.close();} catch (Exception ex) {}
		}
    } //saveUrl(String filename, String urlString)
	
} //public class createGPX

