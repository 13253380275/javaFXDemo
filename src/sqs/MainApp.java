package sqs;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sqs.controller.BirthdayStatisticsController;
import sqs.model.Person;
import sqs.model.PersonListWrapper;
import sqs.controller.PersonEditDialogController;
import sqs.controller.PersonOverviewController;
import sqs.controller.RootLayoutController;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.IOException;
import java.util.prefs.Preferences;

public class MainApp extends Application {

    private Stage primaryStage;
    private BorderPane rootLayout;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("AddressApp");

        this.primaryStage.getIcons().add(new Image("file:resources/images/address.png"));

        initRootLayout();
        System.out.println();
        System.out.println(2);

        showPersonOverview();


        KeyCodeCombination kc = new KeyCodeCombination(KeyCode.S, KeyCombination.SHORTCUT_DOWN);
    }

    /**
     *  初始化最外层的布局
     */
    private void initRootLayout() {
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/RootLayout.fxml"));
            rootLayout = loader.load();

            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);

            RootLayoutController controller = loader.getController();
            controller.setMainApp(this);

            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

        File file = getPersonFilePath();
        if (null != file) {
            loadPersonDataFormFile(file);
        }
    }

    /**
     * 初始化人员显示列表窗体
     */
    private void showPersonOverview() {
        try {
            // Load person overview.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/PersonOverview.fxml"));
            AnchorPane personOverview = loader.load();

            rootLayout.setCenter(personOverview);


            PersonOverviewController controller = loader.getController();

            controller.setMainApp(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }

    /** 全局集合 **/
    private ObservableList<Person> personData = FXCollections.observableArrayList();

    public MainApp() {
        personData.add(new Person("Hans", "Muster"));
        personData.add(new Person("Ruth", "Mueller"));
        personData.add(new Person("Heinz", "Kurz"));
        personData.add(new Person("Cornelia", "Meier"));
        personData.add(new Person("Werner", "Meyer"));
        personData.add(new Person("Lydia", "Kunz"));
        personData.add(new Person("Anna", "Best"));
        personData.add(new Person("Stefan", "Meier"));
        personData.add(new Person("Martin", "Mueller"));
    }

    public ObservableList<Person> getPersonData() {
        return personData;
    }


    /**
     * 编辑人员窗体
     * @param person
     * @return
     */
    public boolean showPersonEditDialog(Person person) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/PersonEditDialog.fxml"));
            AnchorPane  page = loader.load();

            Stage dialogStage  = new Stage();
            dialogStage.setTitle("编辑人员");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            PersonEditDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setPerson(person);

            dialogStage.showAndWait();
            return controller.isOkClicked();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 获取文件上次保存的路径
     * @return
     */
    public File getPersonFilePath() {
        Preferences preferences = Preferences.userNodeForPackage(MainApp.class);
        String filePath = preferences.get("filePath", null);
        if (null == filePath) {
            return null;
        }

        return new File(filePath);
    }

    /**
     * 使用Preferences保存上次打开文件的路径
     * @param file
     */
    public void setPersonFilePath(File file) {
        Preferences preferences = Preferences.userNodeForPackage(MainApp.class);


        if (null == preferences) {
            preferences.remove("filePath");
            primaryStage.setTitle("AddressApp");
        } else {
            if (null != file) {
                preferences.put("filePath", file.getPath());
                primaryStage.setTitle("AddressApp" + file.getName());
            }
        }

    }


    /**
     * 从文件中加载person对象数据，并替换当前的person
     * @param file
     */
    public void loadPersonDataFormFile(File file) {

        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(PersonListWrapper.class);
            // 将xml内容转化为Java的unmarshaller对象
            Unmarshaller um = jaxbContext.createUnmarshaller();
            PersonListWrapper wrapper = (PersonListWrapper) um.unmarshal(file);

            personData.clear();
            if (wrapper.getPersons() != null) {

                personData.addAll(wrapper.getPersons());
            }
            setPersonFilePath(file);

        } catch (JAXBException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText(null);
            alert.setContentText("数据出错");
            alert.setTitle("警告");
            alert.showAndWait();
        }
    }

    /**
     * 将Java内容编组到xml
     * @param file
     */
    public void savePersonDataToFile(File file) {

        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(PersonListWrapper.class);
            // 将Java内容树序列化为xml
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            PersonListWrapper wrapper = new PersonListWrapper();
            wrapper.setPersons(personData);
            // 文件编组到file
            marshaller.marshal(wrapper, file);
            setPersonFilePath(file);

        } catch (JAXBException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText(null);
            alert.setContentText("数据出错");
            alert.setTitle("警告");
            alert.showAndWait();
        }
    }

    public void showBirthdayStatistics() {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MainApp.class.getResource("view/BirthdayStatistics.fxml"));

        try {
            AnchorPane page = loader.load();

            Stage stage = new Stage();
            stage.setTitle("");
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(primaryStage);

            stage.setScene(new Scene(page));

            BirthdayStatisticsController controller = loader.getController();
            controller.setPersonData(personData);

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
