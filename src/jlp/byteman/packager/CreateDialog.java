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

public class CreateDialog extends AnchorPane implements Initializable {

	
	
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

	public  void setBytemanOptsTf(TextField bytemanOptsTf) {
		bytemanOptsTf = bytemanOptsTf;
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
	private  MenuButton othersConfBut;

	@FXML
	private  Button saveLocalbut;
	public Button getSaveLocalbut() {
		return saveLocalbut;
	}

	public void setSaveLocalbut(Button saveLocalbut) {
		saveLocalbut = saveLocalbut;
	}

	@FXML
	private  Button bCancel;
	public Button getbCancel() {
		return bCancel;
	}

	public void setbCancel(Button bCancel) {
		bCancel = bCancel;
	}

	@FXML
	private  Button bSave;
	public Button getbSave() {
		return bSave;
	}

	public void setbSave(Button bSave) {
		bSave = bSave;
	}

	@FXML
	private  Button bUpload;

	public Button getbUpload() {
		return bUpload;
	}

	public void setbUpload(Button bUpload) {
		bUpload = bUpload;
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

	public void setExecuteCbUpld(ComboBox<String> executeCbUpld) {
		executeCbUpld = executeCbUpld;
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

	public void setActionCbDwld(ComboBox<String> actionCbDwld) {
		actionCbDwld = actionCbDwld;
	}

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
	public  Tab getTabRemoteAction() {
		return tabRemoteAction;
	}
	@FXML
	public  void setTabRemoteAction(Tab tabRemoteAction) {
		tabRemoteAction = tabRemoteAction;
	}
	@FXML
	public  TextArea getTaGenRules() {
		return taGenRules;
	}
	@FXML
	public  void setTaGenRules(TextArea taGenRules) {
		taGenRules = taGenRules;
	}

	public  Tab getTabNet() {
		return tabNet;
	}
	@FXML
	public  void setTabNet(Tab tabNet) {
		tabNet = tabNet;
	}
	@FXML
	public  Tab getTabUpld() {
		return tabUpld;
	}
	@FXML
	public  void setTabUpld(Tab tabUpld) {
		tabUpld = tabUpld;
	}
	@FXML
	public  TableView<UpldSSH> getTableUpld() {
		return tableUpld;
	}
	@FXML
	public  void setTableUpld(TableView<UpldSSH> tableUpld) {
		tableUpld = tableUpld;
	}
	@FXML
	public  TableView<DwldSSH> getTableDwld() {
		return tableDwld;
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

	public void setStart(AnchorPane start) {
		start = start;
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

	public  void setGzip(CheckBox gzip) {
		gzip = gzip;
	}

	@FXML
	private  CheckBox fullPackage;
	@FXML
	public  CheckBox getFullPackage() {
		return fullPackage;
	}

	public  void setFullPackage(CheckBox fullPackage) {
		fullPackage = fullPackage;
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

	public void setListView1(ListView<String> listView1) {
		listView1 = listView1;
	}
	@FXML
	public ListView<String> getListView2() {
		return listView2;
	}

	public void setListView2(ListView<String> listView2) {
		listView2 = listView2;
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

	

	@FXML
	public void cancelCreateDiag(ActionEvent event) {
		createDiag.getScene().getWindow().hide();
	}

	@FXML
	public void createNewProject(ActionEvent event) {
		System.out.println("You clicked CreateNewProject!");

		String newProject = newProjectTf.getText();
		System.out.println("newProject=" + newProjectTf.getText());
		ObservableList<Label> items = projectsCB.getItems();
		Comparator<Label> labComp = new Comparator<Label>() {

			@Override
			public int compare(Label o1, Label o2) {
				// TODO Auto-generated method stub
				return o1.getText().compareTo(o2.getText());
			}

		};

		FXCollections.sort(items, labComp);
		projectsCB.setItems(items);
		if (items.contains(newProject)) {
			System.out.println("The project : " + newProject
					+ " already exists");
			newProjectTf.clear();
			newProjectTf.setPromptText("The project : " + newProject
					+ " already exists");
		} else {
			new File(Main.workspace + File.separator + newProject).mkdir();

			createDiag.getScene().getWindow().hide();
			Main.currentProject = newProject;

			Main.currentStage
					.setTitle("Byteman Packager => Current project : "
							+ newProject);
			
            if (null == ((Parent) Main.fxmlLoader.getRoot()).getScene())
            {
			Main.currentStage.setScene(new Scene((Parent) Main.fxmlLoader.getRoot()));
            }
            else
            {
            	Main.currentStage.setScene(((Parent) Main.fxmlLoader.getRoot()).getScene());
            }
			
			
			Main.packRoot.getWork().setVisible(true);
			Main.packRoot.getListView2()
					.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
						@Override
						public ListCell<String> call(ListView<String> list) {
							return new ColorTextCell();
						}
					});
			// listView1
			// .setCellFactory(new Callback<ListView<String>,
			// ListCell<String>>() {
			// @Override
			// public ListCell<String> call(ListView<String> list) {
			// return new ColorTextCell();
			// }
			// });
			Main.currentStage.getScene().getStylesheets()
					.add("jlp/byteman/packager/packager.css");
			// miCreateProject.setId("my-menu-bar");
			// miOpenProject.setId("my-menu-bar");
			// miCloseProject.setId("my-menu-bar");
			// miExit.setId("my-menu-bar");
			Main.packRoot.getbSave().setDisable(false);
			Main.packRoot.getbUpload().setDisable(false);
			Main.packRoot.getSaveLocalbut().setDisable(false);
			File dirLogs = new File(Main.workspace + File.separator +Main. currentProject
					+ File.separator + "logs");
			if (!dirLogs.exists())
				dirLogs.mkdir();
			File dirSave = new File(Main.workspace + File.separator + Main. currentProject
					+ File.separator + "saves");
			if (!dirSave.exists())
				dirSave.mkdir();
			File dirSys = new File(Main.workspace + File.separator + Main.currentProject
					+ File.separator + "sys");
			if (!dirSys.exists())
				dirSys.mkdir();

			File dirBoot = new File(Main.workspace + File.separator +Main. currentProject
					+ File.separator + "boot");
			if (!dirBoot.exists())
				dirBoot.mkdir();
			
			// Copy Helper Class on sys directory
//			File helperSrc=new File(Main.root + File.separator+"lib"+File.separator+"helperPkg.jar");
//			File helperDest=new File(Main.workspace + File.separator + Main.currentProject
//					+ File.separator + "sys"+File.separator+"helperPkg.jar");
//			Utils.copy(helperSrc,helperDest);
			Main.currentStage.show();
			fillOthersConfigurations();
		}
		Main.packRoot.getCmdConsole().textProperty().addListener(new ChangeListener() {
			public void changed(ObservableValue ov, Object oldValue,
					Object newValue) {
				// cmdConsole.setScrollTop(Double.MAX_VALUE); //top
				Main.packRoot.getCmdConsole().setScrollTop(Double.MIN_VALUE); // down
			}

		});
	}

	

	@FXML
	public void fillSelectedProject(Event event) {
		System.out.println("CB2 box event detected");
		if (null != projectsCB2.getSelectionModel().getSelectedItem()) {
			String item = ((Label) projectsCB2.getSelectionModel()
					.getSelectedItem()).getText();

			if (null != item) {
				System.out.println("item=" + item);
				openProjectTf.setEditable(true);
				openProjectTf.setText(item);
				openProjectTf.setEditable(false);
			}
		}
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
		ObservableList<MenuItem> lstMi = Main.packRoot.getOthersConfBut().getItems();
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

	

	private ComboBox<String> getActionCbDwld() {
		// TODO Auto-generated method stub
		return actionCbDwld;
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

	
	

	
	
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		System.out.println("initialisation URL arg0="+arg0);
		
		
		
	}
}
