import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class loadParam {
/*
* механизм загрузки / сохранения значений параметров в файле настроек
* вид :
*	параметр:значение
* 	----пример использования----
*	List<fileData> a = getData(loadData(fileName));
*	System.out.println(getValue(a,"interval"));
*	System.out.println(getValue(a,"prefixFileName"));
*/
	
final static String fileName = "param.ini"; //**имя файла настроек
static List<fileData> v = new ArrayList<fileData>();

public static void main(String[] args) throws Exception {
	if (fileExist(fileName)==false) {
		System.out.println("new file...");
		createNewFile();
	} //if
	List<fileData> a = getData(loadData(fileName));
	System.out.println(getValue(a,"interval"));
	System.out.println(getValue(a,"prefixFileName"));
} //public static void main(String[] args) throws Exception

public static String getValue(List<fileData> x,String param) {
	//****
	//*ищет и возвращает значение переданного параметра @param в 
	//*массиве списка @x
	//*если не находит то null
	//****
	for (int i = 0;i<x.size();i++){
		if (x.get(i).param.equals(param)  ) return x.get(i).valueParam;
	}//for
	return null;
} //public static String getValue(List<fileData> x,String param)

public static void createNewFile() throws IOException {
	//***
	//*создадим новый файл настроек по умолчанию
	//***
	BufferedWriter out = new BufferedWriter(new FileWriter(fileName));
		out.write("programPath;E://sqllite//geo_project# папка с программой и базой данных");
		out.newLine();
		out.write("gpxPath;E://sqllite//geo_project//gpx# папка выгрузки траков");
		out.newLine();
    out.close();
}//public static void createNewFile()

static boolean fileExist(String lnk) {
	//****
	//*проверим есть ли файл 
	//*есть - истина , иначе ложь
	//****
	File theDir = new File(lnk);
	  if (!theDir.exists()) {
		  return false;
	  }
	  else {
		  return true;
	  }
} //boolean fileExist(String lnk)

static List<fileData> getData(String[] fn) throws Exception{
//***
//*читаем данные из мультистроки и разбираем по парам
//*параметр и его значение
//***
	if (fn.length == 0) return null;
		for (int i=0  ; i < fn.length-1; i++) {
			String[] x = fn[i].split(";");
				if (x.length>1){
					fileData v1 = new fileData();
					v1.param 		= x[0];
					String[] x2 = x[1].split("#");
						if (x2.length>1){
							v1.valueParam 	= x2[0].replace("#", "");
						} //if
						else {
							v1.valueParam 	= x[1].replace("#", "");
						} //else
							v.add(v1);
				}//if
		}//for
	return v;		
}//static List<fileData> getData(String[] fn)
	
static String[] loadData(String lnk) throws Exception{
		//*******************************************************
		//*универсальная надстрока, вернет String[] ответ по lnk
		//*читаем тхт файл
		//*******************************************************
		if (fileExist(lnk)==false) {
			System.out.println("new file...");
			createNewFile();
		} //if
			String[] everything=null;
			BufferedReader br = new BufferedReader(new FileReader(lnk));
		    try {
		        StringBuilder sb = new StringBuilder();
		        String line = br.readLine();
	 
		        while (line != null) {
		            sb.append(line);
		            sb.append("\n");
		            line = br.readLine();
		        }
		        everything = sb.toString().split("\\s+");
		    } finally {
		        br.close();
		    }
		    return everything;
	 
} //static String[] loadData(String lnk) throws Exception
} //public class loadParam 

class fileData {
	String param;
	String valueParam;
}//class fileData