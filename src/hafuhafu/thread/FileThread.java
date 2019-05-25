package hafuhafu.thread;

import hafuhafu.controller.HomeController;
import hafuhafu.utils.Info;
import javafx.scene.control.TextField;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.regex.Pattern;

/**
 * @Auther: Tang
 * @Date: 2019/5/20 09:28
 * @Description:
 */
public class FileThread implements Runnable {
    private final BlockingQueue<String> messageQueue;

    public FileThread(BlockingQueue<String> messageQueue) {
        this.messageQueue = messageQueue;
    }

    @Override
    public void run() {
        try {
            long beginTime = System.currentTimeMillis();
            List<String> list = HomeController.pathList;
            if (null == list) {
                messageQueue.put(Info.error("未选择目录"));
            } else if (list.size() == 0) {
                messageQueue.put(Info.error("该目录不包含图片文件"));
            } else {
                int w = Integer.valueOf(HomeController.width.getText());
                int h = Integer.valueOf(HomeController.height.getText());
                messageQueue.put(Info.info("------任务开始------"));
                for (int i = 0; i < list.size(); i++) {
                    try {
                        File file = new File(list.get(i));
                        BufferedImage image = ImageIO.read(file);
                        if (null != image) {
                            //不同情况的判断
                            int width = image.getWidth();
                            int height = image.getHeight();
                            TextField[] textFields = HomeController.textFields;
                            switch (HomeController.type) {
                                case 1:
                                    switch (HomeController.ctype) {
                                        case -1:
                                            if (width < w && height < h) {
                                                copy(file);
                                            }
                                            break;
                                        case 0:
                                            if (width == w && height == h) {
                                                copy(file);
                                            }
                                            break;
                                        case 1:
                                            if (width > w && height > h) {
                                                copy(file);
                                            }
                                            break;
                                        default:
                                            break;
                                    }

                                    break;
                                case 2:
                                    double ratio = Double.valueOf(textFields[0].getText());
                                    if (ratio > 0) {
                                        double fileRatio = 1.0 * width / height;
                                        compare(fileRatio, ratio, file);
                                    } else {
                                        messageQueue.put(Info.error("比例必须大于0"));
                                    }
                                    break;
                                case 3:
                                    double size = Double.valueOf(textFields[0].getText());
                                    if (size > 0) {
                                        double fileSize = 1.0 * file.length() / 1024 / 1024;
                                        compare(fileSize, size, file);
                                    } else {
                                        messageQueue.put(Info.error("文件大小必须大于0"));
                                    }
                                    break;
                                case 4:
                                    String pattern = textFields[0].getText();
                                    if (Pattern.matches(pattern, file.getName())) {
                                        copy(file);
                                    }
                                    break;
                                default:
                                    break;
                            }

                        }
                    } catch (Exception e) {
                        messageQueue.put(Info.error("读取图片时出现异常:" + e.getMessage()));
                        continue;
                    }
                }
                long endTime = System.currentTimeMillis();
                messageQueue.put(Info.info("用时" + String.format("%.2f", (endTime - beginTime) / 1000 * 1.0) + "s"));
                messageQueue.put(Info.info("------任务结束------"));
            }

        } catch (InterruptedException exc) {
            System.out.println("Message producer interrupted: exiting.");
        }
    }

    private void copy(File file) throws InterruptedException {
        try {
            FileOutputStream fos = new FileOutputStream(new File(HomeController.outputPath, file.getName()));
            Files.copy(file.toPath(), fos);
            // TODO: 2019/5/20 若存在同名文件没进行判断
            messageQueue.put(Info.info(file.getName() + "复制成功"));
            fos.close();
        } catch (Exception e) {
            messageQueue.put(Info.error("复制时出现异常:" + e.getMessage()));
        }
    }

    /**
     * 进行比较判断
     *
     * @param file   来自文件的属性
     * @param user   用户设置的属性
     * @param target 目标文件
     * @throws IOException
     * @throws InterruptedException
     */
    private void compare(double file, double user, File target) throws InterruptedException {
        switch (HomeController.ctype) {
            case -1:
                if (file < user) {
                    copy(target);
                }
                break;
            case 0:
                if (file == user) {
                    copy(target);
                }
                break;
            case 1:
                if (file > user) {
                    copy(target);
                }
                break;
            default:
                break;
        }
    }
}
