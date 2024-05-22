package kzclient.event.impl;

import kzclient.event.Event;
import net.minecraft.util.ChatComponentText;

public class ChatMessageEvent extends Event {

    private String message;

    public ChatMessageEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
