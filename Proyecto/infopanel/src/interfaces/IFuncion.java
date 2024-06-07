package interfaces;

public interface IFuncion {
	
	public String getId();
	
	public IFuncion iniciar();
	public IFuncion detener();
	
	public IFuncion encender();
	public IFuncion apagar();
	public IFuncion parpadear();

	public IFuncion habilitar(); // New
	public IFuncion deshabilitar(); // New
	
	public FuncionStatus getStatus();

}
