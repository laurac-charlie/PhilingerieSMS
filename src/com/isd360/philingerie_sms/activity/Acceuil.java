package com.isd360.philingerie_sms.activity;

import java.io.FileNotFoundException;

import com.isd360.philingerie_sms.entity.Destinataire;
import com.isd360.philingerie_sms.util.FTPManager;
import com.isd360.philingerie_sms.util.ParserCSV;
import com.isd360.philingerie_sms.util.SmsSender;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Acceuil extends Activity {
	
	private TextView txtV = null;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        //On déclare la textbox et on met un mouvement de scroll
        this.txtV = (TextView)this.findViewById(R.id.txt_listEnvoi);
        this.txtV.setMovementMethod(new ScrollingMovementMethod());
        
        Button btn = (Button) findViewById(R.id.sendMessage);
		btn.setOnClickListener(this.clickSendListener);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.layout.menu_config, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
           case R.id.menu_config:
        	   Acceuil.this.clickMenuListener.onClick(getCurrentFocus());
              return true;
           default:
              return false;
        }
    }
    
    private OnClickListener clickMenuListener = new OnClickListener() {
		public void onClick(View view) {
				Intent intent = new Intent(Acceuil.this,ConfigurationActivity.class);
				startActivity(intent);
		}
	};
	
	private OnClickListener clickSendListener = new OnClickListener() {
		public void onClick(View view) {			
			
			String filename = "test.csv";
			
			try {
				FTPManager.DownloadCSVfile(filename,Acceuil.this);
				
			} catch (Exception e) {
				Toast.makeText(Acceuil.this,e.getMessage(),300).show();
			}
			
			MessageThread mt = new MessageThread(filename);
			mt.start();
			
		}
	};
	
	private void addMessage(String message){
        //On ajoute la ligne à la TextView
        this.txtV.append(message+"\n");
        //On va ensuite scroller si nécessaire
        final int scrollAmount = this.txtV.getLayout().getLineTop(this.txtV.getLineCount()) - this.txtV.getHeight();
        if(scrollAmount>0)
            this.txtV.scrollTo(0, scrollAmount);
        else
            this.txtV.scrollTo(0,0);
    }
	
	private class MessageThread extends Thread{
		
		public MessageThread(String filename){
			this.filename = filename;
		}
		
		private String filename = "";
		private String logMsg = "";
		
		public void run(){
			ParserCSV psr = null;
			try {
				psr = new ParserCSV(this.filename);
			} catch (FileNotFoundException fnf) {
				runOnUiThread(new Runnable() {
					public void run() {
						Toast.makeText(Acceuil.this, "Le fichier spécifié n'a pas été trouvé. Vérifier d'il existe.", 500).show();
					}
				});
			}
			
			runOnUiThread(new Runnable() {
				public void run() {
					Acceuil.this.txtV.setText("");
				}
			});
			
			for (Destinataire d : psr.parseRecipient())
			{
				/*if (SmsSender.SendMessage(d))
					Toast.makeText(Acceuil.this, "Le message a bien été envoyé à" + d.getLastName() + " " + d.getFirstName(), Toast.LENGTH_SHORT).show();
				else
					Toast.makeText(Acceuil.this, "Erreur d'envoi du message", Toast.LENGTH_SHORT).show();
				*/
				
				if(SmsSender.SendMessage(d))
					this.logMsg = "Envoi : " + d.getLastName() + " " + d.getFirstName() + " [OK]";
				else
					this.logMsg = "Envoi : " + d.getLastName() + " " + d.getFirstName() + " [KO]";
				
				runOnUiThread(new Runnable() {
					public void run() {
						addMessage(MessageThread.this.logMsg);
					}
				});
				
				try 
					{Thread.sleep(500);} 
				catch (InterruptedException e) 
					{e.printStackTrace();}
			}
			
			runOnUiThread(new Runnable() {
				public void run() {
					addMessage("Fin");
				}
			});
		}
	}
}