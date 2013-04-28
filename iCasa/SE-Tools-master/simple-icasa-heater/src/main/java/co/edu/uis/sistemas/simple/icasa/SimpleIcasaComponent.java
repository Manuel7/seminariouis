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
import fr.liglab.adele.icasa.device.temperature.Cooler;
import fr.liglab.adele.icasa.device.temperature.Heater;
import fr.liglab.adele.icasa.device.temperature.Thermometer;


@Component(name="SimpleIcasaComponent")
@Instantiate
public class SimpleIcasaComponent implements DeviceListener {
	
	@Requires(id="heater")
	private Heater[] heater;
	
	@Requires(id="thermometer")
	private Thermometer[] thermometer; 
	
	@Requires(id="cooler")
	private Cooler[] cooler;
	
	private List<GenericDevice> listDevice;
	
	private Thread modifyTemperature;
	
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
	
	@Bind(id="cooler")
	protected void bindCooler(Cooler cooler) {
		System.out.println("A new cooler has been added to the platform " + cooler.getSerialNumber());
		cooler.addListener(this);
	}
	
	@Unbind(id="cooler")
	protected void unBindCooler(Cooler cooler) {
		System.out.println("Cooler removido " + cooler.getSerialNumber());
		cooler.removeListener(this);
	}

	protected List<Heater> getHeater() {
		return Collections.unmodifiableList(Arrays.asList(heater));
	}
	
	protected List<Thermometer> getThermometer() {
		return Collections.unmodifiableList(Arrays.asList(thermometer));
	}
	
	protected List<Cooler> getCooler() {
		return Collections.unmodifiableList(Arrays.asList(cooler));
	}

	
	@Validate
	public void start() {
		modifyTemperature = new Thread(new ModifyRunnable());
		modifyTemperature.start();	
	}
	
	@Invalidate
	public void stop() throws InterruptedException {
		modifyTemperature.interrupt();
		modifyTemperature.join();	;
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

	public void devicePropertyRemoved(GenericDevice arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}

	public void deviceRemoved(GenericDevice arg0) {
		// TODO Auto-generated method stub
		
	}

	
	class ModifyRunnable implements Runnable {

		public void run() {
						
			boolean running = true;			
			while (running) {
				try
				{
					List<Cooler> coolers = getCooler();
					List<Heater> heaters = getHeater();
					List<Thermometer> thermometers = getThermometer();
					for (Thermometer thermometer :  thermometers)
					{
						for (Heater heater : heaters)
						{
							for (Cooler cooler : coolers)
							{
								String locThermo = (String) thermometer.getPropertyValue(GenericDevice.LOCATION_PROPERTY_NAME);
								String locHeater = (String) heater.getPropertyValue(GenericDevice.LOCATION_PROPERTY_NAME);
								String locCooler = (String) cooler.getPropertyValue(GenericDevice.LOCATION_PROPERTY_NAME);
								if (locThermo.equals(locHeater))
								{
									if(thermometer.getTemperature() >= 300)
									{
										cooler.setPowerLevel(1.0);
										System.out.println("La temperatura de " + cooler.getSerialNumber() + " es " + cooler.getPowerLevel());
										heater.setPowerLevel(0.0);
										System.out.println("La temperatura de " + heater.getSerialNumber() + " es " + heater.getPowerLevel());
									}
									else if (thermometer.getTemperature() <= 290)
									{
										cooler.setPowerLevel(0.0);
										System.out.println("La temperatura de " + cooler.getSerialNumber() + " es " + cooler.getPowerLevel());
										heater.setPowerLevel(1.0);
										System.out.println("La temperatura de " + heater.getSerialNumber() + " es " + heater.getPowerLevel());
									}
								}
								if (locThermo.equals(locCooler))
								{
									if(thermometer.getTemperature() >= 300)
									{
										cooler.setPowerLevel(1.0);
										System.out.println("La temperatura de " + cooler.getSerialNumber() + " es " + cooler.getPowerLevel());
										heater.setPowerLevel(0.0);
										System.out.println("La temperatura de " + heater.getSerialNumber() + " es " + heater.getPowerLevel());
									}
									else if (thermometer.getTemperature() <= 290)
									{
										cooler.setPowerLevel(0.0);
										System.out.println("La temperatura de " + cooler.getSerialNumber() + " es " + cooler.getPowerLevel());
										heater.setPowerLevel(1.0);
										System.out.println("La temperatura de " + heater.getSerialNumber() + " es " + heater.getPowerLevel());
									}
								}
							}
						}						
					}
					Thread.sleep(500);		
				} catch (InterruptedException e) {
						running = false;
				}
			}			
		}		
	}	
}
