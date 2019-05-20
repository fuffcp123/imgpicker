package hafuhafu.controller;

import hafuhafu.Main;
import hafuhafu.thread.FileThread;
import hafuhafu.utils.Info;
import javafx.animation.AnimationTimer;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;

import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.regex.Pattern;

public class HomeController implements Initializable {
    @FXML
    private Button inputBt, outputBt, beginBt;
    @FXML
    private VBox root, inputVBox;
    @FXML
    private Hyperlink inputLink, outputLink;
    @FXML
    private Label dirMessage;
    @FXML
    private RadioButton rb1, rb2, rb3, rb4;
    @FXML
    private TextArea textArea;
    public static List<String> pathList = new ArrayList<>();
    private List<String> errorList = new ArrayList<>();
    private ToggleGroup group = new ToggleGroup();
    public static TextField textField, width, height;
    public static String outputPath;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        msg("初始化中...");
        File homeDirectory = FileSystemView.getFileSystemView().getHomeDirectory();
        outputLink.setText(homeDirectory.getPath());
        outputPath = outputLink.getText();
        initRadioGroup();
        initTextArea();
        init1();
        //监听单选按钮的变化
        group.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
        });
        //点击开始按钮
        beginBt.setOnAction(event -> {
            beginFilter();
        });
        msg("初始化完毕...");
    }

    private void beginFilter() {
        int select = Integer.valueOf(String.valueOf(group.getSelectedToggle().getUserData()));
        switch (select) {
            case 1:
                final BlockingQueue<String> messageQueue = new ArrayBlockingQueue<>(1);
                FileThread producer = new FileThread(messageQueue);
                Thread t = new Thread(producer);
                t.setDaemon(true);
                t.start();
                final LongProperty lastUpdate = new SimpleLongProperty();

                final long minUpdateInterval = 0; // nanoseconds. Set to higher number to slow output.

                AnimationTimer timer = new AnimationTimer() {

                    @Override
                    public void handle(long now) {
                        if (now - lastUpdate.get() > minUpdateInterval) {
                            final String message = messageQueue.poll();
                            if (message != null) {
                                msg(message);
                            }
                            lastUpdate.set(now);
                        }
                    }

                };
                timer.start();
                break;
            case 2:
                break;
            case 3:
                break;
            case 4:
                break;
            default:
                ;
        }
    }

    public void chooseDirectory(ActionEvent event) throws IOException {
        Button button = (Button) event.getSource();
        String id = button.getId();
        String linkId = id.substring(0, id.indexOf("Bt")) + "Link";
        Hyperlink hyperlink = (Hyperlink) root.getScene().lookup("#" + linkId);
        //不知道该怎么通过类似getById的方式获取控件 因此以反射的形式获取
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("选择文件目录");
        File file = directoryChooser.showDialog(root.getScene().getWindow());
        if (null != file) {
            //将目录路径显示
            hyperlink.setText(file.getPath());
            //获取该目录中文件的信息
            if (linkId.contains("input")) {
                initData();
                getFiles(file);
                dirMessage.setText("该目录中共有" + pathList.size() + "张图片");
            } else if (linkId.contains("output")) {
                outputPath = file.getPath();
            }
        }
    }

    private void initData() {
        pathList.clear();
        errorList.clear();
    }

    private void initTextArea() {
    }

    public void getFiles(File dir) {
        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                getFiles(file);
            } else {
                //判断文件的类型
                if (file.isFile()) {
                    if (Pattern.matches(".+\\.(png|jpg|jpeg|ico|bmp)", file.getName())) {
                        pathList.add(file.getPath());
                    }

                }
            }
        }
    }

    public void openDir(ActionEvent event) throws IOException {
        Hyperlink source = (Hyperlink) event.getSource();
        File file = new File(source.getText());
        if (!file.exists()) {
            alertMsg("不存在该路径");
        } else {
            Desktop.getDesktop().open(file);
        }
    }

    private void alertMsg(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void msg(String str) {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm:ss");
        String time = sdf.format(date);
        textArea.appendText(time + ":" + str + Info.separator());
    }

    private void initRadioGroup() {
        rb1.setToggleGroup(group);
        rb2.setToggleGroup(group);
        rb3.setToggleGroup(group);
        rb4.setToggleGroup(group);
        rb1.setSelected(true);
        rb1.setUserData(1);
        rb2.setUserData(2);
        rb3.setUserData(3);
        rb4.setUserData(4);
    }

    private void init1() {
        ObservableList<Node> children = inputVBox.getChildren();
        children.clear();
        Label label_width = new Label();
        label_width.setText("宽度");
        Label label_height = new Label();
        label_height.setText("高度");
        width = new TextField();
        height = new TextField();
        width.setText("0");
        height.setText("0");
        children.add(label_width);
        children.add(width);
        children.add(label_height);
        children.add(height);
    }
}
