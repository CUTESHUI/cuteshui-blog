package com.shui.config;


import com.shui.im.handler.MsgHandlerFactory;
import com.shui.im.server.ImServerStarter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Slf4j
@Configuration
public class ImServerConfig {

    @Value("${im.server.port}") // 指定端口
    private int imPort;

    @Bean
    ImServerStarter imServerStarter() {
        try {
            // 启动tio服务
            ImServerStarter serverStarter = new ImServerStarter(imPort);
            serverStarter.start();

            // 初始化消息处理器类别
            // 后端接收前段发来的消息，根据类别找到对应的处理器
            MsgHandlerFactory.init();

            return serverStarter;
        } catch (IOException e) {
            log.error("tio server 启动失败", e);
        }

        return null;
    }
}
