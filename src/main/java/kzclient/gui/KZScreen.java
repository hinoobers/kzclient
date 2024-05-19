package kzclient.gui;

import kzclient.KZClient;
import kzclient.gui.mod.CreateModScreen;
import kzclient.gui.mod.ManageModsScreen;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import java.io.IOException;

public class KZScreen extends GuiScreen {

    public static GuiScreen parent;

    public KZScreen(GuiScreen parent) {
        KZScreen.parent = parent;
    }

    @Override
    public void initGui() {
        super.initGui();

        // add button to bottom left called "Back"
        this.buttonList.add(new GuiButton(0, 10, this.height - 30, 100, 20, "Back"));

        // add more buttons like "create mod"
        this.buttonList.add(new GuiButton(1, this.width / 2 - 100, 100, 200, 20, "Create Mod"));
        this.buttonList.add(new GuiButton(2, this.width / 2 - 100, 130, 200, 20, "Manage Mods"));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawDefaultBackground();

        // add some text about the client, version, etc
        this.drawCenteredString(this.fontRendererObj, "KZClient", this.width / 2, 20, 16777215);
        this.drawCenteredString(this.fontRendererObj, "Version 1.0", this.width / 2, 40, 16777215);
        // how many mods are loaded
        this.drawCenteredString(this.fontRendererObj, "Loaded " + KZClient.getInstance().getModManager().getMods().size() +" mods", this.width / 2, 60, 16777215);


        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if(button.id == 0) {
            this.mc.displayGuiScreen(this.parent);
        } else if(button.id == 1) {
            // create mod button
            this.mc.displayGuiScreen(new CreateModScreen(this));
        } else if(button.id == 2) {
            // manage mods button
            this.mc.displayGuiScreen(new ManageModsScreen(this));
        }

        super.actionPerformed(button);
    }
}
