package kzclient.mod;

import kzclient.KZClient;
import kzclient.event.Listener;
import kzclient.event.impl.ChatMessageEvent;
import kzclient.event.impl.PreMotionEvent;
import kzclient.event.impl.SprintChangeEvent;
import kzclient.mod.function.FunctionManager;
import kzclient.mod.function.ModFunction;
import kzclient.mod.function.ObjectRegistry;
import kzclient.mod.function.impl.event.ChatMessageFunction;
import kzclient.mod.function.impl.event.PostMotionFunction;


public class ModListeners {

    @Listener
    public void onSprintStart(SprintChangeEvent event) {
        for(Mod mod : KZClient.getInstance().getModManager().getMods()) {

        }
    }

    @Listener
    public void onChatMessage(ChatMessageEvent event) {
        System.out.println("EVENT CALLED " + event.getMessage());
        for(Mod mod : KZClient.getInstance().getModManager().getMods()) {
            for(ModFunction function : mod.getFunctions()) {
                System.out.println(function.getClass().getSimpleName());
                if(function.getId() == FunctionManager.getFunctionId(ChatMessageFunction.class)) {
                    System.out.println("FIRE");
                    function.fire(ObjectRegistry.createWith(event.getMessage()));
                }
            }
        }
    }

    @Listener
    public void onPre(PreMotionEvent event) {
        for(Mod mod : KZClient.getInstance().getModManager().getMods()) {
            for(ModFunction function : mod.getFunctions()) {
                if(function.getId() == FunctionManager.getFunctionId(PostMotionFunction.class)) {
                    function.fire(null);
                }
            }
        }
    }
}
