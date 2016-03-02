package com.mycompany.kristell;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.app.FragmentActivity;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class MainActivity extends FragmentActivity {

    private SQLiteDatabase db ;
    private DaoMaster daoMaster ;
    private DaoSession daoSession ;
    private TransactionDao transactionDao ;
    private Transaction transaction ;
    private CardDao cardDao ;
    private Card card ;
    DemoCollectionPagerAdapter mDemoCollectionPagerAdapter;
    ViewPager mViewPager;
    private void RefreshViewPager( ) {
        mDemoCollectionPagerAdapter =
                new DemoCollectionPagerAdapter(
                        getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mDemoCollectionPagerAdapter);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, getResources().getString(R.string.db_name) , null);
        db = helper.getWritableDatabase();
        daoMaster= new DaoMaster( db ) ;
        daoSession = daoMaster.newSession() ;
        showTotalMoney();


        mDemoCollectionPagerAdapter =
                new DemoCollectionPagerAdapter(
                        getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mDemoCollectionPagerAdapter);

    }
    @Override
    public void onPause(){
        finish() ;
        super.onPause();
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
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setCurrentItem(0, true);
    }

    public void ListLast20Trans( MenuItem item ) {
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setCurrentItem(1,true);
    }

    public void ListAllTrans(MenuItem item) {
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setCurrentItem(3,true);
    }

    public void AddCard(MenuItem item) {
        AlertDialog.Builder addCardDialog = new AlertDialog.Builder(this);
        LinearLayout outter = new LinearLayout(this);
        final EditText edittext1 = new EditText(this);
        final EditText edittext2 = new EditText(this);
        outter.setOrientation(LinearLayout.VERTICAL);
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
                            RefreshViewPager( ) ;
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
                            RefreshViewPager() ;
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

                    RefreshViewPager() ;
                    showTotalMoney();
                }
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), R.string.importDB_failed, Toast.LENGTH_SHORT).show();
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
            RefreshViewPager( ) ;
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
            RefreshViewPager();
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
        //四舍五入保留最后两位小数
        __.setText(getResources().getString(R.string.total_money) + " : " + ( ( int )( ( money * 1000 ) + 5 ) / 10 ) / ( 1.0 * 100 ) ) ;
        if(money>1000) __.setTextColor(Color.rgb(0, 128, 0));
        else __.setTextColor(Color.rgb(255, 0, 0));
        __.setTypeface(null, Typeface.BOLD_ITALIC);
    }

    public class DemoCollectionPagerAdapter extends FragmentStatePagerAdapter {
        public ShowEntity GenerateShowEntity( int index ) {
            ShowEntity showEntity = new ShowEntity() ;
            Button tmpButton ;
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT) ;
            switch(index){
                case 0 : {
                    // list all card
                    cardDao = daoSession.getCardDao() ;
                    final List<Card> cards = cardDao.loadAll() ;
                    String message = "" ;
                    for( int i = 0 ; i < cards.size() ; i ++ )
                    {
                        message +=  cards.get(i).getComments() + " : " +
                                ( ( int )( ( cards.get(i).getBalance() * 1000 ) + 5 ) / 10 ) / ( 1.0 * 100 ) + "\n" +
                                new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss").
                                        format(cards.get(i).getCreateTime()) + " created\n" +
                                new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss").
                                        format(cards.get(i).getLastTransaction()) + " final trans";
                        tmpButton = new Button( MainActivity.this ) ;
                        tmpButton.setGravity(Gravity.LEFT);
                        tmpButton.setText(message);
                        final AlertDialog.Builder modifyCardInfoDialog = new AlertDialog.Builder(MainActivity.this) ;
                        EditText editText = new EditText(MainActivity.this);
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
                        showEntity.getButtonList().add(tmpButton) ;
                        message = "" ;
                    }
                }
                break;
                case 1 : {
                    // list last 20 transaction
                    transactionDao = daoSession.getTransactionDao() ;
                    final List<Transaction> allTrans = transactionDao.loadAll() ;
                    List<Transaction> trans = new ArrayList<Transaction>() ;
                    for( int i = allTrans.size() - 1 ; i > allTrans.size() - 20 -1 & i >= 0  ; i -- ) {
                        trans.add( allTrans.get( i ) ) ;
                    }
                    String message = "" ;
                    for( Transaction transaction : trans ) {
                        message += ( ( int )( ( transaction.getAmount() * 1000 ) + 5 ) / 10 ) / ( 1.0 * 100 ) + " -> " + transaction.getCard().getComments() + "\n" +
                                transaction.getComments() + "\n" +
                                new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss").
                                        format(transaction.getOccurredTime()) ;
                        tmpButton = new Button( MainActivity.this );
                        tmpButton.setGravity(Gravity.LEFT);
                        tmpButton.setText(message);
                        final AlertDialog.Builder modifyTransInfoDialog = new AlertDialog.Builder( MainActivity.this );
                        EditText editText = new EditText( MainActivity.this );
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
                        showEntity.getButtonList().add(tmpButton) ;
                        message = "";
                    }
                }
                break ;
                case 2 : {
                    tmpButton = new Button( MainActivity.this );
                    tmpButton.setGravity(Gravity.LEFT);
                    tmpButton.setText("this tab is for lazy-loading all transaction");
                    showEntity.getButtonList().add(tmpButton) ;
                }
                break ;
                case 3 : {
                    // list all transaction
                    transactionDao = daoSession.getTransactionDao() ;
                    final List<Transaction> trans = transactionDao.loadAll() ;
                    java.util.Collections.reverse( trans ) ;
                    String message = "" ;
                    for( Transaction transaction : trans ) {
                        message += ( ( int )( ( transaction.getAmount() * 1000 ) + 5 ) / 10 ) / ( 1.0 * 100 ) + " -> " + transaction.getCard().getComments() + "\n" +
                                transaction.getComments() + "\n" +
                                new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss").
                                        format(transaction.getOccurredTime()) ;
                        tmpButton = new Button( MainActivity.this );
                        tmpButton.setGravity(Gravity.LEFT);
                        tmpButton.setText(message);
                        final AlertDialog.Builder modifyTransInfoDialog = new AlertDialog.Builder( MainActivity.this );
                        EditText editText = new EditText( MainActivity.this );
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
                        showEntity.getButtonList().add(tmpButton) ;
                        message = "";
                    }
                }
                break ;
            }
            return showEntity ;
        }
        public DemoCollectionPagerAdapter(FragmentManager fm) {
            super(fm);
        }
        @Override
        public Fragment getItem(int i) {
            Fragment fragment = new DemoObjectFragment();
            Bundle args = new Bundle();
//            args.putInt(DemoObjectFragment.ARG_OBJECT,i+1) ;
            args.putSerializable(DemoObjectFragment.ARG_OBJECT , GenerateShowEntity( i ));
            fragment.setArguments(args);
            return fragment;
        }
        @Override
        public int getCount() {
            return 4;
        }
        @Override
        public CharSequence getPageTitle(int position) {
            return "OBJECT " + (position + 1);
        }
    }

}
