package com.gdg.backend.chat.socket;

import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.SocketIOClient;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatSocketHandler {

    private final SocketIOServer server;
    private boolean started = false;

    @PostConstruct
    public void startServer() {
        if (started) {
            log.warn("⚠️ Socket server already started. Skip.");
            return;
        }

        try {
            server.start();
            started = true;
            log.info("✅ Chat Socket Server started on port 9092");
        } catch (Exception e) {
            log.error("❌ Failed to start socket server", e);
            throw e;
        }
    }

    @PreDestroy
    public void stopServer() {
        if (started) {
            server.stop();
            started = false;
            log.info("🛑 Chat Socket Server stopped");
        }
    }
}
