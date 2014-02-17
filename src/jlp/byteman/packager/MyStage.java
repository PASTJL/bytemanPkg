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

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import jfx.messagebox.MessageBox;

public class MyStage {

	// Method pattern opt[returnType]
	// opt[package1.package2..].[Class].method.opt[(Typeparam1,TypeParam2,..)]
	// Method example : String
	// package1.package2.package3.MyClass.myMethod(String,Integer)
	// private Pattern patMethods = Pattern
	// .compile("^(\\w+\\s+)?(\\^?\\w+\\.)+\\w+(\\s*\\((\\s*\\w+\\s*,?)*\\))?$");
	private Pattern patMethods = Pattern.compile(Main.packager
			.getProperty("patMethods"));
	private Properties localProps = new Properties();
	public String rule;
	private List<TextArea> lstTa = new ArrayList<TextArea>();
	private List<TextField> lstTf = new ArrayList<TextField>();
	private Dimension dimStage;
	// ButSave global to size the dynamic stage
	private Button butSave = new Button("Save");

	// stgDiag global to hide dialog
	private Stage stgDiag = new Stage();
	private ScrollPane scrl = new ScrollPane();
	private Scene sc = null;
	private String focusedItem;

	public MyStage(String focusedItem) {
		this.focusedItem = focusedItem;
		rule = focusedItem.split("_alias")[0];
		Dimension dimTmp = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		dimStage = new Dimension((int) dimTmp.getWidth() * 3 / 4,
				(int) dimTmp.getHeight() * 4 / 5);

		stgDiag.setTitle("Byteman Packager => setting rule " + focusedItem);

		stgDiag.initModality(Modality.APPLICATION_MODAL);

		// stgDiag.setScene(new Scene(rootfxml));
		fillLocalProps();
		sc = createNewScene();
		stgDiag.setScene(sc);

		stgDiag.show();

		stgDiag.setHeight(Math.min(localToPrimaryScreen(stgDiag, butSave, 0, 0)
				.getY() + 80, Screen.getPrimary().getBounds().getMaxY() - 30));
		scrl.setPrefSize(stgDiag.getWidth(), stgDiag.getHeight());

	}

	private void fillLocalProps() {
		Set<Entry<Object, Object>> set = Main.bytemanRules.entrySet();
		for (Entry<Object, Object> entry : set) {
			if (((String) entry.getKey()).contains(rule + ".")) {
				localProps.put((String) entry.getKey(),
						(String) entry.getValue());
			}
		}
	}

