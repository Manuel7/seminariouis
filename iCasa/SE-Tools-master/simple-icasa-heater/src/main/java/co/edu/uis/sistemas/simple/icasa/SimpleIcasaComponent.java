package co.edu.uis.sistemas.simple.icasa;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.felix.ipojo.annotations.Bind;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Validate;

import fr.liglab.adele.icasa.device.temperature.Heater;


@Component(name="SimpleIcasaComponent")
@Instantiate
public class SimpleIcasaComponent {
	
	@Requires(id="heater")
	private Heater[] heater;
	
	private Thread modifyHeatersThread;
	
	@Bind(id="heater")
	protected void bindHeater(Heater heater) {
		System.out.println("A new heater has been added to the platform " + heater.getSerialNumber());
		heater.setPowerLevel(0.2);
	}

	protected List<Heater> getHeater() {
		return Collections.unmodifiableList(Arrays.asList(heater));
	}

	
	@Validate
	public void start() {
		modifyHeatersThread = new Thread();
		modifyHeatersThread.start();		
	}
	
	@Invalidate
	public void stop() throws InterruptedException {
		modifyHeatersThread.interrupt();
		modifyHeatersThread.join();	
	}

	
	/*class ModifyLigthsRunnable implements Runnable {

		public void run() {
						
			boolean running = true;
			
			boolean onOff = false;
			while (running) {
				onOff = !onOff;
				try {
					List<BinaryLight> lights = getLights();
					for (BinaryLight binaryLight : lights) {
						binaryLight.setPowerStatus(onOff);
					}
					Thread.sleep(1000);					
				} catch (InterruptedException e) {
					running = false;
				}
			}
			
		}
		
	}*/
	
}
