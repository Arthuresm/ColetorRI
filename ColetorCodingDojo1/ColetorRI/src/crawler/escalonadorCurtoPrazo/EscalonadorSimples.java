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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EscalonadorSimples implements Escalonador{
    
    
        public LinkedHashMap<Servidor, HashSet> map = new LinkedHashMap<>();
        public ArrayList<Servidor> filaServidores = new ArrayList<>();
        public HashSet<URLAddress> filaPaginas = new HashSet<>();
        public int limiteProfundidade = 4;
        public HashMap<Servidor, Record> mapServRecord = new HashMap<>();
        public int contadorPaginas = 0;
        
        public int limitePaginas = 500;
        
        
	@Override
	public synchronized URLAddress getURL() {
		// TODO Auto-generated method stub
                Set<Servidor> servidores = map.keySet();
                HashSet paginas = new HashSet<>();
                int existeAcessivel = 0;
                
                while(existeAcessivel == 0){
                    for (Servidor servidor: servidores){
                        int indice_fila_servidores = filaServidores.indexOf(servidor);
                        //System.out.println(indice_fila_servidores);
                        
                        if(indice_fila_servidores >= 0){
                            if(filaServidores.get(indice_fila_servidores).isAccessible()){
                                existeAcessivel = 1;
                                paginas = map.get(servidor);
                                if(!paginas.isEmpty()){
                                    //PARAMOS AQUI - SLIDE 9
                                    Iterator<URLAddress> it = paginas.iterator();
                                    URLAddress proximaUrl;

                                    if(it.hasNext()){

                                        proximaUrl = it.next();

                                        filaServidores.get(indice_fila_servidores).acessadoAgora();
                                        map.get(servidor).remove(proximaUrl);

                                        return proximaUrl;
                                    }
                                    else{
                                        map.remove(servidor);
                                    }
                                }
                            }
                        }
                    }

                }
                                
                try {
                    this.wait(1000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(EscalonadorSimples.class.getName()).log(Level.SEVERE, null, ex);
                }
                
		return null;
	}

	@Override
	public synchronized boolean adicionaNovaPagina(URLAddress urlAdd) {
            Servidor serv = new Servidor(urlAdd.getDomain());
            String addr = urlAdd.getAddress();
            
            //System.out.println(addr + "\n" + urlAdd.getDomain());
            if(urlAdd.getDepth()<=limiteProfundidade && !finalizouColeta()){
                Iterator<URLAddress> it = filaPaginas.iterator();
                URLAddress proximaUrl;
                String addr2;

                //System.out.println("Numero de itens dentro do map: " + map.size());

                while(it.hasNext()){
                    proximaUrl = it.next();
                    addr2 = proximaUrl.getAddress();
                    if(addr2.equals(addr)){
                        return false;
                    }

                }

                if(!map.containsKey(serv)){

                    // TODO Auto-generated method stub

                    if(filaPaginas.contains(urlAdd.getAddress())){

                        return false;
                    }
                    filaPaginas.add(urlAdd);

                    filaServidores.add(serv);
                    map.put(serv, filaPaginas);
                    return true;
                }
                else{
                    // TODO Auto-generated method stub

                    if(filaPaginas.contains(urlAdd.getAddress())){

                        return false;
                    }
                    filaPaginas.add(urlAdd);

                    map.put(serv, filaPaginas);
                    filaServidores.add(serv);
                    return true;
                }
            }else{
                return false;
            }    
	}


	@Override
	public Record getRecordAllowRobots(URLAddress url) {
		// TODO Auto-generated method stub
                String domain = url.getDomain();
                Servidor server = new Servidor(domain);
                
                if(mapServRecord.containsKey(server)){
                    return mapServRecord.get(server);                    
                }
                
		return null;
	}

	@Override
	public synchronized void putRecorded(String domain, Record domainRec) {
		// TODO Auto-generated method stub
                Servidor s = new Servidor(domain);
                mapServRecord.put(s, domainRec);	
	}
        
	@Override
	public boolean finalizouColeta() {
		// TODO Auto-generated method stub
                if(contadorPaginas >= limitePaginas){
                    return true;
                }
		return false;
	}

	@Override
	public void countFetchedPage() {
		// TODO Auto-generated method stub
                contadorPaginas++;
		
	}

	public synchronized boolean retiraPagina(URLAddress linkARetirar){
            Servidor serv = new Servidor(linkARetirar.getDomain());
            
            System.out.println("\n\n\nImpossivel criar o record para robots.txt, tentando tirar pagina "+linkARetirar.getURL()+ " do escalonador");
            
            if(map.containsKey(serv)){
                map.remove(serv);    
                System.out.println("\nRetirado do map: " + linkARetirar.getAddress());

                if(filaServidores.remove(serv))
                    System.out.println("Retirado da filaServidores: " + linkARetirar.getAddress());

                if(filaPaginas.remove(linkARetirar))
                    System.out.println("Retirado da filaPaginas: " + linkARetirar.getAddress());

                if(mapServRecord.containsKey(serv)){
                    mapServRecord.remove(serv);    
                    System.out.println("Retirado do mapServRecord: " + linkARetirar.getAddress());
                }
                
                return true;
            }else
                return false;
        }	
}
