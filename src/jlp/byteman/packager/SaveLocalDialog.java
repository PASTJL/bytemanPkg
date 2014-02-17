/*Copyright 2013 Jean-Louis PASTUREL 
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package jlp.byteman.packager;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFrame;

import scala.swing.Frame;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.converter.DefaultStringConverter;
import jfx.messagebox.MessageBox;
import jlp.byteman.packager.check.MyAntlrParser;

public class SaveLocalDialog extends AnchorPane implements Initializable {

	
	
	@FXML
	private  ComboBox<String> cbxRemoteServers;
	
	@FXML
	public  ComboBox<String> getCbxRemoteServers() {
		return cbxRemoteServers;
	}
	
	public  void setCbxRemoteServers( ComboBox<String> _cbxRemoteServers) {
		cbxRemoteServers = _cbxRemoteServers;
	}

	@FXML
	private  Tab tabRemoteAction;
	
	@FXML
	private  TextField portListenerTf;
	public  TextField getPortListenerTf() {
		return portListenerTf;
	}


	@FXML
	private  TextField cmdPID;
	public  TextField getCmdPID() {
		return cmdPID;
	}

	
	@FXML
	private  TextArea cmdConsole;
	@FXML
	public  TextArea getCmdConsole() {
		return cmdConsole;
	}
	

	@FXML
	private  TextField bytemanOptsTf;

	public  TextField getBytemanOptsTf() {
		return bytemanOptsTf;
	}

	// @FXML
	// private  Menu mFile;
	// @FXML
	// private  Menu mHelp;
	// @FXML
	// private  Menu mEdit;
	@FXML
	private  TextArea taGenRules;
	@FXML
	private  MenuItem miCreateProject;
	@FXML
	private  MenuItem miOpenProject;
	@FXML
	private  MenuItem miCloseProject;
	@FXML
	private  MenuItem miExit;

	@FXML
	private  MenuButton othersConfBut;

	@FXML
	private  Button saveLocalbut;
	public Button getSaveLocalbut() {
		return saveLocalbut;
	}

	
	@FXML
	private  Button bCancel;
	public Button getbCancel() {
		return bCancel;
	}

	

	@FXML
	private  Button bSave;
	public Button getbSave() {
		return bSave;
	}

	

	@FXML
	private  Button bUpload;

	public Button getbUpload() {
		return bUpload;
	}

	

	@FXML
	private  TableColumn<CnxSSH, String> rootpassword;

	@FXML
	private  TableColumn<CnxSSH, String> password;
	@FXML
	private  TableColumn<CnxSSH, String> idServer;
	@FXML
	private  TableColumn<CnxSSH, String> addrServer;
	@FXML
	private  TableColumn<CnxSSH, String> login;
	@FXML
	private  TableColumn<CnxSSH, String> port;
	@FXML
	private  TextField newConfTf;

	@FXML
	private  TextField idServerTf;

	@FXML
	private  TextField addrSrvTf;

	@FXML
	private  TextField portTf;

	@FXML
	private  TextField loginTf;

	@FXML
	private  PasswordField passwordTf;

	@FXML
	private  PasswordField rootPasswordTf;

	@FXML
	private  Tab tabNet;

	@FXML
	private  Tab tabCnx;

	@FXML
	private  Tab tabUpld;
	@FXML
	private  Tab tabDwld;

	@FXML
	private  TableView<CnxSSH> tableCnx;

	@FXML
	private  TableView<UpldSSH> tableUpld;

	@FXML
	private  TableView<DwldSSH> tableDwld;

	@FXML
	private  TextField rankTfUpld;

	@FXML
	private  TextField idServerTfUpld;

	@FXML
	private  TextField localFileTfUpdl;

	@FXML
	private  TextField remoteDirTfUpld;

	@FXML
	private  ComboBox<String> executeCbUpld;

	public ComboBox<String> getExecuteCbUpld() {
		return executeCbUpld;
	}

	

	@FXML
	private  TextField rankTfDwld;

	@FXML
	private  TextField idServerTfDwld;

	@FXML
	private  TextField remoteDirFilesTfDwld;

	@FXML
	private  TextField localDirTfDwld;

	@FXML
	private  TextField howmanyTfDwld;

	@FXML
	private  ComboBox<String> actionCbDwld;



	@FXML
	private  TableColumn<UpldSSH, String> rankUpldCol;
	@FXML
	private  TableColumn<UpldSSH, String> idServerUpldCol;
	@FXML
	private  TableColumn<UpldSSH, String> localFileUpldCol;
	@FXML
	private  TableColumn<UpldSSH, String> remoteDirUpldCol;
	@FXML
	private  TableColumn<UpldSSH, String> executeUpldCol;

	@FXML
	private  TableColumn<DwldSSH, String> rankDwldCol;
	@FXML
	private  TableColumn<DwldSSH, String> idServerDwldCol;
	@FXML
	private  TableColumn<DwldSSH, String> remoteDirFilesDwldCol;
	@FXML
	private  TableColumn<DwldSSH, String> localDirDwldCol;
	@FXML
	private  TableColumn<DwldSSH, String> howmanyDwldCol;
	@FXML
	private  TableColumn<DwldSSH, String> actionDwldCol;

	@FXML
	private  Tab tabCfg;

	@FXML
	private  Tab chooseRule;

	@FXML
	private  TextArea taGenProps;
	@FXML
	public  Tab getTabRemoteAction() {
		return tabRemoteAction;
	}
	
	@FXML
	public  TextArea getTaGenRules() {
		return taGenRules;
	}
	

	public  Tab getTabNet() {
		return tabNet;
	}
	
	@FXML
	public  Tab getTabUpld() {
		return tabUpld;
	}
	
	@FXML
	public  TableView<UpldSSH> getTableUpld() {
		return tableUpld;
	}
	
	@FXML
	public  TableView<DwldSSH> getTableDwld() {
		return tableDwld;
	}
	
	@FXML
	public  TextArea getTaGenProps() {
		return taGenProps;
	}
	
	@FXML
	public  TextArea getTaCheck() {
		return taCheck;
	}
	
	@FXML
	public  TabPane getTabPane() {
		return tabPane;
	}
	
	@FXML
	public  AnchorPane getWork() {
		return work;
	}
	
	
	@FXML
	public  TextField getTfDirWork() {
		return tfDirWork;
	}
	
	
	@FXML
	public  TextField getTfDirLogs() {
		return tfDirLogs;
	}

	
	@FXML
	public  TextField getTfCsv() {
		return tfCsv;
	}

	

	@FXML
	private  TextArea taCheck;
	@FXML
	private  TabPane tabPane;
	@FXML
	private  GridPane gridCnx;
	@FXML
	private  AnchorPane start;
	@FXML
	public AnchorPane getStart() {
		return start;
	}

	

	@FXML
	private  AnchorPane work;

	@FXML
	private  Group group;

	@FXML
	private TextField repInstall;

	@FXML
	private  CheckBox gzip;
	@FXML
	public  CheckBox getGzip() {
		return gzip;
	}

	

	@FXML
	private  CheckBox fullPackage;
	@FXML
	public  CheckBox getFullPackage() {
		return fullPackage;
	}


	@FXML
	private  TextField tfDirWork;

	@FXML
	private  TextField tfDirLogs;

	@FXML
	private  TextField tfCsv;

	@FXML
	private  ListView<String> listView1;
	
	@FXML
	private  ListView<String> listView2;
	@FXML
	public ListView<String> getListView1() {
		return listView1;
	}


	@FXML
	public ListView<String> getListView2() {
		return listView2;
	}

	

	

	@FXML
	private AnchorPane createDiag;

	@FXML
	private AnchorPane openDiag;
	@FXML
	private TextField newProjectTf;
	@FXML
	private TextField openProjectTf;

	@FXML
	private ComboBox<String> confCB;

	@FXML
	private ComboBox<Label> projectsCB;
	@FXML
	private ComboBox<Label> projectsCB2;

	private  boolean isCfListView2 = false;

	// @Override
	// public void start(Stage primaryStage) throws Exception {
//	public Packager(Stage primaryStage) {
//		
//		// Parent rootfxml =
//		// fxmlLoader.load(getClass().getResource("root.fxml"));
//		
//	}

	// public  void main(String[] args) {
	// workspace = System.getProperty("workspace");
	// root = System.getProperty("root");
	// launch(args);
	// }

	@FXML
	public void initializeCB2() {
		// initialiser la comboBox

		File dirWk = new File(Main.workspace);
		File[] lst = dirWk.listFiles(new FileFilter() {
			public boolean accept(File pathname) {
				return pathname.isDirectory();
			}
		});
		// if(null == projectsCB)System.out.println("projectsCB not reachable");
		// else System.out.println("projectsCB is reachable");
		ObservableList<Label> items = projectsCB2.getItems();
		items.clear();
		for (File file : lst) {
			Label lab = new Label(file.getName());
			lab.setFont(new Font("Arial", 16));
			items.add(lab);
		}
		Comparator<Label> labComp = new Comparator<Label>() {

			@Override
			public int compare(Label o1, Label o2) {
				// TODO Auto-generated method stub
				return o1.getText().compareTo(o2.getText());
			}

		};
		FXCollections.sort(items, labComp);
		projectsCB2.setItems(items);
	}

	@FXML
	public void initializeCB() {
		// initialiser la comboBox

		File dirWk = new File(Main.workspace);
		File[] lst = dirWk.listFiles(new FileFilter() {
			public boolean accept(File pathname) {
				return pathname.isDirectory();
			}
		});
		// if(null == projectsCB)System.out.println("projectsCB not reachable");
		// else System.out.println("projectsCB is reachable");
		ObservableList<Label> items = projectsCB.getItems();
		items.clear();
		for (File file : lst) {
			Label lab = new Label(file.getName());
			lab.setFont(new Font("Arial", 16));
			items.add(lab);
		}
		Comparator<Label> labComp = new Comparator<Label>() {

			@Override
			public int compare(Label o1, Label o2) {
				// TODO Auto-generated method stub
				return o1.getText().compareTo(o2.getText());
			}

		};
		FXCollections.sort(items, labComp);
		projectsCB.setItems(items);
	}

	


	
	public void fillOthersConfigurations() {
		File dirWk = new File(Main.workspace + File.separator +Main. currentProject
				+ File.separator + "saves");
		File[] lst = dirWk.listFiles(new FileFilter() {
			public boolean accept(File pathname) {
				return pathname.getName().endsWith(".properties");
			}
		});
		
		Main.packRoot.getOthersConfBut().getItems().clear();
		Main.packRoot.getOthersConfBut().getItems().add(
				new MenuItem("bytemanpkg.properties"));
		for (File file : lst) {
			Main.packRoot.getOthersConfBut().getItems().add(new MenuItem(file.getName()));
		}
		EventHandler<ActionEvent> myHandler = new EventHandler<ActionEvent>() {

			public void handle(ActionEvent arg0) {
				miChosen(arg0);

			}

		};
		ObservableList<MenuItem> lstMi =Main.packRoot.getOthersConfBut().getItems();
		for (MenuItem mi : lstMi) {
			mi.setOnAction(myHandler);
		}

		
	}

	private void miChosen(ActionEvent arg0) {
		System.out.println("MenuItem => "
				+ ((MenuItem) arg0.getSource()).getText());
		// Sauvegarde du fichier bytemanpkg.properties sous saves
		String chStr = ((MenuItem) arg0.getSource()).getText();
		System.out.println("init updating with : " + chStr);
		if (!chStr.equals("bytemanpkg.properties")) {
			System.out.println("0 updating with : " + chStr);
			File from = new File(Main.workspace + File.separator + Main.currentProject
					+ File.separator + "bytemanpkg.properties");
			if (from.exists()) {
				File to = new File(Main.workspace + File.separator + Main.currentProject
						+ File.separator + "saves" + File.separator
						+ "bytemanpkgBck.properties");
				if (to.exists())
					to.delete();
				Utils.copy(from, to);
			}

			File fromBis = new File(Main.workspace + File.separator + Main.currentProject
					+ File.separator + "saves" + File.separator + chStr);
			if (from.exists())
				from.delete();
			Utils.copy(fromBis, from);
			File fprops = new File(Main.workspace + File.separator + Main.currentProject
					+ File.separator + "bytemanpkg.properties");
			if (fprops.exists()) {
				FileInputStream fis = null;

				try {
					fis = new FileInputStream(fprops);

					Main.propsConfRules.load(fis);
					// replissage e l onglet Configuration
					if (Main.propsConfRules.getProperty("bytemanpkg.gzip", "false")
							.equals("false")) {
						gzip.setSelected(false);
					} else {
						gzip.setSelected(true);
					}
					if (Main.propsConfRules.getProperty("bytemanpkg.fullPackage",
							"false").equals("false")) {
						fullPackage.setSelected(false);
					} else {
						fullPackage.setSelected(true);
					}
					tfDirWork.setText(Main.propsConfRules.getProperty(
							"bytemanpkg.dirWork", "/tmp"));
					tfDirLogs.setText(Main.propsConfRules.getProperty(
							"bytemanpkg.dirLogs", "/tmp"));
					tfCsv.setText(Main.propsConfRules.getProperty(
							"bytemanpkg.csvSep", ";"));
					Utils.saveConfiguration(false);
					System.out.println("updating with : " + chStr);

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					if (null != fis) {
						try {
							fis.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}

			}

		} else {
			// do nothing. Continue with the current configuration
		}
	}

	@FXML
	public void closeProject(ActionEvent event) {
		System.out.println("You clicked closeProject!");
		Main.currentProject = "";

		Main.currentStage
				.setTitle("Byteman Packager =>  no opened Project");
		bSave.setDisable(true);
		bUpload.setDisable(true);
		saveLocalbut.setDisable(true);
		work.setVisible(false);
	}

	@FXML
	public void cancelOpenDiag(ActionEvent event) {
		openDiag.getScene().getWindow().hide();
	}

	@FXML
	public void exit(ActionEvent event) {
		System.out.println("You clicked exit!");
		System.exit(0);
	}

	@FXML
	public void chooseRules(ActionEvent event) {
		System.out.println("click on Button Choose Rules");
		if (gzip.isSelected())
			System.out.println("gzip is selected");
		else
			System.out.println("gzip is not selected");
	}

	public  void creerCellFactoryListView2() {
		listView2
				.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
					@Override
					public ListCell<String> call(ListView<String> list) {
						return new ColorTextCell();
					}
				});
	}

	@FXML
	public void creerCellFactory(Event event) {

		// listView2.getSelectionModel().selectedItemProperty()
		// .addListener(new ChangeListener<String>() {
		// public void changed(
		// ObservableValue<? extends String> ov,
		// String old_val, String new_val) {
		// System.out.println("Valeur changee old value="
		// + old_val + "\n new val=" + new_val);
		//
		// }
		// });

		// remettre a jour
		creerCellFactoryListView2();
	}

	@FXML
	public void fillRules(Event event) {

		if (chooseRule.isSelected()) {
			System.out.println("remplissage des regles");
			getListView1().getSelectionModel()
					.setSelectionMode(SelectionMode.SINGLE);
			getListView2().getSelectionModel()
					.setSelectionMode(SelectionMode.SINGLE);
			System.out.println("Tab. Choose Rules selected");
			File fProps = new File(Main.workspace + File.separator + Main.currentProject
					+ File.separator + "bytemanpkg.properties");

			if (fProps.exists()) {
				try {
					Main.propsConfRules.load(new FileInputStream(fProps));
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				ObservableList<String> lst1 = getListView1().getItems();
				lst1.clear();
				for (String rule : Main.lstRules) {
					
					lst1.add(rule);
				}
				System.out.println("lst1.size="+lst1.size());

				ObservableList<String> lst2 = getListView2().getItems();

				lst2.clear();
				String[] listRulesChosen = Main.propsConfRules.getProperty(
						"listRulesChosen", "").split(";");
				for (String rule : listRulesChosen) {
					// Chercher les noms des param
					if (rule.trim().length() > 0) {
						String ruleName = rule.trim().split("_")[0];

						String param1 = Main.bytemanRules.getProperty(
								ruleName + ".param1").split(";")[0];

						String tmpMethods = Main.propsConfRules.getProperty(rule
								+ "." + param1, "");
						if (!"".equals(tmpMethods)) {
							Main.hmRules.put(rule, true);
						} else {
							Main.hmRules.put(rule, false);
						}
						lst2.add(rule);

					}
				}

			} else {
				ObservableList<String> lst1 = getListView1().getItems();
				lst1.clear();
				for (String rule : Main.lstRules) {
					lst1.add(rule);
				}

				ObservableList<String> lst2 = getListView2().getItems();

				lst2.clear();
			}
			// Delete template from listView1 that are allowed to be instanciate
			// only once
			ObservableList<String> lst1 = getListView1().getItems();
			for (String itemAlias : getListView2().getItems()) {
				String itemWithoutAlias = itemAlias.split("_alias")[0];
				if (!Main.bytemanRules.getProperty(
						itemWithoutAlias + ".multiInstantiation", "true")
						.equals("true")) {
					lst1.remove(itemWithoutAlias);
				}
			}
			getListView1().setItems(lst1);
			FXCollections.sort(getListView2().getItems());
			FXCollections.sort(getListView1().getItems());
			// lst2.addAll("Item4");
			if (gzip.isSelected())
				System.out.println("gzip is selected");
			else
				System.out.println("gzip is not selected");
		} else if (tabNet.isSelected() && tabCnx.isSelected()) {

			List<String> list = new ArrayList<String>();
			ObservableList<String> itemsExecute = (ObservableList<String>) FXCollections
					.observableList(list);
			list.add("Yes");
			list.add("No");

			getExecuteCbUpld().setItems(itemsExecute);

			List<String> listdwld = new ArrayList<String>();
			ObservableList<String> itemsAction = (ObservableList<String>) FXCollections
					.observableList(listdwld);
			listdwld.add("NoCompress_Prefix");
			listdwld.add("Compress_NoPrefix");
			listdwld.add("NoCompress_NoPrefix");
			listdwld.add("Compress_Prefix");
			getActionCbDwld().setItems(itemsAction);

			// remplir la table avec le contenu de
			// <workspace>/<project>/connections.properties
			System.out.println("Tab Cnx selected");
			File fProps = new File(Main.workspace + File.separator + Main.currentProject
					+ File.separator + "connections.properties");
			Properties props = new Properties();
			if (fProps.exists()) {
				tableCnx.setEditable(true);
				tableUpld.setEditable(true);
				tableDwld.setEditable(true);
				try {
					props.load(new FileInputStream(fProps));
					// Charger la table View
					int i = 1;
					Callback<TableColumn<CnxSSH, String>, TableCell<CnxSSH, String>> cellFactory1 = new Callback<TableColumn<CnxSSH, String>, TableCell<CnxSSH, String>>() {

						public TableCell<CnxSSH, String> call(
								TableColumn<CnxSSH, String> p) {
							TextFieldTableCell<CnxSSH, String> txtField = new TextFieldTableCell<CnxSSH, String>(
									new DefaultStringConverter());

							return txtField;
						}
					};
					idServer.setCellFactory(cellFactory1);
					addrServer.setCellFactory(cellFactory1);
					port.setCellFactory(cellFactory1);
					login.setCellFactory(cellFactory1);

					Callback<TableColumn<CnxSSH, String>, TableCell<CnxSSH, String>> cellFactory2 = new Callback<TableColumn<CnxSSH, String>, TableCell<CnxSSH, String>>() {

						public TableCell<CnxSSH, String> call(
								TableColumn<CnxSSH, String> p) {
							PasswordFieldCell<CnxSSH, String> txtField = new PasswordFieldCell<CnxSSH, String>(
									password, "password");
							// txtField.setConverter(new
							// DefaultStringConverter());
							return txtField;
						}

					};
					password.setCellFactory(cellFactory2);
					Callback<TableColumn<CnxSSH, String>, TableCell<CnxSSH, String>> cellFactory2Bis = new Callback<TableColumn<CnxSSH, String>, TableCell<CnxSSH, String>>() {

						public TableCell<CnxSSH, String> call(
								TableColumn<CnxSSH, String> p) {
							PasswordFieldCell<CnxSSH, String> txtField = new PasswordFieldCell<CnxSSH, String>(
									rootpassword, "rootpassword");
							// txtField.setConverter(new
							// DefaultStringConverter());
							return txtField;
						}

					};
					rootpassword.setCellFactory(cellFactory2Bis);
					// password.setCellFactory(new Callback<TableColumn<CnxSSH,
					// String>, TableCell<CnxSSH, String>>() {
					//
					// @Override
					// public TableCell<CnxSSH, String> call(
					// TableColumn<CnxSSH, String> cell) {
					// return new PasswordFieldCell(tableCnx);
					// }
					// });

					idServer.setCellValueFactory(new PropertyValueFactory<CnxSSH, String>(
							"idServer"));
					addrServer
							.setCellValueFactory(new PropertyValueFactory<CnxSSH, String>(
									"addrServer"));
					port.setCellValueFactory(new PropertyValueFactory<CnxSSH, String>(
							"port"));
					login.setCellValueFactory(new PropertyValueFactory<CnxSSH, String>(
							"login"));
					password.setCellValueFactory(new PropertyValueFactory<CnxSSH, String>(
							"password"));
					rootpassword
							.setCellValueFactory(new PropertyValueFactory<CnxSSH, String>(
									"rootpassword"));
					idServer.setOnEditCommit(new EventHandler<CellEditEvent<CnxSSH, String>>() {

						@Override
						public void handle(CellEditEvent<CnxSSH, String> t) {
							((CnxSSH) t.getTableView().getItems()
									.get(t.getTablePosition().getRow()))
									.setIdServer(t.getNewValue());

						}
					});
					login.setOnEditCommit(new EventHandler<CellEditEvent<CnxSSH, String>>() {

						@Override
						public void handle(CellEditEvent<CnxSSH, String> t) {
							((CnxSSH) t.getTableView().getItems()
									.get(t.getTablePosition().getRow()))
									.setLogin(t.getNewValue());

						}
					});
					addrServer
							.setOnEditCommit(new EventHandler<CellEditEvent<CnxSSH, String>>() {

								@Override
								public void handle(
										CellEditEvent<CnxSSH, String> t) {
									((CnxSSH) t.getTableView().getItems()
											.get(t.getTablePosition().getRow()))
											.setAddrServer(t.getNewValue());

								}
							});
					port.setOnEditCommit(new EventHandler<CellEditEvent<CnxSSH, String>>() {

						@Override
						public void handle(CellEditEvent<CnxSSH, String> t) {
							((CnxSSH) t.getTableView().getItems()
									.get(t.getTablePosition().getRow()))
									.setPort(t.getNewValue());

						}
					});

					// pour password logique de mise a jour dans la classe
					// PasswordFieldCell

					ObservableList<CnxSSH> lst = ((ObservableList<CnxSSH>) tableCnx
							.getItems());
					lst.clear();
					while (true) {

						if (props.containsKey("server." + i + ".IdServer")) {
							System.out.println("chargement de "
									+ props.getProperty("server." + i
											+ ".IdServer"));
							// Creation d'une Cnx
							CnxSSH cnx = new CnxSSH(
									props.getProperty("server." + i
											+ ".IdServer"),
									props.getProperty("server." + i
											+ ".AddrServer"),
									props.getProperty("server." + i + ".Port"),
									props.getProperty("server." + i + ".Login"),
									props.getProperty("server." + i
											+ ".Password"), props.getProperty(
											"server." + i + ".RootPassword",
											props.getProperty("server." + i
													+ ".Password")));

							lst.add((CnxSSH) cnx);

							i++;
						} else
							break;
					}

					FXCollections.sort(lst);
					tableCnx.setItems(lst);
					tableUpld.setEditable(true);

					// Charger la table View Upload
					i = 1;
					Callback<TableColumn<UpldSSH, String>, TableCell<UpldSSH, String>> cellFactory3 = new Callback<TableColumn<UpldSSH, String>, TableCell<UpldSSH, String>>() {

						public TableCell<UpldSSH, String> call(
								TableColumn<UpldSSH, String> p) {
							TextFieldTableCell<UpldSSH, String> txtField = new TextFieldTableCell<UpldSSH, String>(
									new DefaultStringConverter());

							return txtField;
						}
					};

					Callback<TableColumn<UpldSSH, String>, TableCell<UpldSSH, String>> cellFactoryCb = new Callback<TableColumn<UpldSSH, String>, TableCell<UpldSSH, String>>() {

						public TableCell<UpldSSH, String> call(
								TableColumn<UpldSSH, String> p) {
							ComboBoxCellUpld<UpldSSH, String> txtField = new ComboBoxCellUpld<UpldSSH, String>(
									executeUpldCol);
							// txtField.setConverter(new
							// DefaultStringConverter());
							return txtField;
						}

					};

					rankUpldCol.setCellFactory(cellFactory3);
					idServerUpldCol.setCellFactory(cellFactory3);
					localFileUpldCol.setCellFactory(cellFactory3);
					remoteDirUpldCol.setCellFactory(cellFactory3);

					executeUpldCol.setCellFactory(cellFactoryCb);
					// password.setCellFactory(new
					// Callback<TableColumn<CnxSSH,
					// String>, TableCell<CnxSSH, String>>() {
					//
					// @Override
					// public TableCell<CnxSSH, String> call(
					// TableColumn<CnxSSH, String> cell) {
					// return new PasswordFieldCell(tableCnx);
					// }
					// });

					rankUpldCol
							.setCellValueFactory(new PropertyValueFactory<UpldSSH, String>(
									"rank"));
					idServerUpldCol
							.setCellValueFactory(new PropertyValueFactory<UpldSSH, String>(
									"idServer"));
					localFileUpldCol
							.setCellValueFactory(new PropertyValueFactory<UpldSSH, String>(
									"localFile"));
					remoteDirUpldCol
							.setCellValueFactory(new PropertyValueFactory<UpldSSH, String>(
									"remoteDirectory"));
					executeUpldCol
							.setCellValueFactory(new PropertyValueFactory<UpldSSH, String>(
									"execute"));

					rankUpldCol
							.setOnEditCommit(new EventHandler<CellEditEvent<UpldSSH, String>>() {

								@Override
								public void handle(
										CellEditEvent<UpldSSH, String> t) {
									((UpldSSH) t.getTableView().getItems()
											.get(t.getTablePosition().getRow()))
											.setRank(t.getNewValue());

								}
							});
					idServerUpldCol
							.setOnEditCommit(new EventHandler<CellEditEvent<UpldSSH, String>>() {

								@Override
								public void handle(
										CellEditEvent<UpldSSH, String> t) {
									((UpldSSH) t.getTableView().getItems()
											.get(t.getTablePosition().getRow()))
											.setIdServer(t.getNewValue());

								}
							});
					localFileUpldCol
							.setOnEditCommit(new EventHandler<CellEditEvent<UpldSSH, String>>() {

								@Override
								public void handle(
										CellEditEvent<UpldSSH, String> t) {
									((UpldSSH) t.getTableView().getItems()
											.get(t.getTablePosition().getRow()))
											.setLocalFile(t.getNewValue());

								}
							});
					remoteDirUpldCol
							.setOnEditCommit(new EventHandler<CellEditEvent<UpldSSH, String>>() {

								@Override
								public void handle(
										CellEditEvent<UpldSSH, String> t) {
									((UpldSSH) t.getTableView().getItems()
											.get(t.getTablePosition().getRow()))
											.setRemoteDirectory(t.getNewValue());

								}
							});

					executeUpldCol
							.setOnEditCommit(new EventHandler<CellEditEvent<UpldSSH, String>>() {

								@Override
								public void handle(
										CellEditEvent<UpldSSH, String> t) {
									((UpldSSH) t.getTableView().getItems()
											.get(t.getTablePosition().getRow()))
											.setExecute(t.getNewValue());

								}
							});

					ObservableList<UpldSSH> lst2 = ((ObservableList<UpldSSH>) tableUpld
							.getItems());
					lst2.clear();
					i = 1;
					while (true) {

						if (props.containsKey("upload." + i + ".rank")) {
							System.out
									.println("chargement de "
											+ props.getProperty("upload." + i
													+ ".rank"));
							// Creation d'une Cnx
							UpldSSH upld = new UpldSSH(
									props.getProperty("upload." + i + ".rank"),
									props.getProperty("upload." + i
											+ ".idServer"),
									props.getProperty("upload." + i
											+ ".localFile"),
									props.getProperty("upload." + i
											+ ".remoteDir"),
									props.getProperty("upload." + i
											+ ".execute"));

							lst2.add((UpldSSH) upld);

							i++;
						} else
							break;
					}
					FXCollections.sort(lst2);
					tableUpld.setItems(lst2);

					// Charger la tableView Download
					// @FXML
					// private  TableColumn<DwldSSH, String> rankDwldCol;
					// @FXML
					// private  TableColumn<DwldSSH, String>
					// idServerDwldCol;
					// @FXML
					// private  TableColumn<DwldSSH, String>
					// remoteDirFilesDwldCol;
					// @FXML
					// private  TableColumn<DwldSSH, String>
					// localDirDwldCol;
					// @FXML
					// private  TableColumn<DwldSSH, String>
					// howmanyDwldCol;
					// @FXML
					// private  TableColumn<DwldSSH, String>
					// actionDwldCol;
					//

					i = 1;
					Callback<TableColumn<DwldSSH, String>, TableCell<DwldSSH, String>> cellFactory5 = new Callback<TableColumn<DwldSSH, String>, TableCell<DwldSSH, String>>() {

						public TableCell<DwldSSH, String> call(
								TableColumn<DwldSSH, String> p) {
							TextFieldTableCell<DwldSSH, String> txtField = new TextFieldTableCell<DwldSSH, String>(
									new DefaultStringConverter());

							return txtField;
						}
					};

					Callback<TableColumn<DwldSSH, String>, TableCell<DwldSSH, String>> cellFactoryCb2 = new Callback<TableColumn<DwldSSH, String>, TableCell<DwldSSH, String>>() {

						public TableCell<DwldSSH, String> call(
								TableColumn<DwldSSH, String> p) {
							ComboBoxCellDwld<DwldSSH, String> cb = new ComboBoxCellDwld<DwldSSH, String>(
									actionDwldCol);
							// txtField.setConverter(new
							// DefaultStringConverter());
							return cb;
						}

					};

					rankDwldCol.setCellFactory(cellFactory5);
					idServerDwldCol.setCellFactory(cellFactory5);
					remoteDirFilesDwldCol.setCellFactory(cellFactory5);
					localDirDwldCol.setCellFactory(cellFactory5);
					howmanyDwldCol.setCellFactory(cellFactory5);

					actionDwldCol.setCellFactory(cellFactoryCb2);
					// password.setCellFactory(new
					// Callback<TableColumn<CnxSSH,
					// String>, TableCell<CnxSSH, String>>() {
					//
					// @Override
					// public TableCell<CnxSSH, String> call(
					// TableColumn<CnxSSH, String> cell) {
					// return new PasswordFieldCell(tableCnx);
					// }
					// });

					rankDwldCol
							.setCellValueFactory(new PropertyValueFactory<DwldSSH, String>(
									"rank"));
					idServerDwldCol
							.setCellValueFactory(new PropertyValueFactory<DwldSSH, String>(
									"idServer"));
					localDirDwldCol
							.setCellValueFactory(new PropertyValueFactory<DwldSSH, String>(
									"localDirectory"));
					remoteDirFilesDwldCol
							.setCellValueFactory(new PropertyValueFactory<DwldSSH, String>(
									"remoteFiles"));

					howmanyDwldCol
							.setCellValueFactory(new PropertyValueFactory<DwldSSH, String>(
									"howmany"));
					actionDwldCol
							.setCellValueFactory(new PropertyValueFactory<DwldSSH, String>(
									"action"));

					rankDwldCol
							.setOnEditCommit(new EventHandler<CellEditEvent<DwldSSH, String>>() {

								@Override
								public void handle(
										CellEditEvent<DwldSSH, String> t) {
									((DwldSSH) t.getTableView().getItems()
											.get(t.getTablePosition().getRow()))
											.setRank(t.getNewValue());

								}
							});
					idServerDwldCol
							.setOnEditCommit(new EventHandler<CellEditEvent<DwldSSH, String>>() {

								@Override
								public void handle(
										CellEditEvent<DwldSSH, String> t) {
									((DwldSSH) t.getTableView().getItems()
											.get(t.getTablePosition().getRow()))
											.setIdServer(t.getNewValue());

								}
							});
					localDirDwldCol
							.setOnEditCommit(new EventHandler<CellEditEvent<DwldSSH, String>>() {

								@Override
								public void handle(
										CellEditEvent<DwldSSH, String> t) {
									((DwldSSH) t.getTableView().getItems()
											.get(t.getTablePosition().getRow()))
											.setLocalDirectory(t.getNewValue());

								}
							});
					remoteDirFilesDwldCol
							.setOnEditCommit(new EventHandler<CellEditEvent<DwldSSH, String>>() {

								@Override
								public void handle(
										CellEditEvent<DwldSSH, String> t) {
									((DwldSSH) t.getTableView().getItems()
											.get(t.getTablePosition().getRow()))
											.setRemoteFiles(t.getNewValue());

								}
							});

					howmanyDwldCol
							.setOnEditCommit(new EventHandler<CellEditEvent<DwldSSH, String>>() {

								@Override
								public void handle(
										CellEditEvent<DwldSSH, String> t) {
									((DwldSSH) t.getTableView().getItems()
											.get(t.getTablePosition().getRow()))
											.setHowmany(t.getNewValue());

								}
							});
					actionDwldCol
							.setOnEditCommit(new EventHandler<CellEditEvent<DwldSSH, String>>() {

								@Override
								public void handle(
										CellEditEvent<DwldSSH, String> t) {
									((DwldSSH) t.getTableView().getItems()
											.get(t.getTablePosition().getRow()))
											.setAction(t.getNewValue());

								}
							});

					ObservableList<DwldSSH> lst3 = ((ObservableList<DwldSSH>) tableDwld
							.getItems());
					lst3.clear();
					i = 1;
					while (true) {

						if (props.containsKey("download." + i + ".rank")) {
							System.out.println("chargement de "
									+ props.getProperty("download." + i
											+ ".rank"));
							// Creation d'une Cnx
							DwldSSH dwld = new DwldSSH(
									props.getProperty("download." + i + ".rank"),
									props.getProperty("download." + i
											+ ".idServer"), props
											.getProperty("download." + i
													+ ".remoteFiles"), props
											.getProperty("download." + i
													+ ".localDirectory"), props
											.getProperty("download." + i
													+ ".howmany"), props
											.getProperty("download." + i
													+ ".action"));

							lst3.add((DwldSSH) dwld);

							i++;
						} else
							break;
					}
					FXCollections.sort(lst3);
					tableDwld.setItems(lst3);

				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}

	}

	private ComboBox<String> getActionCbDwld() {
		// TODO Auto-generated method stub
		return actionCbDwld;
	}

	@FXML
	public void fillTabRemoteAction(Event event) {

		// remplir les items de cbxRemoteServers a partir de allServers
		// List<String>

		ObservableList<String> items = cbxRemoteServers.getItems();
		items.clear();
		items.add("ALL");
		for (String item : Main.allServers) {
			items.add((String) item);
			System.out.println("ajout item=" + item);
		}
		FXCollections.sort(items);
		cbxRemoteServers.setItems(items);
		cbxRemoteServers.getSelectionModel().select("ALL");

		// Retrouver la commande PID

		if (Main.propsConfRules.getProperty("bytemanpkg.cmdPID", "").trim().length() == 0) {
			Main.propsConfRules.setProperty("bytemanpkg.cmdPID",
					"jps -lv | grep <a-regexp>  | tr -s ' ' | cut -d ' ' -f1");
			cmdPID.setText(Main.propsConfRules.getProperty("bytemanpkg.cmdPID",
					"jps -lv | grep <a-regexp>  | tr -s ' ' | cut -d ' ' -f1"));
		}
		if (Main.propsConfRules.getProperty("bytemanpkg.portListener", "").trim()
				.length() == 0) {
			Main.propsConfRules.setProperty("bytemanpkg.portListener", "9091");
			portListenerTf.setText(Main.propsConfRules.getProperty(
					"bytemanpkg.portListener", "9091"));

		}

	}

	@FXML
	public void addItem(ActionEvent event) {
		String item = listView1.getFocusModel().getFocusedItem();
		if (null != item) {
			System.out.println("item="
					+ listView1.getFocusModel().getFocusedItem());
			System.out.println("selectedItem=" + item);

			// Verification if item is multi-Instanciable
			if (!Main.bytemanRules.getProperty(item + ".multiInstantiation", "true")
					.equalsIgnoreCase("true")) {
				ObservableList<String> lst1 = listView1.getItems();
				lst1.remove(item);
				FXCollections.sort(lst1);
				listView1.setItems(lst1);
			}

			ObservableList<String> lst2 = listView2.getItems();
			int i = 0;
			boolean cont = true;
			String itemAlias = "";
			while (cont) {
				if (!lst2.contains(item + "_alias" + i)) {
					itemAlias = item + "_alias" + i;
					lst2.add(itemAlias);

					cont = false;
				} else {
					i++;
				}
			}

			FXCollections.sort(lst2);
			listView2.setItems(lst2);
			// propropsConfRulesps
			if (!Main.propsConfRules.containsKey("listRulesChosen")) {
				// le rajouter
				Main.propsConfRules.put("listRulesChosen", "");
			}
			List<String> lstChosen = new ArrayList<String>();
			for (String tmprule : Main.propsConfRules.getProperty(
					"listRulesChosen").split(";")) {
				lstChosen.add(tmprule);
			}

			if (!lstChosen.contains(itemAlias)) {
				lstChosen.add(itemAlias);
			}
			String propsLstRules = "";
			for (String rl : lstChosen) {
				if (rl.trim().length() > 0)
					propsLstRules += rl + ";";
			}

			Main.propsConfRules.put("listRulesChosen", propsLstRules);
			Main.hmRules.put(itemAlias, false);

			MessageBox.show(Main.currentStage, "To configure or modify : "
					+ itemAlias
					+ ", right click on the item in the right ListView\n",
					" Configuring a rule", MessageBox.ICON_INFORMATION);

		}

	}

	@FXML
	public void remItem(ActionEvent event) {

		String item =listView2.getFocusModel().getFocusedItem();
		if (null != item) {
			System.out.println("suppression item=" + item);

			String itemWithoutAlias = item.split("_alias")[0];
			// Multi-instantiation ?
			if (!Main.bytemanRules.getProperty(
					itemWithoutAlias + ".multiInstantiation", "true").equals(
					"true")) {
				ObservableList<String> lst1 = listView1.getItems();
				lst1.add(itemWithoutAlias);
				FXCollections.sort(lst1);
				listView1.setItems(lst1);
			}

			List<String> lstChosen = new ArrayList<String>();
			for (String tmprule : Main.propsConfRules.getProperty(
					"listRulesChosen").split(";")) {
				lstChosen.add(tmprule);
			}

			if (lstChosen.contains(item)) {
				lstChosen.remove(item);
			}
			System.out.println("listRulesChosen="
					+ Main.propsConfRules.getProperty("listRulesChosen"));
			String propsLstRules = "";
			for (String rl : lstChosen) {
				if (rl.trim().length() > 0)
					propsLstRules += rl + ";";
			}

			Main.propsConfRules.put("listRulesChosen", propsLstRules);
			System.out.println("listRulesChosen Apres MAJ ="
					+ Main.propsConfRules.getProperty("listRulesChosen"));
			Set<Object> keys = Main.propsConfRules.keySet();
			Properties tmpProps = new Properties();
			tmpProps.put("listRulesChosen", propsLstRules);

			keys = Main.propsConfRules.keySet();
			for (Object key : keys) {
				if (!((String) key).equals("listRulesChosen")) {
					System.out.println("traitement key=" + key);
					if (!((String) key).split("\\.")[0].equals(item)) {

						System.out.println("ajout key=" + key);
						tmpProps.put((String) key, Main.propsConfRules
								.getProperty((String) key));

					}
				}
			}

			Main.propsConfRules.clear();
			Main.propsConfRules = tmpProps;

			Main.hmRules.remove(item);

			System.out.println("item="
					+ listView2.getFocusModel().getFocusedItem());
			System.out.println("selectedItem=" + item);

			ObservableList<String> lst2 = listView2.getItems();
			lst2.remove(item);
			FXCollections.sort(lst2);
			listView2.setItems(lst2);

			// Resauvegarder le fichier bytemanpkg.properties
			File fProps = new File(Main.workspace + File.separator +Main.currentProject
					+ File.separator + "bytemanpkg.properties");
			if (fProps.exists())
				fProps.delete();
			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(fProps);
				Main.propsConfRules.store(fos, "MAJ " + new Date());

			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				try {
					fos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			// rettoyer propsConfRules
			Utils.saveConfiguration(true);
		}
	}

	@FXML
	public void remAll(ActionEvent event) {

		ObservableList<String> lst2 = listView2.getItems();
		ObservableList<String> lst1 = listView1.getItems();
		for (String item : lst2) {
			String itemWithoutAlias = item.split("_alias")[0];
			// Multi-instantiation ?
			if (!Main.bytemanRules.getProperty(
					itemWithoutAlias + ".multiInstantiation", "true").equals(
					"true")) {

				lst1.add(itemWithoutAlias);

			}
		}
		FXCollections.sort(lst1);
		listView1.setItems(lst1);
		lst2.clear();
		listView2.setItems(lst2);

		Main.propsConfRules.clear();
		Main.hmRules.clear();
	}

	@FXML
	public void openItem(MouseEvent event) {
		if (event.getButton() != MouseButton.PRIMARY) {
			System.out.println("Clicked on :"
					+ listView2.getFocusModel().getFocusedItem());
			new MyStage(listView2.getFocusModel().getFocusedItem());

		}

	}

	@FXML
	public void addUpld(ActionEvent event) {

		if (null != idServerTfUpld.getText()
				&& idServerTfUpld.getText().length() > 0
				&& null != rankTfUpld.getText()
				&&  rankTfUpld.getText().length() > 0
				&& null != localFileTfUpdl.getText()
				&& localFileTfUpdl.getText().length() > 0
				&& null != remoteDirTfUpld.getText()
				&& remoteDirTfUpld.getText().length() > 0
				&& null != (String) executeCbUpld.getSelectionModel()
						.getSelectedItem()
				&& ((String) executeCbUpld.getSelectionModel()
						.getSelectedItem()).length() > 0) {
			try {
				Integer.parseInt(rankTfUpld.getText());
			} catch (NumberFormatException nfe) {
				rankTfUpld.setText("-1");
			}
			UpldSSH upld = new UpldSSH(rankTfUpld.getText(),
					idServerTfUpld.getText(), localFileTfUpdl.getText(),
					remoteDirTfUpld.getText(), (String) executeCbUpld
							.getSelectionModel().getSelectedItem());
			ObservableList<UpldSSH> lst = ((ObservableList<UpldSSH>) tableUpld
					.getItems());

			lst.add(upld);
			FXCollections.sort(lst);
			tableUpld.setItems(lst);
			if (lst.size() == 1) {

				saveCnx(event);

				// remplir la table tabUpld avec le contenu de
				// <workspace>/<project>/connections.properties
				System.out.println("Tab Upld selected");
				File fProps = new File(Main.workspace + File.separator
						+Main. currentProject + File.separator
						+ "connections.properties");
				Properties props = new Properties();
				if (fProps.exists()) {
					tableUpld.setEditable(true);
					try {
						props.load(new FileInputStream(fProps));
						// Charger la table View
						int i = 1;
						Callback<TableColumn<UpldSSH, String>, TableCell<UpldSSH, String>> cellFactory1 = new Callback<TableColumn<UpldSSH, String>, TableCell<UpldSSH, String>>() {

							public TableCell<UpldSSH, String> call(
									TableColumn<UpldSSH, String> p) {
								TextFieldTableCell<UpldSSH, String> txtField = new TextFieldTableCell<UpldSSH, String>(
										new DefaultStringConverter());

								return txtField;
							}
						};

						Callback<TableColumn<UpldSSH, String>, TableCell<UpldSSH, String>> cellFactoryCb = new Callback<TableColumn<UpldSSH, String>, TableCell<UpldSSH, String>>() {

							public TableCell<UpldSSH, String> call(
									TableColumn<UpldSSH, String> p) {
								ComboBoxCellUpld<UpldSSH, String> txtField = new ComboBoxCellUpld<UpldSSH, String>(
										executeUpldCol);
								// txtField.setConverter(new
								// DefaultStringConverter());
								return txtField;
							}

						};
						rankUpldCol.setCellFactory(cellFactory1);
						idServerUpldCol.setCellFactory(cellFactory1);
						localFileUpldCol.setCellFactory(cellFactory1);
						remoteDirUpldCol.setCellFactory(cellFactory1);

						executeUpldCol.setCellFactory(cellFactoryCb);
						// password.setCellFactory(new
						// Callback<TableColumn<CnxSSH,
						// String>, TableCell<CnxSSH, String>>() {
						//
						// @Override
						// public TableCell<CnxSSH, String> call(
						// TableColumn<CnxSSH, String> cell) {
						// return new PasswordFieldCell(tableCnx);
						// }
						// });

						rankUpldCol
								.setCellValueFactory(new PropertyValueFactory<UpldSSH, String>(
										"rank"));
						idServerUpldCol
								.setCellValueFactory(new PropertyValueFactory<UpldSSH, String>(
										"idServer"));
						localFileUpldCol
								.setCellValueFactory(new PropertyValueFactory<UpldSSH, String>(
										"localFile"));
						remoteDirUpldCol
								.setCellValueFactory(new PropertyValueFactory<UpldSSH, String>(
										"remoteDirectory"));
						executeUpldCol
								.setCellValueFactory(new PropertyValueFactory<UpldSSH, String>(
										"execute"));

						rankUpldCol
								.setOnEditCommit(new EventHandler<CellEditEvent<UpldSSH, String>>() {

									@Override
									public void handle(
											CellEditEvent<UpldSSH, String> t) {
										((UpldSSH) t
												.getTableView()
												.getItems()
												.get(t.getTablePosition()
														.getRow())).setRank(t
												.getNewValue());

									}
								});
						idServerUpldCol
								.setOnEditCommit(new EventHandler<CellEditEvent<UpldSSH, String>>() {

									@Override
									public void handle(
											CellEditEvent<UpldSSH, String> t) {
										((UpldSSH) t
												.getTableView()
												.getItems()
												.get(t.getTablePosition()
														.getRow()))
												.setIdServer(t.getNewValue());

									}
								});
						localFileUpldCol
								.setOnEditCommit(new EventHandler<CellEditEvent<UpldSSH, String>>() {

									@Override
									public void handle(
											CellEditEvent<UpldSSH, String> t) {
										((UpldSSH) t
												.getTableView()
												.getItems()
												.get(t.getTablePosition()
														.getRow()))
												.setLocalFile(t.getNewValue());

									}
								});
						remoteDirUpldCol
								.setOnEditCommit(new EventHandler<CellEditEvent<UpldSSH, String>>() {

									@Override
									public void handle(
											CellEditEvent<UpldSSH, String> t) {
										((UpldSSH) t
												.getTableView()
												.getItems()
												.get(t.getTablePosition()
														.getRow()))
												.setRemoteDirectory(t
														.getNewValue());

									}
								});

						executeUpldCol
								.setOnEditCommit(new EventHandler<CellEditEvent<UpldSSH, String>>() {

									@Override
									public void handle(
											CellEditEvent<UpldSSH, String> t) {
										((UpldSSH) t
												.getTableView()
												.getItems()
												.get(t.getTablePosition()
														.getRow()))
												.setExecute(t.getNewValue());

									}
								});

						lst = ((ObservableList<UpldSSH>) tableUpld.getItems());
						lst.clear();

						while (true) {

							if (props.containsKey("upload." + i + ".rank")) {
								System.out.println("chargement de "
										+ props.getProperty("upload." + i
												+ ".rank"));
								// Creation d'une Cnx
								upld = new UpldSSH(props.getProperty("upload."
										+ i + ".rank"),
										props.getProperty("upload." + i
												+ ".idServer"),
										props.getProperty("upload." + i
												+ ".localFile"),
										props.getProperty("upload." + i
												+ ".remoteDir"),
										props.getProperty("upload." + i
												+ ".execute"));

								lst.add((UpldSSH) upld);

								i++;
							} else
								break;
						}
						FXCollections.sort(lst);
						tableUpld.setItems(lst);

					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}

			}

			idServerTfUpld.clear();
			rankTfUpld.clear();
			localFileTfUpdl.clear();
			remoteDirTfUpld.clear();

		}

	}

	public void addDwld(ActionEvent event) {

		//
		if (null != rankTfDwld.getText()
				&& rankTfDwld.getText().length() > 0
				&& null != idServerTfDwld.getText()
				&& idServerTfDwld.getText().length() > 0
				&& null != remoteDirFilesTfDwld.getText()
				&& remoteDirFilesTfDwld.getText().length() > 0
				&& null != localDirTfDwld.getText()
				&& localDirTfDwld.getText().length() > 0

				&& null != howmanyTfDwld.getText()
				&& howmanyTfDwld.getText().length() > 0
				&& null != (String) actionCbDwld.getSelectionModel()
						.getSelectedItem()
				&& ((String) actionCbDwld.getSelectionModel().getSelectedItem())
						.length() > 0) {
			try {
				Integer.parseInt(rankTfDwld.getText());
			} catch (NumberFormatException nfe) {
				rankTfDwld.setText("-1");
			}
			DwldSSH dwld = new DwldSSH(rankTfDwld.getText(),
					idServerTfDwld.getText(), remoteDirFilesTfDwld.getText(),
					localDirTfDwld.getText(), howmanyTfDwld.getText(),
					(String) actionCbDwld.getSelectionModel().getSelectedItem());
			ObservableList<DwldSSH> lst = ((ObservableList<DwldSSH>) tableDwld
					.getItems());

			lst.add(dwld);
			FXCollections.sort(lst);
			tableDwld.setItems(lst);
			if (lst.size() == 1) {

				saveCnx(event);

				// remplir la table tabUpld avec le contenu de
				// <workspace>/<project>/connections.properties
				System.out.println("Tab Dwld selected");
				File fProps = new File(Main.workspace + File.separator
						+ Main.currentProject + File.separator
						+ "connections.properties");
				Properties props = new Properties();
				if (fProps.exists()) {
					tableDwld.setEditable(true);
					try {
						props.load(new FileInputStream(fProps));
						// Charger la table View
						int i = 1;
						Callback<TableColumn<DwldSSH, String>, TableCell<DwldSSH, String>> cellFactory1 = new Callback<TableColumn<DwldSSH, String>, TableCell<DwldSSH, String>>() {

							public TableCell<DwldSSH, String> call(
									TableColumn<DwldSSH, String> p) {
								TextFieldTableCell<DwldSSH, String> txtField = new TextFieldTableCell<DwldSSH, String>(
										new DefaultStringConverter());

								return txtField;
							}
						};

						Callback<TableColumn<DwldSSH, String>, TableCell<DwldSSH, String>> cellFactoryCb = new Callback<TableColumn<DwldSSH, String>, TableCell<DwldSSH, String>>() {

							public TableCell<DwldSSH, String> call(
									TableColumn<DwldSSH, String> p) {
								ComboBoxCellDwld<DwldSSH, String> txtField = new ComboBoxCellDwld<DwldSSH, String>(
										actionDwldCol);
								// txtField.setConverter(new
								// DefaultStringConverter());
								return txtField;
							}

						};
						rankDwldCol.setCellFactory(cellFactory1);
						idServerDwldCol.setCellFactory(cellFactory1);
						remoteDirFilesDwldCol.setCellFactory(cellFactory1);
						localDirDwldCol.setCellFactory(cellFactory1);
						howmanyDwldCol.setCellFactory(cellFactory1);
						actionDwldCol.setCellFactory(cellFactoryCb);
						// password.setCellFactory(new
						// Callback<TableColumn<CnxSSH,
						// String>, TableCell<CnxSSH, String>>() {
						//
						// @Override
						// public TableCell<CnxSSH, String> call(
						// TableColumn<CnxSSH, String> cell) {
						// return new PasswordFieldCell(tableCnx);
						// }
						// });

						rankDwldCol
								.setCellValueFactory(new PropertyValueFactory<DwldSSH, String>(
										"rank"));
						idServerDwldCol
								.setCellValueFactory(new PropertyValueFactory<DwldSSH, String>(
										"idServer"));
						remoteDirFilesDwldCol
								.setCellValueFactory(new PropertyValueFactory<DwldSSH, String>(
										"remoteFiles"));
						localDirDwldCol
								.setCellValueFactory(new PropertyValueFactory<DwldSSH, String>(
										"localDirectory"));
						howmanyDwldCol
								.setCellValueFactory(new PropertyValueFactory<DwldSSH, String>(
										"howmany"));
						actionDwldCol
								.setCellValueFactory(new PropertyValueFactory<DwldSSH, String>(
										"action"));

						rankDwldCol
								.setOnEditCommit(new EventHandler<CellEditEvent<DwldSSH, String>>() {

									@Override
									public void handle(
											CellEditEvent<DwldSSH, String> t) {
										((DwldSSH) t
												.getTableView()
												.getItems()
												.get(t.getTablePosition()
														.getRow())).setRank(t
												.getNewValue());

									}
								});
						idServerDwldCol
								.setOnEditCommit(new EventHandler<CellEditEvent<DwldSSH, String>>() {

									@Override
									public void handle(
											CellEditEvent<DwldSSH, String> t) {
										((DwldSSH) t
												.getTableView()
												.getItems()
												.get(t.getTablePosition()
														.getRow()))
												.setIdServer(t.getNewValue());

									}
								});
						remoteDirFilesDwldCol
								.setOnEditCommit(new EventHandler<CellEditEvent<DwldSSH, String>>() {

									@Override
									public void handle(
											CellEditEvent<DwldSSH, String> t) {
										((DwldSSH) t
												.getTableView()
												.getItems()
												.get(t.getTablePosition()
														.getRow()))
												.setRemoteFiles(t.getNewValue());

									}
								});
						localDirDwldCol
								.setOnEditCommit(new EventHandler<CellEditEvent<DwldSSH, String>>() {

									@Override
									public void handle(
											CellEditEvent<DwldSSH, String> t) {
										((DwldSSH) t
												.getTableView()
												.getItems()
												.get(t.getTablePosition()
														.getRow()))
												.setLocalDirectory(t
														.getNewValue());

									}
								});

						howmanyDwldCol
								.setOnEditCommit(new EventHandler<CellEditEvent<DwldSSH, String>>() {

									@Override
									public void handle(
											CellEditEvent<DwldSSH, String> t) {
										((DwldSSH) t
												.getTableView()
												.getItems()
												.get(t.getTablePosition()
														.getRow()))
												.setHowmany(t.getNewValue());

									}
								});

						actionDwldCol
								.setOnEditCommit(new EventHandler<CellEditEvent<DwldSSH, String>>() {

									@Override
									public void handle(
											CellEditEvent<DwldSSH, String> t) {
										((DwldSSH) t
												.getTableView()
												.getItems()
												.get(t.getTablePosition()
														.getRow())).setAction(t
												.getNewValue());

									}
								});

						lst = ((ObservableList<DwldSSH>) tableDwld.getItems());
						lst.clear();

						while (true) {

							if (props.containsKey("download." + i + ".rank")) {
								System.out.println("chargement de "
										+ props.getProperty("download." + i
												+ ".rank"));
								// Creation d'une Cnx
								dwld = new DwldSSH(
										props.getProperty("download." + i
												+ ".rank"),
										props.getProperty("download." + i
												+ ".idServer"),
										props.getProperty("download." + i
												+ ".remoteFiles"),
										props.getProperty("download." + i
												+ ".localDirectory"),
										props.getProperty("download." + i
												+ ".howmany"),
										props.getProperty("download." + i
												+ ".action"));

								lst.add((DwldSSH) dwld);

								i++;
							} else
								break;
						}
						FXCollections.sort(lst);
						tableDwld.setItems(lst);

					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}

			}

			rankTfDwld.clear();
			idServerTfDwld.clear();
			remoteDirFilesTfDwld.clear();
			localDirTfDwld.clear();
			howmanyTfDwld.clear();
		}

	}

	@FXML
	public void addCnx(ActionEvent event) {

		if (null != idServerTf.getText() && idServerTf.getText().length() > 0
				&& null != addrSrvTf.getText()
				&& addrSrvTf.getText().length() > 0 && null != portTf.getText()
				&& portTf.getText().length() > 0 && null != loginTf.getText()
				&& loginTf.getText().length() > 0
				&& null != passwordTf.getText()
				&& passwordTf.getText().length() > 0) {
			String strRootPassword = "";
			if (loginTf.getText().equals("root")
					|| null == rootPasswordTf.getText()
					|| rootPasswordTf.getText().trim().length() == 0) {
				strRootPassword = passwordTf.getText();
			} else if (null != rootPasswordTf.getText()
					|| rootPasswordTf.getText().trim().length() > 0) {
				strRootPassword = rootPasswordTf.getText();
			}
			CnxSSH cnx = new CnxSSH(idServerTf.getText(), addrSrvTf.getText(),
					portTf.getText(), loginTf.getText(), passwordTf.getText(),
					strRootPassword);
			ObservableList<CnxSSH> lst = ((ObservableList<CnxSSH>) tableCnx
					.getItems());

			// Suppresdion d un eventuel IdServer Identique
			if (null != lst && lst.isEmpty()) {
				for (CnxSSH cnx1 : lst) {
					if (cnx1.getIdServer().equals(cnx.getIdServer())) {
						lst.remove(cnx1);
					}

				}
			}

			lst.add(cnx);
			FXCollections.sort(lst);
			tableCnx.setItems(lst);
			if (lst.size() == 1) {

				saveCnx(event);

				// remplir la table avec le contenu de
				// <workspace>/<project>/connections.properties
				System.out.println("Tab Cnx selected");
				File fProps = new File(Main.workspace + File.separator
						+ Main.currentProject + File.separator
						+ "connections.properties");
				Properties props = new Properties();
				if (fProps.exists()) {
					tableCnx.setEditable(true);
					try {
						props.load(new FileInputStream(fProps));
						// Charger la table View
						int i = 1;
						Callback<TableColumn<CnxSSH, String>, TableCell<CnxSSH, String>> cellFactory1 = new Callback<TableColumn<CnxSSH, String>, TableCell<CnxSSH, String>>() {

							public TableCell<CnxSSH, String> call(
									TableColumn<CnxSSH, String> p) {
								TextFieldTableCell<CnxSSH, String> txtField = new TextFieldTableCell<CnxSSH, String>(
										new DefaultStringConverter());

								return txtField;
							}
						};
						idServer.setCellFactory(cellFactory1);
						addrServer.setCellFactory(cellFactory1);
						port.setCellFactory(cellFactory1);
						login.setCellFactory(cellFactory1);

						Callback<TableColumn<CnxSSH, String>, TableCell<CnxSSH, String>> cellFactory2 = new Callback<TableColumn<CnxSSH, String>, TableCell<CnxSSH, String>>() {

							public TableCell<CnxSSH, String> call(
									TableColumn<CnxSSH, String> p) {
								PasswordFieldCell<CnxSSH, String> txtField = new PasswordFieldCell<CnxSSH, String>(
										password, "password");
								// txtField.setConverter(new
								// DefaultStringConverter());
								return txtField;
							}

						};
						password.setCellFactory(cellFactory2);

						Callback<TableColumn<CnxSSH, String>, TableCell<CnxSSH, String>> cellFactory2Bis = new Callback<TableColumn<CnxSSH, String>, TableCell<CnxSSH, String>>() {

							public TableCell<CnxSSH, String> call(
									TableColumn<CnxSSH, String> p) {
								PasswordFieldCell<CnxSSH, String> txtField = new PasswordFieldCell<CnxSSH, String>(
										rootpassword, "rootpassword");
								// txtField.setConverter(new
								// DefaultStringConverter());
								return txtField;
							}

						};
						rootpassword.setCellFactory(cellFactory2Bis);

						// password.setCellFactory(new
						// Callback<TableColumn<CnxSSH,
						// String>, TableCell<CnxSSH, String>>() {
						//
						// @Override
						// public TableCell<CnxSSH, String> call(
						// TableColumn<CnxSSH, String> cell) {
						// return new PasswordFieldCell(tableCnx);
						// }
						// });

						idServer.setCellValueFactory(new PropertyValueFactory<CnxSSH, String>(
								"idServer"));
						addrServer
								.setCellValueFactory(new PropertyValueFactory<CnxSSH, String>(
										"addrServer"));
						port.setCellValueFactory(new PropertyValueFactory<CnxSSH, String>(
								"port"));
						login.setCellValueFactory(new PropertyValueFactory<CnxSSH, String>(
								"login"));
						password.setCellValueFactory(new PropertyValueFactory<CnxSSH, String>(
								"password"));
						rootpassword
								.setCellValueFactory(new PropertyValueFactory<CnxSSH, String>(
										"rootpassword"));
						idServer.setOnEditCommit(new EventHandler<CellEditEvent<CnxSSH, String>>() {

							@Override
							public void handle(CellEditEvent<CnxSSH, String> t) {
								((CnxSSH) t.getTableView().getItems()
										.get(t.getTablePosition().getRow()))
										.setIdServer(t.getNewValue());

							}
						});
						login.setOnEditCommit(new EventHandler<CellEditEvent<CnxSSH, String>>() {

							@Override
							public void handle(CellEditEvent<CnxSSH, String> t) {
								((CnxSSH) t.getTableView().getItems()
										.get(t.getTablePosition().getRow()))
										.setLogin(t.getNewValue());

							}
						});
						addrServer
								.setOnEditCommit(new EventHandler<CellEditEvent<CnxSSH, String>>() {

									@Override
									public void handle(
											CellEditEvent<CnxSSH, String> t) {
										((CnxSSH) t
												.getTableView()
												.getItems()
												.get(t.getTablePosition()
														.getRow()))
												.setAddrServer(t.getNewValue());

									}
								});
						port.setOnEditCommit(new EventHandler<CellEditEvent<CnxSSH, String>>() {

							@Override
							public void handle(CellEditEvent<CnxSSH, String> t) {
								((CnxSSH) t.getTableView().getItems()
										.get(t.getTablePosition().getRow()))
										.setPort(t.getNewValue());

							}
						});

						// pour password logique de mise a jour dans la classe
						// PasswordFieldCell

						lst = ((ObservableList<CnxSSH>) tableCnx.getItems());
						lst.clear();
						while (true) {

							if (props.containsKey("server." + i + ".IdServer")) {
								System.out.println("chargement de "
										+ props.getProperty("server." + i
												+ ".IdServer"));
								// Creation d'une Cnx
								cnx = new CnxSSH(
										props.getProperty("server." + i
												+ ".IdServer"),
										props.getProperty("server." + i
												+ ".AddrServer"),
										props.getProperty("server." + i
												+ ".Port"),
										props.getProperty("server." + i
												+ ".Login"),
										props.getProperty("server." + i
												+ ".Password"),
										props.getProperty(
												"server." + i + ".RootPassword",
												props.getProperty("server." + i
														+ ".Password")));

								lst.add((CnxSSH) cnx);

								i++;
							} else
								break;
						}
						FXCollections.sort(lst);
						tableCnx.setItems(lst);

					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}

			}

			idServerTf.clear();
			addrSrvTf.clear();
			portTf.setText("22");
			loginTf.clear();
			passwordTf.clear();
			rootPasswordTf.clear();
		}

	}

	@FXML
	public void saveCnx(ActionEvent event) {

		System.out.println("click on Button saveCnx");
		ObservableList<CnxSSH> lst = ((ObservableList<CnxSSH>) tableCnx
				.getItems());
		File cnxF = new File(Main.workspace + File.separator + Main.currentProject
				+ File.separator + "connections.properties");
		if (cnxF.exists())
			cnxF.delete();
		try {
			RandomAccessFile raf = new RandomAccessFile(cnxF, "rw");
			raf.writeBytes("### Servers  beginning to 1" + "\n\n");
			int i = 1;

			for (CnxSSH cnx : lst) {
				raf.writeBytes("server." + i + ".IdServer=" + cnx.getIdServer()
						+ "\n");
				raf.writeBytes("server." + i + ".AddrServer="
						+ cnx.getAddrServer() + "\n");
				raf.writeBytes("server." + i + ".Port=" + cnx.getPort() + "\n");
				raf.writeBytes("server." + i + ".Login=" + cnx.getLogin()
						+ "\n");
				raf.writeBytes("server." + i + ".Password=" + cnx.getPassword()
						+ "\n");
				raf.writeBytes("server." + i + ".RootPassword="
						+ cnx.getRootpassword() + "\n\n");
				i++;
			}
			// sauvetage des Uploads
			ObservableList<UpldSSH> lst2 = ((ObservableList<UpldSSH>) tableUpld
					.getItems());

			i = 1;

			for (UpldSSH upld : lst2) {
				raf.writeBytes("upload." + i + ".rank=" + upld.getRank() + "\n");
				raf.writeBytes("upload." + i + ".idServer="
						+ upld.getIdServer() + "\n");
				raf.writeBytes("upload." + i + ".localFile="
						+ upld.getLocalFile() + "\n");
				raf.writeBytes("upload." + i + ".remoteDir="
						+ upld.getRemoteDirectory() + "\n");
				raf.writeBytes("upload." + i + ".execute=" + upld.getExecute()
						+ "\n\n");
				i++;
			}
			// sauvetage des Downloads
			ObservableList<DwldSSH> lst3 = ((ObservableList<DwldSSH>) tableDwld
					.getItems());

			i = 1;

			for (DwldSSH dwld : lst3) {
				raf.writeBytes("download." + i + ".rank=" + dwld.getRank()
						+ "\n");
				raf.writeBytes("download." + i + ".idServer="
						+ dwld.getIdServer() + "\n");
				raf.writeBytes("download." + i + ".localDirectory="
						+ dwld.getLocalDirectory() + "\n");
				raf.writeBytes("download." + i + ".remoteFiles="
						+ dwld.getRemoteFiles() + "\n");
				raf.writeBytes("download." + i + ".howmany="
						+ dwld.getHowmany() + "\n");
				raf.writeBytes("download." + i + ".action=" + dwld.getAction()
						+ "\n\n");
				i++;
			}
			raf.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		File fCnx = new File(Main.workspace + File.separator
				+ Main.currentProject + File.separator
				+ "connections.properties");
		if (fCnx.exists()) {
			FileInputStream fis2 = null;
			try {
				fis2 = new FileInputStream(fCnx);
				Main.propsCnx.clear();
				Main.allServers.clear();
				Main.propsCnx.load(fis2);
				fis2.close();
				int i = 1;
				boolean cont = true;
				while (cont) {
					if (Main.propsCnx.containsKey("server." + i + ".IdServer")) {
						Main.allServers.add(Main.propsCnx.getProperty("server." + i
								+ ".IdServer"));
						i++;
					} else
						cont = false;
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	@SuppressWarnings("unchecked")
	@FXML
	public void deleteCnx(MouseEvent evt) {
		if (evt.getButton() == MouseButton.SECONDARY) {
			if (tabCnx.isSelected()) {
				System.out.println("MouseEvent on TreeView :"
						+ ((TableView<CnxSSH>) evt.getSource())
								.getSelectionModel().getSelectedIndex());
				CnxSSH cnx = ((TableView<CnxSSH>) evt.getSource())
						.getSelectionModel().getSelectedItem();
				ObservableList<CnxSSH> lst = ((ObservableList<CnxSSH>) tableCnx
						.getItems());
				lst.remove(cnx);
			}

			else if (tabUpld.isSelected()) {
				System.out.println("MouseEvent on TreeView :"
						+ ((TableView<UpldSSH>) evt.getSource())
								.getSelectionModel().getSelectedIndex());
				UpldSSH upld = ((TableView<UpldSSH>) evt.getSource())
						.getSelectionModel().getSelectedItem();
				ObservableList<UpldSSH> lst = ((ObservableList<UpldSSH>) tableUpld
						.getItems());
				lst.remove(upld);
			} else if (tabDwld.isSelected()) {
				System.out.println("MouseEvent on TreeView :"
						+ ((TableView<DwldSSH>) evt.getSource())
								.getSelectionModel().getSelectedIndex());
				DwldSSH dwld = ((TableView<DwldSSH>) evt.getSource())
						.getSelectionModel().getSelectedItem();
				ObservableList<DwldSSH> lst = ((ObservableList<DwldSSH>) tableDwld
						.getItems());
				lst.remove(dwld);
			}

		}
	}

	@FXML
	public void cancel(ActionEvent event) {
		System.exit(0);
	}

	@FXML
	public void upload(ActionEvent event) {
		Utils.saveConfiguration(false);
		System.out.println("uploading agents in series");
		// On va recuperer les serveurs dans le fichier .properties.

		boolean cont = true;
		int i = 1;
		int defaultTimeOut = Integer.parseInt(Main.packager.getProperty(
				"ssh.connection.timeout", "1000"));
		while (cont) {
			if (Main.propsCnx.containsKey("server." + i + ".IdServer")) {
				// Creer une connexion de type MySSHConnectio
				MySSHConnection myCnx = new MySSHConnection(
						Main.propsCnx.getProperty("server." + i + ".AddrServer"),
						Main.propsCnx.getProperty("server." + i + ".Port"),
						Main.propsCnx.getProperty("server." + i + ".Login"),
						Main.propsCnx.getProperty("server." + i + ".Password"),
						Main.propsCnx.getProperty("server." + i + ".RootPassword"));

				HandlerSSH handler = new HandlerSSH(myCnx, defaultTimeOut);
				if (null == handler.sess) {
					MessageBox
							.show(Main.currentStage,
									"Error Connecting to : "
											+ myCnx.getHost()
											+ " with user => "
											+ myCnx.getUser()
											+ "\n Verify the tab \"Networking cfg\"/\"Connections\"",
									"Error Connection", MessageBox.ICON_ERROR);
					return;

				}
				String localFile = Main.workspace + File.separator
						+ Main.currentProject + File.separator
						+ "mybyteman.jar";
				String remoteFile = Main.propsConfRules
						.getProperty("bytemanpkg.dirWork") + "mybyteman.jar";

				handler.upload(localFile, remoteFile);
				String command = "chmod 777 " + remoteFile;
				handler.executeCommand(command);
				// traiter aussi l attach API JMX pour gerer les
				// ouvertures/fermeture/flush de flux activation/desactivation
				// DO statement des rules
				String myPath = Main.workspace + File.separator
						+ Main.currentProject;
				File f2 = new File(Main.workspace + File.separator
						+ Main.currentProject + File.separator
						+ "attachMBean.sh");
				if (f2.exists())
					f2.delete();
				Utils.copy(new File(Main.root + File.separator + "scripts"
						+ File.separator + "attachMBean.sh"), f2);
				try {
					RandomAccessFile raf = new RandomAccessFile(f2, "rw");
					long ln = f2.length();
					byte[] tabBytes = new byte[(int) ln];

					tabBytes = new byte[(int) ln];
					raf.read(tabBytes);
					String str = new String(tabBytes);

					// trouver le PID
					String pidStr = cmdPID.getText();
					if (pidStr.contains("<aregexp>")) {
						MessageBox
								.show(Main.currentStage,
										"You need first to fill correctly the textField \"Cmd Detect PID\" in \"Remote Actions\" Tab  :\n Replace at least the pattern <aregexp> by a correct statement "
												+ "\n or fill the textField with a full custom command",
										"Error filling Detect PID",
										MessageBox.ICON_ERROR);
						return;

					}
					String PID = "";
					if (pidStr.matches("PID=")) {
						PID = pidStr.split("=")[1];
					} else {
						PID = "`" + pidStr + "`";
					}
					str = str.replace("<bytemanpkg.dirWork>",
							Main.propsConfRules.getProperty(
									"bytemanpkg.dirWork", "/tmp/"));
					str = str.replace("<PID>", PID);

					raf.getChannel().truncate(0);
					raf.writeBytes(str);
					raf.close();

				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				localFile = myPath + File.separator + "attachMBean.sh";
				remoteFile = Main.propsConfRules
						.getProperty("bytemanpkg.dirWork") + "attachMBean.sh";
				handler.upload(localFile, remoteFile);
				command = "chmod 777 " + remoteFile;
				handler.executeCommand(command);
				localFile = myPath + File.separator + "findUserAndPidAndJVM.sh";
				remoteFile = Main.propsConfRules
						.getProperty("bytemanpkg.dirWork")
						+ "findUserAndPidAndJVM.sh";
				handler.upload(localFile, remoteFile);
				command = "chmod 777 " + remoteFile;
				handler.executeCommand(command);

				i++;
			} else {
				cont = false;
			}

		}

		MessageBox
				.show(Main.currentStage,
						"All uploads are over.\nLog to remote servers to see if they have correcly ended",
						"Upload status", MessageBox.ICON_INFORMATION);

	}

	@FXML
	public void saveLocal(ActionEvent event) {
		Utils.saveConfiguration(false);
		File localSaves = new File(Main.workspace + File.separator + Main.currentProject
				+ File.separator + "saves");
		if (!localSaves.exists()) {
			localSaves.mkdir();
		}
		
		System.out.println("You clicked saveLocal!");
		
		Main.stgDiagSaveLocal = new Stage();
		Main.stgDiagSaveLocal.setTitle("Saving A Local Configuration");

		Main.stgDiagSaveLocal.setScene(new Scene((Parent) Main.fxmlsaveLocalDialog.getRoot()));
		Main.stgDiagSaveLocal.initModality(Modality.APPLICATION_MODAL);

		Main.stgDiagSaveLocal.show();

	}

	@FXML
	public void initializeCBSave() {
		File dirWk = new File(Main.workspace + File.separator + Main.currentProject
				+ File.separator + "saves");
		File[] lst = dirWk.listFiles(new FileFilter() {
			public boolean accept(File pathname) {
				return pathname.getName().endsWith(".properties");
			}
		});
		// if(null == projectsCB)System.out.println("projectsCB not reachable");
		// else System.out.println("projectsCB is reachable");
		ObservableList<String> items = confCB.getItems();
		items.clear();
		for (File file : lst) {
			items.add((String) file.getName());
		}
		FXCollections.sort(items);
		confCB.setItems(items);

	}

	@FXML
	public void fillSelectedConf(Event event) {
		String item = confCB.getSelectionModel().getSelectedItem();

		if (null != item) {
			System.out.println("item=" + item);
			newConfTf.setEditable(true);
			newConfTf.setText(item);

		}
	}

	@FXML
	public void cancelSaveLocal(ActionEvent Event) {
		Main.stgDiagSaveLocal.getScene().getWindow().hide();
	}

	@FXML
	public void saveLocConfig(ActionEvent event) {

		String newConf = newConfTf.getText();
		if (!newConf.endsWith(".properties")) {
			newConf += ".properties";
		}
		System.out.println("newConf=" + newConfTf.getText());
		ObservableList<String> items = confCB.getItems();
		FXCollections.sort(items);
		confCB.setItems(items);
		File to = new File(Main.workspace + File.separator +Main. currentProject
				+ File.separator + "saves" + File.separator + newConf);
		if (to.exists())
			to.delete();
		File from = new File(Main.workspace + File.separator +Main. currentProject
				+ File.separator + "bytemanpkg.properties");
		Utils.copy(from, to);
		fillOthersConfigurations();
		Main.stgDiagSaveLocal.getScene().getWindow().hide();
	}

	@FXML
	public void saveConfigurations(ActionEvent event) {
		Utils.saveConfiguration(false);
		checkRules();
	}

	private void checkRules() {
		String workDir = Main.workspace + File.separator + Main.currentProject
				+ File.separator;
		String file = workDir + "bytemanpkg.btm";
		String result = MyAntlrParser.result(file);
		System.out.println("ChekRule result : \n" + result);

		Main.packRoot.getTaCheck().setText(result);

	}

	 class ColorTextCell extends ListCell<String> {

		@Override
		public void updateItem(String item, boolean empty) {
			super.updateItem(item, empty);
			Label lab = new Label();
			lab.setText(item);
			lab.setFont(Font.font("Verdana", 14));

			{

				if (item != null) {
					if (!Main.hmRules.containsKey(item)) {
						Main.hmRules.put(item, false);
						lab.setTextFill(Color.RED);

					}
					if (Main.hmRules.get(item)) {
						if (item.equals(listView2.getFocusModel()
								.getFocusedItem())) {
							lab.setTextFill(Color.BLACK);
						}

					} else {

						lab.setTextFill(Color.RED);

					}
				}
				setGraphic(lab);
				this.updateListView(listView2);

			}

		}

		@Override
		public void updateSelected(boolean selected) {
			if (selected) {
				this.setTextFill(Color.WHITE);
			} else {
				this.setTextFill(Color.BLACK);
			}

		}
	}

	 class ColorTextCell2 extends ListCell<String> {

		@Override
		public void updateItem(String item, boolean empty) {
			super.updateItem(item, empty);
			Label lab = new Label();
			lab.setText(item);
			lab.setFont(Font.font("Verdana", 14));

			setGraphic(lab);
			this.updateListView(listView1);

		}
	}

	@FXML
	public void help(ActionEvent event) {
		String help = Main.packager.getProperty("version")
				+ ". Author : Jean-Louis PASTUREL. License : Apache License, Version 2.0\n";
		help += "\n ##############################################\n"
				+ Main.packager.getProperty("lastmodification")
				+ "\n###############################################\n";
		MessageBox.show(Main.currentStage, help,
				"BytemanPkg version : " + Main.packager.getProperty("version"),
				MessageBox.ICON_INFORMATION);

	}

	@FXML
	public void uploadsOnly(ActionEvent event) {
		new PoolUploads();
	}

	@FXML
	public void downloadsOnly(ActionEvent event) {
		new PoolDownloads();
	}

	@FXML
	public void chainingCmds(ActionEvent event) {
		// Construction structure pour rank>=1

		Main.chainCmds.clear();
		Pattern pat = Pattern.compile("(download|upload)\\.\\d+\\.rank");
		Set<Object> keys = Main.propsCnx.keySet();
		for (Object key : keys) {
			Matcher match = pat.matcher((String) key);
			if (match.find()) {
				if (Integer.parseInt(Main.propsCnx.getProperty((String) key)) > 0) {
					String rang = Main.propsCnx.getProperty((String) key);
					switch (rang.length()) {
					case 1:
						rang = "00" + rang;
						break;
					case 2:
						rang = "0" + rang;
						break;
					default:
						break;
					}
					Main.chainCmds.add(rang + "_" + (String) key);
				}
			}
		}

		Collections.sort(Main.chainCmds);
		for (String cmd : Main.chainCmds) {
			System.out.println(cmd);
			if (cmd.contains("upload")) {
				new PoolUploads(cmd.split("_")[1]);
			} else if (cmd.contains("download")) {
				new PoolDownloads(cmd.split("_")[1]);
			}
		}
	}

	@FXML
	public void installByteman(ActionEvent event) {
		Utils.installByteman();
	}

	@FXML
	public void submitByteman(ActionEvent event) {
		Utils.submitByteman();
	}

	@FXML
	public void unsubmitByteman(ActionEvent event) {
		Utils.unsubmitByteman();
	}

	@FXML
	public void clearConsole(ActionEvent event) {
		cmdConsole.setText("");
	}

	@FXML
	public void flushStreams(ActionEvent event) {
		Utils.flushStreams();
	}

	@FXML
	public void reopenStreams(ActionEvent event) {
		Utils.reopenStreams();
	}

	@FXML
	public void activateDeActivateRules(ActionEvent event) {
		Utils.activateDeActivateRules(event);
	}

	@FXML
	public void viewStaticCsv(ActionEvent event) {
		Utils.viewStaticCsv();
	}

	@FXML
	public void viewDynCsv(ActionEvent event) {
		Utils.viewDynCsv();
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		System.out.println("initialisation URL arg0="+arg0);
		
		
		
	}
}
