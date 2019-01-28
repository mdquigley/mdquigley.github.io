package quigley_final2608;

import com.jsyn.data.SegmentedEnvelope;
import com.jsyn.ports.UnitInputPort;
import com.jsyn.ports.UnitOutputPort;
import com.jsyn.unitgen.*;
import com.softsynth.jmsl.JMSLRandom;
import com.softsynth.shared.time.TimeStamp;

/* Michael D. Quigley
 * NYU - Java Music Systems
 * Final Project
 * 12/18/12
 */

public class QuigleyNoise extends Circuit implements UnitSource, UnitVoice {

	private SineOscillator osc;
	private RedNoise noise;
	private Multiply mult;
	private Delay delay;
	private SegmentedEnvelope envelope;
    private VariableRateMonoReader envelopeReader;
    
    public UnitInputPort oscAmp;
	public UnitInputPort oscFreq;
    public UnitInputPort noiseAmp;
	public UnitInputPort noiseFreq;
	public UnitOutputPort output;
	
	public QuigleyNoise() {
		
		// instantiate and add units to this Circuit
		add(osc = new SineOscillator());
		add(noise = new RedNoise());
	    add(mult = new Multiply());
	    add(delay = new Delay());
	    add(envelopeReader = new VariableRateMonoReader());
	    
	    delay.allocate(88200);
	    
	  //connect
	  		osc.output.connect(mult.inputA);
	  		noise.output.connect(mult.inputB);
	  		mult.output.connect(delay.input);
	  		envelopeReader.output.connect(osc.amplitude);
	  		
	  		buildSegmentedEnvelope();
	  		
	  	// expose inputs and outputs
			addPort(oscAmp = osc.amplitude, "Osc Amp");
			addPort(oscFreq = osc.frequency, "Osc Freq");
			addPort(noiseAmp = noise.amplitude, "Noise Amp");
			addPort(noiseFreq= noise.frequency, "Noise Freq");
			addPort(output = delay.output);
			
			// set defaults and ranges
			oscAmp.setup(0, 0.5, 1.0);
			oscFreq.setup(20, 440, 1000);
			noiseAmp.setup(0, 0.2, 1.0);
			noiseFreq.setup(20, 880, 1000);
	    
	}
	
	private void buildSegmentedEnvelope() {
		double[] envData = {
				2, 1.0, 
				0.3, .8, 
				2, .3,
				.8, .5,
				1.8, 0
		};
        envelope = new SegmentedEnvelope(envData);
        envelope.setSustainBegin(2);
        envelope.setSustainEnd(2);
        envData = null;
    }
	
	public UnitOutputPort getOutput() {
		return output;
	}

	public void noteOff(TimeStamp ts) {

		envelopeReader.dataQueue.queueOff(envelope, true, ts);
	}
	
	public void noteOn(double frequency, double amplitude, TimeStamp ts) {

	this.oscAmp.set(amplitude, ts);
	this.oscFreq.set(frequency, ts);
	this.noiseAmp.set(amplitude, ts);
	this.noiseFreq.set(JMSLRandom.choose(0.1, 1.0), ts);
	envelopeReader.amplitude.set(amplitude, ts);
    envelopeReader.dataQueue.queueOn(envelope, ts);

	}
}
