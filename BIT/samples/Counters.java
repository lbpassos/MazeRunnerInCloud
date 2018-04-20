
public class Counters{ //Class para contar 
    private int method_count;
    private int instruction_count;
    
    public Counters(){
        method_count = 0;
        instruction_count = 0;
    }
    public void incMethod(){ ++method_count; }
    public void incInstruction(){ ++instruction_count; }
    public int getMethod(){ return method_count; }
    public int getInstruction(){ return instruction_count; }
}
