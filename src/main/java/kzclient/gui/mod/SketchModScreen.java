package kzclient.gui.mod;

import kzclient.KZClient;
import kzclient.gui.KZScreen;
import kzclient.mod.Mod;
import kzclient.mod.function.ConnectionLine;
import kzclient.mod.function.ModFunction;
import kzclient.mod.function.impl.SayMessageFunction;
import kzclient.mod.function.impl.TestFunction;
import kzclient.mod.function.impl.event.ChatMessageFunction;
import kzclient.mod.function.impl.event.PostMotionFunction;
import kzclient.mod.function.impl.string.ContainsFunction;
import kzclient.mod.function.slot.InputSlot;
import kzclient.mod.function.slot.OutputSlot;
import kzclient.mod.function.slot.Slot;
import kzclient.util.MathHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SketchModScreen extends GuiScreen {

    private final GuiScreen parent;
    private Mod editingMod = null; // NULL if creating
    private ModFunctionElementList functionList;
    private final List<ModFunction> currentFunctions = new ArrayList<>();
    private ModFunction dragging;
    private int offsetX, offsetY;
    private int lastMouseX, lastMouseY;
    private boolean draggingPoint;
    private OutputSlot draggingFrom;
    private ModFunction draggingFunction;
    private boolean creating = false;

    private final int browserWidth = 135;

    private int panOffsetX = 0;
    private int panOffsetY = 0;
    private int lastPanX = 0;
    private int lastPanY = 0;
    private boolean isPanning = false;

    public SketchModScreen(GuiScreen parent) {
        this.parent = parent;
        creating = true;
    }

    public SketchModScreen(GuiScreen parent, Mod mod) {
        this.parent = parent;
        this.currentFunctions.addAll(mod.getFunctions());
        this.editingMod = mod;

        System.out.println("Sketch screen, functions=" + currentFunctions.size());
    }

    @Override
    public void initGui() {
        super.initGui();
        this.buttonList.add(new GuiButton(0, 10, this.height - 30, 100, 20, "Back"));
        // finish button
        this.buttonList.add(new GuiButton(1, this.width - 110, this.height - 30, 100, 20, "Finish"));
        this.functionList = new ModFunctionElementList(this.mc, this.browserWidth, this.height, 0, this.height, 110);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawDefaultBackground();

        functionList.drawScreen(mouseX, mouseY, partialTicks);

        GlStateManager.pushMatrix();
        GlStateManager.translate(panOffsetX, panOffsetY, 0);

        if (dragging != null) {
            dragging.x = mouseX - offsetX - panOffsetX;
            dragging.y = mouseY - offsetY - panOffsetY;
        }

        for (ModFunction function : currentFunctions) {
            function.draw(mouseX - panOffsetX, mouseY - panOffsetY, function.x + panOffsetX, function.y + panOffsetY);

            for (OutputSlot slot : function.getOutputs()) {
                for (ConnectionLine line : slot.getLines()) {
                    if (line.input == null || line.output == null) {
                        KZClient.getLogger().warning(line.input + " / " + line.output);
                        continue;
                    }
                    drawConnection(line.input, line.output);
                }
            }
        }

        GlStateManager.popMatrix();

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void drawConnection(InputSlot from, OutputSlot to) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.disableAlpha();
        GlStateManager.shadeModel(7425);
        worldrenderer.begin(3, DefaultVertexFormats.POSITION_COLOR);

        double steps = 20.0;
        for (int i = 0; i <= steps; i++) {
            double t = i / steps;
            int x = (int) MathHelper.lerp(from.x, to.x, t);
            int y = (int) MathHelper.lerp(from.y, to.y, t);
            worldrenderer.pos(x, y, 0).color(255, 0, 0, 255).endVertex();
        }

        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.enableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
    }


    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        if (isPanning) {
            isPanning = false;
            return;
        }

        if (dragging != null) {
            dragging.x = mouseX - offsetX - panOffsetX;
            dragging.y = mouseY - offsetY - panOffsetY;
            dragging = null;
        } else if (draggingPoint) {
            for (ModFunction function : currentFunctions) {
                Slot pointAtMouse = function.getSlot(mouseX - panOffsetX, mouseY - panOffsetY);
                if (pointAtMouse == null) continue;

                if (pointAtMouse instanceof InputSlot) {
                    draggingFrom.connectInput((InputSlot) pointAtMouse);
                } else {
                    draggingFrom.connectOutput((OutputSlot) pointAtMouse);
                }
                draggingPoint = false;
            }
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);

        for (ModFunction function : currentFunctions) {
            function.keyTyped(typedChar, keyCode);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        lastMouseX = mouseX;
        lastMouseY = mouseY;

        boolean click = false;
        for (ModFunction function : currentFunctions) {
            function.mouseClicked(mouseX, mouseY, mouseButton);

            Slot pointAtMouse = function.getSlot(mouseX - panOffsetX, mouseY - panOffsetY);
            if (pointAtMouse instanceof OutputSlot) {
                draggingFrom = (OutputSlot) pointAtMouse;
                draggingFunction = function;
                draggingPoint = true;
                click = true;
                break;
            }
            if (function.isMouseOver(mouseX - panOffsetX, mouseY - panOffsetY)) {
                dragging = function;
                offsetX = mouseX - function.x - panOffsetX;
                offsetY = mouseY - function.y - panOffsetY;
                click = true;
                break;
            }
        }

        if (!click) {
            if (mouseButton == 3 && mouseX > browserWidth) {
                System.out.println("Started panning");
                isPanning = true;
                lastPanX = mouseX;
                lastPanY = mouseY;
            }
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        functionList.handleMouseInput();
        super.handleMouseInput();

        if (isPanning) {
            int deltaX = Mouse.getEventDX();
            int deltaY = Mouse.getEventDY();
            panOffsetX += deltaX;
            panOffsetY += deltaY;
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        functionList.actionPerformed(button);
        if (button.id == 0) {
            this.mc.displayGuiScreen(new CreateModScreen(this));
        } else if (button.id == 1) {
            if (creating) {
                Mod mod = new Mod(KZClient.getInstance().getModManager().generateNewModID(), CreateModScreen.NAME, CreateModScreen.AUTHOR, CreateModScreen.VERSION, CreateModScreen.DESCRIPTION);
                mod.loadFunctions(currentFunctions);
                KZClient.getInstance().getModManager().loadMod(mod);

                mod.save();

                this.mc.displayGuiScreen(new KZScreen(KZScreen.parent));
            } else {
                editingMod.loadFunctions(currentFunctions);
                editingMod.save();
                this.mc.displayGuiScreen(new ManageModScreen(parent, editingMod));
            }
        }
    }

    class ModFunctionElementList extends GuiSlot {

        private final List<ModFunction> functions = Arrays.asList(new TestFunction(), new PostMotionFunction(), new SayMessageFunction(), new ChatMessageFunction(), new ContainsFunction());

        public ModFunctionElementList(Minecraft mcIn, int width, int height, int topIn, int bottomIn, int slotHeightIn) {
            super(mcIn, width, height, topIn, bottomIn, slotHeightIn);
        }

        @Override
        protected int getSize() {
            return functions.size();
        }

        @Override
        protected void elementClicked(int slotIndex, boolean isDoubleClick, int mouseX, int mouseY) {
            ModFunction function = functions.get(slotIndex)._copy();
            System.out.println("id=" + function.getId());
            function.x = 250;
            function.y = 100;
            function.loadSlots();
            function.prepare();
            currentFunctions.add(function);
            KZClient.getLogger().info("Added " + function.getClass().getSimpleName());
        }

        @Override
        protected boolean isSelected(int slotIndex) {
            return false;
        }

        @Override
        protected void drawBackground() {
            drawDefaultBackground();
        }

        @Override
        protected void drawSlot(int entryID, int p_180791_2_, int p_180791_3_, int p_180791_4_, int mouseXIn, int mouseYIn) {
            functions.get(entryID).drawForDisplay(mouseXIn, p_180791_4_, 15, p_180791_3_);
        }

        @Override
        public void drawScreen(int mouseXIn, int mouseYIn, float p_148128_3_) {
            if (this.field_178041_q) {
                this.mouseX = mouseXIn;
                this.mouseY = mouseYIn;
                this.drawBackground();
                int i = this.getScrollBarX();
                int j = i + 6;
                this.bindAmountScrolled();
                GlStateManager.disableLighting();
                GlStateManager.disableFog();
                Tessellator tessellator = Tessellator.getInstance();
                WorldRenderer worldrenderer = tessellator.getWorldRenderer();
                this.mc.getTextureManager().bindTexture(Gui.optionsBackground);
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                float f = 32.0F;
                worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
                worldrenderer.pos((double) this.left, (double) this.bottom, 0.0D).tex((double) ((float) this.left / f), (double) ((float) (this.bottom + (int) this.amountScrolled) / f)).color(32, 32, 32, 255).endVertex();
                worldrenderer.pos((double) this.right, (double) this.bottom, 0.0D).tex((double) ((float) this.right / f), (double) ((float) (this.bottom + (int) this.amountScrolled) / f)).color(32, 32, 32, 255).endVertex();
                worldrenderer.pos((double) this.right, (double) this.top, 0.0D).tex((double) ((float) this.right / f), (double) ((float) (this.top + (int) this.amountScrolled) / f)).color(32, 32, 32, 255).endVertex();
                worldrenderer.pos((double) this.left, (double) this.top, 0.0D).tex((double) ((float) this.left / f), (double) ((float) (this.top + (int) this.amountScrolled) / f)).color(32, 32, 32, 255).endVertex();
                tessellator.draw();
                int k = this.left + this.width / 2 - this.getListWidth() / 2 + 2;
                int l = this.top + 4 - (int) this.amountScrolled;

                if (this.hasListHeader) {
                    this.drawListHeader(k, l, tessellator);
                }

                this.drawSelectionBox(k, l, mouseXIn, mouseYIn);
                GlStateManager.disableDepth();
                int i1 = 4;
                this.overlayBackground(0, this.top, 255, 255);
                this.overlayBackground(this.bottom, this.height, 255, 255);
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
                GlStateManager.disableAlpha();
                GlStateManager.shadeModel(7425);
                GlStateManager.disableTexture2D();
                worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
                worldrenderer.pos((double) this.left, (double) (this.top + i1), 0.0D).tex(0.0D, 1.0D).color(0, 0, 0, 0).endVertex();
                worldrenderer.pos((double) this.right, (double) (this.top + i1), 0.0D).tex(1.0D, 1.0D).color(0, 0, 0, 0).endVertex();
                worldrenderer.pos((double) this.right, (double) this.top, 0.0D).tex(1.0D, 0.0D).color(0, 0, 0, 255).endVertex();
                worldrenderer.pos((double) this.left, (double) this.top, 0.0D).tex(0.0D, 0.0D).color(0, 0, 0, 255).endVertex();
                tessellator.draw();
                worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
                worldrenderer.pos((double) this.left, (double) this.bottom, 0.0D).tex(0.0D, 1.0D).color(0, 0, 0, 255).endVertex();
                worldrenderer.pos((double) this.right, (double) this.bottom, 0.0D).tex(1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
                worldrenderer.pos((double) this.right, (double) (this.bottom - i1), 0.0D).tex(1.0D, 0.0D).color(0, 0, 0, 0).endVertex();
                worldrenderer.pos((double) this.left, (double) (this.bottom - i1), 0.0D).tex(0.0D, 0.0D).color(0, 0, 0, 0).endVertex();
                tessellator.draw();

                this.func_148142_b(mouseXIn, mouseYIn);
                GlStateManager.enableTexture2D();
                GlStateManager.shadeModel(7424);
                GlStateManager.enableAlpha();
                GlStateManager.disableBlend();
            }
        }

        @Override
        public void handleMouseInput() {
            if (this.isMouseYWithinSlotBounds(this.mouseY)) {
                if (Mouse.getEventButton() == 0 && Mouse.getEventButtonState() && this.mouseY >= this.top && this.mouseY <= this.bottom) {
                    int i = (this.width - this.getListWidth()) / 2;
                    int j = (this.width + this.getListWidth()) / 2;
                    int k = this.mouseY - this.top - this.headerPadding + (int) this.amountScrolled - 4;
                    int l = k / this.slotHeight;

                    if (l < this.getSize() && this.mouseX >= i && this.mouseX <= j && l >= 0 && k >= 0) {
                        this.elementClicked(l, false, this.mouseX, this.mouseY);
                        this.selectedElement = l;
                    } else if (this.mouseX >= i && this.mouseX <= j && k < 0) {
                        this.func_148132_a(this.mouseX - i, this.mouseY - this.top + (int) this.amountScrolled - 4);
                    }
                }

                int scrollAmount = Mouse.getEventDWheel();
                if (scrollAmount != 0) {
                    this.amountScrolled += (float) (scrollAmount > 0 ? -1 : 1) * this.slotHeight / 2;
                }
            }
        }
    }
}
