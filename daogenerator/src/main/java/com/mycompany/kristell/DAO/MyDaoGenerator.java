package com.mycompany.kristell.DAO;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import de.greenrobot.daogenerator.DaoGenerator ;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;

public class MyDaoGenerator {

    private static final String GENERATED_SRC_DIR = "./app/gen-src";

    public static void main(String[] args) throws Exception {
        System.out.println( "[+] " + MyDaoGenerator.class.getPackage().getName()) ;
        Schema schema = new Schema(1, MyDaoGenerator.class.getPackage().getName() );
//        Entity entity = schema.addEntity("User");
//        entity.addLongProperty("UserId").notNull();
//        entity.addStringProperty("Password").notNull();
//        entity.addStringProperty("Name").notNull();
        Entity transaction = schema.addEntity( "Transaction" ) ;
        Entity card = schema.addEntity( "Card" ) ;
        transaction.addIdProperty() ;
        transaction.addDoubleProperty("Amount") ;
        transaction.addDateProperty("OccurredTime") ;
        transaction.addStringProperty("Comments") ;
        Property trans_cardId = transaction.addLongProperty( "CardId" ).getProperty() ;
        card.addIdProperty() ;
        card.addDoubleProperty("Balance") ;
        card.addDateProperty("CreateTime") ;
        card.addDateProperty("LastTransaction");
        card.addStringProperty("Comments") ;

        // one card may associate with many transactions
        card.addToMany( transaction , trans_cardId  ) ;

        // one transactioins can only associate with one card
        transaction.addToOne( card , trans_cardId ) ;

        createDirs(Paths.get(GENERATED_SRC_DIR));
        new DaoGenerator().generateAll(schema, GENERATED_SRC_DIR);
    }

    private static void createDirs(Path path)  throws IOException {
//        if (Files.notExists(path, LinkOption.NOFOLLOW_LINKS)) {
//            Set<PosixFilePermission> perms = PosixFilePermissions.fromString("rwxr-x---");
//            FileAttribute<Set<PosixFilePermission>> attrs = PosixFilePermissions.asFileAttribute(perms);
//            Files.createDirectories(path, attrs);
//        }

        File theDir = new File( path.toString() );
        if (!theDir.exists()) {
            System.out.println("creating directory: " + path.toString() );
            boolean result = false;

            try{
                final boolean mkdir_ = theDir.mkdir();
                result = true;
            }catch(SecurityException se){ System.out.println(se.toString()) ;}
            if(result) {
                System.out.println("DIR created");
            }
        }


    }
}
