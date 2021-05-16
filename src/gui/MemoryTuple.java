package gui;

/**
 *
 * @author edlcorrea
 */

public class MemoryTuple {
    private String tupleIndex;
    private String tupleByte;
    
    MemoryTuple(String memIndex, String memByte) {
        this.tupleIndex = memIndex;
        this.tupleByte = memByte;
    }
    
    MemoryTuple(String memIndex) {
        this.tupleIndex = memIndex;
        this.tupleByte = "00000000";
    }
    
    public void setTupleIndex(String memIndex) {
        this.tupleIndex = memIndex;
    }
    
    public void setTupleByte(String memByte) {
        this.tupleByte = memByte;
    }
    
    public String getTupleIndex() {
        return this.tupleIndex;
    }
    
    public String getTupleByte() {
        return this.tupleByte;
    }
    
}
