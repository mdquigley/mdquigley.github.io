package quigley_final2608;

import com.softsynth.jmsl.*;

/* Michael D. Quigley
 * NYU - Java Music Systems
 * Final Project
 * 12/18/12
 */

public class Quigley_autoPan implements Playable {

	JMSLMixerContainer mixer;
	int index;
	double panChange;
	
	Quigley_autoPan(JMSLMixerContainer mixer, int index) {
		this.mixer = mixer;
		this.index = index;
	}

	public double play(double playTime, Composable parent) {
		JMSLRandom.randomize();
		double change = ((mixer.getPan(index) + (JMSLRandom.choose(0, 3)))/10.0);
		if ((mixer.getPan(index) > 0.5)) {
			panChange =  mixer.getPan(index) - change;
		}
		else {
			panChange = mixer.getPan(index) + change;
		}

			mixer.panAmpChange(index, panChange, mixer.getAmp(index));
			//System.out.println("Ins: " + mixer.getInstrument(index).getName() + " Pan: " + mixer.getPan(index));
		
		return playTime;
	}
	
	
	
}
