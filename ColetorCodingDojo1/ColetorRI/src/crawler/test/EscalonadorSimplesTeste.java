package crawler.test;

import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.net.MalformedURLException;

import org.junit.Test;

import coletorri.Servidor;
import coletorri.URLAddress;
import crawler.escalonadorCurtoPrazo.Escalonador;
import crawler.escalonadorCurtoPrazo.EscalonadorSimples;

public class EscalonadorSimplesTeste {
	private static Escalonador e = new EscalonadorSimples();
        
	@Test
	public synchronized void testServidor() {
		Servidor s= new Servidor("xpto.com");
		assertTrue("Ao iniciar um servidor, ele deve estar acessível",s.isAccessible());
		s.acessadoAgora();
		assertTrue("Como ele acabou de ser acessado, ele não pode estar acessivel",!s.isAccessible());
		try {
			System.out.println("Aguardando "+(Servidor.ACESSO_MILIS+1000)+" milisegundos...");
			this.wait(Servidor.ACESSO_MILIS+1000L);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue("Após a espera de Servidor.ACESSO_MILIS milissegundos, o servidor deve voltar a ficar acessível",s.isAccessible());
                System.out.println("Terminou testServidor");
        }
	
	@Test
	public static synchronized void testAdicionaRemovePagina() throws MalformedURLException {
		
		URLAddress urlProf = new URLAddress("http://www.xpto.com.br/index.html",Integer.MAX_VALUE);
		URLAddress urlTerra = new URLAddress("http://www.terra.com.br/index.html",1);
		URLAddress urlTerraRep = new URLAddress("http://www.terra.com.br/index.html",1);
		URLAddress urlUOL1 = new URLAddress("http://www.uol.com.br/",1);
		URLAddress urlUOL2 = new URLAddress("http://www.uol.com.br/profMax.html",1);
		URLAddress urlGlobo = new URLAddress("http://www.globo.com.br/profMax.html",1);
		long timeFirstHitUOL,timeSecondHitUOL; 
		URLAddress u1,u2,u3;
		
		
		e.adicionaNovaPagina(urlTerra);//terra
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
		URLAddress[] ordemEsperada = {urlTerra,urlUOL1,urlGlobo};
		URLAddress[] ordemFeita = {urlTerra,urlUOL1,urlGlobo};
		
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
                
		//System.out.println(u4.getAddress().equals(urlTerraRep.getAddress()));
                
                
//                
//                Condicao u4.getAddress().equals(urlTerraRep.getAddress())
                        
                        
		//testa se a url urlTerraRep foi adicionada (pois, assim, ela foi adicionada duas vezes já que urlTerra==urlTerraRep)
		if(u4.getAddress().equals(urlTerraRep.getAddress())){
			assertTrue("A URL '"+urlTerraRep+"' foi adicionada duas vezes!",false);
		}

		
		//testa a espera para pegar o u4 (uol)		
                //System.out.println("t1 = "+timeFirstHitUOL);
                //System.out.println("t2 = "+timeSecondHitUOL);
                
		assertTrue("O tempo de espera entre duas requisições do mesmo servidor não foi maior que "+Servidor.ACESSO_MILIS,(timeSecondHitUOL-timeFirstHitUOL)>Servidor.ACESSO_MILIS);
		
		
                System.out.println("Terminou testAdicionaRemovePagina ");
	}
        

        
        public static void main(String[] args) throws MalformedURLException{	
//            testServidor();
            testAdicionaRemovePagina();
	}
        

}
