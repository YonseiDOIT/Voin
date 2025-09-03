import { useNotificationSocket } from "./useNotificationSocket";
import { useState } from "react";

export default function Notifications({ token }: { token: string }) {
    const [items, setItems] = useState<any[]>([]);

    useNotificationSocket(token, (msg) => {
        setItems((prev) => [msg, ...prev]);
    });

    return (
        <div>
            <h3>알림</h3>
            <ul>
                {items.map((it, i) => (
                    <li key={i}>{typeof it === "string" ? it : it.message}</li>
                ))}
            </ul>
        </div>
    );
}
