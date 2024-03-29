package com.crazyprof.rendering;

import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.awt.image.BufferStrategy;
import java.awt.image.DataBufferByte;
import javax.swing.JFrame;

import com.crazyprof.util.Input;

/**
 * Represents a window that can be drawn in using a software renderer.
 */
public class Display extends Canvas
{
	/** The window being used for display */
	private final JFrame         m_frame;
	/** The bitmap representing the final image to display */
	private final RenderContext  m_frameBuffer;
	/** Used to display the framebuffer in the window */
	private final BufferedImage  m_displayImage;
	/** The pixels of the display image, as an array of byte components */
	private final byte[]         m_displayComponents;
	/** The buffers in the Canvas */
	private final BufferStrategy m_bufferStrategy;
	/** A graphics object that can draw into the Canvas's buffers */
	private final Graphics       m_graphics;

	private final Input          m_input;

	public RenderContext GetFrameBuffer() { return m_frameBuffer; }
	public Input GetInput() { return m_input; }

	/**
	 * Creates and initializes a new display.
	 *
	 * @param width  How wide the display is, in pixels.
	 * @param height How tall the display is, in pixels.
	 * @param title  The text displayed in the window's title bar.
	 */
	public Display(int width, int height, String title)
	{
		//Set the canvas's preferred, minimum, and maximum size to prevent
		//unintentional resizing.
		Dimension size = new Dimension(width, height);
		setPreferredSize(size);
		setMinimumSize(size);
		setMaximumSize(size);

		//Creates images used for display.
		m_frameBuffer = new RenderContext(width, height);
		m_displayImage = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
		m_displayComponents = 
			((DataBufferByte)m_displayImage.getRaster().getDataBuffer()).getData();

		m_frameBuffer.Clear((byte)0x80);
		m_frameBuffer.DrawPixel(100, 100, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0xFF);

		//Create a JFrame designed specifically to show this Display.
		m_frame = new JFrame();
		m_frame.add(this);
		m_frame.pack();
		m_frame.setResizable(false);
		m_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		m_frame.setLocationRelativeTo(null);
		m_frame.setTitle(title + " | v0.1 | Software Renderer");
		m_frame.setSize(width, height);
		m_frame.setVisible(true);

		//Allocates 1 display buffer, and gets access to it via the buffer
		//strategy and a graphics object for drawing into it.
		createBufferStrategy(1);
		m_bufferStrategy = getBufferStrategy();
		m_graphics = m_bufferStrategy.getDrawGraphics();
		
		m_input = new Input();
		addKeyListener(m_input);
		addFocusListener(m_input);
		addMouseListener(m_input);
		addMouseMotionListener(m_input);

		setFocusable(true);
		requestFocus();
	}

	/**
	 * Displays in the window.
	 */
	public void SwapBuffers()
	{
		//Display components should be the byte array used for displayImage's pixels.
		//Therefore, this call should effectively copy the frameBuffer into the
		//displayImage.
		m_frameBuffer.CopyToByteArray(m_displayComponents);
		m_graphics.drawImage(m_displayImage, 0, 0, 
			m_frameBuffer.GetWidth(), m_frameBuffer.GetHeight(), null);
		m_bufferStrategy.show();
	}
}
