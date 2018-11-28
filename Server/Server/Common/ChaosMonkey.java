package Server.Common;

import java.util.*;
import java.io.Serializable;

public class ChaosMonkey implements Serializable {
   private HashSet<CrashModes> activeModes = new HashSet<>();

   public void enableCrashMode(CrashModes mode) {
       this.activeModes.add(mode);
   }

   public void disableAll() {
       this.activeModes.clear();
   }

   public boolean checkIfEnabled(CrashModes mode) {
       return this.activeModes.contains(mode);
   }

   public void crashIfEnabled(CrashModes mode) { 
       if (this.activeModes.contains(mode)) {
           System.out.println("Crashing due to " + mode.name());
           System.exit(1);
       }
   }
}
