package cute;

import org.lwjgl.input.Keyboard;

public class InputHandler {
	
	public int direction;
	
	public void tickDirection(){
		
		while(Keyboard.next()){
			
			if(Keyboard.getEventKeyState()){
			
				if(Keyboard.getEventKey() == 200){ //up				
					System.out.println("Up");
					direction = 1;
				}
				else if(Keyboard.getEventKey() == 208){ //down
					System.out.println("Down");
					direction = 2;
				}
				else if(Keyboard.getEventKey() == 203){ //left
					System.out.println("Left");
					direction = 3;
				}
				else if(Keyboard.getEventKey() == 205){ //right
					System.out.println("Right");
					direction = 4;
				}
			}
		}
	}
	
}
