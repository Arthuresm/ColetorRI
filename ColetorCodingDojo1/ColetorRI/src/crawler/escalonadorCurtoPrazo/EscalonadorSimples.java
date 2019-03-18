package crawler.escalonadorCurtoPrazo;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.trigonic.jrobotx.Record;
import com.trigonic.jrobotx.RobotExclusion;

import coletorri.Servidor;
import coletorri.URLAddress;
import java.util.LinkedHashMap;

public class EscalonadorSimples implements Escalonador{
    
    
        public LinkedHashMap<Servidor, HashSet> map = new LinkedHashMap<>();
        public ArrayList<Servidor> filaServidores = new ArrayList<>();
        public HashSet<URLAddress> filaPaginas = new HashSet<>();
        public int limiteProfundidade = 5;
        
	@Override
	public URLAddress getURL() {
		// TODO Auto-generated method stub
                Set<Servidor> servidores = map.keySet();
                HashSet paginas = new HashSet<>();
                for (Servidor servidor: servidores){
                    paginas = map.get(servidor);
                    if(!paginas.isEmpty()){
                        //PARAMOS AQUI - SLIDE 9
                    }
                   
                }
                
		return null;
	}

	@Override
	public boolean adicionaNovaPagina(URLAddress urlAdd) {
            Servidor serv = new Servidor(urlAdd.getDomain());
            if(!map.containsKey(urlAdd.getDomain())){
          
                // TODO Auto-generated method stub
                if(filaPaginas.contains(urlAdd)){
                    return false;
                }
                filaPaginas.add(urlAdd);
                
                map.put(serv, filaPaginas);
            }
            else{
                // TODO Auto-generated method stub
                if(filaPaginas.contains(urlAdd)){
                    return false;
                }
                filaPaginas.add(urlAdd);
                
                
                map.put(serv, filaPaginas);
                
		return true;
            }
            
            return false;
		
	}


	@Override
	public Record getRecordAllowRobots(URLAddress url) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void putRecorded(String domain, Record domainRec) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public boolean finalizouColeta() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void countFetchedPage() {
		// TODO Auto-generated method stub
		
	}

	
}
