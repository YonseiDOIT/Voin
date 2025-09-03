import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";
import { useEffect, useRef } from "react";

export function useNotificationSocket(token: string, onMessage: (msg: any) => void) {
    const clientRef = useRef<Client | null>(null);

    useEffect(() => {
        const client = new Client({
            webSocketFactory: () => new SockJS("/ws"),
            connectHeaders: { Authorization: `Bearer ${token}` },
            reconnectDelay: 3000,
            heartbeatIncoming: 10000,
            heartbeatOutgoing: 10000,
            onConnect: () => {
                // 1:1 개인 큐 구독 (변수 선언 없이 호출)
                client.subscribe("/user/queue/notifications", (frame) => {
                    try {
                        const body = JSON.parse(frame.body);
                        onMessage(body);
                    } catch {
                        onMessage(frame.body);
                    }
                });

                // 서버 @MessageMapping("/notify") 테스트 전송 (옵션)
                client.publish({
                    destination: "/pub/notify",
                    body: "hello-by-stomp",
                });
            },
            onStompError: (frame) => {
                console.error("Broker reported error:", frame.headers["message"]);
            },
        });

        client.activate();
        clientRef.current = client;

        return () => {
            client.deactivate();
        };
    }, [token, onMessage]);
}
