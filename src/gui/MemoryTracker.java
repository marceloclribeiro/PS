package gui;

/**
 *
 * @author edlcorrea
 */

public class MemoryTracker {
    private static int currentPosition;
    private static int size;
    private static MainViewController controller;
        
    public static void setCurrentPosition(int position) {        
        MemoryTracker.currentPosition = position;        
    };
    
    public static void setSize(int size) {
        MemoryTracker.size = size;
        controller.highlightMemory();
    };
    
    public static int getCurrentPosition() {
        return MemoryTracker.currentPosition;
    };
    
    public static void setController(MainViewController control) {
        MemoryTracker.controller = control;
    }
    
    public static int getSize() {
      return MemoryTracker.size;
    };
}
