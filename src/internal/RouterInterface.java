package internal;

import java.util.ArrayList;
import java.util.List;

public class RouterInterface {
	private static final int HISTORY_SIZE = 30;
	
	private Router router;
	private int id;
	private String name;
	private boolean up = false;
	private long time = 0;
	
	private List<Long> times = new ArrayList<>();
	private List<Long> inPackets = new ArrayList<>();
	private List<Long> outPackets = new ArrayList<>();
	private List<Double> inBandwidth = new ArrayList<>();
	private List<Double> outBandwidth = new ArrayList<>();
	
	private NewDataListener listener = null;
	
	public RouterInterface(Router router, int id, String name) {
		this.router = router;
		this.id = id;
		this.name = name;
	}
	
	public Router getRouter() {
		return router;
	}
	
	public int getId() {
		return id;
	}
	
	public void setUp(boolean up) {
		this.up = up;
	}
	
	public boolean getUp() {
		return up;
	}
	
	
	public String getName() {
		return name;
	}
	
	public void newData(long newInPackets, long newOutPackets) {
		inPackets.add(newInPackets);
		outPackets.add(newOutPackets);
		
		//System.out.println(getName() + " " + newOutPackets);
		
		int n = inPackets.size();
		if (n > 1) {
			double newInBandwidth = 8.0 * (inPackets.get(n-1) - inPackets.get(n-2)) / (Router.REFRESH_RATE/1000);
			double newOutBandwidth = 8.0 * (outPackets.get(n-1) - outPackets.get(n-2)) / (Router.REFRESH_RATE/1000);
			inBandwidth.add(newInBandwidth);
			outBandwidth.add(newOutBandwidth);
		} else {
			inBandwidth.add(0.0);
			outBandwidth.add(0.0);
		}
		times.add(++time);
		
		notifyListener();
	}
	
	public void timeout() {
		//System.out.println("TIMEOUT!");
	
		inPackets.add(0L);
		outPackets.add(0L);
		inBandwidth.add(0.0);
		outBandwidth.add(0.0);
		times.add(++time);
		notifyListener();
	}
	
	private synchronized void notifyListener() {
		if (listener != null) {
			listener.newData();
		}
	}
	
	public DataPack getLatestData() {
		int n = inPackets.size();
		int from = Math.max(0, n-HISTORY_SIZE);
		int to = n;
		return new DataPack(times.subList(from, to),
				inPackets.subList(from, to),
				outPackets.subList(from, to),
				inBandwidth.subList(from, to),
				outBandwidth.subList(from, to));
	}
	
	public synchronized void removeListener() {
		if (listener!=null)
			listener.removed();
		listener = null;
	}

	public synchronized void setNewDataListener(NewDataListener listener) {
		this.listener = listener;
	}
}
