package kzclient.gui.mod;

import kzclient.KZClient;
import kzclient.mod.Mod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumChatFormatting;

import java.io.IOException;

public class ManageModsScreen extends GuiScreen
{
    private GuiScreen parent;
    public SlotList list;

    public ManageModsScreen(GuiScreen parent)
    {
        this.parent = parent;
    }

    @Override
    public void initGui()
    {
        super.initGui();
        buttonList.add(new GuiButton(1, width / 2 - 100, height - 25, 200, 20, "Back"));
        buttonList.add(new GuiButton(2, width / 2 - 180, height - 25, 75, 20, "Credits"));
        list = new SlotList(mc, width, height, 32, height - 32, 10);
    }

    @Override
    protected void actionPerformed(GuiButton guiButton) throws IOException
    {
        list.actionPerformed(guiButton);

        if (guiButton.id == 1)
        {
            mc.displayGuiScreen(parent);
        }

    }

    @Override
    public void handleMouseInput() throws IOException
    {
        list.handleMouseInput();
        super.handleMouseInput();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        list.drawScreen(mouseX, mouseY, partialTicks);
        GlStateManager.pushMatrix();
        GlStateManager.scale(2.0, 2.0, 2.0);
        String title = EnumChatFormatting.BOLD + "Mods";
        drawString(this.fontRendererObj, title, (this.width - (this.fontRendererObj.getStringWidth(title) * 2)) / 4, 5, -1);
        GlStateManager.popMatrix();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    class SlotList extends GuiSlot
    {
        public SlotList(Minecraft mc, int width, int height, int top, int bottom, int slotHeight)
        {
            super(mc, width, height, top + 30, bottom, 18);
        }

        @Override
        protected int getSize()
        {
            return KZClient.getInstance().getModManager().getMods().size();
        }

        @Override
        protected void elementClicked(int i, boolean b, int i1, int i2)
        {
            Mod mod = (Mod) KZClient.getInstance().getModManager().getMods().toArray()[i];
            if(mod != null) {
                mc.displayGuiScreen(new ManageModScreen(parent, mod));
            }
        }

        @Override
        protected boolean isSelected(int i)
        {
            return false;
        }

        @Override
        protected void drawBackground()
        {
            drawDefaultBackground();
        }

        @Override
        protected void drawSlot(int i, int i1, int i2, int i3, int i4, int i5)
        {
            Mod mod = (Mod) KZClient.getInstance().getModManager().getMods().toArray()[i];
            drawCenteredString(mc.fontRendererObj, mod.getName().isEmpty() ? "MISSING_MOD_NAME" : mod.getName(), width / 2, i2 + 2, -1);
        }
    }
}