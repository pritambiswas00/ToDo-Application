
package com.example.todolist;


import com.example.todolist.dataModel.ToDoItem;
import com.example.todolist.dataModel.TodoData;
import java.io.IOException;
import java.time.LocalDate;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Dialog;
import javafx.scene.control.ListCell;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.util.Callback;







public class MainController {

    private List<ToDoItem> todoItems;

    @FXML
    private ListView<ToDoItem> todoListView;

    @FXML
    private TextArea itemDetailsTextArea;

    @FXML
    private Label deadLineLabel;
    
    @FXML
    private BorderPane mainBorderPane;
    
    @FXML
    private ContextMenu contextMenu;
    
    @FXML 
    private ToggleButton filterToggleButton;
    
    @FXML
    private FilteredList<ToDoItem> filterList;
    
    @FXML
    private Predicate<ToDoItem> wantTodaysItems;
    private Predicate<ToDoItem> allItems;

         public void initialize() {
             contextMenu = new ContextMenu();
           MenuItem deleteMenu = new MenuItem("Delete"); 
           deleteMenu.setOnAction(new EventHandler<ActionEvent>() {
                 @Override
                 public void handle(ActionEvent event) {
                     ToDoItem item = todoListView.getSelectionModel().getSelectedItem();
                     deleteMenu(item); 
                     
                 }
             });
           
           contextMenu.getItems().setAll(deleteMenu);
       

        todoListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ToDoItem>() {
            @Override
            public void changed(ObservableValue<? extends ToDoItem> observable, ToDoItem oldValue, ToDoItem newValue) {
                if(newValue != null) {
                    ToDoItem item = todoListView.getSelectionModel().getSelectedItem();
                    itemDetailsTextArea.setText(item.getDetails());
                    DateTimeFormatter df = DateTimeFormatter.ofPattern("MMMM d, yyyy"); 
                    deadLineLabel.setText(df.format(item.getDeadLine()));
                }
            }
        });
        allItems = new Predicate<ToDoItem>() {
                 @Override
                 public boolean test(ToDoItem t) {
                     return true;
                 }
             };
        wantTodaysItems = new Predicate<ToDoItem>() {
                 @Override
                 public boolean test(ToDoItem t) {
                     return (t.getDeadLine().equals(LocalDate.now()));
                 }
             };
        filterList= new FilteredList<ToDoItem>(TodoData.getInstance().getTodoItems(), allItems );
                
        
        SortedList<ToDoItem> sortedList = new SortedList<ToDoItem>(filterList, new Comparator<ToDoItem>() {
                 @Override
                 public int compare(ToDoItem o1, ToDoItem o2) {
                     return o1.getDeadLine().compareTo(o2.getDeadLine());
                         
                 }
             });

        //todoListView.setItems(TodoData.getInstance().getTodoItems());
        todoListView.setItems(sortedList);
        todoListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        todoListView.getSelectionModel().selectFirst();
        todoListView.setCellFactory(new Callback<ListView<ToDoItem>, ListCell<ToDoItem>>() {
            
            @Override
            public ListCell<ToDoItem> call(ListView<ToDoItem> param) {
                ListCell<ToDoItem> cell = new ListCell<ToDoItem>(){
                    @Override
                    protected void updateItem(ToDoItem item, boolean empty) {
                        super.updateItem(item, empty);
                        if(empty){
                            setText(null);
                        }else{
                            setText(item.getShortDescription());  //this method handle the short description from the to do item so we dont need toString() method for short description from the ToDoItem class 
                            if(item.getDeadLine().isBefore(LocalDate.now().plusDays(1))){
                                setTextFill(Color.AQUA); 
                            }
                            else if(item.getDeadLine().equals(LocalDate.now().plusDays(1))){
                                setTextFill(Color.BROWN);
                            }
                        }
                    }
                    
                };
                cell.emptyProperty().addListener((obs, wasEmpty, isNowEmpty)->{
                    if(isNowEmpty){
                        cell.setContextMenu(null);
                    }else{
                        cell.setContextMenu(contextMenu);
                    }
                
                });
                        return cell;
                
            }
        });
         }

    @FXML
    public void showNewItemDialog(){
        Dialog<ButtonType> dialog = new Dialog();
        dialog.initOwner(mainBorderPane.getScene().getWindow()); //Here we are Initializing dialog over main window 
        dialog.setTitle("Add New ToDo Item");
        FXMLLoader fxmlloader = new FXMLLoader();
        fxmlloader.setLocation(getClass().getResource("Dialog.fxml"));
        try{
            dialog.getDialogPane().setContent(fxmlloader.load());
            
        }catch(IOException e){
            System.out.println("Couldnot load the dialog");
            e.printStackTrace();
            return;
        }
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        Optional<ButtonType> result = dialog.showAndWait();  //**/A container object which may or may not contain a 
        if(result.isPresent() && result.get() == ButtonType.OK){ //non-null value. If a value is present, 
            DialogController controller = fxmlloader.getController();  //isPresent() will return true and get() 
          ToDoItem newItem = controller.processResult();                //will return the value. **/
           //todoListView.getItems().setAll(TodoData.getInstance().getTodoItems());
           todoListView.getSelectionModel().select(newItem);
           
           
        }
    } 
    
    @FXML
    public void handleClickListView() {
        ToDoItem item = todoListView.getSelectionModel().getSelectedItem();
        itemDetailsTextArea.setText(item.getDetails());
        deadLineLabel.setText(item.getDeadLine().toString());



    
}
    public void deleteMenu(ToDoItem item){
       Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
       alert.setTitle("Delete ToDo Item");
       alert.setHeaderText("Are you sure, You want to Delete this Item? PRESS OK to Continue or CANCEL to Stop");
       Optional<ButtonType> button = alert.showAndWait();
       if(button.isPresent() && button.get()== ButtonType.OK){
           TodoData.getInstance().deleteToDoItem(item);
           
       }
    }
    
    
    @FXML
    
    public void handleFilterButton(){
        ToDoItem selectedItem = todoListView.getSelectionModel().getSelectedItem();
        if(filterToggleButton.isSelected()){
            filterList.setPredicate(wantTodaysItems);
               
        
               if(filterList.isEmpty()){
                itemDetailsTextArea.clear();
                deadLineLabel.setText("");
                }else if(filterList.contains(selectedItem)){
                    todoListView.getSelectionModel().select(selectedItem);
                }
                else{
                    todoListView.getSelectionModel().selectFirst();
                }
        }  else{
                   filterList.setPredicate(allItems);
                   todoListView.getSelectionModel().select(selectedItem);
                       }
        
                
        }
    @FXML
    public void onExit(){
        Platform.exit();
        
    }
    
    }

   


