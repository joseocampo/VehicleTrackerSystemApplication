package joseocampo.VehicleTrackerSystemApp.com;
public class ExpandObjects {
    private String atributo01;
    private String atributo02;
    private int NumDibujo1,NumDibujo2;
    public ExpandObjects(String atributo01, String atributo02, int NumDibujo1,int NumDibujo2 ){
        super();
        this.atributo01 = atributo01;
        this.atributo02 = atributo02;
        this.NumDibujo1 = NumDibujo1;
        this.NumDibujo2 = NumDibujo2;

    }
    public String getAtributo01() {
        return atributo01;
    }
    public String getAtributo02() {
        return atributo02;
    }
    public int getNumDibujo1() {
        return NumDibujo1;
    }
    public int getNumDibujo2() {
        return NumDibujo2;
    }

}
