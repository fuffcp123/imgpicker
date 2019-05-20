package hafuhafu.thread;

import hafuhafu.controller.HomeController;
import hafuhafu.utils.Info;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.concurrent.BlockingQueue;

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
            List<String> list = HomeController.pathList;
            if (null == list) {
                messageQueue.put(Info.error("未选择目录"));
            } else if (list.size() == 0) {
                messageQueue.put(Info.error("该目录不包含图片文件"));
            } else {
                int w = Integer.valueOf(HomeController.width.getText());
                int h = Integer.valueOf(HomeController.height.getText());
                messageQueue.put(Info.info("任务开始"));
                for (int i = 0; i < list.size(); i++) {
                    File file = new File(list.get(i));
                    BufferedImage image = ImageIO.read(file);
                    if (null != image) {
                        int width = image.getWidth();
                        int height = image.getHeight();
                        if (width > w && height > h) {
                            FileOutputStream fos = new FileOutputStream(new File(HomeController.outputPath, file.getName()));
                            Files.copy(file.toPath(), fos);
                            // TODO: 2019/5/20 若存在同名文件没进行判断
                            messageQueue.put(Info.info(file.getName() + "复制成功"));
                            fos.close();
                        }
                    }
                }
                messageQueue.put(Info.info("任务结束"));
            }

        } catch (InterruptedException exc) {
            System.out.println("Message producer interrupted: exiting.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
