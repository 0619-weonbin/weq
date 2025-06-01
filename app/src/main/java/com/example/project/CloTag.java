package com.example.project;

public class CloTag {
    public String CloUrl;
    public String TmpTag;
    public String StyTag;
    public String TypeTag;
    public String NameTag;
    public int pref;


    public CloTag(String CloUrl,String TmpTag,String StyTag,String TypeTag,String NameTag, int pref){
        this.CloUrl = CloUrl;
        this.NameTag = NameTag;
        this.StyTag = StyTag;
        this.TmpTag = TmpTag;
        this.TypeTag = TypeTag;
        this.pref = pref;

    }
}
