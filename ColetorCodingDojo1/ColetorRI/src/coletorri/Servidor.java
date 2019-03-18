package coletorri;

import java.sql.Timestamp;
import java.util.Date;


public class Servidor {
	public static final long ACESSO_MILIS = 30*1000;
	
	
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
            
            
            return (vistoPorUltimo - horarioInicial);
	}
	
	/**
	 * 
	 * Atualiza o acesso
	 */
	public void acessadoAgora()
	{
            horarioInicial = System.currentTimeMillis();
            System.out.println(horarioInicial);

            //method 2 - via Date
            Date date = new Date();
            System.out.println(new Timestamp(date.getTime()));

            //return number of milliseconds since January 1, 1970, 00:00:00 GMT
//            System.out.println(horarioInicial.getTime());

	}
	
	/**
	 * Verifica se é possivel acessar o dominio
	 * @return
	 */
	public synchronized boolean isAccessible()
	{
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
