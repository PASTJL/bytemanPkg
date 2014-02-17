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

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import jfx.messagebox.MessageBox;


@SuppressWarnings("hiding")
public class PasswordFieldCell<CnxSSH, String> extends TableCell<CnxSSH, String> {
	
	

		private PasswordField passwordField; 
		
		
		public PasswordFieldCell(TableColumn<CnxSSH,String> _col,final  String id) {
		   super();
			passwordField = new PasswordField();
		    passwordField.setEditable(true);
		   
		  //  System.out.println("this.col => "+ id);
		    passwordField.setOnAction(new EventHandler<ActionEvent>(){
		    	@Override
		    	public void handle(ActionEvent ev){
		    		
		    		PasswordFieldCell.this.setText(passwordField.getText());
		    		//System.out.println("passwordField.getText()="+passwordField.getText());
		    		
		    		TableView<CnxSSH> table=PasswordFieldCell.this.getTableView();
		    		int row=PasswordFieldCell.this.getTableRow().getIndex();
		    		table.getSelectionModel().select(row);
		    		jlp.byteman.packager.CnxSSH cnx=(jlp.byteman.packager.CnxSSH) table.getSelectionModel().getSelectedItem();
		    		if ( null != cnx)
		    			{
		    			//System.out.println("cnx is not null!");
		    			
		    			if(id.equals("password")){
		    			cnx.setPassword(passwordField.getText());
		    			}
		    			else if(id.equals("rootpassword")){
			    			cnx.setRootpassword(passwordField.getText());
			    			}
		    				
		    			
		    			
		    			}
		    		else
		    		{
		    			MessageBox.show(Main.currentStage,
		    				    "You must select a ligne before changing password",
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
		    	
		    	
		        passwordField.setText( (java.lang.String) item);
		        
		        setGraphic(passwordField);
		        
		      
		        
		    }else{
		        setGraphic(null);
		    }
		}
		

		
		



		

}
