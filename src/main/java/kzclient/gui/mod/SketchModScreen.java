package kzclient.gui.mod;

import kzclient.KZClient;
import kzclient.gui.KZScreen;
import kzclient.mod.Mod;
import kzclient.mod.functions.ModFunction;
import kzclient.mod.functions.impl.IsSprinting;
import kzclient.mod.functions.impl.StartSprinting;
import kzclient.mod.functions.impl.event.PreTick;
import kzclient.mod.functions.impl.SayMessage;
import kzclient.mod.functions.impl.event.StartSprintingEvent;
import kzclient.mod.functions.point.FromPoint;
import kzclient.mod.functions.point.Point;
import kzclient.mod.functions.point.ToPoint;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.MathHelper;
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
    private int panX = 0, panY = 0;
    private int lastMouseX, lastMouseY;
    private boolean panning, draggingPoint;
    private Point draggingFrom;
    private ModFunction draggingFunction;
    private boolean creating = false;

    public SketchModScreen(GuiScreen parent) {
        this.parent = parent;
        creating = true;
    }

    public SketchModScreen(GuiScreen parent, Mod mod) {
        this.parent = parent;
        this.currentFunctions.addAll(mod.getFunctions());
        this.editingMod = mod;
    }

    @Override
    public void initGui() {
        super.initGui();
        this.buttonList.add(new GuiButton(0, 10, this.height - 30, 100, 20, "Back"));
        // finish button
        this.buttonList.add(new GuiButton(1, this.width - 110, this.height - 30, 100, 20, "Finish"));
        this.functionList = new ModFunctionElementList(this.mc, 135, this.height, 0, this.height, 110);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawDefaultBackground();

        int adjustedMouseX = mouseX - panX;
        int adjustedMouseY = mouseY - panY;

        functionList.drawScreen(adjustedMouseX, adjustedMouseY, partialTicks);

        if (dragging != null) {
            dragging.draw(this, adjustedMouseX - offsetX, adjustedMouseY - offsetY, adjustedMouseX, adjustedMouseY);
        }

        for (ModFunction function : currentFunctions) {
            if (function.equals(dragging)) continue;

            for (FromPoint from : function.from) {
                for (Point to : from.connections) {
                    drawConnection(from, to);
                }
            }
            for (ToPoint to : function.to) {
                for (Point from : to.connections) {
                    drawConnection(from, to);
                }
            }

            function.draw(this, function.x + panX, function.y + panY, adjustedMouseX, adjustedMouseY);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void drawConnection(Point from, Point to) {
        for (int i = 0; i < 10; i++) {
            int x = from.x + (to.x - from.x) / 10 * i;
            int y = from.y + (to.y - from.y) / 10 * i;
            Gui.drawRect(x, y, x + 2, y + 2, Color.RED.getRGB());
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        if(panning) {
            panning = false;
            panX = 0;
            panY = 0;
        }
        if (dragging != null) {
            dragging.x = mouseX - offsetX - panX;
            dragging.y = mouseY - offsetY - panY;
            dragging = null;
        } else if (draggingPoint) {
            for (ModFunction function : currentFunctions) {
                Point pointAtMouse = function.getPointAt(mouseX, mouseY);
                if (pointAtMouse != null) {
                    if (draggingFrom instanceof FromPoint) {
                        ((FromPoint) draggingFrom).connections.add(pointAtMouse);
                    } else if (draggingFrom instanceof ToPoint) {
                        ((ToPoint) draggingFrom).connections.add(pointAtMouse);
                    }
                    draggingPoint = false;
                    break;
                }
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

        boolean clicked = false;
        int adjustedMouseX = mouseX - panX;
        int adjustedMouseY = mouseY - panY;
        for (ModFunction function : currentFunctions) {
            function.mouseClicked(adjustedMouseX, adjustedMouseY, mouseButton);

            Point pointAtMouse = function.getPointAt(mouseX, mouseY);
            if (pointAtMouse != null) {
                draggingFrom = pointAtMouse;
                draggingFunction = function;
                draggingPoint = true;
                clicked = true;
                break;
            }
            if (function.isMouseOver(adjustedMouseX, adjustedMouseY)) {
                dragging = function;
                offsetX = mouseX - function.x - panX;
                offsetY = mouseY - function.y - panY;
                clicked = true;
                break;
            }
        }
        if(!clicked) {
            panning = true;
            draggingPoint = false;
        }
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        if (panning) {
            panX = mouseX - lastMouseX;
            panY = mouseY - lastMouseY;

            lastMouseX = mouseX;
            lastMouseY = mouseY;
        } else if (dragging != null) {
            dragging.x = mouseX - offsetX - panX;
            dragging.y = mouseY - offsetY - panY;
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        functionList.handleMouseInput();
        super.handleMouseInput();
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        functionList.actionPerformed(button);
        if (button.id == 0) {
            this.mc.displayGuiScreen(new CreateModScreen(this));
        } else if(button.id == 1) {
            if(creating) {
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

        private final List<ModFunction> functions = Arrays.asList(new StartSprintingEvent(), new SayMessage(), new IsSprinting(), new PreTick(), new StartSprinting());

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
            function.x = 250 - panX;
            function.y = 100 - panY;
            function.init();
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
            functions.get(entryID).init();
            functions.get(entryID).draw(SketchModScreen.this, 15, p_180791_3_, mouseXIn, p_180791_4_);
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
                    int k = this.mouseY - this.top - this.headerPadding + (int)this.amountScrolled - 4;
                    int l = k / this.slotHeight;

                    if (l < this.getSize() && this.mouseX >= i && this.mouseX <= j && l >= 0 && k >= 0) {
                        this.elementClicked(l, false, this.mouseX, this.mouseY);
                        this.selectedElement = l;
                    } else if (this.mouseX >= i && this.mouseX <= j && k < 0) {
                        this.func_148132_a(this.mouseX - i, this.mouseY - this.top + (int)this.amountScrolled - 4);
                    }
                }

                int scrollAmount = Mouse.getEventDWheel();
                if (scrollAmount != 0) {
                    this.amountScrolled += (float)(scrollAmount > 0 ? -1 : 1) * this.slotHeight / 2;
                }
            }
        }
    }
}
