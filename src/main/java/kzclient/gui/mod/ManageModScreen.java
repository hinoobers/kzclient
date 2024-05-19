package kzclient.gui.mod;

import kzclient.mod.Mod;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import java.io.IOException;

public class ManageModScreen extends GuiScreen {

    private GuiScreen parent;
    private Mod mod;

    public ManageModScreen(GuiScreen parent, Mod mod) {
        this.parent = parent;
        this.mod = mod;
    }


    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);

        // display basic information
        this.drawCenteredString(this.fontRendererObj, "Mod: " + mod.getName(), this.width / 2, 20, 16777215);
        this.drawCenteredString(this.fontRendererObj, "Author: " + mod.getAuthor(), this.width / 2, 40, 16777215);
        this.drawCenteredString(this.fontRendererObj, "Version: " + mod.getVersion(), this.width / 2, 60, 16777215);
        this.drawCenteredString(this.fontRendererObj, "Description: " + mod.getDescription(), this.width / 2, 80, 16777215);

    }

    @Override
    public void initGui() {
        super.initGui();

        // add button to bottom left called "Back"
        this.buttonList.add(new GuiButton(0, 10, this.height - 30, 100, 20, "Back"));
        // add button to bottom right called "Edit functionality"
        this.buttonList.add(new GuiButton(1, this.width - 110, this.height - 30, 100, 20, "Edit functionality"));
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);

        if(button.id == 0) {
            this.mc.displayGuiScreen(this.parent);
        } else if(button.id == 1) {
            // edit functionality button
            this.mc.displayGuiScreen(new SketchModScreen(this, mod));
        }
    }
}
