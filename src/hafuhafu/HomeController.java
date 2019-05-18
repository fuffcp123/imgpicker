package hafuhafu;

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

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.file.Files;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class HomeController implements Initializable {
    @FXML
    private Button fileChooseBt, beginBt;
    @FXML
    private VBox root, inputVBox;
    @FXML
    private Hyperlink dirLink;
    @FXML
    private Label dirMessage;
    @FXML
    private RadioButton rb1, rb2, rb3, rb4;
    @FXML
    private TextArea textArea;
    private List<String> pathList = new ArrayList<>();
    private ToggleGroup group = new ToggleGroup();

    private TextField textField, width, height;
    private long count, time, size;
    private List<String> errorList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        msg("初始化中...");
        initRadioGroup();
        initTextArea();
        init1();
        //监听单选按钮的变化
        group.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
        });
        //点击开始按钮
        beginBt.setOnAction(event -> {
            int select = Integer.valueOf(String.valueOf(group.getSelectedToggle().getUserData()));
            switch (select) {
                case 1:
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
        });
        msg("初始化完毕...");
    }

    public void beginChosse() {
        try {
            int w = Integer.valueOf(width.getText());
            int h = Integer.valueOf(height.getText());
            if (w < 0 || h < 0) {
                alertMsg("请输入正确的数字");
            } else {
                if (pathList != null && pathList.size() > 0) {
                    File file = null;
                    for (int i = 0; i < pathList.size(); i++) {
                        String path = pathList.get(i);
                        final long begin = System.currentTimeMillis();
                        file = new File(path);
                        if (file.exists()) {
                            try {
                                BufferedImage image = null;
                                image = ImageIO.read(file);
                                if (image != null) {
                                    int width = image.getWidth();
                                    int height = image.getHeight();
                                    if (width >= w && height >= h) {
                                        FileOutputStream fos = new FileOutputStream("H:/" + file.getName());
                                        Files.copy(file.toPath(), fos);
                                        count++;
                                        size += file.length();
                                        long end = System.currentTimeMillis();
                                        long spend = (end - begin) / 1000;
                                        time += spend;
                                        msg(file.getName() + "......" + spend + "s");
                                        fos.close();
                                    }
                                }
                            } catch (Exception e) {
                                errorList.add(path);
                                continue;
                            }
                        }
                    }
                    msg("筛选结束,共计" + count + "张图片,总大小" + size * 1.0 / 1024 / 1024 + "mb,用时:" + time);
                    msg("错误个数" + errorList.size());
                    for (String msg : errorList) {
                        msg("错误信息:" + msg);
                    }
                    initData();

                }
            }
        } catch (NumberFormatException e) {
            alertMsg("请输入正确的数字");
        }
    }

    public void chooseDirectory(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("选择文件目录");
        File file = directoryChooser.showDialog(root.getScene().getWindow());
        if (null != file) {
            //将目录路径显示
            dirLink.setText(file.getPath());
            //获取该目录中文件的信息
            initData();
            getFiles(file);
            dirMessage.setText("该目录中共有" + pathList.size() + "张图片");
        }
    }

    private void initData() {
        pathList.clear();
        errorList.clear();
        time = count = size = 0;
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
        File file = new File(dirLink.getText());
        if (!file.exists()) {
            alertMsg("不存在该路径");
        } else {
            //存在则可以打开该文件夹
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
        SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
        String time = sdf.format(date);
        textArea.appendText(time + ":" + str + "\n");
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
