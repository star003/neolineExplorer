import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Vector;
import javax.swing.JTextField;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class editPoints extends JFrame {
	private static final long serialVersionUID = 1L;
	String globalID = "";
	private JFrame editPoints;
	private JTable tblEdit;
	private JTextField textLat;
	private JTextField textLon;
	private JTextField textSpeed;
	private JTextField textDescr;
	
	public static void main(String[] args) {
		startDialog();
	} //public static void main(String[] args)

	public static void startDialog() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					editPoints frame = new editPoints();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}//public static void startDialog()
	
	public editPoints() throws ClassNotFoundException, SQLException {
		initialize();
	}//public editPoints() throws ClassNotFoundException, SQLException
	
	private void initialize() throws ClassNotFoundException, SQLException  {
		editPoints = new JFrame();
		setBounds(0, 0, 800, 528);
		editPoints.getContentPane().setLayout(null);
		
		getContentPane().setLayout(null);
		tblEdit = new JTable();
		tblEdit.setBounds(20, 41, 700, 355);
		
		textLat = new JTextField();
		textLat.setBounds(166, 438, 123, 20);
		getContentPane().add(textLat);
		textLat.setColumns(10);
		
		textLon = new JTextField();
		textLon.setBounds(315, 438, 133, 20);
		getContentPane().add(textLon);
		textLon.setColumns(10);
		
		textSpeed = new JTextField();
		textSpeed.setBounds(461, 438, 133, 20);
		getContentPane().add(textSpeed);
		textSpeed.setColumns(10);
		
		textDescr = new JTextField();
		textDescr.setBounds(609, 438, 147, 20);
		final ResultSet x1 = readAnotherDB.getResult("SELECT * FROM points;");
		tblEdit = new JTable(buildTableModel(x1,false));
		tblEdit.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent arg0) {
	            int[] selectedRows = tblEdit.getSelectedRows();
	            int i 			= 0;
                int selIndex 	= selectedRows[i];
                TableModel model= tblEdit.getModel();
                globalID 		= model.getValueAt(selIndex	, 0).toString();
               	textLat.setText(model.getValueAt(selIndex	, 1).toString());
               	textLon.setText(model.getValueAt(selIndex	, 2).toString());
               	textSpeed.setText(model.getValueAt(selIndex	, 3).toString());
               	textDescr.setText(model.getValueAt(selIndex	, 4).toString());
			} //public void mouseClicked(MouseEvent arg0)   
		});
		tblEdit.setAlignmentX(Component.LEFT_ALIGNMENT);
		tblEdit.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tblEdit.setBounds(10, 10, 337, 208);
		JScrollPane scrollPane = new JScrollPane(tblEdit);
		scrollPane.setBounds(20, 41, 736, 382);
		getContentPane().add(scrollPane);
		JButton btnReadData = new JButton("Получить данные");
		btnReadData.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				readAnotherDB.tableOperation(tblEdit, "");
			}//public void actionPerformed(ActionEvent arg0)
		});
		btnReadData.setBounds(20, 7, 150, 23);
		getContentPane().add(btnReadData);
		
		JButton btnSaveData = new JButton("Записать данные");
		btnSaveData.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//***обновляем данные в строке таблицы
				String sql = 
				"UPDATE points SET " +
				" speed= "	 +textSpeed.getText()+
				" ,lat= "	 +readAnotherDB.delSymbol(textLat.getText())+ 
				" ,lon= " 	 +readAnotherDB.delSymbol(textLon.getText())+
				" ,descr= '" +textDescr.getText()+"' "+
				" WHERE id=" +globalID+";";
				readAnotherDB.tableOperation(tblEdit, sql);
			}//public void actionPerformed(ActionEvent e)
		});
		btnSaveData.setBounds(180, 7, 150, 23);
		getContentPane().add(btnSaveData);
		
		JButton btnDeleteData = new JButton("Удалить данные");
		btnDeleteData.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String sql ="DELETE FROM points WHERE id = "+globalID+";";
				readAnotherDB.tableOperation(tblEdit, sql);
			}//public void actionPerformed(ActionEvent e)
		});
		btnDeleteData.setBounds(340, 7, 150, 23);
		getContentPane().add(btnDeleteData);

		JButton btnNewData = new JButton("Ввести  новые");
		btnNewData.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
					String sql = 
					"INSERT INTO points (speed,lat,lon,descr) VALUES " +
					" ("	+ (textSpeed.getText() 	== null ? 0: textSpeed.getText()) +
					" ,"	+(textLat.getText()		== null ? 0 : readAnotherDB.delSymbol(textLat.getText()))+ 
					" ," 	+(textLon.getText()		==null ? 0 : readAnotherDB.delSymbol(textLon.getText()))+
					" ,'" 	+(textDescr.getText()	==null ? "EROOR!!!":textDescr.getText())+"');";
					readAnotherDB.tableOperation(tblEdit, sql);
			}//public void actionPerformed(ActionEvent e)
		});
		btnNewData.setBounds(501, 7, 150, 23);
		getContentPane().add(btnNewData);
		getContentPane().add(textDescr);
		textDescr.setColumns(10);
		setTitle("Редактор точек");
	}// private void initialize() throws ClassNotFoundException, SQLException
	
	public static DefaultTableModel buildTableModel(ResultSet rs,boolean emtyModel) throws SQLException {
		if (emtyModel == true) {
			//**случай , когда нужно получить просто пустую таблицу
			Vector<String> columnNames = new Vector<String>();
			columnNames.add("ID");
			columnNames.add("lat");
			columnNames.add("lon");
			columnNames.add("speed");
			columnNames.add("descr");
			Vector<Vector<Object>> data = new Vector<Vector<Object>>();
			return new DefaultTableModel(data, columnNames);
		}
	    ResultSetMetaData metaData = rs.getMetaData();
	    // names of columns
	    Vector<String> columnNames = new Vector<String>();
	    int columnCount = metaData.getColumnCount();
	    for (int column = 1; column <= columnCount; column++) {
	        columnNames.add(metaData.getColumnName(column));
	    }
	    // data of the table
	    Vector<Vector<Object>> data = new Vector<Vector<Object>>();
	    while (rs.next()) {
	        Vector<Object> vector = new Vector<Object>();
	        for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
	            vector.add(rs.getObject(columnIndex));
	        }
	        data.add(vector);
	    }
	    return new DefaultTableModel(data, columnNames);
	} //public static DefaultTableModel buildTableModel(ResultSet rs,boolean emtyModel) throws SQLException
} //public class editPoints extends JFrame
