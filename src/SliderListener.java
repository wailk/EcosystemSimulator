import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

	class SliderListener implements ChangeListener {
		private ImageFrame im;
		
		SliderListener(ImageFrame img){
			super();
			this.im = img;
		}
		
	    public void stateChanged(ChangeEvent e) {
	        JSlider source = (JSlider)e.getSource();
	        if (!source.getValueIsAdjusting()) {
	            this.im.setRefreshDelay((source.getValue()*10));
	        }    
	    }

	}
	