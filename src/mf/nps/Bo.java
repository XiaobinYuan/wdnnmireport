package mf.nps;

import java.util.LinkedHashMap;

public class Bo {
    private String name;
    private LinkedHashMap<String,Long> inmap=new LinkedHashMap<>();
    private LinkedHashMap<String,Long> out=new LinkedHashMap<>();
    private LinkedHashMap<String,Double> inutil=new LinkedHashMap<>();
    private LinkedHashMap<String,Double> oututil=new LinkedHashMap<>();

    public LinkedHashMap<String, Double> getInutil() {
        return inutil;
    }

    public void setInutil(LinkedHashMap<String, Double> inutil) {
        this.inutil = inutil;
    }

    public LinkedHashMap<String, Double> getOututil() {
        return oututil;
    }

    public void setOututil(LinkedHashMap<String, Double> oututil) {
        this.oututil = oututil;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LinkedHashMap<String, Long> getInmap() {
        return inmap;
    }

    public void setInmap(LinkedHashMap<String, Long> inmap) {
        this.inmap = inmap;
    }

    public LinkedHashMap<String, Long> getOut() {
        return out;
    }

    public void setOut(LinkedHashMap<String, Long> out) {
        this.out = out;
    }
}
