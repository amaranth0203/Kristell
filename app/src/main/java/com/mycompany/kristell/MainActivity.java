package com.mycompany.kristell;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.mycompany.kristell.DAO.Card;
import com.mycompany.kristell.DAO.CardDao;
import com.mycompany.kristell.DAO.DaoMaster;
import com.mycompany.kristell.DAO.DaoSession;
import com.mycompany.kristell.DAO.Transaction;
import com.mycompany.kristell.DAO.TransactionDao;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private SQLiteDatabase db ;
    private DaoMaster daoMaster ;
    private DaoSession daoSession ;
    private TransactionDao transactionDao ;
    private Transaction transaction ;
    private CardDao cardDao ;
    private Card card ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "test-db", null);
        db = helper.getWritableDatabase();
        daoMaster= new DaoMaster( db ) ;
        daoSession = daoMaster.newSession() ;
        listAllCards();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void CardsButtonClicked( View view ) {
        Button button = ( Button )findViewById( R.id.button2 ) ;
        button.setText( "Add");
        listAllCards();
    }

    public void TransButtonClicked(View view ) {
        Button button = ( Button )findViewById( R.id.button2 ) ;
        button.setText( "Add");
        listAllTrans();
    }

    public void AddButtonClicked( View view ) {
        System.out.println("[+] This is testButtonClicked function" + new Date().toString()) ;
        EditText editText = ( EditText )findViewById( R.id.editText_test ) ;
        String message = editText.getText().toString() ;
        if(message.split(" ")[0].equals("1")){
            //add cards
            editText.setText("");
            Button button = ( Button )findViewById( R.id.button2 ) ;
            button.setText( "comment : " + message.split(" ")[1] +
                    "balance : " + message.split(" ")[2]  ) ;
            card = new Card( ) ;
            card.setCreateTime(new Date()) ;
            card.setLastTransaction( new Date(new Date().getTime()+1000000) );
            card.setComments(message.split(" ")[1]) ;
            card.setBalance(Double.parseDouble(message.split(" ")[2])) ;
            cardDao = daoSession.getCardDao() ;
            cardDao.insert( card ) ;
        }else{
            //add transaction
            editText.setText("");
            Button button = ( Button )findViewById( R.id.button2 ) ;
            button.setText( "comment : " + message.split(" ")[1] +
                    "amount : " + message.split(" ")[2] +
                    "cardComment : " + message.split(" ")[3]  ) ;
            transaction = new Transaction( ) ;
            transaction.setComments( message.split(" ")[1] ) ;
            transaction.setAmount( Double.parseDouble(message.split(" ")[2]) ) ;
            transaction.setCard( getCardByComment( message.split(" ")[3] ) ) ;
            transaction.setOccurredTime(new Date()) ;
            transactionDao = daoSession.getTransactionDao() ;
            transactionDao.insert(transaction) ;
        }
    }

    private void listAllCards( ) {
        LinearLayout showLinear = ( LinearLayout )findViewById( R.id.showLinear ) ;
        Button __ = ( Button )findViewById( R.id.button2 ) ;
        showLinear.removeAllViews();
        showLinear.addView(__);
        Button tmpButton ;
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT) ;
        cardDao = daoSession.getCardDao() ;
        final List<Card> cards = cardDao.loadAll() ;
        String message = "" ;
        for( int i = 0 ; i < cards.size() ; i ++ )
        {
            message +=  cards.get(i).getComments() + " : " +
                    cards.get(i).getBalance() + "\n" +
                    new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss").
                            format(cards.get(i).getCreateTime()) + "\n" +
                    new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss").
                            format(cards.get(i).getLastTransaction()) ;
            tmpButton = new Button( this ) ;
            tmpButton.setGravity(Gravity.LEFT);
            tmpButton.setId(Integer.parseInt(cards.get(i).getId() + ""));
            tmpButton.setText(message);
            LayoutInflater inflater = this.getLayoutInflater();
            View v_iew= inflater.inflate( R.layout.activity_main , null) ;
            final AlertDialog.Builder modifyCardInfoDialog = new AlertDialog.Builder(this) ;
            modifyCardInfoDialog.setView(v_iew);
            EditText editText = new EditText(this);
            modifyCardInfoDialog.setTitle("输入新的注释") ;
            editText.setHint(cards.get(i).getComments().toString()) ;
            editText.setId(Integer.parseInt(cards.get(i).getId() + "" )) ;
            modifyCardInfoDialog.setView(editText) ;
            modifyCardInfoDialog.setPositiveButton("好的",
                    new modifyCardCommentListener(
                            cards.get(i) ,
                            editText
                    )
            );
            modifyCardInfoDialog.setNegativeButton("算了", null);
            tmpButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    modifyCardInfoDialog.show() ;
                }
            });
            showLinear.addView(tmpButton, layoutParams);
            message = "" ;
        }
    }

    private class modifyCardCommentListener implements DialogInterface.OnClickListener{
        private Card card ;
        private EditText editText ;
        public modifyCardCommentListener( Card card , EditText editText ){
            this.card = card ;
            this.editText = editText ;
        }
        public void onClick(DialogInterface dialogInterface, int i) {
            card.setComments(editText.getText().toString());
            cardDao = daoSession.getCardDao() ;
            cardDao.update(card);
            listAllCards();
        }
    }

    private void listAllTrans( ) {
    }

    private Card getCardByComment( String comment ) {
        List<Card> cards = daoSession.getCardDao().loadAll() ;
        for( Card card : cards ) {
            if( card.getComments().equals( comment ) )
                return card ;
        }
        return null ;
    }
}
