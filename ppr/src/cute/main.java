package cute;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

public class main implements Runnable {

	public final int FPS = 60;

	public boolean running;

	private int texture;
	private int background;
	
	private InputHandler input;

	private int wid = 800;
	private int hie = 600;
	
	private int x = 20;
	private int y = 20;
	
	private int rightLimit = wid - 30;
	private int leftLimit = 30;
	private int upLimit = 30;
	private int downLimit = hie - 30;
	
	private int base = 30;
	
	private int a = base; // x1 top left
	private int aa = base; // y1
	
	private int b = base; // x2 bottom left
	private int bb = base + 20; // y2
	
	private int c = base + 20; // x3 bottom right
	private int cc = base + 20; // y3
	
	private int d = base + 20; // x4 top right
	private int dd = base; // y4
	
	private int beep = 0;

	public static void main(String arg[]) {
		main t = new main();
		t.start();
	}

	public void start() {
		this.running = true;
		new Thread(this).start();
	}

	public void stop() {
		this.running = false;
	}

	public void initProg() {
		texture = this.loadTexture("img.png");
		background = this.loadTexture("bg.png");
		input = new InputHandler();
		input.direction = 0;
	}

	private int loadTexture(String text) {
		int tex;
		BufferedImage img = null;

		try {
			img = ImageIO.read(main.class.getResourceAsStream(text));
		} catch (IOException e) {
			e.printStackTrace();
			return 0;
		}

		int width = img.getWidth();
		int height = img.getHeight();
		int[] pixels = img.getRGB(0, 0, width, height, null, 0, width);

		ByteBuffer b = BufferUtils.createByteBuffer((width * height) * 3);
		tex = GL11.glGenTextures();

		for (int i = 0; i < pixels.length; i++) {
			byte rr = (byte) ((pixels[i] >> 16) & 0xff);
			byte bb = (byte) ((pixels[i]) & 0xff);
			byte gg = (byte) ((pixels[i] >> 8) & 0xff);

			b.put(rr);
			b.put(bb);
			b.put(gg);
		}

		b.flip();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, tex);

		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER,
				GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER,
				GL11.GL_NEAREST);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, width, height, 0,
				GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, b);

		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

		return tex;
	}

	public void background() {
		GL11.glBindTexture( GL11.GL_TEXTURE_2D, background ); 
	    GL11.glBegin(GL11.GL_QUADS);
	    	GL11.glTexCoord2d(0.0,0.0); GL11.glVertex2f(0, 0);
	    	GL11.glTexCoord2d(1.0,0.0); GL11.glVertex2f(wid, 0);
	    	GL11.glTexCoord2d(1.0,1.0); GL11.glVertex2f(wid, hie);
	    	GL11.glTexCoord2d(0.0,1.0); GL11.glVertex2f(0, hie);
	    GL11.glEnd();
	}
	
	public void initDisplay() {
		try {
			Display.setResizable(false);
			Display.setDisplayMode(new DisplayMode(wid, hie));
			Display.create();
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
	}

	public void initOpenGL() {
		GL11.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0, Display.getWidth(), Display.getHeight(), 0.0, -1.0, 1.0);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}

	public void render() {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
		GL11.glPushMatrix();

		GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.background);

		GL11.glBegin(GL11.GL_QUADS);
			GL11.glTexCoord2f(0, 0);
			GL11.glVertex2f(0, 0);
			GL11.glTexCoord2f(1, 0);
			GL11.glVertex2f(wid, 0);
			GL11.glTexCoord2f(1, 1);
			GL11.glVertex2f(wid, hie);
			GL11.glTexCoord2f(0, 1);
			GL11.glVertex2f(0, hie);
		GL11.glEnd();
		
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.texture);

		GL11.glBegin(GL11.GL_QUADS);
			GL11.glTexCoord2f(0, 0);
			GL11.glVertex2f(a, aa);
			GL11.glTexCoord2f(1, 0);
			GL11.glVertex2f(b, bb);
			GL11.glTexCoord2f(1, 1);
			GL11.glVertex2f(c, cc);
			GL11.glTexCoord2f(0, 1);
			GL11.glVertex2f(d, dd);
		GL11.glEnd();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		GL11.glPopMatrix();
	}

	
	public void tickUp() { // up
		if ((aa != upLimit) && (dd != upLimit)) {
			
			aa = aa - y; // y-axis
			bb = bb - y; // y-axis
			cc = cc - y; // y-axis
			dd = dd - y; // y-axis
					
		} else if ((aa == upLimit) || (dd == upLimit)) {
			
			aa = aa; // y-axis
			bb = bb; // y-axis
			cc = cc; // y-axis
			dd = dd; // y-axis
		}
	}
	
	public void tickDown() { // down
		if ((bb != downLimit) && (cc != downLimit)) {
			
			aa = aa + y; // y-axis
			bb = bb + y; // y-axis
			cc = cc + y; // y-axis
			dd = dd + y; // y-axis
			
		} else if ((bb == downLimit) || (cc == downLimit)) {
			
			aa = aa; // y-axis
			bb = bb; // y-axis
			cc = cc; // y-axis
			dd = dd; // y-axis
		}
	}
	
	public void tickLeft() { // left
		if ((a != leftLimit) && (d != leftLimit)) {
			
			a = a - x; // x-axis
			b = b - x; // x-axis
			c = c - x; // x-axis
			d = d - x; // x-axis
			
		} else if ((a == leftLimit) || (d == leftLimit)) {
			
			a = a; // x-axis
			b = b; // x-axis
			c = c; // x-axis
			d = d; // x-axis
		}
	}
	
	public void tickRight() { // right
		if ((c != rightLimit) && (d != rightLimit)) {
			
			a = a + x; // x-axis
			b = b + x; // x-axis
			c = c + x; // x-axis
			d = d + x; // x-axis
			
		} else if ((c == rightLimit) && (d == rightLimit)) {
			
			a = a; // x-axis
			b = b; // x-axis
			c = c; // x-axis
			d = d; // x-axis
		}
	}
	
	
	public void resetDirection(){
		beep = 0;
		input.direction = 0;
		input.tickDirection();
	}
	
	public void tick(){
		
		input.tickDirection();
		
		beep = input.direction;
		
		if (beep == 1) { // up
			this.tickUp();
			this.resetDirection();
		}
		else if (beep == 2) { // down
			this.tickDown();
			this.resetDirection();
		}
		else if (beep == 3) { // left
			this.tickLeft();
			this.resetDirection();
		}
		else if (beep == 4) { // right
			this.tickRight();
			this.resetDirection();
		}
	}

	@Override
	public void run() {
		this.initDisplay();
		this.initOpenGL();
		this.initProg();

		while (this.running) {
			
			this.tick();
			
			this.render();
			
			if (Display.isCloseRequested()) {
				stop();
			}
			Display.sync(FPS);
			Display.update();
		}
	}
}