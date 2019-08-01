package sqs.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import sqs.MainApp;
import sqs.model.Person;


public class PersonOverviewController {

    @FXML
    private TableView<Person> personTable;
    @FXML
    private TableColumn<Person, String> firstNameColumn;
    @FXML
    private TableColumn<Person, String> lastNameColumn;

    @FXML
    private Label firstNameLabel;
    @FXML
    private Label lastNameLabel;
    @FXML
    private Label streetLabel;
    @FXML
    private Label postalCodeLabel;
    @FXML
    private Label cityLabel;
    @FXML
    private Label birthdayLabel;

    private MainApp mainApp;

    public PersonOverviewController() {
    }

    @FXML
    private void initialize() {
        // 初始化赋值
        firstNameColumn.setCellValueFactory(cellData -> cellData.getValue().firstNameProperty());
        lastNameColumn.setCellValueFactory(cellData -> cellData.getValue().lastNameProperty());

        showPersonDetails(null);

        // 创建监听，并调用showPersonDetails方法
        personTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> showPersonDetails(newValue));

    }

    @FXML
    private void handleDeletePerson() {
        int selectedIndex = personTable.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {

            personTable.getItems().remove(selectedIndex);
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("警告");
            alert.setHeaderText(null);
            alert.setContentText("请在左边列表选中删除的人员名单");
            alert.showAndWait();
        }


    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
        personTable.setItems(mainApp.getPersonData());

    }

    public void showPersonDetails(Person person) {
        if (null != person) {
            firstNameLabel.setText(person.getFirstName());
            lastNameLabel.setText(person.getLastName());
            streetLabel.setText(person.getStreet());
            cityLabel.setText(person.getCity());
            birthdayLabel.setText(person.getBirthday().toString());
            postalCodeLabel.setText(Integer.toString(person.getPostalCode()));
        } else {
            firstNameLabel.setText("");
            lastNameLabel.setText("");
            streetLabel.setText("");
            cityLabel.setText("");
            birthdayLabel.setText("");
            postalCodeLabel.setText("");
        }

    }

    @FXML
    private void handleNewPerson() {
        Person person = new Person();
        boolean isOk = mainApp.showPersonEditDialog(person);
        if (isOk) {
            mainApp.getPersonData().add(person);
        }
    }

    @FXML
    private void handleEditPerson() {
        Person person = personTable.getSelectionModel().getSelectedItem();
        if (null != person) {
            boolean isOk = mainApp.showPersonEditDialog(person);
            if (isOk) {
                showPersonDetails(person);
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("警告");
            alert.setHeaderText(null);
            alert.setContentText("请选择需要编辑的人物信息");
            alert.showAndWait();

            new Dialog<>();
        }

    }

}
