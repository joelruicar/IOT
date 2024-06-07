package componentes;

import interfaces.FuncionStatus;
import interfaces.IFuncion;
import utils.MySimpleLogger;
// import api.mqtt.FunctionPublisher_APIMQTT;

public class Funcion implements IFuncion {
	
	protected String id = null;

	protected FuncionStatus initialStatus = null;
	protected FuncionStatus status = null;

	protected Boolean deviceHabilitado = false; // New
	
	private String loggerId = null;
	
	public static Funcion build(String id) {
		return new Funcion(id, FuncionStatus.OFF);
	}
	
	public static Funcion build(String id, FuncionStatus initialStatus) {
		return new Funcion(id, initialStatus);
	}

	protected Funcion(String id, FuncionStatus initialStatus) {
		this.id = id;
		this.initialStatus = initialStatus;
		this.loggerId = "Funcion " + id;
	}
		
	@Override
	public String getId() {
		return this.id;
	}
		
	@Override
	public IFuncion encender() {

		MySimpleLogger.info(this.loggerId, "==> Encender");
		this.setStatus(FuncionStatus.ON);
		return this;
	}

	@Override
	public IFuncion apagar() {

		MySimpleLogger.info(this.loggerId, "==> Apagar");
		this.setStatus(FuncionStatus.OFF);
		return this;
	}

	@Override
	public IFuncion parpadear() {
		MySimpleLogger.info(this.loggerId, "==> Parpadear");
		this.setStatus(FuncionStatus.BLINK);
		return this;
	}

	@Override
	public IFuncion habilitar() {
		MySimpleLogger.info(this.loggerId, "==> Habilitar");
		this.deviceHabilitado = true;
		return this;
	}

	@Override
	public IFuncion deshabilitar() {
		MySimpleLogger.info(this.loggerId, "==> Deshabilitar");
		this.deviceHabilitado = false;
		return this;
	}
	
	protected IFuncion _putIntoInitialStatus() {
		switch (this.initialStatus) {
		case ON:
			this.encender();
			break;
		case OFF:
			this.apagar();
			break;
		case BLINK:
			this.parpadear();
			break;

		default:
			break;
		}
		
		return this;

	}

	@Override
	public FuncionStatus getStatus() {
		return this.status;
	}
	
	protected IFuncion setStatus(FuncionStatus status) {
		this.status = status;
		return this;
	}
	
	@Override
	public IFuncion iniciar() {
		this._putIntoInitialStatus();
		return this;
	}
	
	@Override
	public IFuncion detener() {
		return this;
	}

}
