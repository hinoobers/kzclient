package kzclient.mod.function;

import kzclient.KZClient;
import kzclient.mod.Mod;
import kzclient.mod.function.slot.InputSlot;
import kzclient.mod.function.slot.OutputSlot;
import org.bspfsystems.yamlconfiguration.configuration.ConfigurationSection;

public class ConnectionLine {


    // dynamic drawing because fuck storing coordinates

    private int inputFunctionId, outputFunctionId;
    public InputSlot input;
    public OutputSlot output;


    public void save(ConfigurationSection section) {
        if(input == null || output == null) {
            KZClient.getLogger().severe("Failed to save a connection line (OUTPUT OR INPUT IS NULL)");
            return;
        }
        section.set("input", input.uniqueId);
        section.set("input_func", input.parentFunction.getUniqueId());
        section.set("output", output.uniqueId);
        section.set("output_func", output.parentFunction.getUniqueId());
    }

    public void load(Mod mod, ConfigurationSection section) {
        this.inputFunctionId = section.getInt("input_func");
        this.outputFunctionId = section.getInt("output_func");

        for(ModFunction func : mod.getFunctions()) {
            if(func.getUniqueId() == outputFunctionId) {
                for(OutputSlot output : func.getOutputs()) {
                    if(output.uniqueId == section.getInt("output")) {
                        System.out.println("O " + output);
                        this.output = output;
                    }
                }
            } else if(func.getUniqueId() == inputFunctionId) {
                for(InputSlot input : func.getInputs()) {
                    if(input.uniqueId == section.getInt("input")) {
                        this.input = input;
                    }
                }
            }
        }
    }
}
