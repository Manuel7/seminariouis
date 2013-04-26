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
import fr.liglab.adele.icasa.location.LocatedDevice;
import fr.liglab.adele.icasa.location.Position;
import fr.liglab.adele.icasa.location.ZoneListener;
import fr.liglab.adele.icasa.location.Zone;
import fr.liglab.adele.icasa.location.ZonePropListener;


@Component(name="SimpleIcasaComponent")
@Instantiate
public class SimpleIcasaComponent implements DeviceListener, ZoneListener {
	
	@Requires(id="heater")
	private Heater[] heater;
	
	@Requires(id="thermometer")
	private Thermometer[] thermometer; 
	
	@Requires(id="cooler")
	private Cooler[] cooler;
	
	@Requires(id="zones")
	private Zone[] zones;
	
	private List<GenericDevice> listDevice;
	
	private Thread modifyHeatersThread;
	private Thread modifyThermometerThread;
	private Thread modifyCoolerThread;
	
	protected void bindZone(Zone zone)
	{
		System.out.println("A new zone has been added to the platform " + zone.getVariableNames());
		zone.addListener(this);
	}
	
	protected void unBindZone(Zone zone)
	{
		System.out.println("Zona removida " + zone.getVariableNames());
		zone.removeListener(this);
	}
	
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
		modifyHeatersThread = new Thread(new ModifyRunnable());
		modifyHeatersThread.start();	
		modifyThermometerThread = new Thread(new ModifyRunnable());
		modifyThermometerThread.start();
		modifyCoolerThread = new Thread(new ModifyRunnable());
		modifyCoolerThread.start();
	}
	
	@Invalidate
	public void stop() throws InterruptedException {
		modifyHeatersThread.interrupt();
		modifyHeatersThread.join();	
		modifyThermometerThread.interrupt();
		modifyThermometerThread.join();
		modifyCoolerThread.interrupt();
		modifyCoolerThread.join();
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
			String locThermo;
			String locHeater; 
			String locCooler; 
			
			int t=0,h=0,c=0;
			
			while (running) {
				try
				{
					List<Cooler> coolers = getCooler();
					List<Heater> heaters = getHeater();
					List<Thermometer> thermometers = getThermometer();
					for (GenericDevice device : listDevice)
					{
						for (Thermometer thermometer :  thermometers)
						{
							for (Heater heater : heaters)
							{
								for (Cooler cooler : coolers)
								{
									locThermo = (String) device.getPropertyValue(GenericDevice.LOCATION_PROPERTY_NAME);
									locHeater = (String) device.getPropertyValue(GenericDevice.LOCATION_PROPERTY_NAME);
									locCooler = (String) device.getPropertyValue(GenericDevice.LOCATION_PROPERTY_NAME);	
									String idT = device.getSerialNumber();
									String idH = device.getSerialNumber();
									String idC = device.getSerialNumber();
									System.out.println("Modificado " + idT + " propiedad ubicacion valor " + locThermo);
									System.out.println("Modificado " + idH + " propiedad ubicacion valor " + locHeater);
									System.out.println("Modificado " + idC + " propiedad ubicacion valor " + locCooler);
									if (locThermo.equals(locHeater) && locThermo.equals(locCooler))
									{										
										Thermometer T = thermometers.get(t);
										
										Heater H = heaters.get(h);
										Cooler C = coolers.get(c);
										
										if(T.getTemperature() >= 300)
										{
											C.setPowerLevel(1);
											H.setPowerLevel(0);
										}
										else if (T.getTemperature() <= 290)
										{
											C.setPowerLevel(0);
											H.setPowerLevel(1);
										}
									}
									c++;
								}
								c=0;
								h++;
							}
							h=0;
							t++;							
						}
					}
					Thread.sleep(1000);	
				} catch (InterruptedException e) {
					running = false;
				}
			}
			
		}
		
	}


	public void zoneVariableAdded(Zone arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}

	public void zoneVariableModified(Zone arg0, String arg1, Object arg2) {
		// TODO Auto-generated method stub
		
	}

	public void zoneVariableRemoved(Zone arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}

	public void deviceAttached(Zone arg0, LocatedDevice arg1) {
		// TODO Auto-generated method stub
		
	}

	public void deviceDetached(Zone arg0, LocatedDevice arg1) {
		// TODO Auto-generated method stub
		
	}

	public void zoneAdded(Zone arg0) {
		// TODO Auto-generated method stub
		
	}

	public void zoneMoved(Zone arg0, Position arg1) {
		// TODO Auto-generated method stub
		
	}

	public void zoneParentModified(Zone arg0, Zone arg1) {
		// TODO Auto-generated method stub
		
	}

	public void zoneRemoved(Zone arg0) {
		// TODO Auto-generated method stub
		
	}

	public void zoneResized(Zone arg0) {
		// TODO Auto-generated method stub
		
	}
	
}
