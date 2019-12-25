package internal;

import java.util.List;

public class DataPack {
	private List<Long> times;
	private List<Long> inPackets;
	private List<Long> outPackets;
	private List<Double> inBandwidth;
	private List<Double> outBandwidth;
	
	public DataPack(List<Long> times, List<Long> inPackets, List<Long> outPackets, List<Double> inBandwidth,
			List<Double> outBandwidth) {
		super();
		this.times = times;
		this.inPackets = inPackets;
		this.outPackets = outPackets;
		this.inBandwidth = inBandwidth;
		this.outBandwidth = outBandwidth;
	}

	public List<Long> getTimes() {
		return times;
	}

	public List<Long> getInPackets() {
		return inPackets;
	}

	public List<Long> getOutPackets() {
		return outPackets;
	}

	public List<Double> getInBandwidth() {
		return inBandwidth;
	}

	public List<Double> getOutBandwidth() {
		return outBandwidth;
	}
	
}
