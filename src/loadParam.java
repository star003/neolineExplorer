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
* �������� �������� / ���������� �������� ���������� � ����� ��������
* ��� :
*	��������:��������
* 	----������ �������������----
*	List<fileData> a = getData(loadData(fileName));
*	System.out.println(getValue(a,"interval"));
*	System.out.println(getValue(a,"prefixFileName"));
*/
	
final static String fileName = "param.ini"; //**��� ����� ��������
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
	//*���� � ���������� �������� ����������� ��������� @param � 
	//*������� ������ @x
	//*���� �� ������� �� null
	//****
	for (int i = 0;i<x.size();i++){
		if (x.get(i).param.equals(param)  ) return x.get(i).valueParam;
	}//for
	return null;
} //public static String getValue(List<fileData> x,String param)

public static void createNewFile() throws IOException {
	//***
	//*�������� ����� ���� �������� �� ���������
	//***
	BufferedWriter out = new BufferedWriter(new FileWriter(fileName));
		out.write("programPath;E://sqllite//geo_project# ����� � ���������� � ����� ������");
		out.newLine();
		out.write("gpxPath;E://sqllite//geo_project//gpx# ����� �������� ������");
		out.newLine();
    out.close();
}//public static void createNewFile()

static boolean fileExist(String lnk) {
	//****
	//*�������� ���� �� ���� 
	//*���� - ������ , ����� ����
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
//*������ ������ �� ������������ � ��������� �� �����
//*�������� � ��� ��������
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
		//*������������� ���������, ������ String[] ����� �� lnk
		//*������ ��� ����
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