	private Scene createNewScene() {

		scrl.setFitToHeight(true);
		scrl.setFitToWidth(true);

		Scene sc = new Scene(scrl, dimStage.getWidth(), 900, Color.WHITE);
		sc.getStylesheets().add("jlp/byteman/packager/packager.css");

		//
		GridPane gridpane = new GridPane();

		gridpane.setPrefWidth(sc.getWidth());
		Label lab = new Label("Comments");
		lab.setId("my-labels");
		TextArea tarea = new TextArea();

		// tarea.setPrefHeight(245);
		tarea.setMinHeight(70);
		tarea.setText(localProps.getProperty(rule + ".comment",
				"no comment found"));
		tarea.setEditable(false);
		tarea.setId("my-text-area");
		tarea.setPrefRowCount(10);
		tarea.setWrapText(false);
		ColumnConstraints column1 = new ColumnConstraints();
		column1.setPercentWidth(20);
		column1.setHalignment(HPos.CENTER);
		ColumnConstraints column2 = new ColumnConstraints();
		column2.setPercentWidth(80);
		column2.setHalignment(HPos.RIGHT);
		GridPane.setMargin(tarea, new Insets(5, 0, 5, 0));
		gridpane.getColumnConstraints().addAll(column1, column2);
		gridpane.add(lab, 0, 0);
		gridpane.add(tarea, 1, 0);

		// Ajout Dynamique des paramettres
		int i = 1;
		boolean bool = true;
		while (bool) {
			if (localProps.containsKey(rule + ".param" + i)) {
				String valueType = localProps.getProperty(rule + ".param" + i);
				String name1 = valueType.split(";")[0];
				Label lab2 = new Label(name1);
				lab2.setId("my-labels");
				switch (valueType.split(";")[1]) {

				case "TextArea":
				{
					TextArea tareabis = new TextArea();
					// traitement des cas ou nam1e1=bindStatement et
					// name1=doStatement

					switch (name1) {

					case "bindStatement":
						// gestion des lignes
						String bindStatement = "";
						int j = 0;
						boolean cont = true;
						while (cont) {
							if (Main.propsConfRules.containsKey(focusedItem
									+ "." + name1 + ".line" + j)) {
								bindStatement += Main.propsConfRules
										.getProperty(focusedItem + "." + name1
												+ ".line" + j)+"\n";
							} else {
								cont = false;
							}
							j++;
						}
						tareabis.setText(bindStatement);

						break;
					case "doStatement":
						// gestion des lignes
						String doStatement = "";
						int jj = 0;
						boolean cont2 = true;
						while (cont2) {
							if (Main.propsConfRules.containsKey(focusedItem
									+ "." + name1 + ".line" + jj)) {
								doStatement += Main.propsConfRules
										.getProperty(focusedItem + "." + name1
												+ ".line" + jj)+"\n";
							} else {
								cont2 = false;
							}
							jj++;
						}
						tareabis.setText(doStatement);
						break;
					default:

						// tarea.setPrefHeight(245);
						if (Main.propsConfRules.containsKey(focusedItem
								+ "." + name1)) {
							tareabis.setText(Main.propsConfRules
									.getProperty(focusedItem + "." + name1)
									.replaceAll(";", ";\n"));
						}
						break;
					}
					tareabis.setEditable(true);
					tareabis.setId("my-text-area");
					tareabis.setPrefRowCount(3);
					tareabis.setWrapText(false);
					tareabis.setPromptText(focusedItem + "." + name1);
					tareabis.setMinHeight(70);
					GridPane.setMargin(tareabis, new Insets(5, 0, 5, 0));
					gridpane.add(lab2, 0, i);
					gridpane.add(tareabis, 1, i);

					this.lstTa.add(tareabis);

					break;
				}
				default:
				{
					TextField tf = new TextField();

					// tarea.setPrefHeight(245);
					if (Main.propsConfRules.containsKey(focusedItem + "."
							+ name1)) {
						tf.setText((Main.propsConfRules
								.getProperty(focusedItem + "." + name1))
								.replaceAll(";", ";\n"));
					}

					tf.setEditable(true);
					tf.setId("my-text-area");
					tf.setPromptText(focusedItem + "." + name1);
					tf.setMinHeight(20);
					GridPane.setMargin(tf, new Insets(5, 0, 5, 0));
					gridpane.add(lab2, 0, i);
					gridpane.add(tf, 1, i);
					this.lstTf.add(tf);
					break;
				}

				}
				i++;

			} else
				bool = false;

		}

		// Ajouter 3 buutons
		gridpane.setVgap(10);

		butSave.setId("my-buttons");

		Button butView = new Button("View Template");
		butView.setId("my-buttons");
		Button butCancel = new Button("Cancel");
		butCancel.setId("my-buttons");

		butView.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Stage stg = new Stage();
				ScrollPane scr2 = new ScrollPane();
				scr2.setFitToHeight(true);
				scr2.setFitToWidth(true);
				Scene sc1 = new Scene(scr2, dimStage.getWidth(), 900,
						Color.WHITE);
				sc1.getStylesheets().add("jlp/byteman/packager/packager.css");
				TextArea ta1 = new TextArea();
				File ftpl = new File(Main.root + File.separator
						+ "templates" + File.separator + "byteman"
						+ File.separator + rule + ".tpl");
				try {
					RandomAccessFile raf = new RandomAccessFile(ftpl, "r");
					int len = (int) raf.length();
					byte[] tabBytes = new byte[len];
					raf.read(tabBytes);
					raf.close();
					ta1.setEditable(true);
					ta1.setText(new String(tabBytes));
					ta1.setEditable(false);
					ta1.setPrefSize(dimStage.getWidth(), 900);
					ta1.setId("my-text-area");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				scr2.setContent(ta1);
				stg.setScene(sc1);
				stg.initModality(Modality.NONE);
				stg.show();
			}
		});
		butSave.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				System.out.println("saving config of " + rule);
				// Verifier que tous les champs sont bien remplis
				boolean isAllFilled = true;
				String errorMessage = "";
				for (TextField tf : lstTf) {
					if (null == tf.getText() || "".equals(tf.getText())) {
						isAllFilled = false;
					}
				}
				for (TextArea ta : lstTa) {
					if (null == ta.getText() || "".equals(ta.getText())) {
						isAllFilled = false;
					} else {
						if (ta.getPromptText().contains(focusedItem + ".listMethods")) {

							// Controle regexp
							for (String str : ta.getText().replaceAll("\n", "")
									.replaceAll("\r", "").split(";")) {
								Matcher match0 = patMethods.matcher(str);
								if (!match0.find()) {
									isAllFilled = false;
									if (errorMessage.length() == 0) {
										errorMessage += "\n"
												+ str
												+ " : method not correct => it does not match the pattern : "
												+ "\n"
												+ patMethods.toString()
												// +"^(\\w+\\s+)?(\\^?\\w+\\.)+\\w+(\\s*\\((\\s*\\w+\\s*,?)*\\))?$"
												+ "\n"
												+ " like for example String package1.package2.package3.MyClass.method(String, Integer)\n"
												+ "return Type String and packag1.package2.package3 and (list parameters) are optionnal\n"
												+ " MyClass.myMethod is correct too";
									}
								}
							}
						}
					}
				}
				if (!isAllFilled) {
					MessageBox
							.show(stgDiag,
									"The rule "
											+ rule
											+ " is not correctly filled.\n Please check and complete  the configuration\n"
											+ errorMessage, "Saving a rule "
											+ rule, MessageBox.ICON_ERROR);

				} else {
					// TODO la suavegarde
					// Recuperer les props du projet => bytemanpkg.properties /
					// propsConfRules
					// On verifie d abord que la rule est presente dans la liste
					// de listRulesChosen
					if (!Main.propsConfRules.containsKey("listRulesChosen")) {
						// le rajouter
						Main.propsConfRules.put("listRulesChosen", "");
					}
					List<String> lstChosen = new ArrayList<String>();
					for (String tmprule : Main.propsConfRules.getProperty(
							"listRulesChosen").split(";")) {
						lstChosen.add(tmprule);
					}

					if (!lstChosen.contains(focusedItem)) {
						lstChosen.add(focusedItem);
					}
					// reconstituer la propriete listRulesChosen
					String propsLstRules = "";
					for (String rl : lstChosen) {
						if (rl.trim().length() > 0)
							propsLstRules += rl + ";";
					}

					Main.propsConfRules.put("listRulesChosen",
							propsLstRules);
					// recuperer les valeurs des TextField et TextArea
					for (TextField tf : lstTf) {
						Main.propsConfRules.put(
								tf.getPromptText(),
								tf.getText().replaceAll("\n", "")
										.replaceAll("\r", ""));
					}
					for (TextArea ta : lstTa) {
						String prompt=ta.getPromptText();
						if(prompt.contains(focusedItem+".bindStatement")){
							String[] tabLines=ta.getText().split("\n");
							for(int i=0;i<tabLines.length;i++){
								Main.propsConfRules.put(
										ta.getPromptText()+".line"+i,tabLines[i]);
							}
						}
						else if(prompt.contains(focusedItem+".doStatement"))
						{
							String[] tabLines=ta.getText().split("\n");
							for(int i=0;i<tabLines.length;i++){
								Main.propsConfRules.put(
										ta.getPromptText()+".line"+i,tabLines[i]);
							}
						}
						else
						{
						Main.propsConfRules.put(
								ta.getPromptText(),
								ta.getText().replaceAll("\n", "")
										.replaceAll("\r", ""));
						}
					}
					Main.hmRules.put(focusedItem, true);
					Main.packRoot.creerCellFactoryListView2();
					stgDiag.hide();
				}
			}

		});
		butCancel.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				stgDiag.hide();
			}

		});

		HBox hbox = new HBox(150);
		HBox.setHgrow(butSave, Priority.ALWAYS);
		HBox.setHgrow(butCancel, Priority.ALWAYS);
		HBox.setHgrow(butView, Priority.ALWAYS);
		butSave.setMaxWidth(120);
		butSave.setMinWidth(80);
		butSave.setPrefWidth(80);
		butSave.setMaxWidth(120);

		butCancel.setMaxWidth(120);
		butCancel.setMinWidth(80);
		butCancel.setPrefWidth(80);
		butCancel.setMaxWidth(120);

		butView.setMaxWidth(200);
		butView.setMinWidth(120);
		butView.setPrefWidth(120);
		butView.setMaxWidth(200);

		hbox.setAlignment(Pos.CENTER);
		hbox.getChildren().addAll(butSave, butView, butCancel);

		gridpane.add(hbox, 0, i, 2, 1);
		scrl.setContent(gridpane);

		scrl.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

		scrl.setVmax(100);
		scrl.setVmin(0);

		scrl.setFitToHeight(true);

		return sc;
	}

	private Point2D localToPrimaryScreen(Stage stage, Node n, double x, double y) {
		if (stage.getScene() != n.getScene())
			return null;
		return new Point2D(Screen.getPrimary().getBounds().getMinX()
				+ stage.getX() + n.getScene().getX()
				+ n.localToScene(x, y).getX(), Screen.getPrimary().getBounds()
				.getMinY()
				+ stage.getY()
				+ n.getScene().getY()
				+ n.localToScene(x, y).getY());
	}
}
