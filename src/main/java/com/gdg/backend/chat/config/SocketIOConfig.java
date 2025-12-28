package com.gdg.backend.chat.config;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.Transport;
import org.springframework.context.annotation.Bean;

@org.springframework.context.annotation.Configuration
public class SocketIOConfig {

    @Bean
    public SocketIOServer socketIOServer() {
        Configuration config = new Configuration();

        config.setHostname("0.0.0.0");
        config.setPort(9092); // ⚠️ 프론트 VITE_SOCKET_URL 과 반드시 동일

        config.setContext("/socket.io");
        config.setTransports(
                Transport.POLLING,
                Transport.WEBSOCKET
        );

        config.setOrigin("*");

        return new SocketIOServer(config);
    }
}

