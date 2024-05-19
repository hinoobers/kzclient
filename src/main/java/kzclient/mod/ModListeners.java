package kzclient.mod;

import kzclient.KZClient;
import kzclient.event.Listener;
import kzclient.event.impl.PreMotionEvent;
import kzclient.event.impl.SprintChangeEvent;
import kzclient.mod.functions.ModFunction;
import kzclient.mod.functions.impl.event.PreTick;
import kzclient.mod.functions.impl.event.StartSprintingEvent;

public class ModListeners {

    @Listener
    public void onSprintStart(SprintChangeEvent event) {
        for(Mod mod : KZClient.getInstance().getModManager().getMods()) {
            for(ModFunction function : mod.getFunctions()) {
                if(function instanceof StartSprintingEvent && event.isNewValue()) {
                    function.to.forEach(to -> to.connections.forEach(c -> c.parent.fire()));
                }
            }
        }
    }

    @Listener
    public void onPre(PreMotionEvent event) {
        for(Mod mod : KZClient.getInstance().getModManager().getMods()) {
            for(ModFunction function : mod.getFunctions()) {
                if(function instanceof PreTick) {
                    function.to.forEach(to -> to.connections.forEach(c -> c.parent.fire()));
                }
            }
        }
    }
}
