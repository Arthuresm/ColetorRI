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
        private static boolean [] fetcherState = new boolean[2];
        
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
		
                Thread [] PageFetchers = new Thread [2]; 
		URLAddress primeiraSemente = new URLAddress("https://www.globo.com/",1);
		URLAddress segundaSemente = new URLAddress("https://www.amazon.com/",1);
		URLAddress terceiraSemente = new URLAddress("https://www.americanas.com.br/",1);
		URLAddress urlUOL1 = new URLAddress("http://www.uol.com.br/",1);
		long timeFirstHitUOL,timeSecondHitUOL; 
		
                //Inicializando estados das threads
                for(int i=0;i<2;i++){
                    fetcherState[i]=false;
                }
                
                //Declarando as threads
                for(int i=0;i<2;i++){
                PageFetchers[i] = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        
                            PageFetcher fetcher = new PageFetcher(e);
                            System.out.println("Page Fetcher criado");
                            
                            int id = ++fetcherid;//id da thread
                            
                        try {
                            while(fetcherState[id]=(!e.finalizouColeta())){
                                if(fetcher.pageRequest()){
                                    //System.out.println("Pagina requisitada com sucesso");
                                }else{
                                    System.out.println("Falha na requisicao da pagina");
                                }
                            }
                            System.out.println("Fim teste Page Fetcher");
                            System.out.println("Thread "+id+": stop");
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
		//timeFirstHitUOL = System.currentTimeMillis();
		//e.adicionaNovaPagina(terceiraSemente);
		//e.adicionaNovaPagina(urlUOL2);//uol2
                
                System.out.println("Paginas adicionadas... ");
                
                //Inicializando as Threads
                for(int i=0;i<2;i++){
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
                
                System.out.println("Terminou testAdicionaRemovePagina ");
		
        }
        
        public static void main(String[] args) throws MalformedURLException, InterruptedException, IOException, Exception{
            System.out.println("Inicio main");
            testaPageFetcher();
        }
}    