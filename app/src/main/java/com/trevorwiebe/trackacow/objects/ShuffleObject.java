package com.trevorwiebe.trackacow.objects;

public class ShuffleObject {

    private int type;
    private String name;
    private String id;

    public ShuffleObject(int type, String name, String id) {
        this.type = type;
        this.name = name;
        this.id = id;
    }

    public ShuffleObject(ShuffleObject shuffleObject) {
        this.type = shuffleObject.type;
        this.name = shuffleObject.name;
        this.id = shuffleObject.id;
    }

    public ShuffleObject() {
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
