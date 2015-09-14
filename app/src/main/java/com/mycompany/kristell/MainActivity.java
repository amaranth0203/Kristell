package com.mycompany.kristell;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.display.DisplayManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.mycompany.DaoMaster;
import com.mycompany.DaoSession;
import com.mycompany.User;
import com.mycompany.UserDao;

import java.util.Date;
import java.util.List;

import de.greenrobot.dao.internal.DaoConfig;

public class MainActivity extends AppCompatActivity {

    private SQLiteDatabase db ;
    private DaoMaster daoMaster ;
    private DaoSession daoSession ;
    private UserDao userDao ;
    private User user ;

    public void testButtonClicked( View view ) {
        System.out.println("[+] This is testButtonClicked function" + new Date().toString()) ;


        EditText editText = ( EditText )findViewById( R.id.editText_test ) ;
        String Message = editText.getText().toString() ;
        TextView textView = ( TextView )findViewById( R.id.textView_test ) ;
        textView.setText(Message) ;

        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "test-db", null);
        db = helper.getWritableDatabase();
        daoMaster= new DaoMaster( db ) ;
        daoSession = daoMaster.newSession() ;
        userDao = daoSession.getUserDao() ;
        user = new User( ) ;
        user.setName("ben") ;
        user.setPassword("ben's passwd") ;
        user.setUserId(Long.parseLong(Message)) ;
        userDao.insert(user) ;
    }

    public void showButtonClicked( View view ) {
        System.out.println("[+] This is showButtonClicked function" + new Date().toString()) ;
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "test-db", null);
        db = helper.getWritableDatabase();
        daoMaster= new DaoMaster( db ) ;
        daoSession = daoMaster.newSession() ;
        userDao = daoSession.getUserDao() ;
        List<User> users = userDao.loadAll() ;
        TextView textView = ( TextView )findViewById( R.id.textView_test ) ;
        String Message = "" ;
        for( int i = 0 ; i < users.size() ; i ++ )
            Message += users.get(i).getUserId() + "\n";
        textView.setText(Message) ;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
