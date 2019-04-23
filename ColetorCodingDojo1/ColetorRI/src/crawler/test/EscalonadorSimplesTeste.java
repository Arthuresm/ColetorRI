package crawler.test;

import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.net.MalformedURLException;

import org.junit.Test;

import coletorri.Servidor;
import coletorri.URLAddress;
import com.trigonic.jrobotx.RecordIterator;
import crawler.escalonadorCurtoPrazo.*;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EscalonadorSimplesTeste {
	private static EscalonadorSimples e = new EscalonadorSimples();
        private static int fetcherid = -1;
        private static int numThreads = 10;
        private static boolean [] fetcherState = new boolean[numThreads];
        
        @Test
	public static synchronized void testAdicionaRemovePagina() throws MalformedURLException, InterruptedException {
		
		URLAddress urlProf = new URLAddress("http://www.xpto.com.br/index.html",Integer.MAX_VALUE);
		URLAddress urlAmazon = new URLAddress("http://www.terra.com.br/index.html",1);
		URLAddress urlTerraRep = new URLAddress("http://www.terra.com.br/index.html",1);
		URLAddress urlUOL1 = new URLAddress("http://www.uol.com.br/",1);
		URLAddress urlUOL2 = new URLAddress("http://www.uol.com.br/profMax.html",1);
		URLAddress urlGlobo = new URLAddress("http://www.globo.com.br/index.html",1);
		long timeFirstHitUOL,timeSecondHitUOL; 
		URLAddress u1,u2,u3;
		

		e.adicionaNovaPagina(urlAmazon);//amazon
		e.adicionaNovaPagina(urlTerraRep);
		timeFirstHitUOL = System.currentTimeMillis();
		e.adicionaNovaPagina(urlUOL1);//uol1
		e.adicionaNovaPagina(urlUOL2);//uol2
		e.adicionaNovaPagina(urlGlobo);
		e.adicionaNovaPagina(urlProf);//xpto
                
		//testa a ordem dos elementos
		u1 = e.getURL();
		u2 = e.getURL();
		u3 = e.getURL();
		URLAddress[] ordemEsperada = {urlAmazon,urlUOL1,urlGlobo};
		URLAddress[] ordemFeita = {urlAmazon,urlUOL1,urlGlobo};
		
		//o primeiro nao pode ser urlProf (profundidade infinita)
		assertNotSame("A URL '"+urlProf+"' deveria ser desconsiderada pois possui profundidade muito alta",urlProf,u1);
		
		//verifica se está na ordem correta (os tres primeiros)
		for(int i = 0; i<ordemEsperada.length ; i++){
			assertSame("o end. "+ordemEsperada[i]+" deveria ser o "+i+"º a sair (e deve ser o MESMO objeto)",
					ordemEsperada[i],ordemFeita[i]);
		}
		
		//resgata o 4o (UOL)
		System.out.println("\n\nResgatando uma URL de um dominio que acaba de ser acessado... \n\n");
		URLAddress u4 = e.getURL();
		timeSecondHitUOL = System.currentTimeMillis();
                
		System.out.println(u4.getAddress().equals(urlTerraRep.getAddress()));
                      
                //Condicao u4.getAddress().equals(urlTerraRep.getAddress())
                        
                        
		//testa se a url urlTerraRep foi adicionada (pois, assim, ela foi adicionada duas vezes já que urlTerra==urlTerraRep)
		if(u4.getAddress().equals(urlTerraRep.getAddress())){
			assertTrue("A URL '"+urlTerraRep+"' foi adicionada duas vezes!",false);
		}
                
		//testa a espera para pegar o u4 (uol)		
                System.out.println("t1 = "+timeFirstHitUOL);
                System.out.println("t2 = "+timeSecondHitUOL);
                
		assertTrue("O tempo de espera entre duas requisições do mesmo servidor não foi maior que "+Servidor.ACESSO_MILIS,(timeSecondHitUOL-timeFirstHitUOL)>Servidor.ACESSO_MILIS);
		              
                System.out.println("Terminou testAdicionaRemovePagina ");
        }
        
	@Test
	public synchronized void testServidor() {
		Servidor s= new Servidor("xpto.com");
		assertTrue("Ao iniciar um servidor, ele deve estar acessível",s.isAccessible());
		s.acessadoAgora();
		assertTrue("Como ele acabou de ser acessado, ele não pode estar acessivel",!s.isAccessible());
		try {
			System.out.println("Aguardando "+(Servidor.ACESSO_MILIS+1000)+" milisegundos...");
			this.wait(Servidor.ACESSO_MILIS+1000L);
		} catch (InterruptedException exc) {
			// TODO Auto-generated catch block
			exc.printStackTrace();
		}
		assertTrue("Após a espera de Servidor.ACESSO_MILIS milissegundos, o servidor deve voltar a ficar acessível",s.isAccessible());
                System.out.println("Terminou testServidor");
        }
        
	@Test
	public static synchronized void testaPageFetcher() throws MalformedURLException, InterruptedException, IOException, Exception {
		
                Thread [] PageFetchers = new Thread [numThreads]; 
		URLAddress primeiraSemente = new URLAddress("https://www.globo.com/",1);
		URLAddress segundaSemente = new URLAddress("https://www.amazon.com/",1);
		URLAddress terceiraSemente = new URLAddress("https://www.americanas.com.br/",1);
		URLAddress quartaSemente = new URLAddress("http://www.reuters.com/",1);
		long timeFirstHitUOL,timeSecondHitUOL; 
		
                //Inicializando estados das threads
                for(int i=0;i<numThreads;i++){
                    fetcherState[i]=false;
                }
                
                //Declarando as threads
                for(int i=0;i<numThreads;i++){
                PageFetchers[i] = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        
                            PageFetcher fetcher = new PageFetcher(e);
                            
                            int id = ++fetcherid;//id da thread
                            
                            //System.out.println("Page Fetcher "+id+" criado");
                            
                        try {
                            while(fetcherState[id]=(!e.finalizouColeta())){
                                if(fetcher.pageRequest()){
                                    //System.out.println("Pagina requisitada com sucesso");
                                }else{
                                    System.out.println("Thread "+id+": Falha na requisicao da pagina");
                                }
                            }
                            System.out.println("Thread "+id+": Fim teste Page Fetcher");
                        } catch (MalformedURLException ex) {
                            //Logger.getLogger(EscalonadorSimplesTeste.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (InterruptedException ex) {
                            //Logger.getLogger(EscalonadorSimplesTeste.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (Exception ex) {
                            //Logger.getLogger(EscalonadorSimplesTeste.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        }
                    });
                }
		
		e.adicionaNovaPagina(primeiraSemente);
		//e.adicionaNovaPagina(segundaSemente);
		//e.adicionaNovaPagina(terceiraSemente);
		//e.adicionaNovaPagina(quartaSemente);
                
                System.out.println("Paginas sementes adicionadas... ");
                
                //Inicializando as Threads
                for(int i=0;i<numThreads;i++){
                    PageFetchers[i].start();
                }
                
                int aux=0, finished=0;
                
                //Verificando se todas as threads finalizaram
                while(!e.finalizouColeta()){
                    if(fetcherState[aux])
                        finished++;
                    if(aux==1)
                        aux=0;
                    else
                        aux++;
                }
                
                //Interrompendo as threads
                //for(int i=0;i<2;i++){
                  //  System.out.println("Terminou Thread: "+i);
                    //PageFetchers[i].interrupt();
                //}
                
                System.out.println("Terminou testaPageFetcher");
        }
        
        public static void main(String[] args) throws MalformedURLException, InterruptedException, IOException, Exception{
            System.out.println("Coletor RI - Inicio main:");
            //testServidor();
            //testAdicionaRemovePagina();
            testaPageFetcher();
        }
}    