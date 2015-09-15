package com.mycompany.kristell;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.display.DisplayManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.mycompany.kristell.DAO.Card;
import com.mycompany.kristell.DAO.CardDao;
import com.mycompany.kristell.DAO.DaoMaster;
import com.mycompany.kristell.DAO.DaoSession;
import com.mycompany.kristell.DAO.Transaction;
import com.mycompany.kristell.DAO.TransactionDao;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.greenrobot.dao.internal.DaoConfig;

public class MainActivity extends AppCompatActivity {

    private SQLiteDatabase db ;
    private DaoMaster daoMaster ;
    private DaoSession daoSession ;
    private TransactionDao transactionDao ;
    private Transaction transaction ;
    private CardDao cardDao ;
    private Card card ;
//    private UserDao userDao ;
//    private User user ;

    public void testButtonClicked( View view ) {
        System.out.println("[+] This is testButtonClicked function" + new Date().toString()) ;
        EditText editText = ( EditText )findViewById( R.id.editText_test ) ;
        String Message = editText.getText().toString() ;
        editText.setText("");
        Button button = ( Button )findViewById( R.id.button2 ) ;
        button.setText( "comment : " + Message.split(" ")[0] ) ;
        TextView textView = ( TextView )findViewById( R.id.textView_test ) ;
        textView.setText( "balance : " + Message.split(" ")[1] ) ;
        card = new Card( ) ;
        card.setCreateTime( new Date() ) ;
        card.setComments(Message.split(" ")[0]) ;
        card.setBalance(Double.parseDouble(Message.split(" ")[1])) ;
        cardDao = daoSession.getCardDao() ;
        cardDao.insert( card ) ;

//        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "test-db", null);
//        db = helper.getWritableDatabase();
//        daoMaster= new DaoMaster( db ) ;
//        daoSession = daoMaster.newSession() ;
//        userDao = daoSession.getUserDao() ;
//        user = new User( ) ;
//        user.setName("ben") ;
//        user.setPassword("ben's passwd") ;
//        user.setUserId(Long.parseLong(Message)) ;
//        userDao.insert(user) ;
    }

    public void showButtonClicked( View view ) {
        System.out.println("[+] This is showButtonClicked function" + new Date().toString()) ;
        cardDao = daoSession.getCardDao() ;
        String message = "" ;
        List<Card> cards = cardDao.loadAll() ;
        for( int i = 0 ; i < cards.size() ; i ++ )
        {
//            message +=  cards.get(i).getId() + " -- id\n" ;
            message +=  cards.get(i).getComments() + " -- 注释\n" +
                        cards.get(i).getBalance() + " -- 余额\n" +
                        new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss").
                            format(cards.get(i).getCreateTime()) + " -- 添加时间\n\n" ;
        }
        TextView textView = ( TextView )findViewById( R.id.textView_test ) ;
        textView.setText( message ) ;
//        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "test-db", null);
//        db = helper.getWritableDatabase();
//        daoMaster= new DaoMaster( db ) ;
//        daoSession = daoMaster.newSession() ;
//        userDao = daoSession.getUserDao() ;
//        List<User> users = userDao.loadAll() ;
//        TextView textView = ( TextView )findViewById( R.id.textView_test ) ;
//        String Message = "" ;
//        for( int i = 0 ; i < users.size() ; i ++ )
//            Message += users.get(i).getUserId() + "\n";
//        textView.setText(Message) ;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "test-db", null);
        db = helper.getWritableDatabase();
        daoMaster= new DaoMaster( db ) ;
        daoSession = daoMaster.newSession() ;
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
}
