package quigley_final2608;

import java.awt.*;
import java.awt.event.*;
import java.awt.BorderLayout;

import javax.swing.JApplet;

import com.softsynth.jmsl.*;
import com.softsynth.jmsl.jsyn2.JSynMusicDevice;
import com.softsynth.jmsl.jsyn2.JSynUnitVoiceInstrument;

/* Michael D. Quigley
 * NYU - Java Music Systems
 * Final Project
 * 12/18/12
 */

public class Quigley_FinalApplet extends JApplet implements ActionListener {

	Panel p1, p2;
	Button start, stop;
	Frame myFrame;

	JMSLMixerContainer mixer;

	JSynUnitVoiceInstrument ins1;
	JSynUnitVoiceInstrument ins2;
	JSynUnitVoiceInstrument ins3;

	MusicShape bass;
	MusicShape mid; 
	MusicShape noise; 
	MusicDevice device;
	
	public void init() {

		JMSLRandom.randomize();
		JMSL.setIsApplet(true);
		setSize(610, 300);
	}

	public void start() {
		synchronized (JMSL.class) {
			initJMSL();
			initMusicDevices();
			initMixer();
			initInstruments();
			buildGUI();
			buildBass();
			buildMid();
			buildNoise();
		}
	}
	
	private void initJMSL() {
		JMSL.scheduler = new EventScheduler();
		JMSL.scheduler.start();
		JMSL.clock.setAdvance(0.1);
	}
		
	private void initMusicDevices() {

		JSynMusicDevice dev = JSynMusicDevice.instance();
		dev.open();

	}
	
	void initMixer() {
		mixer = new JMSLMixerContainer();
		mixer.start();
		}

	private void initInstruments() {
		ins1 = new JSynUnitVoiceInstrument(4, QuigleyBass.class.getName());
		ins2 = new JSynUnitVoiceInstrument(4, QuigleyMid.class.getName());
		ins3 = new JSynUnitVoiceInstrument(4, QuigleyNoise.class.getName());

		ins1.setName("Bass");
		ins2.setName("Mid");
		ins3.setName("Noise");

		mixer.addInstrument(ins1);
		mixer.addInstrument(ins2);
		mixer.addInstrument(ins3);
		mixer.panAmpChange(0, .2, .5);
		mixer.panAmpChange(1, .8, .4);	

	}

	public void buildBass() {

		bass = new MusicShape(ins1.getDimensionNameSpace());
		bass.setRepeats(3);
		bass.setRepeatPause(5);
		bass.setInstrument(ins1);
		bass.addRepeatPlayable(new Quigley_autoPan(mixer, 0));
		
		for (int i = 0; i < 50; i++) {
			double freqs[] = { 46.25, 58.27, 69.30, 87.31 };
			double chooseFreq = freqs[JMSLRandom.choose(0, freqs.length)];

			double duration = JMSLRandom.choose(3, 7);
			double pitch = 1;
			double amplitude = JMSLRandom.choose(bass.getLowLimit(2), bass.getHighLimit(2));;
			double hold = JMSLRandom.choose(5, 9);
			double oscAmp = JMSLRandom.choose(0.0, 1.0);
			double oscFreq = chooseFreq * (JMSLRandom.choose(1, 3));
			bass.add(duration, pitch, amplitude, hold, oscAmp, oscFreq);
		}
	}

	public void buildMid() {

		mid = new MusicShape(ins2.getDimensionNameSpace());
		mid.setRepeats(4);
		mid.setRepeatPause(5);
		mid.setInstrument(ins2);
		mid.addRepeatPlayable(new Quigley_autoPan(mixer, 1));

		for (int i = 0; i < 50; i++) {
			
			double freqs[] = { 46.25, 58.27, 69.30, 87.31, 103.83};
			double chooseFreq = freqs[JMSLRandom.choose(0, freqs.length)];

			double duration = JMSLRandom.choose(2, 4);
			double pitch = 1;
			double amplitude = JMSLRandom.choose(mid.getLowLimit(2), 0.5);;
			double hold = JMSLRandom.choose(5, 9);
			double oscAmp = JMSLRandom.choose(0.0, 0.5);
			double oscFreq = chooseFreq * (JMSLRandom.choose(3, 4));
			double filterAmp = JMSLRandom.choose(0.0, 1.0);
			double filterFreq = chooseFreq * (JMSLRandom.choose(4, 6));
			double filterQ = JMSLRandom.choose(0.0, 1.0);
			double delayTime = JMSLRandom.choose(0, 5);
			mid.add(duration, pitch, amplitude, hold, oscAmp, oscFreq, filterAmp, filterFreq, filterQ, delayTime);
		}

	}
		
	public void buildNoise() {

		noise = new MusicShape(ins3.getDimensionNameSpace());
		noise.setRepeats(3);
		noise.setRepeatPause(1);
		noise.setInstrument(ins3);
		noise.addRepeatPlayable(new Quigley_autoPan(mixer, 2));
		
		for (int i = 0; i < 50; i++){
			double[] data = new double[noise.dimension()];
			for (int dim = 0; dim < data.length; dim++) {
				double value = JMSLRandom.choose(noise.getLowLimit(dim), noise.getHighLimit(dim));
				data[dim] = value;		
			}
			noise.add(data);
		}
		
	}
	
	private void buildGUI() {
		myFrame = new Frame("Parameters");
		myFrame.setLayout(new BorderLayout());
		myFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				bass.finishAll();
				mid.finishAll();
				noise.finishAll();
				JMSL.scheduler.stop();
				mixer.stop();
				JMSL.closeMusicDevices();
				removeAll();
				System.exit(0);
			}
		});
		p1 = new Panel();
		setLayout(new BorderLayout());
		add(BorderLayout.CENTER, p1);
		p1.setLayout(new BorderLayout());
		p1.add(BorderLayout.CENTER, start = new Button("Zone Out"));
		start.addActionListener(this);

	}

	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source == start) {

			start();
			bass.launch(JMSL.now());
			mid.launch(JMSL.now());
			noise.launch(JMSL.now());
		}
		if (source == stop) {
			stop();
		}
	}

}
