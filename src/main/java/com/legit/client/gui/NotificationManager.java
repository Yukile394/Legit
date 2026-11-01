/*
 * Decompiled with CFR 0.152.
 */
package com.legit.client.gui;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class NotificationManager {
    private final List<Toast> toasts = new CopyOnWriteArrayList<Toast>();

    public void push(String title, String message, boolean positive) {
        this.toasts.add(new Toast(title, message, positive, 2600L));
        if (this.toasts.size() > 5) {
            this.toasts.remove(0);
        }
    }

    public List<Toast> getActiveToasts() {
        long now = System.currentTimeMillis();
        this.toasts.removeIf(t -> now - t.createdAt >= t.durationMs);
        return this.toasts;
    }

    public static class Toast {
        public final String title;
        public final String message;
        public final boolean positive;
        public final long createdAt;
        public final long durationMs;

        public Toast(String title, String message, boolean positive, long durationMs) {
            this.title = title;
            this.message = message;
            this.positive = positive;
            this.createdAt = System.currentTimeMillis();
            this.durationMs = durationMs;
        }

        public float getLifeProgress(long now) {
            return Math.min(1.0f, (float)(now - this.createdAt) / (float)this.durationMs);
        }
    }
}

