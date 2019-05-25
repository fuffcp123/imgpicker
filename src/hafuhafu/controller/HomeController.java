package hafuhafu.controller;

import hafuhafu.thread.FileThread;
import hafuhafu.utils.Info;
import javafx.animation.AnimationTimer;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
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
import java.util.concurrent.*;
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
    private RadioButton rb1, rb2, rb3, rb4, LT_RB, GT_RB, EQ_RB;
    @FXML
    private TextArea textArea;
    public static List<String> pathList = new ArrayList<>();
    private ToggleGroup group = new ToggleGroup(), compareGroup = new ToggleGroup();
    public static TextField width, height;
    public static TextField[] textFields;
    public static String outputPath;
    public static int type = 1, ctype = 1;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        msg("初始化中...");
        File homeDirectory = FileSystemView.getFileSystemView().getHomeDirectory();
        outputLink.setText(homeDirectory.getPath());
        outputPath = outputLink.getText();
        initRadioGroup();
        initType();
        //监听类型单选按钮的变化
        group.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            type = Integer.valueOf(newValue.getUserData().toString());
            switch (type) {
                case 1:
                    initType();
                    break;
                case 2:
                    initTypeUI(new String[]{"按比例(W/H)"}, new String[]{"1"});
                    break;
                case 3:
                    initTypeUI(new String[]{"按文件大小"}, new String[]{"1"});
                    break;
                case 4:
                    initTypeUI(new String[]{"按文件名"}, new String[]{"*.jpg"});
                    break;
                default:
                    break;
            }
        });
        compareGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            ctype = Integer.valueOf(newValue.getUserData().toString());
        });
        //点击开始按钮
        beginBt.setOnAction(event -> beginFilter());
        msg("初始化完毕...");
    }

    private void beginFilter() {

        final BlockingQueue<String> messageQueue = new ArrayBlockingQueue<>(1);
        FileThread producer = new FileThread(messageQueue);
        ExecutorService service = Executors.newCachedThreadPool();
        service.execute(producer);
        final LongProperty lastUpdate = new SimpleLongProperty();
        // nanoseconds. Set to higher number to slow output.
        final long minUpdateInterval = 0;

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
    }

    /**
     * 获取选中的目录的路径
     *
     * @param event 点击事件
     */
    public void chooseDirectory(ActionEvent event) {
        Button button = (Button) event.getSource();
        String id = button.getId();
        String linkId = id.substring(0, id.indexOf("Bt")) + "Link";
        Hyperlink hyperlink = (Hyperlink) root.getScene().lookup("#" + linkId);
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("选择文件目录");
        File file = directoryChooser.showDialog(root.getScene().getWindow());
        if (null != file) {
            //将目录路径显示
            hyperlink.setText(file.getPath());
            //获取该目录中文件的信息
            String inputType = "input";
            String outputType = "output";
            if (linkId.contains(inputType)) {
                //初始化数据
                pathList.clear();
                //如果点击的是选择输入的按钮,获取该目录中的信息
                getFiles(file);
                dirMessage.setText("该目录中共有" + pathList.size() + "张图片");
            } else if (linkId.contains(outputType)) {
                //如果时输出的按钮,设置输出目录
                outputPath = file.getPath();
            }
        }
    }


    /**
     * 根据文件类型获取匹配的文件路径
     *
     * @param dir 根目录
     */
    public void getFiles(File dir) {
        File[] files = dir.listFiles();
        //遍历并筛选
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

    /**
     * 打开目录
     *
     * @param event 点击事件
     * @throws IOException
     */
    public void openDir(ActionEvent event) throws IOException {
        Hyperlink source = (Hyperlink) event.getSource();
        File file = new File(source.getText());
        if (!file.exists()) {
            alertMsg("不存在该路径");
        } else {
            Desktop.getDesktop().open(file);
        }
    }

    /**
     * 弹出框
     *
     * @param msg 消息
     */
    private void alertMsg(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    /**
     * 在textArea控制台追加消息,带换行和时间
     *
     * @param str 消息
     */
    private void msg(String str) {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm:ss");
        String time = sdf.format(date);
        textArea.appendText(time + ":" + str + Info.separator());
    }

    /**
     * 初始化按钮组
     */
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
        //比较方式的按钮组
        compareGroup = new ToggleGroup();
        LT_RB.setToggleGroup(compareGroup);
        GT_RB.setSelected(true);
        GT_RB.setToggleGroup(compareGroup);
        EQ_RB.setToggleGroup(compareGroup);
        LT_RB.setUserData(-1);
        GT_RB.setUserData(1);
        EQ_RB.setUserData(0);
    }

    /**
     * 初始化宽高类型
     */
    private void initType() {
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

    /**
     * 初始化其他类型
     *
     * @param texts  类型label数组
     * @param values 类型textField的值
     */
    private void initTypeUI(String[] texts, String[] values) {
        ObservableList<Node> children = inputVBox.getChildren();
        children.clear();
        for (String t : texts) {
            Label label = new Label();
            label.setText(t);
            children.add(label);
        }
        textFields = new TextField[values.length];
        for (int i = 0; i < values.length; i++) {
            textFields[i] = new TextField();
            textFields[i].setText(values[i]);
            children.add(textFields[i]);
        }

    }
}
