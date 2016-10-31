package bitTrolls;

import org.bitcoinj.core.*;
import org.bitcoinj.core.ECKey.ECDSASignature;
import org.bitcoinj.crypto.TransactionSignature;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.params.RegTestParams;
import org.bitcoinj.utils.BriefLogFormatter;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;
import org.bitcoinj.wallet.SendRequest;

import java.io.File;
import java.util.*;

import javax.annotation.Nullable;

import static org.bitcoinj.core.Coin.*;
public class TestRecTx {
    
	  public static byte[] hexStringToByteArray(String s) {
		  int len = s.length();
		  byte[] data = new byte[len / 2];
		  for (int i = 0; i < len; i += 2) {
			  data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
					  				+ Character.digit(s.charAt(i+1), 16));
		  }
		  return data;
	  }
	
	  public static void main(String[] args) throws Exception {
	        BriefLogFormatter.init();
	        final RegTestParams params = RegTestParams.get();
	        byte[] checksum1 = hexStringToByteArray("e04fd020ea3a6910a2d808002b30309d");
	        byte[] checksum2 = hexStringToByteArray("e04fd020ea3a6910a2d808002b30309e");
	        boolean flag=true;
	        int OP_EQUAL=0x87;  
	        int OP_DUP=0x76;  
	        int OP_HASH160=0xa9;  
	        int OP_EQUALVERIFY=0x88;  
	        int OP_CHECKSIG=0xac;  
	        int OP_VERIFY=0x69;

	        WalletAppKit wallet1 = new WalletAppKit(params, new File("."), "wallet1");
	        WalletAppKit wallet2 = new WalletAppKit(params, new File("."), "wallet2");
	        WalletAppKit wallet3 = new WalletAppKit(params, new File("."), "wallet3");
	        WalletAppKit wallet4 = new WalletAppKit(params, new File("."), "wallet4");
		    
	        
	        wallet1.connectToLocalHost();
	        wallet1.setAutoSave(false);
	        wallet1.startAsync();
	        wallet1.awaitRunning();
	        wallet2.connectToLocalHost();
	        wallet2.setAutoSave(false);
	        wallet2.startAsync();
	        wallet2.awaitRunning();
	        wallet3.connectToLocalHost();
	        wallet3.setAutoSave(false);
	        wallet3.startAsync();
	        wallet3.awaitRunning();
	        wallet4.connectToLocalHost();
	        wallet4.setAutoSave(false);
	        wallet4.startAsync();
	        wallet4.awaitRunning();
        
	        
	        
	        Address address4 = wallet4.wallet().currentReceiveAddress();
	        Address address3 = wallet3.wallet().currentReceiveAddress();
	        Set<Transaction> transactions = wallet3.wallet().getTransactions(true);
	        // to get second param, <rawid>, get txid and do the following:
	        //./bitcoin-cli -regtest getrawtransation <txid> --> outputs long <rawid>
	        //./bitcoin-cli -regtest decoderawtransaction<rawid> --> outputs hashtable of tx to check.
	        
	        Transaction temp = new Transaction(params, hexStringToByteArray("01000000018000292266ef1b817eac51a5e1b98918e53cdc3b9ac231704f69b7d2da987b99000000006b483045022100952f58844697a3166b1e1062d498cba5a63ff0b493418ea820470172065f0f1302206c85736a69eb39979dd25392a1b60f2bcb0d4ec1e647296f85a6aa5c38aefdae012102583612dc34a5e8df2806c519027962719925d706aea1857e3df569ccc386c350ffffffff0200f90295000000002c76a9143cb5180e3a59bba6d3fcd1cdf8701d6d429fd63e88ac6910e04fd020ea3a6910a2d808002b30309d87f4ba08bf010000001976a9144e90eb242dd4e66197c6a8c6f4c261aff4a07bbc88ac00000000") );
	        TransactionOutput output = temp.getOutput(0);	        
	        System.out.println("Tansaction output:\n"+output);	 
	        try{
	        	
		        ECKey publicKey3 = wallet3.wallet().getIssuedReceiveKeys().get(0);
		        /*found online ECDSASignature(Hash(Transaction-scriptSig)+PreTransaction_scriptPubKey)*/
		        ECDSASignature sigECDSA = publicKey3.sign(output.getHash()/*I don't think I'm signing the right thing*/);
		        
		        TransactionSignature sig = new TransactionSignature(sigECDSA.r, sigECDSA.s);
		        
		        Script unlocking = ScriptBuilder.createInputScript(sig, publicKey3, checksum1);
		        byte[] unlockingbytes = unlocking.getProgram();
		        TransactionOutPoint outpoint = new TransactionOutPoint(params, output);
		        TransactionInput Input = new TransactionInput(params, temp, unlockingbytes, outpoint);
		        Input.setScriptSig(unlocking);
		        
		        
		        
		        System.out.println("Tansaction input:\n"+Input);
		        
		        Input.verify(output);
		        
	        }catch(Exception e){
	        	e.printStackTrace();
	        	System.out.println("Exception:\n"+e);
	        } 
	        
	        
	        
	        Thread.sleep(1000);
	        wallet1.stopAsync();
	        wallet1.awaitTerminated();
	        wallet2.stopAsync();
	        wallet2.awaitTerminated();
	        wallet3.stopAsync();
	        wallet3.awaitTerminated();
	        wallet4.stopAsync();
	        wallet4.awaitTerminated();
	       
	}      
	   
}