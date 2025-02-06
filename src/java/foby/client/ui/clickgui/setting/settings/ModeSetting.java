package foby.client.ui.clickgui.setting.settings;

import foby.client.ui.clickgui.setting.Setting;

import java.util.ArrayList;
import java.util.function.Supplier;

public class ModeSetting extends Setting {

    public ArrayList<String> values = new ArrayList<>();

    private int index;

    private String mode;

    public ModeSetting(String name, Supplier<Boolean> visibility, String... values){
        this.setName(name);
        this.setVisibility(visibility);
        for (String value : values) {
            this.values.add(value);
        }
        this.index = 0;
        this.mode = this.values.get(index);
    }

    public ModeSetting(String... values){
        this.setName("Mode");
        this.setVisibility(() -> true);
        for (String value : values) {
            this.values.add(value);
        }
        this.index = 0;
        this.mode = this.values.get(index);
    }

    public ModeSetting(Supplier<Boolean> visibility, String... values){
        this.setName("Mode");
        this.setVisibility(visibility);
        for (String value : values) {
            this.values.add(value);
        }
        this.index = 0;
        this.mode = this.values.get(index);
    }


    public void updateValue(){
        this.index++;
        if(index == values.size()) index = 0;
        this.mode = values.get(index);
    }

    public boolean is(String value){
        return this.mode.equals(value);
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getMode() {
        return mode;
    }
}
