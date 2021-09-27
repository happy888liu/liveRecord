package org.liu.liverecorder;

import org.liu.liverecorder.liver.RoomDealer;
import org.liu.liverecorder.liver.domain.RoomInfo;
import org.liu.liverecorder.threads.RecordThread;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Scanner;
import java.util.TreeMap;

public class Main {
    final static String version = "v0.0.1";

    public static void main(String[] args) {
        Config.init(args);
        System.out.println(Config.liver + " 直播录制 version " + version);

        // 等待输入房间号
        Scanner scanner = new Scanner(System.in);
        if (Config.shortId == null) {
            System.out.println("请输入房间号(直播网址是https://xxx.com/xxx，那么房间号就是xxx)");
            String line = scanner.nextLine();
            Config.shortId = line;
        }
        // 加载cookie
        String cookie = null;
        try {
            BufferedReader buReader = new BufferedReader(new FileReader(Config.liver + "-cookie.txt"));
            cookie = buReader.readLine();
            buReader.close();
        } catch (Exception e) {
        }

        // 获取对应的直播
        RoomDealer roomDealer = getRoomDealer(Config.liver);
        if (cookie != null) roomDealer.setCookie(cookie);
        RoomInfo roomInfo = getRoomInfo(roomDealer);
        // 清晰度设置
        qnSetting(roomInfo);
        //开始录制
        new RecordThread(roomDealer,roomInfo,cookie);
    }

    /**
     * 获取正确的视频录制器
     *
     * @param liver
     * @return
     */
    private static RoomDealer getRoomDealer(String liver) {
        return RoomDealer.createRoomDealer(liver);
    }

    /**
     * 获取直播间信息
     *
     * @param roomDealer
     * @return
     */
    private static RoomInfo getRoomInfo(RoomDealer roomDealer) {
        RoomInfo roomInfo = roomDealer.getRoomInfo(Config.shortId);
        if (roomInfo == null) {
            System.err.println("解析失败！！");
            System.exit(-2);
        }
        //查看是否在线，并设置重试
        if (roomInfo.getLiveStatus() != 1) {
            System.out.println("当前没有在直播");
            int retryTime = 0;
            if (Config.retryIfLiveOff) {
                while (roomInfo.getLiveStatus() != 1 && (Config.maxRetryIfLiveOff == 0 || Config.maxRetryIfLiveOff > retryTime)) {
                    retryTime++;
                    try {
                        System.out.println(Config.retryAfterMinutes + "分钟左右后重试");
                        Thread.sleep((long) (Config.retryAfterMinutes * 60000));
                    } catch (InterruptedException e) {
                        break;
                    }
                    roomInfo = roomDealer.getRoomInfo(Config.shortId);
                    if (roomInfo == null) {
                        System.err.println("解析失败！！");
                        System.exit(-2);
                    }
                }
            } else {
                System.exit(3);
            }
        }
        return roomInfo;
    }

    /**
     * 清晰度设置
     *
     * @param roomInfo
     */
    private static void qnSetting(RoomInfo roomInfo) {
        String[] qualityDesc = roomInfo.getAcceptQualityDesc();
        boolean findQn = false;
        for (int i = 0; i < Config.qnPriority.length; i++) {
            for (int j = 0; j < qualityDesc.length; j++) {
                if (qualityDesc[j].equals(Config.qnPriority[i])) {
                    Config.qn = roomInfo.getAcceptQuality()[j];
                    findQn = true;
                    break;
                }
            }
            if (findQn) break;
        }
        // qn = -1, 使用最高画质
        if ("-1".equals(Config.qn)) {
            Config.qn = roomInfo.getAcceptQuality()[0];
        }
        // 检查清晰度的合法性
        boolean qnIsValid = false;
        String[] validQN = roomInfo.getAcceptQuality();
        for (String s : validQN) {
            if (s.equals(Config.qn)) {
                qnIsValid = true;
                break;
            }
        }
        if (!qnIsValid) {
            System.err.println("输入的qn值不在当前可获取清晰度列表中");
            System.exit(-1);
        }
    }
}
