package pk2;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

public class main implements Runnable {

	public final int FPS = 60;

	public boolean running;

	private int texture;

	private int wid = 800;
	private int hie = 600;
	private int timeOne;
	private int timeTwo;
	private boolean timeCheckOne;
	private boolean timeCheckTwo;
	private int i = 600;
	private int z = 800;

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

	public void initDisplay() {
		try {
			Display.getAvailableDisplayModes();
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

		GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.texture);

		GL11.glTranslatef(timeTwo, timeOne, 0.0f);
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2f(0, 0);
		GL11.glVertex2f(0, 0);
		GL11.glTexCoord2f(1, 0);
		GL11.glVertex2f(100, 0);
		GL11.glTexCoord2f(1, 1);
		GL11.glVertex2f(100, 100);
		GL11.glTexCoord2f(0, 1);
		GL11.glVertex2f(0, 100);
		GL11.glEnd();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		GL11.glPopMatrix();
	}

	public void tickRight() {
		int wid = Display.getWidth();
		if (timeTwo < wid - 100) {
			timeCheckTwo = false;
			this.timeTwo++;
		} else if (timeTwo >= wid - 100) {
			timeCheckTwo = true;
		}
	}

	public void tickLeft() {
		if (timeTwo > 0) {
			timeCheckTwo = true;
			this.timeTwo--;
		} else if (timeTwo == 0) {
			timeCheckTwo = false;
		}
	}

	public void tickDown() {
		hie = Display.getHeight();
		if (timeOne < hie - 100) {
			timeCheckOne = false;
			this.timeOne++;
		} else if (timeOne >= hie - 100) {
			timeCheckOne = true;
		}
	}

	public void tickUp() {
		if (timeOne > 0) {
			timeCheckOne = true;
			this.timeOne--;
		} else if (timeOne == 0) {
			timeCheckOne = false;
		}
	}

	@Override
	public void run() {
		this.initDisplay();
		this.initOpenGL();
		this.initProg();

		while (this.running) {
			if (i != Display.getHeight()) {
				i = Display.getHeight();
			}
			if (z != Display.getWidth()) {
				z = Display.getWidth();
			}
			if (timeCheckOne == false) {
				this.tickDown();
			}
			if (timeCheckOne == true) {
				this.tickUp();
			}
			if (timeCheckTwo == false) {
				this.tickRight();
			}
			if (timeCheckTwo == true) {
				this.tickLeft();
			}

			this.render();

			if (Display.isCloseRequested()) {
				stop();
			}
			Display.sync(FPS);
			Display.update();
		}
	}
}
