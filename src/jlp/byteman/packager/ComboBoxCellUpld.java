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

import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import jfx.messagebox.MessageBox;


@SuppressWarnings("hiding")
public class ComboBoxCellUpld<UpldSSH, String> extends TableCell<UpldSSH, String> {
	
	

		private ComboBox<String> comboBox; 
		
		
		public ComboBoxCellUpld(TableColumn<UpldSSH,String> col) {
		   super();
		   comboBox = new ComboBox<String>();
		   List<String> list = new ArrayList<String>();
			ObservableList<String> itemsExecute = (ObservableList<String>) FXCollections
					.observableList(list);
			list.add( (String) "Yes");
			list.add((String) "No");
		   comboBox.setItems(itemsExecute);
		   comboBox.setEditable(false);
		   
		     
		   comboBox.setOnAction(new EventHandler<ActionEvent>(){
		    	@Override
		    	public void handle(ActionEvent ev){
		    		
		    		ComboBoxCellUpld.this.setText((java.lang.String) comboBox.getSelectionModel().getSelectedItem());
		    		
		    		TableView<UpldSSH> table=ComboBoxCellUpld.this.getTableView();
		    		int row=ComboBoxCellUpld.this.getTableRow().getIndex();
		    		table.getSelectionModel().select(row);
		    		jlp.byteman.packager.UpldSSH upld=(jlp.byteman.packager.UpldSSH) table.getSelectionModel().getSelectedItem();
		    		if ( null != upld)
		    			{
		    			System.out.println("upld is not null!");
		    			upld.setExecute((java.lang.String) comboBox.getSelectionModel().getSelectedItem());
		    			
		    			}
		    		else
		    		{
		    			MessageBox.show(Main.currentStage,
		    				    "You must select a ligne before changing exute mode",
		    				    "Change password", 
		    				    MessageBox.ICON_INFORMATION | MessageBox.OK | MessageBox.CANCEL);
		    		}
		    		
		    		
		    		ev.consume();
		    		
		    	}
		    });
		    //this.setConverter(new DefaultStringConverter());
		    this.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		    this.setGraphic(null);
		   
		}
		
		@Override
		public void updateItem(String item, boolean empty) {
			
		    super.updateItem(item, empty);
		   
		    if(!isEmpty()){
		    	
		    	
		    	comboBox.getSelectionModel().select((String) item);
		        
		        setGraphic(comboBox);
		        
		      
		        
		    }else{
		        setGraphic(null);
		    }
		}
		

		
		



		

}
