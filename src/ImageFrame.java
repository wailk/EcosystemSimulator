


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.SwingConstants;

/**
 * This class makes it easy to display an image -- Display is also refreshed anytime the enclosed source image is modified.
 * You may want to use an ImageBuffer object (or a BufferedImage object) as enclosed image.
 *  
 * @author nicolas
 * @modified by WAIL
 * 20070919
 *
 */

public class ImageFrame extends JPanel implements Runnable {

	//private static final long serialVersionUID = 1L;
	private BufferedImage image;
	private int refreshDelay = 500; // in ms. -- default is 1s.
	private JLabel currentDate;
	
	public static final int MIN_DELAY = 1;
	public static final int MAX_DELAY = 100;
	public static final int DEFAULT_DELAY = 50;
	
	
	public void updateLabels(World w){
		
		this.currentDate.setText(w.dispTimeString());
	}
	
	
	public int getRefreshDelay() {
		return refreshDelay;
	}   

	public ImageFrame() 
	{
		super();

		new Thread(this).start();
	}

	public void setImage( BufferedImage __image)
	{
		this.image = __image;
	}
	
	public void paintComponent(Graphics g) 
	{
		//g.drawImage(this.image, 0, 0, this); // fixed size
		g.drawImage(this.image,0,0,getWidth(),getHeight(),this); // resize image wrt. window size
	}

	public void run() 
	{
		while(true) {
			repaint();
			try {
				Thread.sleep(this.refreshDelay);
			} catch (InterruptedException e) 
			{
			}
		}
	}
	
	public JLabel getCurrentDate() {
		return currentDate;
	}

	public void setCurrentDate(JLabel currentDate) {
		this.currentDate = currentDate;
	}

	public void setRefreshDelay(int delay)
	{
		this.refreshDelay = delay;
	}
	
	/**
	 * create and display an ImageFrame object
	 * @param __name
	 * @param __image
	 * @param __refreshDelay
	 * @return object created
	 */
	static public ImageFrame makeFrame ( String __name, BufferedImage __image, int __refreshDelay, World w)
	{
		return makeFrame(__name, __image, __refreshDelay, __image.getWidth(), __image.getHeight(), w);
	}
	
	/**
	 * create and display an ImageFrame object
	 * @param __name
	 * @param __image
	 * @param __refreshDelay
	 * @param __width initial window width
	 * @param __height initial window height
	 * @return
	 */
	static public ImageFrame makeFrame ( String __name, BufferedImage __image, int __refreshDelay, int __width, int __height, World w)
	{
		ImageFrame imageFrame = new ImageFrame();
		JFrame frame = new JFrame(__name);
		frame.setBounds(100, 400, 450, 450);
		
		frame.setSize(__width, __height);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		
		
		JPanel panel = new JPanel();
		// Initialisation des Labels
		JLabel lblSpeed = new JLabel("Speed");
		JLabel lblDate = new JLabel("Date: ");
		
		
		JSlider slider = new JSlider(JSlider.HORIZONTAL, MIN_DELAY, MAX_DELAY, DEFAULT_DELAY);
		JSeparator separator = new JSeparator();
		
		
		
		imageFrame.setImage(__image);
		imageFrame.setRefreshDelay(__refreshDelay);
		
		panel.add(lblSpeed);
		panel.add(slider);
		separator.setForeground(Color.BLACK);
		separator.setOrientation(SwingConstants.VERTICAL);
		panel.add(separator);
		imageFrame.setCurrentDate(new JLabel(w.dispTimeString()));
		slider.addChangeListener(new SliderListener(imageFrame));
		
		panel.add(lblDate);
		panel.add(imageFrame.getCurrentDate());
		
		
		frame.getContentPane().add(panel, BorderLayout.NORTH);
		frame.getContentPane().add(imageFrame, BorderLayout.CENTER);
        frame.setVisible(true);
		
		
		return imageFrame;
	}

	
	// *** demo *** 
	
	public static void main(String[] args) 
	{
		int w = 100;
	    int h = 100;
		ImageBuffer image = new ImageBuffer(w,h);
	    
		// create and display frame
		ImageFrame imageFrame =	makeFrame( "ImageFrame Demo", image, 1000, 400, 400, null);
        
		// randomly change the pixels color in the enclosed image
        do {
        	
        	
		    for ( int j = 0 ; j != h ; j++ )
		    	for ( int i = 0 ; i != w ; i++ )
		    	{
		    		
		    		int r = (int)(Math.random()*255.);
		    		int g = (int)(Math.random()*255.);
		    		int b = (int)(Math.random()*255.);
		    		if(j!=0)
		    		image.setPixel(i, j, r, g ,b );
		    		else image.setPixel(i, j, 0, 0 ,0 );
		    	}
        } while ( true );
	}
}
