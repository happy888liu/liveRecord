package org.liu.liverecorder.threads;

import org.liu.liverecorder.liver.RoomDealer;
import org.liu.liverecorder.liver.domain.RoomInfo;

public class RecordThread extends Thread {
    RoomDealer roomDealer;
    RoomInfo roomInfo;

    public RecordThread(RoomDealer roomDealer, RoomInfo roomInfo) {
        this.roomDealer = roomDealer;
        this.roomInfo = roomInfo;
        this.setName("thread-record");
    }

    @Override
    public void run() {

    }
}
