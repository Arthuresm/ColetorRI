package coletorri;

import java.sql.Timestamp;
import java.util.Date;


public class Servidor {
	public static final long ACESSO_MILIS = 30*100;
	
	
	private String nome;
	private long lastAccess;
        public long horarioInicial;
	
	public Servidor(String nome)
	{
		this.nome = nome;
		this.lastAccess =0;
	}
	
	
	
	/**
	 * 
	 * @return
	 */
	public long getTimeSinceLastAcess()
	{
            long vistoPorUltimo = System.currentTimeMillis();
            
            //System.out.println("Servidor " + nome + " teve o ultimo acesso em " + lastAccess);
            //System.out.println("Horario atual " + vistoPorUltimo);
            
           
            return vistoPorUltimo - lastAccess;
	}
	
	/**
	 * 
	 * Atualiza o acesso
	 */
	public void acessadoAgora()
	{
            horarioInicial = System.currentTimeMillis();
            lastAccess = horarioInicial;
            //System.out.println("Acessei o servidor " + nome + " aos "+ lastAccess);

	}
	
	/**
	 * Verifica se é possivel acessar o dominio
	 * @return
	 */
	public synchronized boolean isAccessible()
	{
            //System.out.println(getTimeSinceLastAcess() > ACESSO_MILIS);
            if (getTimeSinceLastAcess() > ACESSO_MILIS)
                return true;
            
            return false;
	}
	
	
	
	
	
	
	
	
	public long getLastAcess()
	{
		return this.lastAccess;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((nome == null) ? 0 : nome.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Servidor other = (Servidor) obj;
		if (nome == null) {
			if (other.nome != null)
				return false;
		} else if (!nome.equals(other.nome))
			return false;
		return true;
	}

	

	
	public String getNome()
	{
		return this.nome;
	}
}
