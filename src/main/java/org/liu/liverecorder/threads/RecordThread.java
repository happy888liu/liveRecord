package org.liu.liverecorder.threads;

import org.liu.liverecorder.Config;
import org.liu.liverecorder.liver.RoomDealer;
import org.liu.liverecorder.liver.domain.RoomInfo;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RecordThread extends Thread {
    RoomDealer roomDealer;
    RoomInfo roomInfo;
    String cookie;

    public RecordThread(RoomDealer roomDealer, RoomInfo roomInfo, String cookie) {
        this.roomDealer = roomDealer;
        this.roomInfo = roomInfo;
        this.cookie = cookie;
        this.setName("thread-record");
    }

    @Override
    public void run() {
        String url = roomDealer.getLiveUrl(roomInfo.getRoomId(), Config.qn.toString(), roomInfo.getRemark());
        System.out.println("开始录制，输入stop停止录制");
        List<String> fileList = new ArrayList<>(); // 用于存放录制产生的初始flv文件
        record(roomDealer, roomInfo, url, fileList);
        System.out.println("下载停止");
        if (fileList.size() > 0) {
            Thread th = new CheckMediaThread(roomDealer, fileList);
            th.start();
        }
    }

    private void record(RoomDealer roomDealer, RoomInfo roomInfo, String url, List<String> fileList) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(Config.timeFormat);
        String realName = pathFormat(Config.fileName, roomInfo, dateFormat).replace("{seq}", "" + fileList.size())
                .replaceAll("[\\\\|\\/|:\\*\\?|<|>|\\||\\\"$]", ".");
        // 如果saveFolder不为空
        if (Config.saveFolder != null) {
            Config.saveFolder = pathFormat(Config.saveFolder, roomInfo, dateFormat);
            roomDealer.util.setSavePath(Config.saveFolder);
        }
        // 如果saveFolderAfterCheck不为空
        if (Config.autoCheck && Config.saveFolderAfterCheck != null) {
            Config.saveFolderAfterCheck = pathFormat(Config.saveFolderAfterCheck, roomInfo, dateFormat);
            File f = new File(Config.saveFolderAfterCheck);
            if (!f.exists())
                f.mkdirs();
        }
        // 此处一直堵塞， 直至停止
        roomDealer.startRecord(url, realName, roomInfo.getShortId());
        File file = roomDealer.util.getFileDownload();

        File partFile = new File(file.getParent(), realName + roomDealer.getType() + ".part");
        File completeFile = new File(file.getParent(), realName + roomDealer.getType());
        realName = realName.replace("{endTime}", dateFormat.format(new Date()));
        File dstFile = new File(file.getParent(), realName + roomDealer.getType());

        if (partFile.exists())
            partFile.renameTo(dstFile);
        else
            completeFile.renameTo(dstFile);

        // 加入已下载列表
        fileList.add(dstFile.getAbsolutePath());
    }

    private static String pathFormat(String pattern, RoomInfo roomInfo, SimpleDateFormat sdf) {
        return pattern.replace("{name}", roomInfo.getUserName()).replace("{shortId}", roomInfo.getShortId())
                .replace("{roomId}", roomInfo.getRoomId()).replace("{liver}", Config.liver)
                .replace("{startTime}", sdf.format(new Date()));
    }
}
