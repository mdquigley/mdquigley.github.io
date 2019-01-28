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

public class QuigleyMid extends Circuit implements UnitSource, UnitVoice {

	private SineOscillator osc;
	private TriangleOscillator osc2;
	private FilterBandPass filter;
	private InterpolatingDelay delay;
	private Add add1, add2;
	private SegmentedEnvelope envelope;
    private VariableRateMonoReader envelopeReader;
    
    public UnitInputPort oscAmp;
	public UnitInputPort oscFreq;
    public UnitInputPort filterAmp;
	public UnitInputPort filterFreq;
	public UnitInputPort filterQ;
	public UnitInputPort delayTime;
	public UnitOutputPort output;
	
	public QuigleyMid() {
		
		// instantiate and add units to this Circuit
		add(osc = new SineOscillator());
		add(osc2 = new TriangleOscillator());
		add(filter = new FilterBandPass());
	    add(delay = new InterpolatingDelay());
	    add(add1 = new Add());
	    add(add2 = new Add());
	    add(envelopeReader = new VariableRateMonoReader());
	    
	    delay.allocate(220500);
	    
	    //connect
	    osc.output.connect(add1.inputA);
	    osc2.output.connect(add1.inputB);
	    add1.output.connect(filter.input);
	    filter.output.connect(delay.input);
	    filter.output.connect(add2.inputA);
	    delay.output.connect(add2.inputB);
	    envelopeReader.output.connect(osc.amplitude);
	    envelopeReader.output.connect(osc2.amplitude);

	    buildSegmentedEnvelope();

	  	// expose inputs and outputs
			addPort(oscAmp = osc.amplitude, "Osc Amp");
			addPort(oscFreq = osc.frequency, "Osc Freq");
			addPort(filterAmp = filter.amplitude, "Filter Amp");
			addPort(filterFreq = filter.frequency, "Filter Freq");
			addPort(filterQ = filter.Q, "Filter Q");
			addPort(delayTime = delay.delay, "Delay Time");
			addPort(output = add2.output);
			
			// set defaults and ranges
			oscAmp.setup(0, 0.2, 0.2);
			oscFreq.setup(20, 440, 1000);
			filterAmp.setup(0, 0.2, 0.5);
			filterFreq.setup(20, 440, 1000);
			filterQ.setup(0.0, 1.0, 5);
			delayTime.setup(0, 2.5, 5);
			
			osc2.amplitude = osc.amplitude;
			osc2.frequency = osc.frequency;
	    
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
	this.osc2.amplitude.set(amplitude, ts);
	this.osc2.frequency.set(frequency, ts);
	envelopeReader.amplitude.set(amplitude, ts);
    envelopeReader.dataQueue.queueOn(envelope, ts);

	}
}
