package foby.client.ui.clickgui.setting.settings;


import foby.client.ui.clickgui.setting.Setting;

import java.util.ArrayList;
import java.util.List;

public class ListSetting extends Setting {

    private ArrayList<BooleanSetting> booleanSliders;

    public ArrayList<BooleanSetting> getBooleanSliders() {
        return booleanSliders;
    }

    public ListSetting(String name, List<BooleanSetting> booleanSliders) {
        this.booleanSliders = new ArrayList<>();
        this.setName(name);
        this.booleanSliders.addAll(booleanSliders);
        this.setVisibility(() -> true);
    }

    public BooleanSetting getByNameIgnoreCase(String name) {

        for(BooleanSetting slider : this.booleanSliders){
            if(slider.getName().equalsIgnoreCase(name)){
                return slider;
            }
        }

        return null;
    }

    public BooleanSetting getByName(String name) {

        for(BooleanSetting slider : this.booleanSliders){
            if(slider.getName().equals(name)){
                return slider;
            }
        }

        return null;
    }

    public BooleanSetting getSliderById(int id) {
        return this.booleanSliders.get(id);
    }
}
