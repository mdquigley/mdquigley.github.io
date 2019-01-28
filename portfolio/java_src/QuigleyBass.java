package quigley_final2608;

import com.jsyn.data.SegmentedEnvelope;
import com.jsyn.ports.UnitInputPort;
import com.jsyn.ports.UnitOutputPort;
import com.jsyn.unitgen.*;
import com.softsynth.shared.time.TimeStamp;

/* Michael D. Quigley
 * NYU - Java Music Systems
 * Final Project
 * 12/18/12
 */

public class QuigleyBass extends Circuit implements UnitSource, UnitVoice {

	private SineOscillator osc;
	private SegmentedEnvelope envelope;
    private VariableRateMonoReader envelopeReader;
    
    public UnitInputPort oscAmp;
	public UnitInputPort oscFreq;
	public UnitOutputPort output;
	
	public QuigleyBass() {
		
		// instantiate and add units to this Circuit
		add(osc = new SineOscillator());
	    add(envelopeReader = new VariableRateMonoReader());
	    
	  //connect
	  		envelopeReader.output.connect(osc.amplitude);
	  		
	  		buildSegmentedEnvelope();
	  		
	  	// expose inputs and outputs
			addPort(oscAmp = osc.amplitude, "Osc Amp");
			addPort(oscFreq = osc.frequency, "Osc Freq");
			addPort(output = osc.output);
			
			// set defaults and ranges
			oscAmp.setup(0, 0.5, 1.0);
			oscFreq.setup(20, 440, 1000);

	    
	}
	
	private void buildSegmentedEnvelope() {
        double[] envData = {
        		6, 1.0,
     			0.3, 0.7,
     			0.25, 0.599, 
                4, 0.0, 
     			0.01, 0.0,
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
	envelopeReader.amplitude.set(amplitude, ts);
    envelopeReader.dataQueue.queueOn(envelope, ts);

	}
}
