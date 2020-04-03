
package com.example.todolist;

import com.example.todolist.dataModel.ToDoItem;
import com.example.todolist.dataModel.TodoData;

import java.time.LocalDate;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;


public class DialogController{  
   @FXML
    private TextField shortDescription;
   @FXML
    private TextArea detailsArea;
   @FXML
    private DatePicker deadLinePicker;
   
   public ToDoItem processResult(){
       String shortDescription = this.shortDescription.getText().trim();
       String details = detailsArea.getText().trim();
       
       LocalDate deadLinePicker = this.deadLinePicker.getValue();
       ToDoItem newItem = new ToDoItem(shortDescription, details, deadLinePicker);
       TodoData.getInstance().addToDoItem(newItem);
       return newItem;
   }
   
   
}
