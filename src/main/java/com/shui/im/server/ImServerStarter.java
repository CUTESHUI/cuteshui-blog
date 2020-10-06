package com.shui.im.server;

import lombok.extern.slf4j.Slf4j;
import org.tio.server.ServerGroupContext;
import org.tio.websocket.server.WsServerStarter;
import org.tio.websocket.server.handler.IWsMsgHandler;

import java.io.IOException;

/**
 *  启动tio服务
 */
@Slf4j
public class ImServerStarter {

    private WsServerStarter starter;

    public ImServerStarter(int port) throws IOException {
        // 消息处理器
        IWsMsgHandler handler = new ImWsMsgHandler();
        starter = new WsServerStarter(port, handler);
        // 配置上下文
        ServerGroupContext serverGroupContext = starter.getServerGroupContext();
        // 5秒一次心跳
        serverGroupContext.setHeartbeatTimeout(50000);
    }

    public void start() throws IOException {
        starter.start();
        log.info("tio server start !!");
    }

}
