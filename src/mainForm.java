import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.ListSelectionModel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

public class mainForm {
	private JFrame frmLoadYandexFoto;
	private JTable table;
	String id = "";
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					mainForm window = new mainForm();
					window.frmLoadYandexFoto.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	} //public static void main(String[] args) 
	
	public mainForm() throws ClassNotFoundException, SQLException  {
		initialize();
	} //public mainForm() throws ClassNotFoundException, SQLException
	
	private void initialize() throws ClassNotFoundException, SQLException {
		frmLoadYandexFoto = new JFrame();
		frmLoadYandexFoto.setBounds(0, 0, 702, 530);
		final JLabel labInfo = new JLabel("");
		labInfo.setBounds(0, 451, 658, 41);
		frmLoadYandexFoto.getContentPane().add(labInfo);
		final ResultSet x1 = null;
		frmLoadYandexFoto.getContentPane().setLayout(null);
		table = new JTable(buildTableModel(x1,true));
		table.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent arg0) {
				String result = "";
				int[] selectedRows = table.getSelectedRows();
				int i = 0;
                	int selIndex = selectedRows[i];
                	TableModel model = table.getModel();
                	Object value = model.getValueAt(selIndex, 0);
                	result = result + value;
                	if(i != selectedRows.length - 1) {
                		result += ", ";
                	}
                id = String.valueOf(result);    
                labInfo.setText("   выбран трак: "+String.valueOf(result));
			}
		});
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setBounds(10, 10, 337, 208);
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setBounds(10, 51, 469, 389);
		frmLoadYandexFoto.getContentPane().add(scrollPane);
		
		JButton btnGetData = new JButton("Получить данные");
		btnGetData.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				readAnotherDB.mainFormTableOperation(table,"");
				labInfo.setText("   Данные обновлены...");
			} //public void actionPerformed(ActionEvent arg0)
		}); //btnNewButton.addActionListener(new ActionListener() 
		
		JButton btnDeleteTrack = new JButton("Удалить трак");
		btnDeleteTrack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int selection = JOptionPane.showConfirmDialog(null
							, "Будет удален трак: "+id+" Вы уверены?"
							, "Окно сообщения"
							, JOptionPane.YES_NO_OPTION
							, JOptionPane.QUESTION_MESSAGE);
				if (selection == JOptionPane.YES_OPTION) {
					readAnotherDB.mainFormTableOperation(table,"DELETE FROM points WHERE id = '"+id+"';");
					labInfo.setText("   Трак удален...");
				}
			}
		}); //JButton btnDeleteTrack = new JButton("Удалить трак");
		
		btnDeleteTrack.setBounds(507, 191, 169, 23);
		btnGetData.setBounds(507, 54, 169, 23);
		frmLoadYandexFoto.getContentPane().add(btnGetData);
		frmLoadYandexFoto.setTitle("Обработка GPX");
		frmLoadYandexFoto.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		labInfo.setText("   Жду...");
		frmLoadYandexFoto.getContentPane().add(btnDeleteTrack);
		JButton btnInfo = new JButton("ИНФОРМАЦИЯ");
		btnInfo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					labInfo.setText("   Трак ID="+id+" ,расстояние: "+String.format("%.2f", readAnotherDB.readDistance(id))+" м.");
				} catch (ClassNotFoundException | SQLException e1) {
					e1.printStackTrace();
				}
			}
		}); //JButton btnInfo = new JButton("ИНФОРМАЦИЯ");
		
		JButton btnUpLoadTrack = new JButton("ВЫГРУЗИТЬ ТРАК...");
		btnUpLoadTrack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					createGPX.saveGPX(id);
					String cmd = "java -jar E:\\sqllite\\geo_project\\gpsprune_15.jar " 
								+"E:\\sqllite\\geo_project\\gpx\\"+createGPX.getFilename(id);
					try {
						Runtime.getRuntime().exec(cmd);
					} catch (IOException e1) {
						labInfo.setText("   ошибка запуска программы просмотра...");
						e1.printStackTrace();
					}
				} catch (ClassNotFoundException | SQLException e) {
					e.printStackTrace();
				}
			}
		}); //JButton btnUpLoadTrack = new JButton("ВЫГРУЗИТЬ ТРАК...");
		btnInfo.setBounds(507, 88, 169, 23);
		frmLoadYandexFoto.getContentPane().add(btnInfo);
		btnUpLoadTrack.setBounds(507, 157, 169, 23);
		frmLoadYandexFoto.getContentPane().add(btnUpLoadTrack);
		
		JButton btnNewButton = new JButton("Загрузить из логгера");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String cmd =  "E:\\sqllite\\geo_project\\logger_sync.bat";
				readAnotherDB.mainFormTableOperation(table,"");
				labInfo.setText("   Данные обновлены...");
			}
		});
		btnNewButton.setBounds(507, 228, 169, 23);
		frmLoadYandexFoto.getContentPane().add(btnNewButton);
		
		JButton btnEditForm = new JButton("Точки интереса");
		btnEditForm.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				editPoints.startDialog();
			}
		});
		btnEditForm.setBounds(507, 262, 169, 23);
		frmLoadYandexFoto.getContentPane().add(btnEditForm);
		
		JButton btnAllInfo = new JButton("Сводная информация");
		btnAllInfo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					readAnotherDB.calculateDistance();
				} catch (ClassNotFoundException | SQLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		btnAllInfo.setBounds(507, 122, 169, 23);
		frmLoadYandexFoto.getContentPane().add(btnAllInfo);
	} //private void initialize() throws ClassNotFoundException, SQLException 
	
	public static DefaultTableModel buildTableModel(ResultSet rs,boolean emtyModel) throws SQLException {
		if (emtyModel == true) {
			//**случай , когда нужно получить просто пустую таблицу
			Vector<String> columnNames = new Vector<String>();
			columnNames.add("maxID");
			columnNames.add("countID");
			columnNames.add("date");
			columnNames.add("time");
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
} //public class mainForm
