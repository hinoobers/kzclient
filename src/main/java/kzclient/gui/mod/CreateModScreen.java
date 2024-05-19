package kzclient.gui.mod;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;

import java.awt.*;
import java.io.IOException;

public class CreateModScreen extends GuiScreen {

    private GuiScreen parent;

    public CreateModScreen(GuiScreen parent) {
        this.parent = parent;
    }

    // text fields like mod name, mod author, etc
    // then continue button, and back button

    private GuiTextField nameField, authorField, versionField, descriptionField;
    private String error;

    public static String NAME, AUTHOR, VERSION, DESCRIPTION;


    @Override
    public void initGui() {
        super.initGui();

        // add button to bottom left called "Back"
        this.buttonList.add(new GuiButton(0, 10, this.height - 30, 100, 20, "Back"));

        // add text fields for mod name, mod author, etc
        this.nameField = new GuiTextField(0, this.fontRendererObj, this.width / 2 - 100, 100, 200, 20);
        this.nameField.setFocused(true);
        this.nameField.setCanLoseFocus(true);
        this.nameField.setMaxStringLength(32);

        this.authorField = new GuiTextField(1, this.fontRendererObj, this.width / 2 - 100, 130, 200, 20);
        this.authorField.setMaxStringLength(32);

        this.versionField = new GuiTextField(2, this.fontRendererObj, this.width / 2 - 100, 160, 200, 20);
        this.versionField.setMaxStringLength(32);

        this.descriptionField = new GuiTextField(3, this.fontRendererObj, this.width / 2 - 100, 190, 200, 20);
        this.descriptionField.setMaxStringLength(128);



        // add button to bottom right called "Continue"
        this.buttonList.add(new GuiButton(1, this.width - 110, this.height - 30, 100, 20, "Continue"));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawDefaultBackground();

        // add some text about the client, version, etc
        this.drawCenteredString(this.fontRendererObj, "KZClient", this.width / 2, 20, 16777215);
        this.drawCenteredString(this.fontRendererObj, "Version 1.0", this.width / 2, 40, 16777215);
        // how many mods are loaded
        this.drawCenteredString(this.fontRendererObj, "Loaded 0 mods", this.width / 2, 60, 16777215);

        if(error != null) {
            this.drawCenteredString(this.fontRendererObj, error, this.width / 2, 80, Color.RED.getRGB());
        }

        // draw text fields
        this.nameField.drawTextBox();
        this.authorField.drawTextBox();
        this.versionField.drawTextBox();
        this.descriptionField.drawTextBox();

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        this.nameField.mouseClicked(mouseX, mouseY, mouseButton);
        this.authorField.mouseClicked(mouseX, mouseY, mouseButton);
        this.versionField.mouseClicked(mouseX, mouseY, mouseButton);
        this.descriptionField.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);

        this.nameField.textboxKeyTyped(typedChar, keyCode);
        this.authorField.textboxKeyTyped(typedChar, keyCode);
        this.versionField.textboxKeyTyped(typedChar, keyCode);
        this.descriptionField.textboxKeyTyped(typedChar, keyCode);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if(button.id == 0) {
            this.mc.displayGuiScreen(this.parent);
        } else if(button.id == 1) {
            // continue button
            errorize();
            if(error == null) {
                NAME = nameField.getText();
                AUTHOR = authorField.getText();
                VERSION = versionField.getText();
                DESCRIPTION = descriptionField.getText();
                this.mc.displayGuiScreen(new SketchModScreen(this));
            }
        }

        super.actionPerformed(button);
    }

    private void errorize() {
        if(nameField.getText().isEmpty()) {
            error = "Name cannot be empty";
        } else if(authorField.getText().isEmpty()) {
            error = "Author cannot be empty";
        } else if(versionField.getText().isEmpty()) {
            error = "Version cannot be empty";
        } else if(descriptionField.getText().isEmpty()) {
            error = "Description cannot be empty";
        } else {
            error = null;
        }
    }
}
