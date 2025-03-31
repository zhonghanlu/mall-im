package cn.source.system.websocket;

import cn.source.system.service.IImMessagesService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

import javax.annotation.Resource;

/**
 * websocket 配置
 *
 * @author ruoyi
 */
@Configuration
public class WebSocketConfig {
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }

    @Resource
    public void imMessagesService(IImMessagesService imMessagesService) {
        WebSocketServer.iImMessagesService = imMessagesService;
    }

}
