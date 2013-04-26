package co.edu.uis.sistemas.simple.icasa;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Unbind;
import org.apache.felix.ipojo.annotations.Validate;

import fr.liglab.adele.icasa.device.DeviceListener;
import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.device.temperature.Heater;
import fr.liglab.adele.icasa.device.temperature.Thermometer;


@Component(name="SimpleIcasaComponent")
@Instantiate
public class SimpleIcasaComponent implements DeviceListener {
	
	@Requires(id="heater")
	private Heater[] heater;
	
	@Requires(id="thermometer")
	private Thermometer[] thermometer; 
	
	private List<GenericDevice> listDevice;
	
	private Thread modifyHeatersThread;
	private Thread modifyThermometerThread;
	
	@Bind(id="heater")
	protected void bindHeater(Heater heater) {
		System.out.println("A new heater has been added to the platform " + heater.getSerialNumber());
		heater.setPowerLevel(0.2);
		heater.addListener(this);
	}
	
	@Unbind(id="heater")
	protected void unBindHeater(Heater heater) {
		System.out.println("Heater removido " + heater.getSerialNumber());
		heater.removeListener(this);
	}
	
	@Bind(id="thermometer")
	protected void bindThermometer(Thermometer thermometer) {
		System.out.println("A new thermometer has been added to the platform " + thermometer.getSerialNumber());
		thermometer.addListener(this);
	}
	
	@Unbind(id="thermometer")
	protected void unBindThermometer(Thermometer thermometer) {
		System.out.println("Thermometer removido " + thermometer.getSerialNumber());
		thermometer.removeListener(this);
	}

	protected List<Heater> getHeater() {
		return Collections.unmodifiableList(Arrays.asList(heater));
	}
	
	protected List<Thermometer> getThermometer() {
		return Collections.unmodifiableList(Arrays.asList(thermometer));
	}

	
	@Validate
	public void start() {
		modifyHeatersThread = new Thread();
		modifyHeatersThread.start();	
		modifyThermometerThread = new Thread();
		modifyThermometerThread.start();
	}
	
	@Invalidate
	public void stop() throws InterruptedException {
		modifyHeatersThread.interrupt();
		modifyHeatersThread.join();	
		modifyThermometerThread.interrupt();
		modifyThermometerThread.join();
	}

	public void deviceAdded(GenericDevice arg0) {
		// TODO Auto-generated method stub
		
	}

	public void devicePropertyAdded(GenericDevice arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}

	public void devicePropertyModified(GenericDevice device, String property, Object value) {
		String id = device.getSerialNumber();
		String location = (String) device.getPropertyValue(GenericDevice.LOCATION_PROPERTY_NAME);
		System.out.println("Modificado " + id + " propiedad " + property + " valor " + location);
		
		listDevice.add(device);
	}

	public void temperature()
	{
		
	}

	public void devicePropertyRemoved(GenericDevice arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}

	public void deviceRemoved(GenericDevice arg0) {
		// TODO Auto-generated method stub
		
	}

	
	class ModifyRunnable implements Runnable {

		public void run() {
						
			boolean running = true;
			
			boolean onOff = false;
			while (running) {
				try {
					
					for (BinaryLight binaryLight : lights) {
						binaryLight.setPowerStatus(onOff);
					}
					Thread.sleep(1000);					
				} catch (InterruptedException e) {
					running = false;
				}
			}
			
		}
		
	}
	
}
