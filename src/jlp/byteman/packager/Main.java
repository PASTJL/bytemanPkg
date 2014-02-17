package jlp.byteman.packager;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class Main extends Application {
	public static String workspace = ".";
	public static String root = ".";
	public static int counterRules = 1;
	public static String currentProject = "";
	public static Properties bytemanRules = new Properties();
	public static List<String> lstRules = new ArrayList<String>();
	public static Properties packager = new Properties();
	public static RandomAccessFile rafBtm = null;
	public static Stage stgDiagSaveLocal=null;
	public static Properties propsCnx = new Properties();
	public static List<String> allServers = new ArrayList<String>();
	public static List<String> chainCmds = new ArrayList<String>();
	public static HashMap<String, Boolean> hmRules = new HashMap<String, Boolean>();
	public static FXMLLoader fxmlLoader = null;
	public static Properties propsConfRules = new Properties();
	public static FXMLLoader fxmlCreateDialog = null;
	public static FXMLLoader fxmlOpenDialog = null;
	public static FXMLLoader fxmlsaveLocalDialog = null;
	public static Packager packRoot = null;
	public static CreateDialog packCreate = null;
	public static OpenDialog packOpen = null;
	public static SaveLocalDialog packSaveLocal = null;
	public static Stage currentStage;

	public static void main(String[] args) {
		workspace = System.getProperty("workspace");
		root = System.getProperty("root");
		launch(args);

	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("start in main");
		currentStage = primaryStage;
		System.out.println("Start creating Packager");
		InputStream in = ClassLoader
				.getSystemResourceAsStream("bytemanRules.properties");
		if (null != in) {
			try {
				bytemanRules.load(in);
				in.close();
				String[] rules = bytemanRules.getProperty("listRules", "")
						.split(";");
				for (String rule : rules) {
					lstRules.add(rule);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			System.out
					.println("bytemanRules not reached. Add it to the classpath");
		}

		InputStream in2 = ClassLoader
				.getSystemResourceAsStream("packager.properties");
		if (null != in2) {
			try {
				packager.load(in2);
				in2.close();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			System.out
					.println("packager.properties not reached. Add it to the classpath");
		}

		String jvmVersion = System.getProperty("java.version");

		URL location = getClass().getResource("root.fxml");
		if (null == location)
			System.out.println("location is null");
		fxmlLoader = new FXMLLoader(location);
		URL locationDiag = getClass().getResource("createDialog.fxml");
		fxmlCreateDialog = new FXMLLoader(locationDiag);
		URL locationOpenDiag = getClass().getResource("openDialog.fxml");
		fxmlOpenDialog = new FXMLLoader(locationOpenDiag);
		URL locationDiagSaveLocal = getClass()
				.getResource("diagSaveLocal.fxml");
		fxmlsaveLocalDialog = new FXMLLoader(locationDiagSaveLocal);
		// fxmlLoader.setRoot(this);
		// fxmlLoader.setController(this);
		System.out.println("avant load");
		AnchorPane start = null;
		System.out.println("jvmVersion => " + jvmVersion);
		try {

			if (jvmVersion.startsWith("1.8")) {
				System.out.println("treating JVM 1.8");
				packRoot = new Packager();
				packCreate=new CreateDialog();
				packOpen=new OpenDialog();
				packSaveLocal=new SaveLocalDialog();
				fxmlLoader.setRoot(packRoot);
				start = (AnchorPane) fxmlLoader.load();
				//fxmlCreateDialog.setRoot(packCreate);
				fxmlCreateDialog.load();
				packCreate=fxmlCreateDialog.getController();
				
				//fxmlOpenDialog.setRoot(packOpen);
				fxmlOpenDialog.load();
				packOpen=fxmlOpenDialog.getController();
				

				//fxmlsaveLocalDialog.setRoot(packSaveLocal);
				fxmlsaveLocalDialog.load();
				packSaveLocal=fxmlsaveLocalDialog.getController();
				
				
				packRoot = fxmlLoader.getController();
				if (packRoot == null)
					System.out
							.println("packRoot is null while treating JVM 1.8");
				// fxmlDialog.load();
			} else {
				// start=(Packager) fxmlLoader.load();
				start = (AnchorPane) fxmlLoader.load();
				System.out.println("init1");
				packRoot = fxmlLoader.getController();

				fxmlCreateDialog.load();
				packCreate = fxmlCreateDialog.getController();
				System.out.println("init2");
				fxmlOpenDialog.load();
				packOpen = fxmlOpenDialog.getController();
				System.out.println("init3");
				fxmlsaveLocalDialog.load();
				packSaveLocal = fxmlsaveLocalDialog.getController();
				System.out.println("init4");

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("message" + e.getMessage());
			e.printStackTrace();
		}

		primaryStage.setTitle("Byteman Packager : no opened Project");
		primaryStage.setScene(new Scene((Parent) fxmlLoader.getRoot()));

		primaryStage.getScene().getStylesheets()
				.add("jlp/byteman/packager/packager.css");
		// mFile.getStyleClass().add("my-menu");
		// mHelp.getStyleClass().add("my-menu");
		// mEdit.getStyleClass().add("my-menu");

		// test

		primaryStage.show();
		if (null == ((Packager) fxmlLoader.getController()).getWork()) {
			System.out.println("work is null");
		} else {
			System.out.println("work =>"
					+ ((Packager) fxmlLoader.getController()).getWork()
							.toString());
			((Packager) fxmlLoader.getController()).getWork().setVisible(false);
		}

		System.out.println("End creating Packager");

	}

}
