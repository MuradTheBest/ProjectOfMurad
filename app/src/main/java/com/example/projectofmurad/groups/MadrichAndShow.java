package com.example.projectofmurad.groups;

public class MadrichAndShow {

    private boolean madrich;
    private int show;

    public MadrichAndShow(){}

    public MadrichAndShow(boolean madrich, int show) {
        this.madrich = madrich;
        this.show = show;
    }

    public boolean isMadrich() {
        return madrich;
    }

    public void setMadrich(boolean madrich) {
        this.madrich = madrich;
    }

    public int getShow() {
        return show;
    }

    public void setShow(int show) {
        this.show = show;
    }
}
