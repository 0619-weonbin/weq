// CloTag.java (확인용)
package com.example.myapplication123;

public class CloTag {
    public String CloUrl;
    public String TmpTag;
    public String StyTag;
    public String TypeTag; // type
    public String NameTag; // detailed_type
    public int preference;

    public CloTag(String CloUrl, String TmpTag, String StyTag, String TypeTag, String NameTag, int preference){
        this.CloUrl = CloUrl;
        this.NameTag = NameTag;
        this.StyTag = StyTag;
        this.TmpTag = TmpTag;
        this.TypeTag = TypeTag;
        this.preference = preference;
    }

    // ★★★ 아래 getter들이 반드시 있어야 합니다 ★★★
    public String getImageUrl() { return CloUrl; }
    public String getType() { return TypeTag; } // filterByType에서 사용
    public String getDetailedType() { return NameTag; }
    public int getPreference() { return preference; }
}