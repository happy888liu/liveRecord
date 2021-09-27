package org.liu.liverecorder.threads;

import org.liu.liverecorder.Config;
import org.liu.liverecorder.liver.RoomDealer;
import org.liu.liverecorder.liver.check.FlvCheckerWithBufferEx;
import org.liu.liverecorder.util.ZipUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CheckMediaThread extends Thread {
    private RoomDealer roomDealer;
    private List<String> fileList;

    public CheckMediaThread(RoomDealer roomDealer, List<String> fileList) {
        this.roomDealer = roomDealer;
        this.fileList = fileList;
        this.setName("thread-check");
    }

    @Override
    public void run() {
        System.out.println("校对文件中");
        if (".flv".equals(roomDealer.getType())) {
            if (Config.autoCheck) {
                for (String path : fileList) {
                    try {
                        new FlvCheckerWithBufferEx().check(path, Config.deleteOnchecked, Config.splitScriptTagsIfCheck,
                                Config.splitAVHeaderTagsIfCheck, Config.saveFolderAfterCheck);
                        System.out.println("校对时间戳完毕。");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
            if (Config.flagZip) {
                List<File> filesAll = new ArrayList<File>(); // 用于存放录制产生的flv文件
                for (String path : fileList) {
                    if (!Config.autoCheck) {
                        filesAll.add(new File(path));
                    } else {
                        // 如果校正时间戳，一个个文件名进行尝试，直至不存在
                        File f = null;
                        Pattern fileNamePattern = Pattern.compile("[^/\\\\]+$");
                        for (int count = 0; ; count++) {
                            if (Config.saveFolderAfterCheck != null) {
                                Matcher matcher = fileNamePattern.matcher(path);
                                matcher.find();
                                f = new File(Config.saveFolderAfterCheck,
                                        matcher.group().replaceFirst(".flv$", "-checked" + count + ".flv"));
                            } else {
                                String path_i = path.replaceFirst(".flv$", "-checked" + count + ".flv");
                                f = new File(path_i);
                            }
                            if (f.exists())
                                filesAll.add(f);
                            else
                                break;
                        }
                    }
                }
                if (Config.flagZip) {
                    ZipUtil.zipFiles(filesAll, fileList.get(0) + ".zip");
                }
            }
        }
    }
}
