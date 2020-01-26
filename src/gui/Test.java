package gui;

import com.nedap.go.gui.GoGUIIntegrator;

public class Test {
	
	
	public Test() {
		GoGUIIntegrator g = new GoGUIIntegrator(false, false, 3);
		g.startGUI();
	}
	
	
 public static void main (String[] args) {
	 new Test();
 }
 
}
