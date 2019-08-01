package sqs.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import sqs.MainApp;

import java.io.File;

public class RootLayoutController {

    private MainApp mainApp;

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    /**
     * 用户新建一个空白的地址簿
     */
    @FXML
    private void handleNew() {
        mainApp.getPersonData().clear();
        mainApp.setPersonFilePath(null);
    }

    /**
     * 用户选择打开一个xml文件并加载
     */
    @FXML
    private void handleOpen() {
        FileChooser fileChooser = new FileChooser();

        // 过滤文件类型
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("选择xml类型文件 (*.xml)", "*.xml");
        fileChooser.getExtensionFilters().add(filter);


        File file = fileChooser.showOpenDialog(mainApp.getPrimaryStage());

        // 加载文件
        if (null != file) {
            mainApp.loadPersonDataFormFile(file);
        }
    }

    /**
     * 保存文件，当文件原地址不存在时另存
     */
    @FXML
    private void handleSave() {



        /* 获取原文件地址 **/
        File personFilePath = mainApp.getPersonFilePath();
        if (null != personFilePath) {
            mainApp.savePersonDataToFile(personFilePath);
        } else {
            handleSaveAs();
        }
    }

    /**
     * 文件另存为
     */
    @FXML
    private void handleSaveAs() {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("请选择xml 文件", "*.xml");
        fileChooser.getExtensionFilters().add(extFilter);

        File file = fileChooser.showSaveDialog(mainApp.getPrimaryStage());

        if (null != file) {
            // 判断文件类型是否是xml
            if (!file.getPath().endsWith(".xml")) {
                file = new File(file.getPath() + ".xml");
            }
            mainApp.savePersonDataToFile(file);
        }

    }

    @FXML
    private void handleAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("关于我：");
        alert.setHeaderText("我是一名程序猿");
        alert.setContentText("欢迎访问我的新浪微博：大明明VS亮");

        alert.showAndWait();
    }

    @FXML
    private void handleExit() {
        System.exit(0);
    }

    @FXML
    public void handleShowBirthdayStatistics() {
        mainApp.showBirthdayStatistics();
    }
}

