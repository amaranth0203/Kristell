package com.mycompany.kristell;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mycompany.kristell.DAO.Card;
import com.mycompany.kristell.DAO.CardDao;
import com.mycompany.kristell.DAO.DaoMaster;
import com.mycompany.kristell.DAO.DaoSession;
import com.mycompany.kristell.DAO.Transaction;
import com.mycompany.kristell.DAO.TransactionDao;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
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
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, getResources().getString(R.string.db_name) , null);
        db = helper.getWritableDatabase();
        daoMaster= new DaoMaster( db ) ;
        daoSession = daoMaster.newSession() ;
        listAllCards();
        showTotalMoney();
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
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    public void ListAllCards(MenuItem item) {
        listAllCards();
    }

    public void ListAllTrans(MenuItem item) {
        listAllTrans();
    }

    public void AddCard(MenuItem item) {
        AlertDialog.Builder addCardDialog = new AlertDialog.Builder(this) ;
        LinearLayout outter = new LinearLayout(this) ;
        final EditText edittext1 = new EditText(this);
        final EditText edittext2 = new EditText(this);
        outter.setOrientation(LinearLayout.VERTICAL) ;
        edittext1.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
        edittext2.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
        edittext2.setRawInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        edittext2.setFilters(new InputFilter[]{
                new InputFilter() {
                    public CharSequence filter(CharSequence src, int start, int end, Spanned dst, int dstart, int dend) {
                        if (src.toString().matches("-|\\d|.")) {
                            return src;
                        } else {
                            return "";
                        }
                    }
                }
        });
        edittext1.setHint(getResources().getString(R.string.addCard_Comment_hint));
        edittext2.setHint(getResources().getString(R.string.addCard_Balance_hint));
        outter.addView(edittext1);
        outter.addView(edittext2);

        addCardDialog.setTitle(getResources().getString(R.string.addCard_title)) ;
        addCardDialog.setView(outter) ;
        addCardDialog.setPositiveButton(getResources().getString(R.string.addCard_PositiveButton),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (edittext1.getText().toString().matches("(\t)+| +") ||
                                edittext1.getText().toString().equals("")) {
                        } else {
                            card = new Card();
                            card.setCreateTime(new Date());
                            card.setLastTransaction(new Date());
                            card.setComments(edittext1.getText().toString());
                            if (edittext2.getText().toString().matches("^-?(\\d)+(.(\\d)+)?$")) {
                                card.setBalance(Double.parseDouble(edittext2.getText().toString()));
                            } else {
                                card.setBalance(0.0);
                            }
                            daoSession.getCardDao().insert(card);
                            listAllCards();
                            showTotalMoney();
                        }
                    }
                }
        );
        addCardDialog.setNegativeButton(getResources().getString(R.string.addCard_NegativeButton), null);
        addCardDialog.show() ;
    }

    public void AddTrans( View view ) {
        AlertDialog.Builder addTransDialog = new AlertDialog.Builder(this) ;
        LinearLayout outter = new LinearLayout(this) ;
        final EditText edittext1 = new EditText(this);
        final EditText edittext2 = new EditText(this);
        final EditText edittext3 = new EditText(this);
        outter.setOrientation(LinearLayout.VERTICAL) ;
        edittext1.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
        edittext2.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
        edittext3.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
        edittext2.setRawInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        edittext2.setFilters(new InputFilter[]{
                new InputFilter() {
                    public CharSequence filter(CharSequence src, int start, int end, Spanned dst, int dstart, int dend) {
                        if (src.toString().matches("-|\\d|.")) {
                            return src;
                        } else {
                            return "";
                        }
                    }
                }
        });
        edittext1.setHint(getResources().getString(R.string.addTrans_Comment_hint));
        edittext2.setHint(getResources().getString(R.string.addTrans_Amount_hint));
        edittext3.setHint(getResources().getString(R.string.addTrans_CardComment_hint));
        outter.addView(edittext1);
        outter.addView(edittext2);
        outter.addView(edittext3);

        addTransDialog.setTitle(getResources().getString(R.string.addTrans_title)) ;
        addTransDialog.setView(outter) ;
        addTransDialog.setPositiveButton(getResources().getString(R.string.addTrans_PositiveButton),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (edittext1.getText().toString().matches("(\t)+| +") ||
                                edittext1.getText().toString().equals("")) {
                        } else {
                            transaction = new Transaction();
                            transaction.setOccurredTime(new Date());
                            transaction.setComments(edittext1.getText().toString());
                            card = getCardByComment(edittext3.getText().toString());
                            if (edittext2.getText().toString().matches("^-?(\\d)+(.(\\d)+)?$")) {
                                transaction.setAmount(Double.parseDouble(edittext2.getText().toString()));
                                card.setLastTransaction(new Date());
                                card.setBalance(card.getBalance() - transaction.getAmount());
                                daoSession.getCardDao().update(card);
                            } else {
                                transaction.setAmount(0.0);
                            }
                            transaction.setCard(card);
                            daoSession.getTransactionDao().insert(transaction);
                            listAllCards();
                            showTotalMoney();
                        }
                    }
                }
        );
        addTransDialog.setNegativeButton(getResources().getString(R.string.addTrans_NegativeButton), null);
        addTransDialog.show() ;
    }

    public void exportDatabase( MenuItem item ) {
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (sd.canWrite()) {
                String currentDBPath = "//data//com.mycompany.kristell//databases//test-db";
                String backupDBPath = "test-db";
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                    Toast.makeText(getApplicationContext(), R.string.exportDB_success, Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            Toast.makeText( getApplicationContext() , R.string.exportDB_failed , Toast.LENGTH_SHORT ).show();
        }
    }

    public void ImportDatabase( MenuItem item ) {
        //仅仅把传输方向倒转过来
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (sd.canWrite()) {
                String currentDBPath = "//data//com.mycompany.kristell//databases//test-db";
                String backupDBPath = "test-db";
//                File currentDB = new File(data, currentDBPath);
//                File backupDB = new File(sd, backupDBPath);
                File backupDB = new File(data, currentDBPath);
                File currentDB = new File(sd, backupDBPath);

                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                    Toast.makeText(getApplicationContext(), R.string.importDB_success, Toast.LENGTH_SHORT).show();

                    //重新打开数据库
                    DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, getResources().getString(R.string.db_name) , null);
                    db = helper.getWritableDatabase();
                    daoMaster= new DaoMaster( db ) ;
                    daoSession = daoMaster.newSession() ;

                    listAllCards();
                    showTotalMoney();
                }
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), R.string.importDB_failed, Toast.LENGTH_SHORT).show();
        }
    }

    private void listAllCards( ) {
        LinearLayout showLinear = ( LinearLayout )findViewById( R.id.showLinear ) ;
        showLinear.removeAllViews();
        Button tmpButton ;
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT) ;
        cardDao = daoSession.getCardDao() ;
        final List<Card> cards = cardDao.loadAll() ;
        String message = "" ;
        for( int i = 0 ; i < cards.size() ; i ++ )
        {
            message +=  cards.get(i).getComments() + " : " +
                    cards.get(i).getBalance() + "\n" +
                    new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss").
                            format(cards.get(i).getCreateTime()) + " created\n" +
                    new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss").
                            format(cards.get(i).getLastTransaction()) + " final trans";
            tmpButton = new Button( this ) ;
            tmpButton.setGravity(Gravity.LEFT);
            tmpButton.setText(message);
            final AlertDialog.Builder modifyCardInfoDialog = new AlertDialog.Builder(this) ;
            EditText editText = new EditText(this);
            modifyCardInfoDialog.setTitle(getResources().getString(R.string.modifyCard_title)) ;
            editText.setHint(cards.get(i).getComments().toString()) ;
            editText.setId(Integer.parseInt(cards.get(i).getId() + "")) ;
            modifyCardInfoDialog.setView(editText) ;
            modifyCardInfoDialog.setPositiveButton(getResources().getString(R.string.modifyCard_PositiveButton),
                    new modifyCardCommentListener(
                            cards.get(i),
                            editText
                    )
            );
            modifyCardInfoDialog.setNegativeButton(getResources().getString(R.string.modifyCard_NegativeButton), null);
            tmpButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    modifyCardInfoDialog.show();
                }
            });
            showLinear.addView(tmpButton, layoutParams);
            message = "" ;
        }
    }

    private void listAllTrans( ) {
        LinearLayout showLinear = ( LinearLayout )findViewById( R.id.showLinear ) ;
        showLinear.removeAllViews();
        Button tmpButton ;
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT) ;
        transactionDao = daoSession.getTransactionDao() ;
        final List<Transaction> trans = transactionDao.loadAll() ;
        String message = "" ;
        for( Transaction transaction : trans ) {
            message +=  transaction.getAmount() + "\n" +
                    transaction.getComments() + "\n" +
                    new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss").
                            format(transaction.getOccurredTime()) ;
            tmpButton = new Button(this);
            tmpButton.setGravity(Gravity.LEFT);
            tmpButton.setText(message);
            final AlertDialog.Builder modifyTransInfoDialog = new AlertDialog.Builder(this);
            EditText editText = new EditText(this);
            modifyTransInfoDialog.setTitle(getResources().getString(R.string.modifyTrans_title));
            editText.setHint(transaction.getComments());
            editText.setId(Integer.parseInt(transaction.getId() + ""));
            modifyTransInfoDialog.setView(editText);
            modifyTransInfoDialog.setPositiveButton(getResources().getString(R.string.modifyTrans_PositiveButton),
                    new modifyTransCommentListener(
                            transaction,
                            editText
                    )
            );
            modifyTransInfoDialog.setNegativeButton(getResources().getString(R.string.modifyTrans_NegativeButton), null);
            tmpButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    modifyTransInfoDialog.show();
                }
            });
            showLinear.addView(tmpButton, layoutParams);
            message = "";
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

    private class modifyTransCommentListener implements DialogInterface.OnClickListener{
        private Transaction transaction ;
        private EditText editText ;
        public modifyTransCommentListener( Transaction transaction , EditText editText ){
            this.transaction = transaction ;
            this.editText = editText ;
        }
        public void onClick(DialogInterface dialogInterface, int i) {
            transaction.setComments(editText.getText().toString());
            transactionDao.getSession().update(transaction) ;
            listAllTrans();
        }
    }

    private Card getCardByComment( String comment ) {
        List<Card> cards = daoSession.getCardDao().loadAll() ;
        for( Card card : cards ) {
            if( card.getComments().equals( comment ) )
                return card ;
        }
        return null ;
    }

    private void showTotalMoney() {
        double money = 0 ;
        for( Card card : daoSession.getCardDao().loadAll() )
            money += card.getBalance() ;
        TextView __ = (TextView)findViewById(R.id.editText_test) ;
        __.setText(getResources().getString(R.string.total_money) + " : " + money) ;
        if(money>1000) __.setTextColor(Color.rgb(0, 128, 0));
        else __.setTextColor(Color.rgb(255, 0, 0));
        __.setTypeface(null, Typeface.BOLD_ITALIC);
    }
}
